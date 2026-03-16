import { Sidebar } from '../components/Sidebar';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function Dashboard() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { user } = useAuth();

    const handleLogout = () => {
        localStorage.removeItem('@Progressor:token');
        navigate('/login');
    };

    if (!user) return null;

    return (
        <div className="flex min-h-screen bg-gray-50">
            <Sidebar role={user.role} />

            <div className="flex-1">
                <header className="bg-white border-b border-gray-200 p-4 flex justify-between items-center">
                    <h2 className="text-lg font-semibold text-gray-700 uppercase tracking-wider">
                        {t('sidebar.dashboard')}
                    </h2>
                    <div className="flex items-center gap-4">
                        <span className="text-sm text-gray-500">{user.email}</span>
                        <button
                            onClick={handleLogout}
                            className="px-4 py-2 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition-all"
                        >
                            Logout
                        </button>
                    </div>
                </header>

                <main className="p-8">
                    <div className="bg-white rounded-xl shadow-sm p-6 border border-gray-200">
                        <h3 className="text-xl font-bold text-gray-800">
                            Welcome back!
                        </h3>
                        <p className="text-gray-600 mt-1">
                            You are logged in as a <span className="font-bold text-blue-600">{user.role}</span>
                        </p>
                    </div>
                </main>
            </div>
        </div>
    );
}