import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AlertCircle, UserPlus, Search } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

interface Patient {
    id: string;
    name: string;
    email: string;
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';
const inputClass = 'flex-1 px-4 py-2.5 border border-black/10 dark:border-white/10 rounded-2xl bg-white/60 dark:bg-white/5 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none text-sm backdrop-blur-sm';

export function NutritionistPatients() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [patients, setPatients] = useState<Patient[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [searchEmail, setSearchEmail] = useState('');
    const [searchResult, setSearchResult] = useState<Patient | null>(null);
    const [isSearching, setIsSearching] = useState(false);
    const [searchError, setSearchError] = useState('');
    const [assignSuccess, setAssignSuccess] = useState('');

    function loadPatients() {
        setIsLoading(true);
        api.get('/users/my-students/nutritionist')
            .then(r => setPatients(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }

    useEffect(() => { loadPatients(); }, []);

    if (!user) return null;

    async function handleSearch(e: React.FormEvent) {
        e.preventDefault();
        setSearchResult(null);
        setSearchError('');
        setAssignSuccess('');
        setIsSearching(true);
        try {
            const response = await api.get(`/users/students/search/nutritionist?email=${encodeURIComponent(searchEmail)}`);
            setSearchResult(response.data);
        } catch (error: any) {
            setSearchError(error.response?.status === 404 ? t('nutrition.no_patients') : t('login.errors.server_error'));
        } finally {
            setIsSearching(false);
        }
    }

    async function handleAssign(patientId: string) {
        try {
            await api.post(`/users/${patientId}/assign-nutritionist`);
            setAssignSuccess(t('nutrition.assign_success'));
            setSearchResult(null);
            setSearchEmail('');
            loadPatients();
        } catch {
            setSearchError(t('login.errors.server_error'));
        }
    }

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/70 dark:bg-slate-800/60 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-10">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                        {t('nutrition.students_title')}
                    </h2>
                </header>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">
                    <div className={`${glassCard} p-5`}>
                        <form onSubmit={handleSearch} className="flex gap-2">
                            <input
                                type="email"
                                value={searchEmail}
                                onChange={e => setSearchEmail(e.target.value)}
                                placeholder={t('nutrition.search_placeholder')}
                                className={inputClass}
                                required
                            />
                            <button
                                type="submit"
                                disabled={isSearching}
                                className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white text-sm font-medium rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                            >
                                <Search className="w-4 h-4" />
                                {isSearching ? '...' : t('nutrition.search_button')}
                            </button>
                        </form>

                        {assignSuccess && (
                            <p className="mt-3 text-sm text-green-600 dark:text-green-400">{assignSuccess}</p>
                        )}
                        {searchError && (
                            <p className="mt-3 text-sm text-red-500">{searchError}</p>
                        )}

                        {searchResult && (
                            <div className="mt-4 flex items-center justify-between p-4 bg-blue-50/80 dark:bg-blue-900/20 rounded-2xl border border-blue-200/60 dark:border-blue-700/40">
                                <div>
                                    <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{searchResult.name}</p>
                                    <p className="text-xs text-gray-500 dark:text-gray-400">{searchResult.email}</p>
                                </div>
                                <button
                                    onClick={() => handleAssign(searchResult.id)}
                                    className="flex items-center gap-1.5 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-xl hover:bg-blue-700 transition-colors active:scale-95"
                                >
                                    <UserPlus className="w-3.5 h-3.5" />
                                    {t('nutrition.assign_button')}
                                </button>
                            </div>
                        )}
                    </div>

                    <div className={glassCard + ' overflow-hidden'}>
                        {isLoading ? (
                            <div className="p-10 text-center text-gray-400">...</div>
                        ) : patients.length === 0 ? (
                            <div className="p-10 text-center text-gray-400 text-sm">
                                {t('nutrition.no_patients')}
                            </div>
                        ) : (
                            <div className="divide-y divide-black/5 dark:divide-white/5">
                                {patients.map(patient => (
                                    <div key={patient.id} onClick={() => navigate(`/professional/student/${patient.id}`, { state: { studentName: patient.name } })} className="flex items-center gap-3 px-6 py-4 hover:bg-black/5 dark:hover:bg-white/5 transition-colors cursor-pointer">
                                        <div className="w-9 h-9 rounded-full bg-gradient-to-br from-green-400 to-teal-500 flex items-center justify-center text-white text-sm font-semibold shrink-0">
                                            {patient.name.charAt(0).toUpperCase()}
                                        </div>
                                        <div>
                                            <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{patient.name}</p>
                                            <p className="text-xs text-gray-500 dark:text-gray-400">{patient.email}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </div>
    );
}
