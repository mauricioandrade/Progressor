import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { ConversationList } from '../components/ConversationList';
import { MessageThread } from '../components/MessageThread';
import { useConversations } from '../hooks/queries';
import { getAuthState } from '../hooks/useAuth';
import { ArrowLeft, MessageSquare } from 'lucide-react';

export function ChatPage() {
  const { partnerId: routePartnerId } = useParams<{ partnerId?: string }>();
  const navigate = useNavigate();
  const { user } = getAuthState();
  const { data: conversations = [] } = useConversations();

  const [selectedPartnerId, setSelectedPartnerId] = useState<string | undefined>(routePartnerId);

  if (!user) return null;

  const selectedConv = conversations.find((c) => c.partnerId === selectedPartnerId);

  function handleSelect(partnerId: string) {
    setSelectedPartnerId(partnerId);
    navigate(`/chat/${partnerId}`, { replace: true });
  }

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
      <Sidebar role={user.role} />

      {/* Desktop: two-panel */}
      <div className="hidden md:flex flex-1 overflow-hidden h-screen">
        {/* Conversation list */}
        <aside className="w-80 shrink-0 bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl border-r border-black/5 dark:border-white/[0.07] overflow-y-auto">
          <div className="px-4 pt-6 pb-3">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white">Chat</h2>
          </div>
          <ConversationList
            conversations={conversations}
            selectedPartnerId={selectedPartnerId}
            onSelect={handleSelect}
          />
        </aside>

        {/* Message thread */}
        <main className="flex-1 bg-white/60 dark:bg-slate-800/40 backdrop-blur-xl overflow-hidden flex flex-col">
          {selectedPartnerId && selectedConv ? (
            <MessageThread
              partnerId={selectedPartnerId}
              partnerName={selectedConv.partnerName}
              currentUserId={user.id}
            />
          ) : (
            <div className="flex flex-col items-center justify-center h-full text-gray-400 gap-3">
              <MessageSquare className="w-12 h-12 text-gray-300 dark:text-gray-700" />
              <p className="text-sm">Selecione uma conversa para começar</p>
            </div>
          )}
        </main>
      </div>

      {/* Mobile: navigate between list and thread */}
      <div className="flex md:hidden flex-1 flex-col overflow-hidden">
        {selectedPartnerId && selectedConv ? (
          <div className="flex flex-col h-screen bg-white/90 dark:bg-slate-900/90 pb-16">
            <div className="px-4 pt-4 pb-2 border-b border-black/5 dark:border-white/[0.07] shrink-0">
              <button
                onClick={() => {
                  setSelectedPartnerId(undefined);
                  navigate('/chat', { replace: true });
                }}
                className="flex items-center gap-2 text-blue-600 text-sm font-medium"
              >
                <ArrowLeft className="w-4 h-4" />
                Conversas
              </button>
            </div>
            <div className="flex-1 overflow-hidden">
              <MessageThread
                partnerId={selectedPartnerId}
                partnerName={selectedConv.partnerName}
                currentUserId={user.id}
              />
            </div>
          </div>
        ) : (
          <div className="flex-1 bg-white/90 dark:bg-slate-900/90 overflow-y-auto pb-16">
            <div className="px-4 pt-6 pb-3">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white">Chat</h2>
            </div>
            <ConversationList
              conversations={conversations}
              selectedPartnerId={undefined}
              onSelect={handleSelect}
            />
          </div>
        )}
      </div>
    </div>
  );
}
