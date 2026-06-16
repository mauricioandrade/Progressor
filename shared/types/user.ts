export type UserRole = 'STUDENT' | 'PERSONALTRAINER' | 'NUTRITIONIST';

export interface UserInfo {
    userId: string;
    email: string;
    role: UserRole;
}

export interface UserProfile {
    fullName: string;
    email: string;
    hasAvatar: boolean;
}
