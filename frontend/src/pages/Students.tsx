import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

interface Student {
    id: string;
    name: string;
    email: string;
}

export function Students() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const [students, setStudents] = useState<Student[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        async function loadStudents() {
            try {
                const response = await api.get('/users/students');
                setStudents(response.data);
            } catch (error) {
                console.error('Failed to load students', error);
            } finally {
                setIsLoading(false);
            }
        }
        loadStudents();
    }, []);

    if (!user) return null;

    return (
        <div className="flex min-h-screen bg-gray-50">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white border-b border-gray-200 p-4">
                    <h2 className="text-lg font-semibold text-gray-700 uppercase">
                        {t('students_list.title')}
                    </h2>
                </header>

                <main className="p-8">
                    <div className="bg-white rounded-xl shadow-sm overflow-hidden border border-gray-200">
                        {isLoading ? (
                            <div className="p-8 text-center text-gray-500">...</div>
                        ) : (
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 border-b border-gray-200">
                                    <tr>
                                        <th className="px-6 py-3 text-xs font-bold text-gray-500 uppercase">{t('students_list.name')}</th>
                                        <th className="px-6 py-3 text-xs font-bold text-gray-500 uppercase">{t('students_list.email')}</th>
                                        <th className="px-6 py-3 text-xs font-bold text-gray-500 uppercase text-right">{t('students_list.actions')}</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200">
                                    {students.map((student) => (
                                        <tr key={student.id} className="hover:bg-gray-50 transition-colors">
                                            <td className="px-6 py-4 text-sm text-gray-900 font-medium">{student.name}</td>
                                            <td className="px-6 py-4 text-sm text-gray-600">{student.email}</td>
                                            <td className="px-6 py-4 text-sm text-right">
                                                <button className="text-blue-600 hover:text-blue-800 font-bold">
                                                    {t('students_list.view_profile')}
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    {students.length === 0 && (
                                        <tr>
                                            <td colSpan={3} className="px-6 py-8 text-center text-gray-500">
                                                {t('students_list.no_students')}
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        )}
                    </div>
                </main>
            </div>
        </div>
    );
}