import * as SecureStore from 'expo-secure-store';
import type { UserInfo } from '../../../shared/types/user';

const TOKEN_KEY = 'progressor_jwt';
const USER_KEY = 'progressor_user';

export async function saveToken(token: string): Promise<void> {
    await SecureStore.setItemAsync(TOKEN_KEY, token);
}

export async function getToken(): Promise<string | null> {
    return SecureStore.getItemAsync(TOKEN_KEY);
}

export async function removeToken(): Promise<void> {
    await SecureStore.deleteItemAsync(TOKEN_KEY);
}

export async function saveUser(user: UserInfo): Promise<void> {
    await SecureStore.setItemAsync(USER_KEY, JSON.stringify(user));
}

export async function getUser(): Promise<UserInfo | null> {
    const raw = await SecureStore.getItemAsync(USER_KEY);
    if (!raw) return null;
    try {
        return JSON.parse(raw) as UserInfo;
    } catch {
        return null;
    }
}

export async function clearAuth(): Promise<void> {
    await Promise.all([
        SecureStore.deleteItemAsync(TOKEN_KEY),
        SecureStore.deleteItemAsync(USER_KEY),
    ]);
}
