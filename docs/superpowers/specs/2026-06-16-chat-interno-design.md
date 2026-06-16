# Chat Interno Aluno ↔ Profissional — Design

**Data:** 2026-06-16
**Status:** aprovado

---

## Contexto

O Progressor já substitui o WhatsApp para acompanhamento de treino e nutrição, mas a comunicação direta aluno↔profissional ainda depende de apps externos. Este feature adiciona mensagens diretas dentro da plataforma.

**Escopo:** texto + imagens, polling, aluno ↔ trainer e/ou aluno ↔ nutricionista.

---

## Decisões de arquitetura

| Decisão | Escolha | Motivo |
|---|---|---|
| Transporte | Polling 15s | Latência de 5–30s aceitável; sem nova infra |
| Modelo de dados | Tabela única `chat_messages` | Suficiente com índices compostos; sem entidade Conversation separada |
| Storage de imagens | BYTEA no banco | Consistente com avatares e fotos de progresso |
| Marcar como lida | Automático no GET messages | Reduz round-trips; sem endpoint PATCH separado |

---

## Modelo de dados (V8)

```sql
CREATE TABLE chat_messages (
    id          UUID PRIMARY KEY,
    sender_id   UUID        NOT NULL REFERENCES app_users(id),
    receiver_id UUID        NOT NULL REFERENCES app_users(id),
    content     TEXT,
    image_data  BYTEA,
    sent_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    read_at     TIMESTAMP,
    CONSTRAINT chk_content CHECK (content IS NOT NULL OR image_data IS NOT NULL)
);

CREATE INDEX idx_chat_conversation
    ON chat_messages(LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id), sent_at DESC);

CREATE INDEX idx_chat_unread
    ON chat_messages(receiver_id, read_at) WHERE read_at IS NULL;
```

**Regras:**
- `content` e `image_data` são individualmente opcionais — a constraint garante que ao menos um existe.
- O índice `LEAST/GREATEST` torna a busca bidirecional eficiente sem duplicar linhas.
- O partial index `WHERE read_at IS NULL` torna `countUnread` uma varredura mínima.

---

## Backend

### Port

```java
interface ChatRepository {
    ChatMessage save(ChatMessage message);
    List<ChatMessage> findConversation(UUID userA, UUID userB, Instant since);
    List<ConversationSummary> findConversationsByUser(UUID userId);
    int countUnread(UUID receiverId, UUID senderId);
    void markAsRead(UUID receiverId, UUID senderId);
}
```

### Use cases

| Classe | Responsabilidade |
|---|---|
| `SendMessageUseCase` | Valida autorização (aluno só manda para seu trainer/nutri via OwnershipValidator), persiste, dispara push |
| `GetConversationUseCase` | Busca mensagens entre dois usuários; marca as recebidas como lidas no mesmo call |
| `GetConversationsUseCase` | Lista todas as conversas do usuário com último msg + unread count |

### API — `/api/chat`

| Método | Path | Corpo | Autorização |
|---|---|---|---|
| `POST` | `/api/chat/messages` | `multipart/form-data`: `content` (texto, opcional), `image` (arquivo, opcional) | autenticado |
| `GET` | `/api/chat/messages?partnerId=&since=` | — | autenticado |
| `GET` | `/api/chat/conversations` | — | autenticado |
| `GET` | `/api/chat/messages/{id}/image` | — | autenticado |

**Observações:**
- `since` é opcional no GET messages (formato ISO-8601, ex: `2026-06-16T10:00:00Z`); omitir retorna histórico completo.
- GET messages marca automaticamente como lidas as mensagens recebidas do parceiro (`receiver_id = eu`, `sender_id = partnerId`, `read_at IS NULL` → `read_at = NOW()`).
- A imagem é servida como endpoint separado, seguindo o padrão de `/api/users/me/avatar`. Somente sender ou receiver podem acessar — verificação no controller via `currentUser.getId()`.
- `SendMessageUseCase` chama `PushNotificationPort` para notificar o receptor.

### Autorização
- Aluno só pode enviar mensagem para o seu `personalTrainerId` ou `nutritionistId`.
- Profissional só pode enviar mensagem para alunos vinculados a ele.
- Verificação feita no use case via `OwnershipValidator` (ou query direta ao `UserRepository`).
- SecurityConfig: todos os endpoints `/api/chat/**` requerem `.authenticated()`.

---

## Frontend

### Rotas

| Path | Componente |
|---|---|
| `/chat` | `ChatPage` — abre na conversa mais recente ou empty state |
| `/chat/:partnerId` | `ChatPage` — abre direto na conversa com o parceiro |

### Componentes

- **`ChatPage.tsx`** — layout two-panel (lista + thread). Em mobile: navegação entre telas.
- **`ConversationList.tsx`** — lista de conversas com avatar, nome, preview, badge de não-lidas.
- **`MessageThread.tsx`** — histórico cronológico + input de texto/imagem no rodapé. Polling 15s.
- **`MessageBubble.tsx`** — bolha alinhada esquerda/direita conforme sender; imagem inline se existir.

### React Query

```ts
// polling ativo apenas enquanto a aba está em foco
useQuery(['chat', partnerId], () => fetchMessages(partnerId), {
  refetchInterval: 15_000,
  refetchIntervalInBackground: false,
})

useQuery(['conversations'], fetchConversations, {
  refetchInterval: 30_000,
  refetchIntervalInBackground: false,
})
```

### Sidebar

Novo item "Chat" com badge numérico de não-lidas agregado de `useConversations()`.

### Upload de imagem

`<input type="file" accept="image/*">` envia `multipart/form-data`. Preview local via `URL.createObjectURL` antes de enviar.

### Novas dependências

Nenhuma — usa fetch e React Query já presentes.

---

## Arquivos a criar/modificar

### Backend (novos)
- `core/domain/chat/ChatMessage.java`
- `core/domain/chat/ConversationSummary.java`
- `core/application/ports/ChatRepository.java`
- `core/application/dto/SendMessageRequest.java`
- `core/application/dto/ChatMessageResponse.java`
- `core/application/dto/ConversationSummaryResponse.java`
- `core/application/usecases/SendMessageUseCase.java`
- `core/application/usecases/GetConversationUseCase.java`
- `core/application/usecases/GetConversationsUseCase.java`
- `infrastructure/persistence/entities/ChatMessageEntity.java`
- `infrastructure/persistence/repositories/SpringDataChatRepository.java`
- `infrastructure/persistence/adapters/ChatRepositoryAdapter.java`
- `infrastructure/api/controllers/ChatController.java`
- `resources/db/migration/V8__add_chat_messages.sql`

### Backend (modificados)
- `infrastructure/config/UseCaseConfig.java` — registrar os 3 novos use cases
- `infrastructure/security/config/SecurityConfig.java` — adicionar regras `/api/chat/**`

### Frontend (novos)
- `src/pages/ChatPage.tsx`
- `src/components/ConversationList.tsx`
- `src/components/MessageThread.tsx`
- `src/components/MessageBubble.tsx`

### Frontend (modificados)
- `src/App.tsx` — nova rota `/chat` e `/chat/:partnerId`
- `src/components/Sidebar.tsx` — novo item Chat com badge
- `src/hooks/queries.ts` — `useConversations`, `useMessages`
- `src/services/api.ts` — `fetchConversations`, `fetchMessages`, `sendMessage`
- `src/types/api.ts` — `ChatMessage`, `ConversationSummary`
