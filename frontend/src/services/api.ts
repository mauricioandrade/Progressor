import axios from 'axios';

export const api = axios.create({
    baseURL: 'http://localhost:8081/api',
});
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('@Progressor:token');

    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});