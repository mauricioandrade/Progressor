import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { AlertCircle, Search, Send } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

interface Student {
    id: string;
    name: string;
    email: string;
    lastCheckIn?: string | null;
}

function daysSince(dateStr: string | null | undefined): number | null {
    if (!dateStr) return null;
    const last = new Date(dateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    last.setHours(0, 0, 0, 0);
    return Math.floor((today.getTime() - last.getTime()) / (1000 * 60 * 60 * 24));
}

function isInactive(student: Student): boolean {
    const days = daysSince(student.lastCheckIn);
    return days === null || days >= 3;
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';
const inputClass = 'flex-1 px-4 py-2.5 border border-black/10 dark:border-white/10 rounded-2xl bg-white/60 dark:bg-white/5 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none text-sm backdrop-blur-sm';

export function Students() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [students, setStudents] = useState<Student[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [searchEmail, setSearchEmail] = useState('');
    const [searchResult, setSearchResult] = useState<Student | null>(null);
    const [isSearching, setIsSearching] = useState(false);
    const [searchError, setSearchError] = useState('');
    const [assignSuccess, setAssignSuccess] = useState('');

    function loadStudents() {
        setIsLoading(true);
        api.get('/users/students')
            .then(r => setStudents(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }

    useEffect(() => { loadStudents(); }, []);

    if (!user) return null;

    async function handleSearch(e: React.FormEvent) {
        e.preventDefault();
        setSearchResult(null);
        setSearchError('');
        setAssignSuccess('');
        setIsSearching(true);
        try {
            const response = await api.get(`/users/students/search?email=${encodeURIComponent(searchEmail)}`);
            setSearchResult(response.data);
        } catch (error: any) {
            setSearchError(error.response?.status === 404 ? t('students_list.no_students') : t('login.errors.server_error'));
        } finally {
            setIsSearching(false);
        }
    }

    async function handleSendInvite(studentEmail: string) {
        try {
            await api.post(`/connections/invite?studentEmail=${encodeURIComponent(studentEmail)}&role=COACH`);
            setAssignSuccess('Convite enviado! O aluno precisa aceitar no seu app.');
            setSearchResult(null);
            setSearchEmail('');
        } catch (error: any) {
            const msg = error.response?.data?.message;
            setSearchError(msg ?? t('login.errors.server_error'));
        }
    }

    const inactiveCount = students.filter(isInactive).length;

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/70 dark:bg-slate-800/60 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                            {t('students_list.title')}
                        </h2>
                        {inactiveCount > 0 && (
                            <div className="flex items-center gap-1.5 bg-amber-50 dark:bg-amber-900/20 text-amber-600 dark:text-amber-400 px-3 py-1.5 rounded-full text-xs font-medium">
                                <AlertCircle className="w-3.5 h-3.5" />
                                {inactiveCount} {t('students_list.inactive_count')}
                            </div>
                        )}
                    </div>
                </header>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">
                    <div className={`${glassCard} p-5`}>
                        <form onSubmit={handleSearch} className="flex gap-2">
                            <input
                                type="email"
                                value={searchEmail}
                                onChange={e => setSearchEmail(e.target.value)}
                                placeholder={t('students_list.search_student_placeholder')}
                                className={inputClass}
                                required
                            />
                            <button
                                type="submit"
                                disabled={isSearching}
                                className="flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white text-sm font-medium rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                            >
                                <Search className="w-4 h-4" />
                                {isSearching ? '...' : t('students_list.search_button')}
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
                                    onClick={() => handleSendInvite(searchResult.email)}
                                    className="flex items-center gap-1.5 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-xl hover:bg-blue-700 transition-colors active:scale-95"
                                >
                                    <Send className="w-3.5 h-3.5" />
                                    Enviar Convite
                                </button>
                            </div>
                        )}
                    </div>

                    <div className={glassCard + ' overflow-hidden'}>
                        {isLoading ? (
                            <div className="p-10 text-center text-gray-400">...</div>
                        ) : students.length === 0 ? (
                            <div className="p-10 text-center text-gray-400 text-sm">
                                {t('students_list.no_students')}
                            </div>
                        ) : (
                            <div className="divide-y divide-black/5 dark:divide-white/5">
                                {students.map(student => {
                                    const days = daysSince(student.lastCheckIn);
                                    const inactive = isInactive(student);
                                    return (
                                        <div key={student.id} onClick={() => navigate(`/professional/student/${student.id}`, { state: { studentName: student.name } })} className="flex items-center justify-between px-6 py-4 hover:bg-black/5 dark:hover:bg-white/5 transition-colors cursor-pointer">
                                            <div className="flex items-center gap-3">
                                                <div className="w-9 h-9 rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center text-white text-sm font-semibold shrink-0">
                                                    {student.name.charAt(0).toUpperCase()}
                                                </div>
                                                <div>
                                                    <div className="flex items-center gap-2">
                                                        <p className="text-sm font-medium text-gray-900 dark:text-gray-100">{student.name}</p>
                                                        {inactive && (
                                                            <span className="flex items-center gap-1 text-[10px] font-medium text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-900/20 px-2 py-0.5 rounded-full">
                                                                <AlertCircle className="w-2.5 h-2.5" />
                                                                {days === null ? t('students_list.never_trained') : t('students_list.inactive_days', { days })}
                                                            </span>
                                                        )}
                                                    </div>
                                                    <p className="text-xs text-gray-500 dark:text-gray-400">{student.email}</p>
                                                </div>
                                            </div>
                                            {!inactive && days !== null && (
                                                <span className="text-xs text-gray-400">
                                                    {days === 0 ? t('students_list.trained_today') : t('students_list.days_ago', { days })}
                                                </span>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </div>
    );
}
