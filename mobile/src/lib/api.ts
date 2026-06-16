import axios from 'axios';
import { getToken, clearAuth } from './auth';

const API_URL = process.env.EXPO_PUBLIC_API_URL ?? 'http://10.0.2.2:8081/api';

export const api = axios.create({ baseURL: API_URL });

api.interceptors.request.use(async (config) => {
    const token = await getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            await clearAuth();
        }
        return Promise.reject(error);
    }
);
