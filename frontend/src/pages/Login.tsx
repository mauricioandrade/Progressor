import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/auth';
import axios from 'axios';

export function Login() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            const data = await authService.signIn({ email, password });
            localStorage.setItem('@Progressor:token', data.token);
            console.log('Authentication successful');
            alert(t('login.success'));
            navigate('/dashboard');
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response?.status === 403 || error.response?.status === 401) {
                    console.error('Unauthorized: Invalid credentials');
                    alert(t('login.errors.invalid_credentials'));
                } else {
                    console.error('Server error during login:', error.message);
                    alert(t('login.errors.server_error'));
                }
            } else {
                console.error('Unexpected error:', error);
                alert(t('login.errors.unexpected'));
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-blue-600 mb-2">
                        {t('login.title')}
                    </h1>
                    <p className="text-gray-500">
                        {t('login.subtitle')}
                    </p>
                </div>

                <form onSubmit={handleLogin} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            {t('login.email_label')}
                        </label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            placeholder="seu@email.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            {t('login.password_label')}
                        </label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
                    >
                        {isLoading ? '...' : t('login.button')}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm text-gray-600">
                    {t('login.no_account')}{' '}
                    <Link to="/signup" className="text-blue-600 hover:underline font-medium">
                        {t('login.signup_link')}
                    </Link>
                </div>
            </div>
        </div>
    );
}