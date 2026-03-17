import { useTranslation } from 'react-i18next';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTheme } from '../hooks/useTheme';
import {
    LayoutDashboard,
    Dumbbell,
    Users,
    Activity,
    Sun,
    Moon,
    LogOut,
    Ruler,
    PenLine,
    UtensilsCrossed,
    Salad,
    Settings,
    Camera
} from 'lucide-react';

interface SidebarProps {
    role: 'STUDENT' | 'PERSONALTRAINER' | 'NUTRITIONIST';
}

const iconClass = 'w-4 h-4 shrink-0';

export function Sidebar({ role }: SidebarProps) {
    const { t } = useTranslation();
    const location = useLocation();
    const navigate = useNavigate();
    const { theme, toggleTheme } = useTheme();

    function handleLogout() {
        localStorage.removeItem('@Progressor:token');
        navigate('/login', { replace: true });
    }

    const menuItems = {
        STUDENT: [
            { label: t('sidebar.dashboard'), path: '/dashboard', icon: <LayoutDashboard className={iconClass} /> },
            { label: t('sidebar.my_workouts'), path: '/workouts', icon: <Dumbbell className={iconClass} /> },
            { label: t('sidebar.my_diet'), path: '/my-diet', icon: <Salad className={iconClass} /> },
            { label: t('sidebar.measurements'), path: '/measurements', icon: <Activity className={iconClass} /> },
            { label: t('sidebar.visual_progress'), path: '/progress', icon: <Camera className={iconClass} /> },
            { label: t('sidebar.settings'), path: '/settings', icon: <Settings className={iconClass} /> },
        ],
        PERSONALTRAINER: [
            { label: t('sidebar.dashboard'), path: '/dashboard', icon: <LayoutDashboard className={iconClass} /> },
            { label: t('sidebar.students'), path: '/students', icon: <Users className={iconClass} /> },
            { label: t('sidebar.create_workout'), path: '/workouts/new', icon: <Dumbbell className={iconClass} /> },
            { label: t('sidebar.new_assessment'), path: '/assessments/new', icon: <Ruler className={iconClass} /> },
            { label: t('sidebar.settings'), path: '/settings', icon: <Settings className={iconClass} /> },
        ],
        NUTRITIONIST: [
            { label: t('sidebar.dashboard'), path: '/dashboard', icon: <LayoutDashboard className={iconClass} /> },
            { label: t('sidebar.my_patients'), path: '/diet/patients', icon: <Users className={iconClass} /> },
            { label: t('sidebar.diet_builder'), path: '/diet/builder', icon: <UtensilsCrossed className={iconClass} /> },
            { label: t('sidebar.settings'), path: '/settings', icon: <Settings className={iconClass} /> },
        ]
    };

    const bottomNavItems = menuItems[role].slice(0, 5);

    return (
        <>
            {/* Desktop sidebar */}
            <aside className="hidden md:flex w-64 bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl border-r border-black/5 dark:border-white/[0.07] min-h-screen flex-col p-4 shrink-0">
                <div className="mb-8 px-2">
                    <span className="text-2xl font-extrabold tracking-tight text-gray-900 dark:text-white" style={{ fontFamily: "'Inter Variable', Inter, sans-serif" }}>
                        Progressor
                    </span>
                </div>
                <nav className="space-y-0.5 flex-1">
                    {menuItems[role].map((item) => {
                        const isActive = location.pathname === item.path;
                        return (
                            <Link
                                key={item.path}
                                to={item.path}
                                className={`flex items-center gap-3 px-4 py-2.5 text-sm font-medium rounded-xl transition-all duration-200 active:scale-95 ${
                                    isActive
                                        ? 'bg-blue-50 dark:bg-blue-950/60 text-blue-600 dark:text-blue-400'
                                        : 'text-gray-600 dark:text-gray-400 hover:bg-black/5 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-gray-100'
                                }`}
                            >
                                {item.icon}
                                {item.label}
                            </Link>
                        );
                    })}
                </nav>
                <div className="pt-4 border-t border-black/5 dark:border-white/[0.07] space-y-0.5">
                    <button
                        onClick={toggleTheme}
                        className="w-full flex items-center justify-between px-4 py-2.5 text-sm font-medium text-gray-600 dark:text-gray-400 hover:bg-black/5 dark:hover:bg-white/5 hover:text-gray-900 dark:hover:text-gray-100 rounded-xl transition-all duration-200 active:scale-95"
                    >
                        <span className="text-xs text-gray-400 dark:text-gray-500">
                            {theme === 'dark' ? t('sidebar.theme_toggle_light') : t('sidebar.theme_toggle_dark')}
                        </span>
                        {theme === 'dark' ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
                    </button>
                    <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-3 px-4 py-2.5 text-sm font-medium text-left text-red-500 hover:bg-red-50 dark:hover:bg-red-950/30 rounded-xl transition-all duration-200 active:scale-95"
                    >
                        <LogOut className={iconClass} />
                        {t('sidebar.logout')}
                    </button>
                </div>
            </aside>

            {/* Mobile bottom navigation */}
            <nav className="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-white/95 dark:bg-slate-900/95 backdrop-blur-xl border-t border-black/5 dark:border-white/[0.07] flex safe-area-pb">
                {bottomNavItems.map((item) => {
                    const isActive = location.pathname === item.path;
                    return (
                        <Link
                            key={item.path}
                            to={item.path}
                            className={`flex-1 flex flex-col items-center justify-center py-2 gap-0.5 transition-all duration-200 active:scale-90 ${
                                isActive ? 'text-blue-600 dark:text-blue-400' : 'text-gray-400 dark:text-gray-500'
                            }`}
                        >
                            <span className={`transition-all duration-200 ${isActive ? 'scale-110' : ''}`}>
                                {item.icon}
                            </span>
                            <span className="text-[9px] font-medium truncate max-w-[52px] text-center">{item.label}</span>
                        </Link>
                    );
                })}
            </nav>
        </>
    );
}
