import { api } from './api';

interface LoginCredentials {
    email: string;
    password: string;
}

interface LoginResponse {
    token: string;
}

export const authService = {
    async signIn({ email, password }: LoginCredentials): Promise<LoginResponse> {
        const response = await api.post<LoginResponse>('/auth/login', {
            email,
            password,
        });

        return response.data;
    },
};