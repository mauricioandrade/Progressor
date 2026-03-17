import { X } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import { useRestTimer } from '../contexts/RestTimerContext';

const RADIUS = 18;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

export function RestTimerPill() {
    const { t } = useTranslation();
    const { isActive, secondsLeft, totalSeconds, exerciseName, stop, adjust } = useRestTimer();

    if (!isActive) return null;

    const progress = totalSeconds > 0 ? (totalSeconds - secondsLeft) / totalSeconds : 0;
    const strokeDashoffset = CIRCUMFERENCE * (1 - progress);

    const minutes = Math.floor(secondsLeft / 60);
    const secs = secondsLeft % 60;
    const timeStr = minutes > 0
        ? `${minutes}:${String(secs).padStart(2, '0')}`
        : String(secs);

    const isUrgent = secondsLeft <= 10;

    return (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 animate-slide-up">
            <div className={`flex items-center gap-3 pl-3 pr-4 py-2.5 backdrop-blur-xl border rounded-full shadow-2xl shadow-black/20 transition-colors ${
                isUrgent
                    ? 'bg-red-50/90 dark:bg-red-950/90 border-red-200/40 dark:border-red-800/40'
                    : 'bg-white/90 dark:bg-slate-800/90 border-white/20 dark:border-white/[0.08]'
            }`}>
                <div className="relative w-10 h-10 shrink-0">
                    <svg className="w-10 h-10 -rotate-90" viewBox="0 0 40 40">
                        <circle
                            cx="20" cy="20" r={RADIUS}
                            fill="none" strokeWidth="2.5"
                            className="text-gray-200 dark:text-gray-700"
                            stroke="currentColor"
                        />
                        <circle
                            cx="20" cy="20" r={RADIUS}
                            fill="none" strokeWidth="2.5"
                            strokeLinecap="round"
                            strokeDasharray={CIRCUMFERENCE}
                            strokeDashoffset={strokeDashoffset}
                            stroke="currentColor"
                            className={`transition-all duration-500 ${isUrgent ? 'text-red-500' : 'text-blue-500'}`}
                        />
                    </svg>
                    <span className={`absolute inset-0 flex items-center justify-center text-xs font-bold ${isUrgent ? 'text-red-600 dark:text-red-400' : 'text-gray-900 dark:text-gray-100'}`}>
                        {timeStr}
                    </span>
                </div>

                <div className="flex flex-col min-w-0">
                    <span className="text-xs font-semibold text-gray-900 dark:text-white leading-tight">
                        {t('rest_timer.resting')}
                    </span>
                    <span className="text-xs text-gray-500 dark:text-gray-400 truncate max-w-[7rem]">
                        {exerciseName}
                    </span>
                </div>

                <div className="flex items-center gap-1">
                    <button
                        onClick={() => adjust(-15)}
                        className="w-7 h-7 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-400 text-[10px] font-bold hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                    >
                        −15
                    </button>
                    <button
                        onClick={() => adjust(15)}
                        className="w-7 h-7 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-400 text-[10px] font-bold hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                    >
                        +15
                    </button>
                    <button
                        onClick={stop}
                        className="w-7 h-7 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                    >
                        <X className="w-3.5 h-3.5" />
                    </button>
                </div>
            </div>
        </div>
    );
}
