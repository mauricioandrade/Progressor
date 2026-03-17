import { useEffect, useState } from 'react';

type Theme = 'light' | 'dark';

export function useTheme() {
    const [theme, setTheme] = useState<Theme>(() => {
        return (localStorage.getItem('@Progressor:theme') as Theme) ?? 'light';
    });

    useEffect(() => {
        const root = document.documentElement;
        if (theme === 'dark') {
            root.classList.add('dark');
        } else {
            root.classList.remove('dark');
        }
        localStorage.setItem('@Progressor:theme', theme);
    }, [theme]);

    function toggleTheme() {
        setTheme(prev => (prev === 'light' ? 'dark' : 'light'));
    }

    return { theme, toggleTheme };
}
