import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router-dom';
import { Sun, Moon } from 'lucide-react';
import { api } from '../services/api';
import { useTheme } from '../hooks/useTheme';

type UserRole = 'STUDENT' | 'PERSONALTRAINER' | 'NUTRITIONIST';

export function SignUp() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { theme, toggleTheme } = useTheme();
    const [role, setRole] = useState<UserRole>('STUDENT');

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        birthDate: '',
        cref: '',
        crn: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [birthDateError, setBirthDateError] = useState('');

    const today = new Date();
    const maxDate = today.toISOString().split('T')[0];

    function validateBirthDate(dateStr: string): string {
        if (!dateStr) return '';
        const date = new Date(dateStr + 'T00:00:00');
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        if (date >= now) return t('signup.error_future_date');
        const minAgeDate = new Date(now.getFullYear() - 10, now.getMonth(), now.getDate());
        if (date > minAgeDate) return t('signup.error_min_age');
        return '';
    }

    const roleLabels: Record<UserRole, string> = {
        STUDENT: t('signup.role_student'),
        PERSONALTRAINER: t('signup.role_personal'),
        NUTRITIONIST: t('signup.role_nutritionist')
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        const bdErr = validateBirthDate(formData.birthDate);
        if (bdErr) { setBirthDateError(bdErr); return; }
        setIsLoading(true);
        setErrorMessage('');

        const payload: Record<string, string> = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            password: formData.password,
            birthDate: formData.birthDate,
        };

        if (role === 'PERSONALTRAINER') payload.cref = formData.cref;
        if (role === 'NUTRITIONIST') payload.crn = formData.crn;

        const endpoint: Record<UserRole, string> = {
            STUDENT: '/users/register/student',
            PERSONALTRAINER: '/users/register/personal',
            NUTRITIONIST: '/users/register/nutritionist'
        };

        try {
            await api.post(endpoint[role], payload);
            navigate('/login');
        } catch (error: any) {
            if (error.response?.status === 409) {
                setErrorMessage(t('login.errors.server_error'));
            } else if (error.response?.status === 400) {
                const msg = error.response?.data?.message;
                setErrorMessage(msg || t('login.errors.unexpected'));
            } else {
                setErrorMessage(t('login.errors.server_error'));
            }
        } finally {
            setIsLoading(false);
        }
    };

    const inputClass = "w-full px-4 py-2 border border-gray-200 dark:border-gray-700 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none";

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
                    <p className="text-gray-500 dark:text-gray-400">{t('signup.subtitle')}</p>
                </div>

                <div className="flex bg-gray-100 dark:bg-slate-800 p-1 rounded-xl mb-6">
                    {(Object.keys(roleLabels) as UserRole[]).map((r) => (
                        <button
                            key={r}
                            onClick={() => setRole(r)}
                            type="button"
                            className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${role === r ? 'bg-white dark:bg-gray-700 text-blue-600 shadow-sm' : 'text-gray-500 dark:text-gray-400'}`}
                        >
                            {roleLabels[r]}
                        </button>
                    ))}
                </div>

                {errorMessage && (
                    <div className="mb-4 p-3 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 rounded-lg text-sm">
                        {errorMessage}
                    </div>
                )}

                <form onSubmit={handleRegister} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <input
                            type="text"
                            placeholder={t('signup.first_name_label')}
                            className={inputClass}
                            value={formData.firstName}
                            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder={t('signup.last_name_label')}
                            className={inputClass}
                            value={formData.lastName}
                            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                            required
                        />
                    </div>

                    <input
                        type="email"
                        placeholder={t('signup.email_label')}
                        className={inputClass}
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        required
                    />

                    <input
                        type="password"
                        placeholder={t('signup.password_label')}
                        className={inputClass}
                        value={formData.password}
                        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        required
                    />

                    <div className="space-y-1">
                        <label className="text-xs font-semibold text-gray-500 dark:text-gray-400 ml-1">
                            {t('signup.birthdate_label')}
                        </label>
                        <input
                            type="date"
                            className={`${inputClass}${birthDateError ? ' border-red-400 focus:ring-red-400' : ''}`}
                            value={formData.birthDate}
                            max={maxDate}
                            onChange={(e) => {
                                setFormData({ ...formData, birthDate: e.target.value });
                                setBirthDateError(validateBirthDate(e.target.value));
                            }}
                            required
                        />
                        {birthDateError && (
                            <p className="text-xs text-red-500 ml-1">{birthDateError}</p>
                        )}
                    </div>

                    {role === 'PERSONALTRAINER' && (
                        <input
                            type="text"
                            placeholder={t('signup.cref_label')}
                            className={inputClass}
                            value={formData.cref}
                            onChange={(e) => setFormData({ ...formData, cref: e.target.value })}
                            required
                        />
                    )}

                    {role === 'NUTRITIONIST' && (
                        <input
                            type="text"
                            placeholder={t('signup.crn_label')}
                            className={inputClass}
                            value={formData.crn}
                            onChange={(e) => setFormData({ ...formData, crn: e.target.value })}
                            required
                        />
                    )}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-600 text-white font-semibold py-2 rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-all"
                    >
                        {isLoading ? '...' : t('signup.button')}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm text-gray-600 dark:text-gray-400">
                    {t('signup.have_account')}{' '}
                    <Link to="/login" className="text-blue-600 hover:underline font-medium">
                        {t('signup.login_link')}
                    </Link>
                </div>
            </div>
        </div>
    );
}
