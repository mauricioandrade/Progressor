import { useRef, useEffect, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { MessageBubble } from './MessageBubble';
import { useMessages } from '../hooks/queries';
import { sendMessage } from '../services/api';
import { QK } from '../hooks/queries';
import { Send, Image, X } from 'lucide-react';
import toast from 'react-hot-toast';

interface MessageThreadProps {
  partnerId: string;
  partnerName: string;
  currentUserId: string;
}

export function MessageThread({ partnerId, partnerName, currentUserId }: MessageThreadProps) {
  const { data: messages = [], isLoading } = useMessages(partnerId);
  const [text, setText] = useState('');
  const [image, setImage] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [sending, setSending] = useState(false);
  const bottomRef = useRef<HTMLDivElement>(null);
  const fileRef = useRef<HTMLInputElement>(null);
  const queryClient = useQueryClient();

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  function handleImageChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setImage(file);
    setImagePreview(URL.createObjectURL(file));
  }

  function clearImage() {
    setImage(null);
    setImagePreview(null);
    if (fileRef.current) fileRef.current.value = '';
  }

  async function handleSend() {
    if (!text.trim() && !image) return;
    setSending(true);
    try {
      await sendMessage(partnerId, text.trim() || undefined, image ?? undefined);
      setText('');
      clearImage();
      queryClient.invalidateQueries({ queryKey: QK.messages(partnerId) });
      queryClient.invalidateQueries({ queryKey: QK.conversations });
    } catch {
      toast.error('Erro ao enviar mensagem.');
    } finally {
      setSending(false);
    }
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  }

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="px-5 py-4 border-b border-black/5 dark:border-white/[0.07] shrink-0">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-gradient-to-br from-blue-500 to-indigo-600 flex items-center justify-center text-white font-semibold text-sm">
            {partnerName.charAt(0).toUpperCase()}
          </div>
          <span className="font-semibold text-gray-900 dark:text-gray-100">{partnerName}</span>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        {isLoading && (
          <div className="flex justify-center py-8 text-gray-400 text-sm">Carregando...</div>
        )}
        {!isLoading && messages.length === 0 && (
          <div className="flex flex-col items-center justify-center h-full text-gray-400 gap-1">
            <p className="text-sm">Nenhuma mensagem ainda. Diga olá!</p>
          </div>
        )}
        {messages.map((msg) => (
          <MessageBubble key={msg.id} message={msg} isMine={msg.senderId === currentUserId} />
        ))}
        <div ref={bottomRef} />
      </div>

      {/* Image preview */}
      {imagePreview && (
        <div className="px-4 pb-2 shrink-0">
          <div className="relative inline-block">
            <img src={imagePreview} alt="preview" className="h-20 rounded-xl object-cover" />
            <button
              onClick={clearImage}
              className="absolute -top-2 -right-2 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center text-white"
            >
              <X className="w-3 h-3" />
            </button>
          </div>
        </div>
      )}

      {/* Input */}
      <div className="px-4 pb-4 pt-2 border-t border-black/5 dark:border-white/[0.07] shrink-0">
        <div className="flex items-end gap-2">
          <button
            onClick={() => fileRef.current?.click()}
            className="p-2.5 rounded-xl text-gray-400 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-950/30 transition-colors shrink-0"
          >
            <Image className="w-5 h-5" />
          </button>
          <input ref={fileRef} type="file" accept="image/*" className="hidden" onChange={handleImageChange} />
          <textarea
            value={text}
            onChange={(e) => setText(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Digite uma mensagem..."
            rows={1}
            className="flex-1 resize-none px-4 py-2.5 border border-black/15 dark:border-white/20 rounded-2xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:ring-2 focus:ring-blue-500 outline-none text-sm max-h-32 overflow-y-auto"
          />
          <button
            onClick={handleSend}
            disabled={sending || (!text.trim() && !image)}
            className="p-2.5 rounded-xl bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors shrink-0"
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
}
