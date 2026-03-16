import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';

interface SidebarProps {
    role: 'STUDENT' | 'PERSONAL' | 'NUTRITIONIST';
}

export function Sidebar({ role }: SidebarProps) {
    const { t } = useTranslation();

    const menuItems = {
        STUDENT: [
            { label: t('sidebar.dashboard'), path: '/dashboard' },
            { label: t('sidebar.my_workouts'), path: '/workouts' },
            { label: t('sidebar.measurements'), path: '/measurements' },
        ],
        PERSONAL: [
            { label: t('sidebar.dashboard'), path: '/dashboard' },
            { label: t('sidebar.students'), path: '/students' },
            { label: t('sidebar.create_workout'), path: '/workouts/new' },
        ],
        NUTRITIONIST: [
            { label: t('sidebar.dashboard'), path: '/dashboard' },
            { label: t('sidebar.students'), path: '/students' },
            { label: t('sidebar.reports'), path: '/reports' },
        ]
    };

    return (
        <aside className="w-64 bg-white border-r border-gray-200 min-h-screen p-4">
            <div className="mb-8 px-2">
                <h1 className="text-2xl font-bold text-blue-600">Progressor</h1>
            </div>
            <nav className="space-y-1">
                {menuItems[role].map((item) => (
                    <Link
                        key={item.path}
                        to={item.path}
                        className="block px-4 py-2.5 text-sm font-medium text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                    >
                        {item.label}
                    </Link>
                ))}
            </nav>
        </aside>
    );
}