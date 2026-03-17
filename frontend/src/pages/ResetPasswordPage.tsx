import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useSearchParams } from 'react-router-dom';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from '../hooks/useTheme';
import { api } from '../services/api';

export function ResetPasswordPage() {
    const { t } = useTranslation();
    const { theme, toggleTheme } = useTheme();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token') ?? '';

    const [password, setPassword] = useState('');
    const [confirm, setConfirm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        if (password !== confirm) {
            setError(t('reset_password.error_mismatch'));
            return;
        }
        setIsLoading(true);
        try {
            await api.post('/auth/reset-password', { token, newPassword: password });
            setSuccess(true);
        } catch {
            setError(t('reset_password.error_invalid'));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 dark:bg-slate-950 flex items-center justify-center p-4 transition-colors">
            <button
                onClick={toggleTheme}
                className="fixed top-4 right-4 w-9 h-9 flex items-center justify-center rounded-xl bg-white dark:bg-slate-800 text-gray-600 dark:text-gray-300 border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
                {theme === 'dark' ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
            </button>

            <div className="bg-white/80 dark:bg-slate-900/80 backdrop-blur-md p-8 rounded-3xl shadow-xl w-full max-w-md border border-gray-200/60 dark:border-gray-800">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-extrabold tracking-tight text-gray-900 dark:text-white mb-2" style={{ fontFamily: "'Inter Variable', Inter, sans-serif" }}>
                        {t('reset_password.title')}
                    </h1>
                    <p className="text-gray-500 dark:text-gray-400 text-sm">
                        {t('reset_password.subtitle')}
                    </p>
                </div>

                {success ? (
                    <div className="text-center space-y-4">
                        <div className="p-4 bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-800 text-green-700 dark:text-green-400 rounded-2xl text-sm">
                            {t('reset_password.success')}
                        </div>
                        <Link to="/login" className="block text-blue-600 hover:underline text-sm font-medium mt-4">
                            {t('reset_password.back_to_login')}
                        </Link>
                    </div>
                ) : (
                    <>
                        {error && (
                            <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 rounded-2xl text-sm">
                                {error}
                            </div>
                        )}
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                    {t('reset_password.password_label')}
                                </label>
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-200 dark:border-gray-700 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                                    minLength={6}
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                                    {t('reset_password.confirm_label')}
                                </label>
                                <input
                                    type="password"
                                    value={confirm}
                                    onChange={(e) => setConfirm(e.target.value)}
                                    className="w-full px-4 py-2 border border-gray-200 dark:border-gray-700 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                                    required
                                />
                            </div>
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-2xl hover:bg-blue-700 transition-colors disabled:opacity-50"
                            >
                                {isLoading ? '...' : t('reset_password.button')}
                            </button>
                        </form>
                        <div className="mt-6 text-center text-sm">
                            <Link to="/login" className="text-blue-600 hover:underline font-medium">
                                {t('reset_password.back_to_login')}
                            </Link>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}
