import { createContext, useContext, useEffect, useRef, useState } from 'react';

interface RestTimerContextType {
    isActive: boolean;
    secondsLeft: number;
    totalSeconds: number;
    exerciseName: string;
    start: (seconds: number, exerciseName: string) => void;
    stop: () => void;
    adjust: (delta: number) => void;
}

const RestTimerContext = createContext<RestTimerContextType | null>(null);

export function useRestTimer() {
    const ctx = useContext(RestTimerContext);
    if (!ctx) throw new Error('useRestTimer must be used inside RestTimerProvider');
    return ctx;
}

function playPing() {
    try {
        const audioCtx = new AudioContext();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        osc.type = 'sine';
        osc.frequency.setValueAtTime(880, audioCtx.currentTime);
        gain.gain.setValueAtTime(0.3, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.9);
        osc.start(audioCtx.currentTime);
        osc.stop(audioCtx.currentTime + 0.9);
    } catch {
    }
}

function notifyTimeUp(exerciseName: string) {
    if ('Notification' in window && Notification.permission === 'granted') {
        new Notification('Progressor — Rest over!', {
            body: `Time to go! ${exerciseName}`,
            icon: '/favicon.svg',
        });
    }
}

export function RestTimerProvider({ children }: { children: React.ReactNode }) {
    const [isActive, setIsActive] = useState(false);
    const [secondsLeft, setSecondsLeft] = useState(0);
    const [totalSeconds, setTotalSeconds] = useState(0);
    const [exerciseName, setExerciseName] = useState('');

    const endTimeRef = useRef<number | null>(null);
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
    const exerciseNameRef = useRef('');

    function start(seconds: number, name: string) {
        if (intervalRef.current) clearInterval(intervalRef.current);

        const end = Date.now() + seconds * 1000;
        endTimeRef.current = end;
        exerciseNameRef.current = name;
        setTotalSeconds(seconds);
        setSecondsLeft(seconds);
        setExerciseName(name);
        setIsActive(true);

        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }

        intervalRef.current = setInterval(() => {
            const remaining = Math.ceil((endTimeRef.current! - Date.now()) / 1000);
            if (remaining <= 0) {
                clearInterval(intervalRef.current!);
                setSecondsLeft(0);
                setIsActive(false);
                playPing();
                notifyTimeUp(exerciseNameRef.current);
            } else {
                setSecondsLeft(remaining);
            }
        }, 250);
    }

    function stop() {
        if (intervalRef.current) clearInterval(intervalRef.current);
        setIsActive(false);
        setSecondsLeft(0);
    }

    function adjust(delta: number) {
        if (!endTimeRef.current) return;
        endTimeRef.current = endTimeRef.current + delta * 1000;
        const remaining = Math.ceil((endTimeRef.current - Date.now()) / 1000);
        const clamped = Math.max(1, remaining);
        setSecondsLeft(clamped);
        setTotalSeconds(prev => Math.max(1, prev + delta));
    }

    useEffect(() => {
        return () => {
            if (intervalRef.current) clearInterval(intervalRef.current);
        };
    }, []);

    return (
        <RestTimerContext.Provider value={{ isActive, secondsLeft, totalSeconds, exerciseName, start, stop, adjust }}>
            {children}
        </RestTimerContext.Provider>
    );
}
