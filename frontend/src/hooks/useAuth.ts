import { jwtDecode } from 'jwt-decode';

interface TokenPayload {
    sub: string;
    role: 'STUDENT' | 'PERSONALTRAINER' | 'NUTRITIONIST';
    userId: string;
    exp: number;
}

export function useAuth() {
    const token = localStorage.getItem('@Progressor:token');

    if (!token) {
        return { signed: false, user: null };
    }

    try {
        const decoded = jwtDecode<TokenPayload>(token);
        const isTokenExpired = Date.now() >= decoded.exp * 1000;

        if (isTokenExpired) {
            localStorage.removeItem('@Progressor:token');
            return { signed: false, user: null };
        }

        return {
            signed: true,
            user: {
                id: decoded.userId,
                email: decoded.sub,
                role: decoded.role,
            }
        };
    } catch {
        return { signed: false, user: null };
    }
}
