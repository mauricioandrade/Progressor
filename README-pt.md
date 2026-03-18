<h1 align="center">
  <br/>
  Progressor
</h1>

<p align="center">
  <strong>Sua Jornada Fitness, com Segurança.</strong>
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img alt="React" src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5-3178C6?style=for-the-badge&logo=typescript&logoColor=white"/>
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16+-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img alt="Tailwind CSS" src="https://img.shields.io/badge/Tailwind_CSS-4-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white"/>
</p>

<p align="center">
  <img alt="Licença" src="https://img.shields.io/badge/Licença-MIT-22c55e?style=flat-square"/>
  <img alt="Status" src="https://img.shields.io/badge/Status-Em_Desenvolvimento-8b5cf6?style=flat-square"/>
  <img alt="Arquitetura" src="https://img.shields.io/badge/Arquitetura-Clean_Architecture-f97316?style=flat-square"/>
  <img alt="Segurança" src="https://img.shields.io/badge/Criptografia-AES--256--GCM-ef4444?style=flat-square"/>
  <img alt="Testes" src="https://img.shields.io/badge/Testes-32%2B_Automatizados-16a34a?style=flat-square"/>
  <img alt="LGPD" src="https://img.shields.io/badge/Conformidade-LGPD%20%7C%20GDPR-3b82f6?style=flat-square"/>
</p>

<p align="center">
  <a href="README.md"><img alt="English" src="https://img.shields.io/badge/🇺🇸-English-blue?style=for-the-badge"/></a>
  &nbsp;
  <a href="README-pt.md"><img alt="Português" src="https://img.shields.io/badge/🇧🇷-Português-green?style=for-the-badge"/></a>
</p>

---

> Progressor é uma plataforma completa de gestão fitness que conecta alunos, personal trainers e nutricionistas. Construída sobre **Clean Architecture** com criptografia de ponta a ponta nos dados sensíveis, relatórios PDF profissionais, busca de alimentos em tempo real pelo **Open Food Facts**, e um fluxo de consentimento para vinculação de profissionais.

---

## 📋 Índice

- [✨ Funcionalidades Principais](#-funcionalidades-principais)
- [🏗️ Arquitetura](#️-arquitetura)
- [🔐 Segurança](#-segurança)
- [🩺 V1.1 — Fluxo de Consentimento & Matriz de Treinos](#-v11--fluxo-de-consentimento--matriz-de-treinos)
- [🥗 Módulo de Nutrição](#-módulo-de-nutrição)
- [📄 Relatórios PDF](#-relatórios-pdf)
- [🧪 Testes](#-testes)
- [🖥️ Stack Tecnológica](#️-stack-tecnológica)
- [⚙️ Como Executar](#️-como-executar)
- [🗺️ Roadmap](#️-roadmap)

---

## ✨ Funcionalidades Principais

### 👥 Sistema Multi-Papel

| Papel | Capacidades |
|---|---|
| 🎓 **Aluno** | Ver treinos e dieta, registrar check-ins, enviar fotos de progresso, acompanhar hidratação, auto-avaliações |
| 💪 **Personal Trainer** | Gerenciar alunos via fluxo de consentimento, criar planos de treino com hierarquia de blocos, deixar feedbacks, gerar relatórios PDF |
| 🥗 **Nutricionista** | Gerenciar pacientes via fluxo de consentimento, montar planos alimentares com alimentos brasileiros reais via Open Food Facts, gerar relatórios PDF de dieta |

### 🏋️ Gestão de Treinos
- Planos estruturados com hierarquia **PlanoTreino → BlocoTreino → Exercício**
- **Visão em Planilha (Matriz)** — veja as últimas 4 sessões de cada exercício lado a lado, agrupadas por bloco
- Sistema de check-in diário com gráfico de frequência (`ContributionGraph`)
- Histórico de exercícios e recordes pessoais
- Cronômetro de descanso com UI de pílula flutuante
- Planilha de treino exportável em PDF via JasperReports

### 📊 Acompanhamento de Progresso
- Timeline de medidas corporais (peso, % gordura, 8 pontos de circunferência)
- **Slider Antes & Depois** — arraste para comparar fotos ao longo do tempo
- Upload de fotos de progresso com notas do aluno e balões de feedback do profissional
- Badges de papel no feedback (🏋️ Personal / 🥗 Nutri)
- Edição e exclusão do próprio feedback (apenas o autor, validado no servidor)
- Relatório de progresso exportável em PDF

### 🥗 Módulo de Nutrição
- Busca de alimentos em tempo real pelo **Open Food Facts** — banco de dados global, gratuito e colaborativo, sem paywall comercial
- **Alimentos brasileiros nativos em PT-BR** — marcas reais de supermercado e produtos locais (ex.: "Frango Assado Bob's", "Pão de Queijo Forno de Minas") com nomes já em português
- Divisão de macronutrientes por 100g e por refeição (kcal, proteína, carboidratos, gordura)
- Planos alimentares agrupados por CAFÉ DA MANHÃ / ALMOÇO / JANTAR / LANCHE
- Rastreador de hidratação diária com metas personalizadas
- Marcador de refeição cheat 🍔

### 🔑 Autenticação & Identidade
- Autenticação JWT com claims de `email`, `role` e `userId`
- Cadeia de filtros Spring Security stateless
- Fluxo de redefinição de senha (baseado em token, validade de 15 min)
- Upload de avatar do usuário (armazenado como `bytea` criptografado)

---

## 🏗️ Arquitetura

O Progressor segue a **Clean Architecture** com uma regra estrita de dependência: camadas externas dependem das internas, nunca o contrário.

```
┌────────────────────────────────────────────────────────────────┐
│                         Infraestrutura                         │
│  Controllers │ Adaptadores JPA │ Flyway │ JasperReports │ OFF  │
├────────────────────────────────────────────────────────────────┤
│                       Camada de Aplicação                      │
│            Casos de Uso │ DTOs │ Interfaces de Porta           │
├────────────────────────────────────────────────────────────────┤
│                         Camada de Domínio                      │
│   Entidades (User, Student, WorkoutPlan, MealPlan, Photo…)     │
└────────────────────────────────────────────────────────────────┘
```

**Decisões estruturais importantes:**

- **Herança JOINED no JPA** para `UserEntity` → `StudentEntity` / `PersonalTrainerEntity` / `NutritionistEntity`
- Todos os Casos de Uso são **objetos Java simples** — sem anotações Spring — instanciados como `@Bean` em `UseCaseConfig`. Isso mantém a lógica de negócio independente do framework e trivialmente testável com Mockito.
- Interfaces de repositório vivem em `application.ports` (inversão de dependência); os adaptadores JPA as implementam por fora.
- **Flyway** gerencia todas as alterações de schema a partir da V1.1, viabilizando migrações reproduzíveis e versionadas.

---

## 🔐 Segurança

### Criptografia de Colunas AES-256-GCM

Dados sensíveis são criptografados **na camada de persistência** por `AttributeConverter`s do JPA antes de chegarem ao banco. Nenhum dado de saúde é gravado em disco sem criptografia.

```
Aplicação ──► AttributeConverter ──► AES-256-GCM ──► PostgreSQL bytea / TEXT
                                          ▲
                              Variável de ambiente ENCRYPTION_KEY (32 bytes / 64 hex)
```

| Campo | Converter | Motivo |
|---|---|---|
| `progress_photos.photo_data` | `EncryptedByteArrayConverter` | Bytes brutos da imagem — mais sensível |
| `progress_photos.professional_feedback` | `EncryptedStringConverter` | Comunicação privada com o profissional |
| `progress_photos.student_notes` | `EncryptedStringConverter` | Metas sensíveis do paciente |
| `body_measurements.*` (colunas sensíveis) | `EncryptedStringConverter` | Dados de saúde (LGPD Art. 11) |
| `app_users.email` | ❌ Não criptografado | Precisa ser pesquisável para login |
| `app_users.password` | BCrypt (Spring Security) | Hash padrão de senha |

> **Por que o e-mail não é criptografado:** O AES-GCM usa um nonce aleatório por criptografia, gerando um ciphertext diferente a cada chamada. Isso torna impossível `WHERE email = ?`. O e-mail não é dado sensível de saúde pelo Art. 11 da LGPD, portanto esta é a decisão correta.

### Conformidade LGPD / GDPR
- Dados de saúde (fotos, medidas, feedbacks) criptografados em repouso ✅
- Dados acessados apenas pelo titular ou pelo profissional explicitamente autorizado ✅
- Usuários podem excluir suas próprias fotos e feedbacks ✅
- Senhas jamais armazenadas em texto claro ✅

---

## 🩺 V1.1 — Fluxo de Consentimento & Matriz de Treinos

A versão 1.1 introduziu dois grandes aprimoramentos arquiteturais baseados em feedback real de personal trainers.

### Fluxo de Consentimento de Profissionais

Antes da V1.1, um profissional podia se vincular diretamente a um aluno sem qualquer consentimento. Isso foi substituído por um fluxo de **ConnectionRequest** adequado:

```
Personal Trainer           Backend                        Aluno
     │                       │                              │
     │  POST /connections/invite                            │
     │──────────────────────►│                              │
     │                       │── salva solicitação PENDING ─►│
     │                       │                              │
     │                       │◄── POST /connections/respond (accepted: true)
     │                       │                              │
     │                       │── ACCEPTED → vincula PT ────►│
```

- Entidade `ConnectionRequest` com máquina de estados `PENDING → ACCEPTED / REJECTED`
- Alunos visualizam convites pendentes no dashboard e aceitam ou recusam com um toque
- Guarda contra convite duplicado: um profissional não pode enviar um segundo convite enquanto há um pendente
- Suporta papéis `COACH` e `NUTRI` pelo mesmo fluxo

### Matriz de Treinos em Planilha

Um novo botão de alternância na tela de treinos troca o layout de cards para uma **grade compacta no estilo planilha**:

- Linhas = exercícios, agrupados sob seu **BlocoTreino** (ex.: "Bloco A – Superior")
- Colunas = **Semana 1 / 2 / 3 / 4** — as últimas 4 sessões registradas por exercício
- Cada célula mostra o peso/repetições executados e a data — a sessão mais recente destacada em verde-esmeralda
- Totalmente responsivo com coluna de nome de exercício fixada

### Migrações com Flyway

A V1.1 adotou o Flyway para gerenciar a evolução do schema com segurança:

```sql
-- V2__add_invites_and_workout_blocks.sql (trecho)
CREATE TABLE connection_requests (
  id UUID PRIMARY KEY,
  professional_id UUID NOT NULL,
  student_id UUID NOT NULL REFERENCES students(user_id),
  professional_role VARCHAR(10) NOT NULL CHECK (professional_role IN ('COACH', 'NUTRI')),
  status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
  CONSTRAINT uq_connection_request UNIQUE (professional_id, student_id, professional_role)
);

CREATE TABLE workout_plans ( … );
CREATE TABLE workout_blocks ( … );
ALTER TABLE workout_exercises ADD COLUMN block_id UUID REFERENCES workout_blocks(id) ON DELETE SET NULL;
```

---

## 🥗 Módulo de Nutrição

### Integração com Open Food Facts

```
Usuário digita "frango"
     │
     ▼
GET world.openfoodfacts.org/cgi/search.pl?search_terms=frango&lc=pt&cc=br
     │
     ▼
Open Food Facts: "Frango Grelhado  |  nutriscore: B  |  por 100g"
     │
     ▼
OpenFoodFactsClient mapeia nutriments → FoodItemResponse
     │
     ▼
Frontend exibe: "Frango Grelhado  |  165kcal · P31g · C0g · G4g  (por 100g)"
```

**Por que Open Food Facts:**
- **Sem chave de API ou OAuth** — HTTP stateless, sem dependência de fornecedor
- **Resultados nativos em PT-BR** — os parâmetros de localidade `lc=pt&cc=br` retornam produtos reais de supermercados brasileiros com nomes em português
- **Amplitude colaborativa** — produtos de marca, itens regionais e alimentos embalados que bancos de dados governamentais simplesmente não têm
- **Macros por 100g** — base nutricional consistente e comparável

**Resiliência do cliente HTTP (`OpenFoodFactsClient`):**
- `java.net.http.HttpClient` nativo — sem dependências externas
- Timeout de 30 segundos para conexão e resposta
- Header `User-Agent: ProgressorApp/1.0`
- Redirecionamentos automáticos
- Tratamento gracioso de resultados vazios

---

## 📄 Relatórios PDF

Gerados com **JasperReports 6.21.3**:

| Relatório | Endpoint | Descrição |
|---|---|---|
| 🏋️ Planilha de Treino | `GET /api/reports/workout-sheet/{studentId}` | Plano completo com séries / repetições / carga |
| 📊 Relatório de Progresso | `GET /api/reports/progress/{studentId}` | Histórico de medidas + evolução |
| 🥗 Plano Alimentar | `GET /api/reports/meal-plan/{studentId}` | Refeições agrupadas com breakdown de macros |

---

## 🧪 Testes

O Progressor conta com **32+ testes automatizados** cobrindo backend e frontend.

### Backend — JUnit 5 + Mockito

| Classe de Teste | Cenários |
|---|---|
| `SendConnectionRequestUseCaseTest` | Caminho feliz salva PENDING, guarda de convite duplicado, profissional não encontrado, aluno não encontrado |
| `RespondToConnectionRequestUseCaseTest` | Aceitar COACH vincula trainer, aceitar NUTRI vincula nutricionista, rejeitar atualiza só o status, aluno errado lança exceção, guarda de já-aceito |
| `WorkoutHierarchyUseCaseTest` | Criação de plano + validação, bloco vinculado ao plano, invariantes de domínio (posição negativa, nome em branco) |
| `ProgressorApplicationTests` | Contexto Spring carrega corretamente |

Para executar:
```bash
cd backend
JAVA_HOME=/caminho/para/java25 ./mvnw test
```

### Frontend — Vitest + React Testing Library

| Arquivo de Teste | Cenários |
|---|---|
| `WorkoutSpreadsheetView.test.tsx` | Estado vazio, cabeçalhos de plano+bloco, exibição de séries×reps+carga, logs históricos nas colunas, segurança contra blocos/exercícios nulos, roteamento por studentId |
| `InviteNotification.test.tsx` | Lista vazia não renderiza nada, lista de convites exibida, Aceitar → `POST` com `accepted: true`, Recusar → `POST` com `accepted: false`, convite removido após resposta, toast de erro em falha |

Para executar:
```bash
cd frontend
npm test
```

---

## 🖥️ Stack Tecnológica

### Backend
| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | **25** | Linguagem principal (records, switch expressions, pattern matching) |
| Spring Boot | **4.0.3** | Framework de aplicação |
| Spring Security | 4.0.x | Auth JWT stateless, autorização por papel |
| Spring Data JPA / Hibernate | 4.0.x / 7.x | ORM, estratégia de herança JOINED |
| PostgreSQL | 16+ | Banco de dados principal |
| Flyway | 10.x | Migrações de schema versionadas |
| JasperReports | 6.21.3 | Geração de PDF a partir de templates JRXML |
| JUnit 5 + Mockito | — | Testes unitários do backend |

### Frontend
| Tecnologia | Versão | Finalidade |
|---|---|---|
| React | 19 | Framework de UI |
| TypeScript | 5 | Tipagem estática |
| Vite | 8 | Ferramenta de build |
| Tailwind CSS | 4 | Estilização utilitária com modo escuro |
| react-router-dom | 7 | Roteamento client-side, rotas protegidas |
| react-i18next | — | UI bilíngue (PT-BR / EN) |
| Vitest + React Testing Library | — | Testes de componentes e unitários |
| Axios | — | Cliente HTTP |
| lucide-react | — | Biblioteca de ícones |
| Recharts | — | Gráfico de macros em anel, gráfico de contribuição |

### Infraestrutura
| Tecnologia | Finalidade |
|---|---|
| Docker / Docker Compose | Ambiente local com um único comando |
| Maven 3.9+ | Build e gerenciamento de dependências do backend |

---

## ⚙️ Como Executar

### Pré-requisitos
- Docker & Docker Compose
- Node.js 20+
- Java 25
- Maven 3.9+

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/progressor.git
cd progressor
```

### 2. Configurar variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env`:

```env
# Banco de dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/progressorv1
SPRING_DATASOURCE_USERNAME=progressorv1
SPRING_DATASOURCE_PASSWORD=progressorv1

# Open Food Facts não requer chave de API — é um banco de dados gratuito e aberto

# Chave de criptografia AES-256 — gere com: openssl rand -hex 32
# AVISO: alterar esta chave após dados existirem corrompirá todas as linhas criptografadas
ENCRYPTION_KEY=substitua_por_64_chars_hex
```

### 3a. Docker Compose (recomendado)

```bash
docker-compose up --build
```

| Serviço | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API Backend | http://localhost:8081/api |

### 3b. Manual (desenvolvimento)

```bash
# Backend
cd backend && ./mvnw spring-boot:run

# Frontend (terminal separado)
cd frontend && npm install && npm run dev
```

### 4. Criar conta

Acesse `http://localhost:5173/signup` e registre-se como **Aluno**, **Personal Trainer** (exige CREF) ou **Nutricionista** (exige CRN).

---

## 🗺️ Roadmap

### ✅ V1.0 — Concluído
- [x] Auth multi-papel (Aluno / Personal Trainer / Nutricionista)
- [x] Criação de planos de treino e exportação PDF
- [x] Módulo de nutrição com busca Open Food Facts (resultados nativos em PT-BR)
- [x] Upload de fotos de progresso com criptografia AES-256-GCM
- [x] Slider visual Antes & Depois
- [x] Balões de feedback profissional (edição/exclusão somente pelo autor)
- [x] Rastreamento de medidas corporais + relatório PDF
- [x] Rastreador de hidratação, gráfico de frequência no ginásio
- [x] Fluxo de recuperação de senha
- [x] Modo escuro + UI bilíngue (PT-BR / EN)

### ✅ V1.1 — Concluído
- [x] Fluxo de consentimento de profissionais (`ConnectionRequest` PENDING → ACCEPTED / REJECTED)
- [x] Hierarquia `WorkoutPlan → WorkoutBlock → WorkoutExercise`
- [x] Visão em Planilha de Treinos (últimas 4 sessões por exercício)
- [x] Migrações Flyway (`V2__add_invites_and_workout_blocks.sql`)
- [x] 32+ testes automatizados (JUnit 5 backend + Vitest frontend)

### 🔄 Em Andamento
- [ ] Notificações push para alertas de inatividade do aluno (> 3 dias sem check-in)
- [ ] Módulo de metas e anotações pessoais do aluno

### 🔮 Planejado
- [ ] Aplicativo mobile (React Native)
- [ ] Sugestão de treinos por IA baseada no histórico do aluno
- [ ] Integração com wearables (Garmin, Apple Health, Google Fit)

---

<p align="center">
  Desenvolvido com ❤️ por <strong>Mauricio Andrade</strong><br/>
  <sub>Projeto de portfólio — Clean Architecture · Spring Boot 4 · Criptografia AES-256 · React 19 · 32+ Testes Automatizados</sub>
</p>
