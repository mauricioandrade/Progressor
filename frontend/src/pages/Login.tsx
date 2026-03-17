import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router-dom';
import { Sun, Moon } from 'lucide-react';
import { authService } from '../services/auth';
import { useTheme } from '../hooks/useTheme';
import axios from 'axios';

export function Login() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { theme, toggleTheme } = useTheme();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setErrorMessage('');

        try {
            const data = await authService.signIn({ email, password });
            localStorage.setItem('@Progressor:token', data.token);
            navigate('/dashboard');
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 403 || error.response?.status === 401) {
                    setErrorMessage(t('login.errors.invalid_credentials'));
                } else if (error.response?.status === 400) {
                    setErrorMessage(t('login.errors.invalid_credentials'));
                } else {
                    setErrorMessage(t('login.errors.server_error'));
                }
            } else {
                setErrorMessage(t('login.errors.unexpected'));
            }
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
                    <h1 className="text-4xl font-extrabold tracking-tight text-gray-900 dark:text-white mb-2" style={{ fontFamily: "'Inter Variable', Inter, sans-serif" }}>
                        Progressor
                    </h1>
                    <p className="text-gray-500 dark:text-gray-400">
                        {t('login.subtitle')}
                    </p>
                </div>

                {errorMessage && (
                    <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 rounded-2xl text-sm">
                        {errorMessage}
                    </div>
                )}

                <form onSubmit={handleLogin} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                            {t('login.email_label')}
                        </label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-200 dark:border-gray-700 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                            {t('login.password_label')}
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-200 dark:border-gray-700 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-2xl hover:bg-blue-700 transition-colors disabled:opacity-50"
                    >
                        {isLoading ? '...' : t('login.button')}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm text-gray-600 dark:text-gray-400">
                    {t('login.no_account')}{' '}
                    <Link to="/signup" className="text-blue-600 hover:underline font-medium">
                        {t('login.signup_link')}
                    </Link>
                </div>
                <div className="mt-3 text-center text-sm text-gray-500 dark:text-gray-500">
                    {t('login.forgot_password')}{' '}
                    <Link to="/forgot-password" className="text-blue-600 hover:underline font-medium">
                        {t('login.forgot_password_link')}
                    </Link>
                </div>
            </div>
        </div>
    );
}
