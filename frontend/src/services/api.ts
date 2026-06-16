import axios from 'axios';
import toast from 'react-hot-toast';

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
