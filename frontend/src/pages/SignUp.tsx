import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../services/api';

type UserRole = 'STUDENT' | 'PERSONAL' | 'NUTRITIONIST';

export function SignUp() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [role, setRole] = useState<UserRole>('STUDENT');
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        birthDate: '',
        cref: '',
        crn: ''
    });
    const [isLoading, setIsLoading] = useState(false);

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);

        const nameParts = formData.name.trim().split(' ');
        const firstName = nameParts[0];
        const lastName = nameParts.slice(1).join(' ');

        const payload: any = {
            firstName,
            lastName: lastName || "",
            email: formData.email,
            password: formData.password,
            birthDate: formData.birthDate,
        };

        if (role === 'PERSONAL') payload.cref = formData.cref;
        if (role === 'NUTRITIONIST') payload.crn = formData.crn;

        const endpoint = {
            STUDENT: '/users/register/student',
            PERSONAL: '/users/register/personal',
            NUTRITIONIST: '/users/register/nutritionist'
        }[role];

        try {
            await api.post(endpoint, payload);
            alert(t('signup.success'));
            navigate('/login');
        } catch (error: any) {
            console.error('Registration failed:', error.response?.data || error.message);
            alert(t('login.errors.unexpected'));
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center p-4">
            <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold text-blue-600 mb-2">{t('signup.title')}</h1>
                    <p className="text-gray-500">{t('signup.subtitle')}</p>
                </div>

                <div className="flex bg-gray-100 p-1 rounded-xl mb-6">
                    {(['STUDENT', 'PERSONAL', 'NUTRITIONIST'] as UserRole[]).map((r) => (
                        <button
                            key={r}
                            onClick={() => setRole(r)}
                            type="button"
                            className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${role === r ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500'}`}
                        >
                            {t(`signup.role_${r.toLowerCase()}`)}
                        </button>
                    ))}
                </div>

                <form onSubmit={handleRegister} className="space-y-4">
                    <input
                        type="text"
                        placeholder={t('signup.name_label')}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        required
                    />
                    <input
                        type="email"
                        placeholder={t('signup.email_label')}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        required
                    />
                    <input
                        type="password"
                        placeholder={t('signup.password_label')}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        required
                    />

                    <div className="space-y-1">
                        <label className="text-xs font-semibold text-gray-500 ml-1">
                            {t('signup.birthdate_label')}
                        </label>
                        <input
                            type="date"
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-gray-700"
                            onChange={(e) => setFormData({ ...formData, birthDate: e.target.value })}
                            required
                        />
                    </div>

                    {role === 'PERSONAL' && (
                        <input
                            type="text"
                            placeholder={t('signup.cref_label')}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            onChange={(e) => setFormData({ ...formData, cref: e.target.value })}
                            required
                        />
                    )}

                    {role === 'NUTRITIONIST' && (
                        <input
                            type="text"
                            placeholder={t('signup.crn_label')}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            onChange={(e) => setFormData({ ...formData, crn: e.target.value })}
                            required
                        />
                    )}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-all"
                    >
                        {isLoading ? '...' : t('signup.button')}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm text-gray-600">
                    {t('signup.have_account')}{' '}
                    <Link to="/login" className="text-blue-600 hover:underline font-medium">
                        {t('signup.login_link')}
                    </Link>
                </div>
            </div>
        </div>
    );
}