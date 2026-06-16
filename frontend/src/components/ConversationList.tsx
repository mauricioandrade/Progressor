import type { ConversationSummary } from '../types/api';

interface ConversationListProps {
  conversations: ConversationSummary[];
  selectedPartnerId?: string;
  onSelect: (partnerId: string) => void;
}

export function ConversationList({ conversations, selectedPartnerId, onSelect }: ConversationListProps) {
  if (conversations.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-40 text-gray-400 gap-2">
        <p className="text-sm">Nenhuma conversa ainda</p>
      </div>
    );
  }

  return (
    <ul className="divide-y divide-black/5 dark:divide-white/[0.05]">
      {conversations.map((conv) => {
        const isActive = conv.partnerId === selectedPartnerId;
        const timeLabel = conv.lastMessageAt
          ? new Date(conv.lastMessageAt).toLocaleDateString([], { day: '2-digit', month: '2-digit' })
          : '';

        return (
          <li key={conv.partnerId}>
            <button
              onClick={() => onSelect(conv.partnerId)}
              className={`w-full flex items-center gap-3 px-4 py-3 text-left transition-colors ${
                isActive
                  ? 'bg-blue-50 dark:bg-blue-950/40'
                  : 'hover:bg-black/5 dark:hover:bg-white/5'
              }`}
            >
              <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm shrink-0">
                {conv.partnerName.charAt(0).toUpperCase()}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <span className="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">
                    {conv.partnerName}
                  </span>
                  <span className="text-[10px] text-gray-400 ml-2 shrink-0">{timeLabel}</span>
                </div>
                <p className="text-xs text-gray-500 dark:text-gray-400 truncate mt-0.5">
                  {conv.lastMessageContent ?? '📷 Imagem'}
                </p>
              </div>
              {conv.unreadCount > 0 && (
                <span className="ml-1 min-w-[20px] h-5 rounded-full bg-blue-600 text-white text-[10px] font-bold flex items-center justify-center px-1 shrink-0">
                  {conv.unreadCount > 99 ? '99+' : conv.unreadCount}
                </span>
              )}
            </button>
          </li>
        );
      })}
    </ul>
  );
}
