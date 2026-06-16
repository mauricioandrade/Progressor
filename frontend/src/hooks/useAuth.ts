import type { UserInfo } from '../services/auth';

export function getAuthState() {
    const raw = localStorage.getItem('@Progressor:user');

    if (!raw) {
        return { signed: false, user: null };
    }

    try {
        const user = JSON.parse(raw) as UserInfo;

        if (!user.expiresAt || new Date(user.expiresAt) <= new Date()) {
            localStorage.removeItem('@Progressor:user');
            return { signed: false, user: null };
        }

        return {
            signed: true,
            user: {
                id: user.userId,
                email: user.email,
                role: user.role,
            },
        };
    } catch {
        return { signed: false, user: null };
    }
}
