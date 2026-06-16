import axios from 'axios';
import toast from 'react-hot-toast';
import type { ChatMessage, ConversationSummary } from '../types/api';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? '/api',
    withCredentials: true,
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status;
        if (status === 401) {
            localStorage.removeItem('@Progressor:user');
            window.location.href = '/login';
        } else if (status === 403) {
            toast.error('Você não tem permissão para esta ação.');
        } else if (status >= 500) {
            toast.error('Erro interno. Tente novamente.');
        }
        return Promise.reject(error);
    }
);

export async function fetchConversations(): Promise<ConversationSummary[]> {
    const r = await api.get<ConversationSummary[]>('/chat/conversations');
    return r.data;
}

export async function fetchMessages(partnerId: string, since?: string): Promise<ChatMessage[]> {
    const params: Record<string, string> = { partnerId };
    if (since) params.since = since;
    const r = await api.get<ChatMessage[]>('/chat/messages', { params });
    return r.data;
}

export async function sendMessage(
    receiverId: string,
    content?: string,
    image?: File,
): Promise<ChatMessage> {
    const form = new FormData();
    form.append('receiverId', receiverId);
    if (content) form.append('content', content);
    if (image) form.append('image', image);
    const r = await api.post<ChatMessage>('/chat/messages', form, {
        headers: { 'Content-Type': 'multipart/form-data' },
    });
    return r.data;
}

export function getChatImageUrl(messageId: string): string {
    const base = import.meta.env.VITE_API_URL ?? '/api';
    return `${base}/chat/messages/${messageId}/image`;
}
