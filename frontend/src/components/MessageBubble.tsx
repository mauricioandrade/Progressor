import type { ChatMessage } from '../types/api';
import { getChatImageUrl } from '../services/api';

interface MessageBubbleProps {
  message: ChatMessage;
  isMine: boolean;
}

export function MessageBubble({ message, isMine }: MessageBubbleProps) {
  const time = new Date(message.sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

  return (
    <div className={`flex ${isMine ? 'justify-end' : 'justify-start'} mb-2`}>
      <div
        className={`max-w-[75%] rounded-2xl px-4 py-2.5 shadow-sm ${
          isMine
            ? 'bg-blue-600 text-white rounded-br-sm'
            : 'bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 rounded-bl-sm border border-black/5 dark:border-white/[0.07]'
        }`}
      >
        {message.hasImage && (
          <img
            src={getChatImageUrl(message.id)}
            alt="imagem"
            className="rounded-xl mb-2 max-h-48 object-cover w-full"
          />
        )}
        {message.content && (
          <p className="text-sm leading-relaxed whitespace-pre-wrap break-words">{message.content}</p>
        )}
        <p className={`text-[10px] mt-1 text-right ${isMine ? 'text-blue-200' : 'text-gray-400'}`}>
          {time}
        </p>
      </div>
    </div>
  );
}
