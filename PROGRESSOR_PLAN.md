# Progressor — Plano de Melhoria

Projeto: Spring Boot 4 + React 19 + PostgreSQL. Deploy no Render.
Ordem de execução: **Segurança → Frontend → Mobile**.

---

## FASE 1 — Segurança (prioridade máxima)

### 1.1 JWT: sair do localStorage

**Problema:** Token exposto a XSS.

**Solução:** httpOnly cookie + CSRF token.

Backend — `SecurityConfig.java`:
```java
// Adicionar cookie config no login response
ResponseCookie cookie = ResponseCookie.from("jwt", token)
    .httpOnly(true)
    .secure(true)
    .path("/")
    .maxAge(Duration.ofHours(8))
    .sameSite("Strict")
    .build();
response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
```

Criar filtro `JwtCookieFilter` que lê de `request.getCookies()` em vez do header `Authorization`.

Frontend — remover `localStorage.setItem("token", ...)` de todos os lugares. O Axios passa o cookie automaticamente com `withCredentials: true`.

```typescript
// axios instance
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true,
});
```

Logout: endpoint `POST /auth/logout` que seta o cookie com maxAge=0.

---

### 1.2 IDOR — verificação de ownership

**Problema:** Role `COACH` no JWT autoriza, mas não garante que aquele coach tem relação com *aquele* estudante específico.

**Onde corrigir:** Todo endpoint que recebe `studentId` como path/query param.

Criar utilitário `OwnershipValidator`:
```java
@Component
public class OwnershipValidator {

    private final ConnectionRequestRepository connectionRepo;

    public void assertCoachOwnsStudent(UUID coachId, UUID studentId) {
        boolean hasAccess = connectionRepo.existsByProfessionalIdAndStudentIdAndStatus(
            coachId, studentId, ConnectionStatus.ACCEPTED
        );
        if (!hasAccess) throw new AccessDeniedException("Acesso negado a este estudante");
    }

    public void assertNutriOwnsPatient(UUID nutriId, UUID studentId) {
        // mesma lógica com role NUTRI
    }
}
```

Aplicar em: `WorkoutController`, `NutritionController`, `ProgressPhotoController`, `BodyMeasurementController`, `ReportController`.

O estudante também só pode ver os próprios dados — verificar `userId` do JWT contra o `studentId` da rota.

---

### 1.3 Spring Boot Actuator

Verificar `application.properties`:
```properties
# Desabilitar tudo em produção
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never
```

Se quiser métricas internas, proteger com role ADMIN:
```java
.requestMatchers("/actuator/**").hasRole("ADMIN")
```

---

### 1.4 Rate Limiting

Adicionar dependência:
```xml
<dependency>
    <groupId>com.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

Criar `RateLimitFilter` aplicado em:
- `POST /auth/login` — 5 tentativas / minuto por IP
- `POST /auth/forgot-password` — 3 tentativas / 15 min por IP
- `POST /auth/reset-password` — 3 tentativas / 15 min por token

---

### 1.5 CORS — confirmar configuração de produção

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // NUNCA usar * em produção com credentials
    config.setAllowedOrigins(List.of(
        "https://seu-dominio.com",
        "http://localhost:5173" // apenas dev
    ));
    config.setAllowCredentials(true);
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH"));
    config.setAllowedHeaders(List.of("*"));
    // ...
}
```

---

### 1.6 Headers de Segurança HTTP

Adicionar via `SecurityConfig`:
```java
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'"))
    .referrerPolicy(ref -> ref
        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
    .frameOptions(frame -> frame.deny())
    .contentTypeOptions(Customizer.withDefaults())
);
```

---

### 1.7 Upload de avatar — validação de conteúdo

```java
private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
    "image/jpeg", new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
    "image/png",  new byte[]{(byte)0x89, 0x50, 0x4E, 0x47},
    "image/webp", new byte[]{0x52, 0x49, 0x46, 0x46}
);

public void validateImage(MultipartFile file) {
    if (file.getSize() > 5 * 1024 * 1024) throw new InvalidImageException("Máximo 5MB");
    byte[] header = file.getBytes();
    boolean valid = MAGIC_BYTES.values().stream()
        .anyMatch(magic -> startsWithMagic(header, magic));
    if (!valid) throw new InvalidImageException("Formato não suportado");
}
```

---

## FASE 2 — Frontend

### 2.1 Estados de loading — Skeleton screens

Substituir spinners por skeletons. Criar componente reutilizável:

```tsx
// components/ui/Skeleton.tsx
export function Skeleton({ className }: { className?: string }) {
  return (
    <div className={`animate-pulse bg-gray-200 dark:bg-gray-700 rounded ${className}`} />
  );
}

// Exemplo de uso na WorkoutPage
function WorkoutPageSkeleton() {
  return (
    <div className="space-y-4">
      <Skeleton className="h-8 w-48" />
      <Skeleton className="h-32 w-full" />
      <Skeleton className="h-32 w-full" />
    </div>
  );
}
```

---

### 2.2 Tratamento global de erros do Axios

```typescript
// lib/api.ts — interceptor unificado
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado — redirecionar para login
      window.location.href = '/login';
    }
    if (error.response?.status === 403) {
      toast.error('Você não tem permissão para esta ação');
    }
    if (error.response?.status >= 500) {
      toast.error('Erro interno. Tente novamente.');
    }
    return Promise.reject(error);
  }
);
```

---

### 2.3 Empty states com CTA

Para cada tela com lista vazia, adicionar componente com ação clara:

```tsx
// components/ui/EmptyState.tsx
interface EmptyStateProps {
  icon: LucideIcon;
  title: string;
  description: string;
  action?: { label: string; onClick: () => void };
}

export function EmptyState({ icon: Icon, title, description, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <Icon className="w-12 h-12 text-gray-300 mb-4" />
      <h3 className="text-lg font-medium text-gray-700 dark:text-gray-300">{title}</h3>
      <p className="text-sm text-gray-500 mt-1 mb-4">{description}</p>
      {action && (
        <button onClick={action.onClick} className="btn-primary">
          {action.label}
        </button>
      )}
    </div>
  );
}
```

---

### 2.4 Acessibilidade — correções prioritárias

```tsx
// aria-labels em ícones sem texto
<button aria-label="Excluir exercício">
  <Trash2 className="w-4 h-4" aria-hidden="true" />
</button>

// Focus trap em modais — usar @radix-ui/react-dialog
// Já tem lucide-react, adicionar Radix para Dialog/Modal

// Contraste no dark mode — auditar as classes emerald-*
// emerald-400 em bg-gray-900 passa AA? Verificar com WebAIM Contrast Checker
```

---

### 2.5 Workout Matrix — mobile

```tsx
// Garantir scroll horizontal com sticky primeira coluna
<div className="overflow-x-auto">
  <table className="min-w-full">
    <tbody>
      {exercises.map(ex => (
        <tr key={ex.id}>
          {/* Sticky na primeira coluna */}
          <td className="sticky left-0 bg-white dark:bg-gray-900 z-10 min-w-[140px] font-medium">
            {ex.name}
          </td>
          {/* Colunas de sessões */}
          {sessions.map(s => (
            <td key={s.id} className="min-w-[80px] text-center text-sm">
              {s.weight}kg × {s.reps}
            </td>
          ))}
        </tr>
      ))}
    </tbody>
  </table>
</div>
```

---

## FASE 3 — Mobile (React Native + Expo)

### 3.1 Pré-requisito: extrair /shared

Antes de iniciar o app, reestruturar o monorepo:

```
/
├── backend/
├── frontend/          (web — atual)
├── mobile/            (novo — React Native)
└── shared/            (novo — tipos e services compartilhados)
    ├── types/
    │   ├── user.ts
    │   ├── workout.ts
    │   ├── nutrition.ts
    │   └── progress.ts
    └── api/
        ├── authService.ts
        ├── workoutService.ts
        └── nutritionService.ts
```

Os types já existem no frontend — mover para `/shared/types` e importar nos dois projetos.

---

### 3.2 Setup inicial do app mobile

```bash
npx create-expo-app mobile --template blank-typescript
cd mobile
npx expo install expo-secure-store expo-camera expo-image-picker
npm install @react-navigation/native @react-navigation/stack
npx expo install react-native-screens react-native-safe-area-context
```

---

### 3.3 JWT seguro no mobile

```typescript
// mobile/src/lib/auth.ts
import * as SecureStore from 'expo-secure-store';

export async function saveToken(token: string) {
  await SecureStore.setItemAsync('jwt', token);
}

export async function getToken(): Promise<string | null> {
  return SecureStore.getItemAsync('jwt');
}

export async function removeToken() {
  await SecureStore.deleteItemAsync('jwt');
}
```

Usar no interceptor Axios do mobile em vez de cookie (que não existe em app nativo).

---

### 3.4 Features nativas — Fase 2 do mobile

**Push notifications** (já no roadmap):
```bash
npx expo install expo-notifications
```
- Backend: adicionar coluna `push_token` na tabela `app_users`
- Endpoint: `POST /users/push-token`
- Job agendado: `@Scheduled` verificar estudantes sem check-in há >3 dias

**Câmera para fotos de progresso:**
```bash
npx expo install expo-camera expo-image-picker expo-image-manipulator
```

**Biometria no login:**
```bash
npx expo install expo-local-authentication
```

---

### 3.5 Telas prioritárias do mobile (MVP)

1. Login / Registro
2. Dashboard do estudante (check-in, próximo treino, hidratação)
3. Visualizar plano de treino + registrar série
4. Timer de descanso (já existe no web)
5. Fotos de progresso (câmera nativa)
6. Notificações push

Deixar para v2 do mobile: Nutrition module, PDF reports, Workout Matrix (complexo em mobile).

---

## Checklist de execução

### Fase 1 — Segurança
- [ ] 1.1 Migrar JWT para httpOnly cookie
- [ ] 1.2 Implementar OwnershipValidator em todos os controllers
- [ ] 1.3 Restringir Actuator
- [ ] 1.4 Rate limiting em auth endpoints
- [ ] 1.5 Auditar e fixar CORS para produção
- [ ] 1.6 Adicionar security headers
- [ ] 1.7 Validar magic bytes no upload de avatar

### Fase 2 — Frontend
- [ ] 2.1 Skeleton screens nos principais loaders
- [ ] 2.2 Interceptor global de erros Axios
- [ ] 2.3 Empty states com CTA em todas as listas
- [ ] 2.4 aria-labels e contraste dark mode
- [ ] 2.5 Sticky column na Workout Matrix mobile

### Fase 3 — Mobile
- [ ] 3.1 Criar /shared com types e services
- [ ] 3.2 Setup Expo + dependências
- [ ] 3.3 Auth com SecureStore
- [ ] 3.4 Telas MVP (6 telas)
- [ ] 3.5 Push notifications

---

## Notas adicionais

**Não quebrar:** A lógica de ConnectionRequest (consent flow) é a base de toda autorização — o OwnershipValidator da Fase 1 deve usar esse mesmo repository, não criar nova lógica paralela.

**Testes:** Ao implementar OwnershipValidator, adicionar casos de teste no padrão JUnit 5 já existente. O `SendConnectionRequestUseCaseTest` é um bom template.

**Deploy:** O Render já usa variáveis de ambiente. A mudança para httpOnly cookie vai exigir que o domínio do frontend e do backend sejam o mesmo (ou subdomínio) para o cookie funcionar em produção — verificar configuração atual.
