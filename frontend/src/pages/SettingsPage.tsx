import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { User, Camera, Scale, Sun, Moon, LogOut, Save } from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { useTheme } from '../hooks/useTheme';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

interface Profile { fullName: string; email: string; hasAvatar: boolean; }

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';
const inputClass = 'w-full px-4 py-2.5 border border-black/15 dark:border-white/20 rounded-2xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:ring-2 focus:ring-blue-500 outline-none text-sm';

export function SettingsPage() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const { theme, toggleTheme } = useTheme();
    const navigate = useNavigate();

    const [profile, setProfile] = useState<Profile | null>(null);
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
    const [weight, setWeight] = useState('');
    const [isSavingWeight, setIsSavingWeight] = useState(false);
    const [isUploadingAvatar, setIsUploadingAvatar] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        api.get('/users/me')
            .then(r => setProfile(r.data))
            .catch(() => {});

        api.get('/users/me/avatar', { responseType: 'blob' })
            .then(r => {
                const url = URL.createObjectURL(r.data);
                setAvatarUrl(url);
            })
            .catch(() => {});
    }, []);

    if (!user) return null;

    async function handleAvatarChange(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0];
        if (!file) return;
        if (file.size > 2 * 1024 * 1024) {
            toast.error(t('settings.avatar_error'));
            return;
        }
        setIsUploadingAvatar(true);
        const formData = new FormData();
        formData.append('file', file);
        try {
            await api.patch('/users/me/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
            const url = URL.createObjectURL(file);
            setAvatarUrl(url);
            toast.success(t('settings.avatar_success'));
        } catch {
            toast.error(t('settings.avatar_error'));
        } finally {
            setIsUploadingAvatar(false);
        }
    }

    async function handleSaveWeight() {
        const w = parseFloat(weight);
        if (!w || w <= 0) return;
        setIsSavingWeight(true);
        try {
            if (user?.role === 'STUDENT') {
                await api.post('/measurements/my', { weight: w });
                const waterGoal = Math.round(w * 35);
                await api.patch('/nutrition/water/goal', { goal: waterGoal });
            }
            toast.success(t('settings.weight_success'));
            setWeight('');
        } catch {
            toast.error(t('settings.weight_error'));
        } finally {
            setIsSavingWeight(false);
        }
    }

    function handleLogout() {
        localStorage.removeItem('@Progressor:token');
        navigate('/login', { replace: true });
    }

    const initials = profile?.fullName
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase() ?? '?';

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0">
                <header className="bg-white/70 dark:bg-slate-900/80 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-20">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">{t('settings.title')}</h2>
                </header>

                <main className="p-4 md:p-6 max-w-xl space-y-5 pb-24 md:pb-6">

                    {/* Profile section */}
                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-5">{t('settings.profile_section')}</h3>

                        <div className="flex flex-col items-center gap-4 mb-6">
                            <div className="relative">
                                <div className="w-24 h-24 rounded-full overflow-hidden bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center shadow-lg">
                                    {avatarUrl ? (
                                        <img src={avatarUrl} alt="avatar" className="w-full h-full object-cover" />
                                    ) : (
                                        <span className="text-2xl font-bold text-white">{initials}</span>
                                    )}
                                </div>
                                <button
                                    onClick={() => fileInputRef.current?.click()}
                                    disabled={isUploadingAvatar}
                                    className="absolute bottom-0 right-0 w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-md hover:bg-blue-700 active:scale-90 transition-all disabled:opacity-50"
                                >
                                    {isUploadingAvatar ? (
                                        <span className="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin" />
                                    ) : (
                                        <Camera className="w-4 h-4" />
                                    )}
                                </button>
                                <input ref={fileInputRef} type="file" accept="image/jpeg,image/png" className="hidden" onChange={handleAvatarChange} />
                            </div>
                            {profile && (
                                <div className="text-center">
                                    <p className="font-semibold text-gray-800 dark:text-gray-100">{profile.fullName}</p>
                                    <p className="text-sm text-gray-400">{profile.email}</p>
                                </div>
                            )}
                        </div>

                        <div className="space-y-3">
                            <div>
                                <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                    {t('settings.full_name')}
                                </label>
                                <div className="flex items-center gap-3 px-4 py-2.5 border border-black/10 dark:border-white/10 rounded-2xl bg-black/3 dark:bg-white/3">
                                    <User className="w-4 h-4 text-gray-400 shrink-0" />
                                    <span className="text-sm text-gray-600 dark:text-gray-300">{profile?.fullName ?? '—'}</span>
                                </div>
                            </div>
                            <div>
                                <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                    {t('settings.email')}
                                </label>
                                <div className="flex items-center gap-3 px-4 py-2.5 border border-black/10 dark:border-white/10 rounded-2xl bg-black/3 dark:bg-white/3">
                                    <span className="text-sm text-gray-600 dark:text-gray-300">{profile?.email ?? user.email}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Body Stats — only for students */}
                    {user.role === 'STUDENT' && (
                        <div className={`${glassCard} p-6`}>
                            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-5">{t('settings.stats_section')}</h3>
                            <div>
                                <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                    {t('settings.weight_label')}
                                </label>
                                <div className="flex gap-2 items-center">
                                    <div className="relative flex-1">
                                        <Scale className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                                        <input
                                            type="number"
                                            min={1}
                                            step={0.1}
                                            value={weight}
                                            onChange={e => setWeight(e.target.value)}
                                            placeholder="70.5"
                                            className={`${inputClass} pl-10`}
                                            onKeyDown={e => e.key === 'Enter' && handleSaveWeight()}
                                        />
                                    </div>
                                    <button
                                        onClick={handleSaveWeight}
                                        disabled={isSavingWeight || !weight}
                                        className="shrink-0 flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 active:scale-95 transition-all"
                                    >
                                        {isSavingWeight ? (
                                            <span className="w-4 h-4 border-2 border-white/40 border-t-white rounded-full animate-spin" />
                                        ) : (
                                            <Save className="w-4 h-4" />
                                        )}
                                        {t('settings.weight_button')}
                                    </button>
                                </div>
                                <p className="text-[11px] text-gray-400 mt-2">{t('settings.weight_hint')}</p>
                            </div>
                        </div>
                    )}

                    {/* Preferences */}
                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-5">{t('settings.preferences_section')}</h3>

                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-800 dark:text-gray-100">{t('settings.theme_label')}</p>
                                <p className="text-xs text-gray-400">{theme === 'dark' ? t('settings.dark_mode') : t('settings.light_mode')}</p>
                            </div>
                            <button
                                onClick={toggleTheme}
                                className={`relative w-12 h-6 rounded-full transition-all duration-300 focus:outline-none ${theme === 'dark' ? 'bg-blue-600' : 'bg-gray-300'}`}
                            >
                                <div className={`absolute top-0.5 left-0.5 w-5 h-5 rounded-full bg-white shadow flex items-center justify-center transition-transform duration-300 ${theme === 'dark' ? 'translate-x-6' : ''}`}>
                                    {theme === 'dark' ? <Moon className="w-3 h-3 text-blue-600" /> : <Sun className="w-3 h-3 text-yellow-500" />}
                                </div>
                            </button>
                        </div>
                    </div>

                    {/* Danger zone */}
                    <div className={`${glassCard} p-6`}>
                        <button
                            onClick={handleLogout}
                            className="w-full flex items-center justify-center gap-2 py-2.5 text-sm font-semibold text-red-500 border border-red-200 dark:border-red-900/50 rounded-2xl hover:bg-red-50 dark:hover:bg-red-950/30 active:scale-95 transition-all"
                        >
                            <LogOut className="w-4 h-4" />
                            {t('sidebar.logout')}
                        </button>
                    </div>

                </main>
            </div>
        </div>
    );
}
