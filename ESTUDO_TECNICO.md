# Estudo Técnico — Progressor

> **Documento de arquitetura e decisões de design**
> Versão do sistema documentada: **V1.1**
> Audiência: engenheiros de software, avaliadores técnicos, entrevistadores

---

## Índice

1. [Clean Architecture — por que separar o domínio da infraestrutura](#1-clean-architecture--por-que-separar-o-domínio-da-infraestrutura)
2. [Segurança em Primeiro Lugar — AttributeConverters AES-256 e criptografia de mídia](#2-segurança-em-primeiro-lugar--attributeconverters-aes-256-e-criptografia-de-mídia)
3. [Evolução do Banco de Dados — da auto-ddl do Hibernate ao Flyway](#3-evolução-do-banco-de-dados--da-auto-ddl-do-hibernate-ao-flyway)
4. [O Pivot da API de Nutrição — de FatSecret/USDA ao Open Food Facts](#4-o-pivot-da-api-de-nutrição--de-fatsecretusda-ao-open-food-facts)
5. [Estratégia de Testes — o valor dos 32+ testes automatizados](#5-estratégia-de-testes--o-valor-dos-32-testes-automatizados)

---

## 1. Clean Architecture — por que separar o domínio da infraestrutura

### O problema que a Clean Architecture resolve

Em projetos típicos de Spring Boot, é comum ver entidades JPA anotadas com `@Entity`, injetadas diretamente nos controllers, com lógica de negócio espalhada em `@Service`s que dependem de `@Repository`s Spring Data. Essa abordagem funciona para projetos pequenos, mas traz custos crescentes:

- **Acoplamento ao framework:** lógica de negócio fica amarrada ao Spring, impossibilitando testá-la sem subir um contexto.
- **Acoplamento ao banco:** regras de negócio dependem de anotações JPA como `@Column`, `@ManyToOne` e do comportamento do Hibernate.
- **Dificuldade de testar:** uma mudança em uma entidade JPA pode quebrar silenciosamente um caso de uso que parecia não ter relação.

### A solução adotada no Progressor

O projeto divide o código em três camadas concêntricas com uma regra absoluta: **dependências só apontam para dentro**.

```
        ┌──────────────────────────────────────┐
        │           Infraestrutura             │
        │  (Controllers, JPA, Flyway, OFF,     │
        │   JasperReports, Spring Security)    │
        │                                      │
        │   ┌────────────────────────────┐     │
        │   │     Camada de Aplicação    │     │
        │   │  (Casos de Uso, DTOs,      │     │
        │   │   Interfaces de Porta)     │     │
        │   │                            │     │
        │   │   ┌──────────────────┐     │     │
        │   │   │  Camada de       │     │     │
        │   │   │  Domínio         │     │     │
        │   │   │  (Entidades,     │     │     │
        │   │   │  Value Objects,  │     │     │
        │   │   │  Regras de       │     │     │
        │   │   │  Negócio)        │     │     │
        │   │   └──────────────────┘     │     │
        │   └────────────────────────────┘     │
        └──────────────────────────────────────┘
```

#### Domínio: entidades sem dependências externas

A entidade `WorkoutPlan`, por exemplo, é um POJO Java puro:

```
WorkoutPlan
  ├── id: UUID
  ├── studentId: UUID
  ├── trainerId: UUID
  ├── name: String  →  validado no construtor ("Plan name is required")
  └── createdAt: LocalDateTime
```

Sem `@Entity`. Sem `@Column`. Sem Spring. Isso significa que a lógica de validação do domínio pode ser testada com `new WorkoutPlan(...)` — sem banco, sem servidor, sem mocks de infraestrutura.

#### Inversão de dependência com interfaces de porta

O caso de uso `CreateWorkoutPlanUseCase` precisa persistir um plano, mas **não sabe nada sobre JPA ou PostgreSQL**. Ele depende de uma interface:

```java
// Em application.ports
public interface WorkoutPlanRepository {
    void save(WorkoutPlan plan);
    Optional<WorkoutPlan> findById(UUID id);
    List<WorkoutPlan> findByStudentId(UUID studentId);
}
```

O adaptador JPA, em `infrastructure.persistence.adapters`, implementa essa interface e faz a tradução entre o domínio e a entidade JPA (`WorkoutPlanEntity`). O caso de uso nunca vê `WorkoutPlanEntity`. A infraestrutura pode ser substituída — por outro banco, por um repositório em memória para testes — sem tocar uma linha da lógica de negócio.

#### Casos de uso como objetos Java simples

Todos os casos de uso (`CreateWorkoutPlanUseCase`, `SendConnectionRequestUseCase`, etc.) são instanciados manualmente em `UseCaseConfig`:

```java
@Bean
public CreateWorkoutPlanUseCase createWorkoutPlanUseCase(
    WorkoutPlanRepository workoutPlanRepository,
    UserRepository userRepository) {
    return new CreateWorkoutPlanUseCase(workoutPlanRepository, userRepository);
}
```

Não há `@Service`, `@Autowired` ou `@Component` nas classes de caso de uso. Isso garante que:
- São testáveis com `new` e mocks simples do Mockito.
- O framework pode ser trocado sem alterar a lógica de negócio.
- O grafo de dependências fica explícito e auditável em um único arquivo.

#### Herança JOINED para a hierarquia de usuários

O sistema tem três tipos de usuário com campos específicos. A estratégia `JOINED` do JPA foi escolhida deliberadamente:

```
app_users (campos comuns: id, first_name, last_name, email, password, birth_date, role)
    │
    ├── personal_trainers (user_id FK, cref)
    ├── nutritionists (user_id FK, crn)
    └── students (user_id FK, personal_trainer_id, nutritionist_id, daily_water_goal, ...)
```

**Por que JOINED e não SINGLE_TABLE?**
`SINGLE_TABLE` colocaria todos os campos de todas as subclasses em uma única tabela com muitas colunas `NULL`. Com `JOINED`, o banco reflete a hierarquia real do domínio: é possível fazer `SELECT` na tabela `students` sem ver colunas de CREF ou CRN poluindo o resultado.

---

## 2. Segurança em Primeiro Lugar — AttributeConverters AES-256 e criptografia de mídia

### Contexto regulatório

A LGPD (Lei Geral de Proteção de Dados), Art. 11, classifica dados de saúde como **dados sensíveis**, exigindo tratamento diferenciado. O Progressor processa fotos de evolução corporal, medidas biométricas e comunicações médicas entre profissional e paciente — tudo isso se enquadra como dado sensível.

### A escolha: criptografia na camada de persistência

Existem três lugares onde a criptografia pode ser aplicada:
1. **No banco** (Transparent Data Encryption do PostgreSQL)
2. **No transporte** (HTTPS/TLS)
3. **Na aplicação**, antes de enviar ao banco

O Progressor usa a opção 3. A razão é simples: se o banco for comprometido por um dump SQL, por um backup exposto ou por acesso indevido ao servidor de banco de dados, os dados continuam ilegíveis. HTTPS protege o transporte, mas não os dados em repouso no disco.

### Implementação: `AttributeConverter` do JPA

O JPA fornece a interface `AttributeConverter<X, Y>`, onde `X` é o tipo no domínio Java e `Y` é o tipo na coluna do banco. O Progressor implementa dois converters:

**`EncryptedStringConverter`:** cifra strings como `TEXT` no banco.
**`EncryptedByteArrayConverter`:** cifra arrays de bytes (imagens) como `bytea` no banco.

O algoritmo é **AES/GCM/NoPadding** com:
- Chave de 256 bits (32 bytes) derivada da variável de ambiente `ENCRYPTION_KEY`
- IV (vetor de inicialização) gerado aleatoriamente por operação, de 12 bytes
- Tag de autenticação de 128 bits (garante integridade — detecta adulteração)
- O IV é prefixado ao ciphertext antes de armazenar, para permitir a decriptação

A aplicação dos converters nas entidades JPA é declarativa:

```java
@Convert(converter = EncryptedStringConverter.class)
@Column(name = "professional_feedback", columnDefinition = "TEXT")
private String professionalFeedback;

@Convert(converter = EncryptedByteArrayConverter.class)
@Column(name = "photo_data", columnDefinition = "bytea")
private byte[] photoData;
```

### O dilema do e-mail: pesquisabilidade vs. privacidade

O e-mail não é criptografado com AES-GCM. Esta é uma **decisão consciente de design**, não uma omissão:

- AES-GCM é um algoritmo **não determinístico** — o mesmo plaintext produz ciphertexts diferentes a cada chamada (por causa do IV aleatório).
- Um `WHERE email = ?` nunca retornaria resultados, pois `AES("joao@gmail.com", IV₁) ≠ AES("joao@gmail.com", IV₂)`.
- E-mail não é dado de saúde sob o Art. 11 da LGPD.
- E-mail já é protegido por BCrypt na senha e por HTTPS no transporte.

Uma alternativa considerada foi usar **criptografia determinística** (AES-SIV ou HMAC de busca), armazenando um hash separado para `WHERE` e o ciphertext completo para exibição. Essa abordagem (`SearchableEncryptedStringConverter`) foi prototipada no codebase, mas não implementada para o e-mail principal pois adicionaria complexidade desnecessária sem ganho prático de segurança neste contexto.

---

## 3. Evolução do Banco de Dados — da auto-ddl do Hibernate ao Flyway

### O estado em V1.0: Hibernate auto-ddl

Na V1.0, o schema era gerenciado pelo Hibernate com `spring.jpa.hibernate.ddl-auto: update`. Isso é conveniente durante desenvolvimento inicial: o Hibernate detecta mudanças nas entidades e ajusta o banco automaticamente.

**Os problemas que isso cria em produção:**

| Problema | Consequência |
|---|---|
| `ddl-auto: update` nunca **dropa** colunas | Schema acumula lixo de versões antigas silenciosamente |
| Não há histórico de mudanças | Impossível saber qual versão do schema está em qual ambiente |
| Alterações complexas não são executáveis pelo Hibernate | Ex.: adicionar uma constraint UNIQUE em colunas existentes, criar índices parciais |
| Ambientes de staging e produção podem divergir | "Funciona na minha máquina" é um sintoma desse problema |

### A migração para Flyway em V1.1

A V1.1 precisava adicionar duas tabelas novas (`connection_requests`, `workout_plans`, `workout_blocks`) e uma coluna nova (`block_id`) na tabela `workout_exercises`. Era o momento ideal para institucionalizar o controle de schema.

**Estratégia adotada para o baseline:**

O banco de produção já existia (gerenciado pelo Hibernate). O Flyway não pode fingir que um banco vazio é o ponto de partida. A solução foi:

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    baseline-version: 1
```

- `baseline-on-migrate: true` diz ao Flyway: "se este banco ainda não tem a tabela de histórico do Flyway (`flyway_schema_history`), crie-a e marque tudo até a versão `baseline-version` como já aplicado".
- O arquivo `V2__add_invites_and_workout_blocks.sql` contém exclusivamente as mudanças da V1.1.
- O `ddl-auto: update` foi mantido para as tabelas da V1.0 (backward compatibility), mas a V1.1 em diante é puramente via Flyway.

**Por que isso importa?**

Qualquer novo membro da equipe que clonar o repositório e rodar a aplicação terá exatamente o schema correto aplicado, sem intervenção manual. Em staging e produção, a migração é aplicada automaticamente no startup — e o Flyway garante que a mesma migração não será aplicada duas vezes (controle por checksum).

### A constraint UNIQUE como exemplo de poder do Flyway

O Hibernate `ddl-auto: update` não é capaz de criar constraints `UNIQUE` em colunas já existentes de forma confiável. No Flyway, isso é trivial:

```sql
CONSTRAINT uq_connection_request UNIQUE (professional_id, student_id, professional_role)
```

Esta constraint garante que um profissional não possa ter duas solicitações de conexão pendentes para o mesmo aluno no mesmo papel — uma regra de negócio importante que agora está **também no banco**, não apenas na aplicação.

---

## 4. O Pivot da API de Nutrição — de FatSecret/USDA ao Open Food Facts

### O plano original: FatSecret com OAuth 2.0

A primeira versão do módulo de nutrição foi projetada em torno da **FatSecret API**, uma API comercial de banco de dados alimentar com suporte declarado a dados localizados. A implementação chegou a ser completamente construída: um cliente OAuth 2.0 stateful com fluxo de client credentials, cache de token (com renovação automática antes da expiração), e tratamento de erros específico para cada código HTTP da FatSecret.

A estrutura era tecnicamente robusta:

```
ProgressorApp
     │
     ▼
POST accounts.fatsecret.com/connect/token
  (grant_type=client_credentials, scope=basic)
     │
     ▼
{ "access_token": "...", "expires_in": 86400 }
     │  (armazenado em memória, renovado antes de expirar)
     ▼
GET platform.fatsecret.com/rest/server.api
  ?method=foods.search&search_expression=frango&language=pt&region=BR
     │
     ▼
Resultado esperado: alimentos brasileiros em português
```

### O problema descoberto: tier gratuito sem localização real

Após a implementação completa, os testes revelaram um problema crítico: **o tier gratuito da FatSecret ignora os parâmetros `language` e `region`**. Independente de enviar `language=pt&region=BR`, a API retornava exclusivamente resultados genéricos americanos em inglês — `"Chicken Breast, cooked"`, `"White Rice"` — sem nenhuma referência a alimentos brasileiros.

A localização real (dados brasileiros com nomes em português, produtos de marca como "Frango Bob's" ou "Pão de Queijo Forno de Minas") está disponível apenas nos **tiers pagos**, com precificação opaca e lock-in de fornecedor.

Isso criava um impasse arquitetural: a lógica de negócio central do módulo de nutrição — entregar alimentos reconhecíveis ao usuário brasileiro — simplesmente não funcionava com a API escolhida no tier gratuito.

### A alternativa paliativa: USDA com camada de tradução

Como solução temporária, foi construída uma integração com a **USDA FoodData Central** — uma API governamental americana, gratuita e sem paywall. Para contornar o fato de que seus resultados são exclusivamente em inglês, foi implementada uma **tabela de tradução bidirecional** (`BrazilianFoodTranslation`):

1. A busca do usuário em português era convertida para inglês antes de ir para a USDA (`"frango"` → `"Chicken"`).
2. O nome retornado pela USDA era pós-processado de volta para português (`"Chicken, broilers or fryers, breast"` → `"Frango, Peito"`).

Funcionava para os ~85 alimentos mapeados na tabela, mas era uma solução artificial: os dados eram genéricos (sem marcas), os nomes eram aproximações de tradução, e qualquer alimento fora da tabela retornava em inglês.

### O pivot definitivo: Open Food Facts

A solução real veio da análise do problema pela raiz: **o produto precisa de alimentos brasileiros reais com nomes em português**. A USDA com tradução manual era uma gambiarra. A FatSecret exigia pagamento para entregar o valor central.

A **Open Food Facts** resolve todos esses problemas de uma vez:

| Critério | FatSecret (free) | USDA + tradução | Open Food Facts |
|---|---|---|---|
| Alimentos brasileiros | ❌ Bloqueado no tier gratuito | ❌ Apenas genéricos EUA | ✅ Banco de dados brasileiro nativo |
| Nomes em PT-BR | ❌ Inglês apenas | ⚠️ Tradução manual aproximada | ✅ Nativos no banco |
| Marcas brasileiras | ❌ Indisponível | ❌ Não existe | ✅ "Frango Bob's", "Forno de Minas" |
| Autenticação | OAuth 2.0 stateful | Chave de API | ❌ Nenhuma (API aberta) |
| Custo | Pago para localização | Gratuito | Gratuito e open source |
| Lock-in | Alto | Moderado | Zero |

A integração com Open Food Facts **removeu** a necessidade de OAuth, eliminou o gerenciamento de tokens, descartou a tabela de tradução manual, e entregou imediatamente o valor de negócio que as abordagens anteriores tentavam simular.

```
Usuário digita "frango"
     │
     ▼
GET world.openfoodfacts.org/cgi/search.pl
  ?search_terms=frango&lc=pt&cc=br&json=1
     │
     ▼
Open Food Facts retorna produtos reais do mercado brasileiro
  em português, com macros por 100g, sem chave de API
     │
     ▼
OpenFoodFactsClient mapeia nutriments → FoodItemResponse
     │
     ▼
Frontend: "Frango Grelhado Sadia  |  165kcal · P31g · C0g · G4g (por 100g)"
```

### A lição de engenharia

Este pivot ilustra um princípio importante: **complexidade técnica não compensa uma premissa de produto errada**. O cliente OAuth para FatSecret era código bem escrito — mas resolvia o problema errado. Quando a premissa foi invalidada pelos testes, a decisão correta foi descartar a solução e reavaliar a ferramenta, não continuar investindo em uma integração que nunca entregaria o valor esperado no tier disponível.

A escolha pelo Open Food Facts não foi um atalho — foi a escolha certa para a realidade do produto: uma plataforma brasileira, para usuários brasileiros, que precisa de alimentos que eles realmente reconhecem nas prateleiras do supermercado.

---

## 5. Estratégia de Testes — o valor dos 32+ testes automatizados

### Por que testes importam neste projeto específico

O Progressor tem três características que tornam os testes particularmente valiosos:

1. **Fluxos de estado com consequências reais:** aceitar um convite de conexão altera permanentemente quem pode ver os dados de saúde de um aluno. Uma regressão aqui tem impacto de privacidade, não apenas funcional.

2. **Lógica de criptografia:** a camada de criptografia é silenciosamente correta ou silenciosamente catastrófica. Um bug que sempre criptografa mas nunca consegue decriptografar só seria descoberto na produção sem testes.

3. **Integração com API externa:** o Open Food Facts pode mudar seu esquema de resposta. Testes com mocks capturam o contrato esperado.

### Camada backend: testes de caso de uso com Mockito

Os casos de uso são a parte mais crítica da lógica de negócio. Por serem POJOs sem dependências de framework, são testados com **JUnit 5 + Mockito** em milissegundos — sem banco, sem servidor, sem Docker.

**Padrão aplicado:**

```
Dado um caso de uso com repositórios mockados
Quando execute() é chamado com determinadas entradas
Então verifique:
  - Que o repositório correto foi chamado (ArgumentCaptor)
  - Que o objeto persistido tem os valores esperados
  - Que exceções corretas são lançadas para entradas inválidas
```

O `ArgumentCaptor` é a ferramenta-chave aqui. Em vez de apenas verificar se `save()` foi chamado, capturamos o objeto salvo e asserimos seus campos individualmente:

```java
ArgumentCaptor<ConnectionRequest> captor = ArgumentCaptor.forClass(ConnectionRequest.class);
verify(connectionRequestRepository).save(captor.capture());

ConnectionRequest saved = captor.getValue();
assertThat(saved.getStatus().name()).isEqualTo("PENDING");
assertThat(saved.getProfessionalName()).isEqualTo("João Silva");
```

Isso é superior a asserir apenas o retorno do método — captura o **estado interno do objeto** que será persistido.

**Invariantes de domínio testadas diretamente:**

```java
// Testa a entidade de domínio diretamente, sem infraestrutura
assertThatThrownBy(() -> new WorkoutBlock(null, planId, "Bloco A", -1))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessageContaining("Position must be non-negative");
```

Validar que o construtor do domínio rejeita entradas inválidas garante que nenhum adaptador de infraestrutura ou controller consiga criar um estado inválido — independentemente de onde venha a chamada.

### Camada frontend: testes de componente com Vitest

O frontend tem dois componentes de alto risco testados:

**`WorkoutSpreadsheetView`:** renderiza uma matriz de histórico de treinos com dados aninhados (planos → blocos → exercícios → logs). A principal motivação para os testes foi a **regressão de crash que foi corrigida na V1.1** — a tela branca causada por `useTranslation` chamado sem import. Os testes de null-safety garantem que esse tipo de regressão seja detectado imediatamente:

```typescript
it('does not crash when blocks is null or undefined', async () => {
    const planWithNullBlocks = { ...plan, blocks: null };
    mockGet.mockResolvedValueOnce({ data: [planWithNullBlocks] });

    expect(() => render(<WorkoutSpreadsheetView />)).not.toThrow();
});
```

**`InviteNotification` (PendingInvitesCard):** o fluxo de aceitar/rejeitar convites tem consequências de segurança diretas. Os testes verificam o **contrato da API**, não apenas a renderização:

```typescript
it('Accept button calls POST /connections/respond with accepted: true', async () => {
    // ...
    expect(mockPost).toHaveBeenCalledWith('/connections/respond', {
        requestId: 'inv-1',
        accepted: true,
    });
});
```

Isso garante que uma refatoração no componente que acidentalmente inverta `true`/`false` nos botões de aceitar/rejeitar seja detectada antes de chegar à produção.

### A filosofia: testar o contrato, não a implementação

Uma armadilha comum em testes de frontend é testar detalhes de implementação (que classes CSS são aplicadas, que texto interno um elemento tem). O Progressor segue a filosofia do React Testing Library: testar **o que o usuário vê e faz**, e **o que o sistema envia para a API**. Isso torna os testes resilientes a refatorações de UI sem perder a proteção contra regressões funcionais.

---

<p align="center">
  <sub>
    Progressor — Estudo Técnico V1.1<br/>
    Desenvolvido por <strong>Mauricio Andrade</strong>
  </sub>
</p>
