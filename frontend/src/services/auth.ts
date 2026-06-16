import { api } from './api';

interface LoginCredentials {
    email: string;
    password: string;
}

export interface UserInfo {
    userId: string;
    email: string;
    role: 'STUDENT' | 'PERSONALTRAINER' | 'NUTRITIONIST';
    expiresAt: string;
}

export const authService = {
    async signIn({ email, password }: LoginCredentials): Promise<UserInfo> {
        const response = await api.post<UserInfo>('/auth/login', { email, password });
        localStorage.setItem('@Progressor:user', JSON.stringify(response.data));
        return response.data;
    },

    async signOut(): Promise<void> {
        try {
            await api.post('/auth/logout');
        } finally {
            localStorage.removeItem('@Progressor:user');
        }
    },
};
