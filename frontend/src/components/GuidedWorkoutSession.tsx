import { useState, useEffect, useRef } from 'react';
import { X, SkipForward, Trophy } from 'lucide-react';
import { api } from '../services/api';
import toast from 'react-hot-toast';

interface Exercise {
    id: string;
    name: string;
    sets: number;
    repetitions: number;
    weightInKg: number | null;
    restTime: number | null;
    workoutLabel?: string;
}

interface ExerciseStats {
    exerciseId: string;
    lastWeight: number | null;
    lastReps: number | null;
    prWeight: number | null;
    prReps: number | null;
}

interface SessionLog {
    exerciseName: string;
    weight: number | null;
    reps: number;
    pr: boolean;
    tonnage: number;
}

type Phase = 'exercise' | 'logging' | 'resting' | 'done';

interface Props {
    exercises: Exercise[];
    stats: Record<string, ExerciseStats>;
    onClose: () => void;
}

export function GuidedWorkoutSession({ exercises, stats, onClose }: Props) {
    const [exIdx, setExIdx] = useState(0);
    const [setNum, setSetNum] = useState(1);
    const [phase, setPhase] = useState<Phase>('exercise');
    const [logWeight, setLogWeight] = useState('');
    const [logReps, setLogReps] = useState('');
    const [isLogging, setIsLogging] = useState(false);
    const [restSecs, setRestSecs] = useState(0);
    const [restInitial, setRestInitial] = useState(0);
    const [sessionLogs, setSessionLogs] = useState<SessionLog[]>([]);
    const sessionSavedRef = useRef(false);

    const ex = exercises[exIdx];
    const exStats = ex ? (stats[ex.id] ?? null) : null;
    const totalSets = ex?.sets ?? 1;
    const isLastSet = setNum >= totalSets;
    const isLastExercise = exIdx >= exercises.length - 1;

    useEffect(() => {
        if (!ex) return;
        const defaultWeight = exStats?.lastWeight != null
            ? String(exStats.lastWeight)
            : ex.weightInKg != null ? String(ex.weightInKg) : '';
        setLogWeight(defaultWeight);
        setLogReps(String(ex.repetitions));
    }, [exIdx]);

    // Rest countdown — one tick per second
    useEffect(() => {
        if (phase !== 'resting' || restSecs <= 0) return;
        const id = setTimeout(() => setRestSecs(s => s - 1), 1000);
        return () => clearTimeout(id);
    }, [phase, restSecs]);

    // Persist session summary when done phase is reached
    useEffect(() => {
        if (phase !== 'done' || sessionSavedRef.current || sessionLogs.length === 0) return;
        sessionSavedRef.current = true;
        const completedEx = new Set(sessionLogs.map(l => l.exerciseName)).size;
        api.post('/workouts/sessions', {
            exercises: completedEx,
            sets: sessionLogs.length,
            tonnageKg: sessionLogs.reduce((s, l) => s + l.tonnage, 0),
            prCount: sessionLogs.filter(l => l.pr).length,
        }).catch(() => {});
    }, [phase]);

    // Advance when rest finishes
    useEffect(() => {
        if (phase !== 'resting' || restSecs > 0) return;
        if (isLastSet) {
            if (isLastExercise) {
                setPhase('done');
            } else {
                setExIdx(i => i + 1);
                setSetNum(1);
                setPhase('exercise');
            }
        } else {
            setSetNum(s => s + 1);
            setPhase('exercise');
        }
    }, [phase, restSecs, isLastSet, isLastExercise]);

    async function handleLog() {
        if (!ex || !logReps) return;
        setIsLogging(true);
        try {
            const { data } = await api.post<{ personalRecord: boolean; tonnageAchieved: number }>('/workouts/log', {
                exerciseId: ex.id,
                exerciseName: ex.name,
                actualWeight: logWeight ? Number(logWeight) : null,
                actualReps: Number(logReps),
            });
            setSessionLogs(prev => [...prev, {
                exerciseName: ex.name,
                weight: logWeight ? Number(logWeight) : null,
                reps: Number(logReps),
                pr: data.personalRecord,
                tonnage: data.tonnageAchieved ?? 0,
            }]);
            if (data.personalRecord) toast.success('🏆 Novo recorde!', { duration: 2000 });
            const rest = ex.restTime ?? 90;
            setRestInitial(rest);
            setRestSecs(rest);
            setPhase('resting');
        } catch {
            toast.error('Erro ao registrar série.');
        } finally {
            setIsLogging(false);
        }
    }

    function skipRest() {
        setRestSecs(0);
    }

    function skipExercise() {
        if (isLastExercise) {
            setPhase('done');
        } else {
            setExIdx(i => i + 1);
            setSetNum(1);
            setPhase('exercise');
        }
    }

    const totalTonnage = sessionLogs.reduce((s, l) => s + l.tonnage, 0);
    const prCount = sessionLogs.filter(l => l.pr).length;
    const completedExercises = new Set(sessionLogs.map(l => l.exerciseName)).size;

    const circumference = 2 * Math.PI * 44;
    const ringOffset = restInitial > 0 ? circumference * (1 - restSecs / restInitial) : 0;

    return (
        <div className="fixed inset-0 z-50 bg-slate-950 flex flex-col text-white">
            {/* Header */}
            <div className="flex items-center justify-between px-5 py-4 border-b border-white/10 flex-shrink-0">
                <div className="flex items-center gap-3">
                    <button
                        onClick={onClose}
                        className="w-8 h-8 flex items-center justify-center rounded-full bg-white/10 hover:bg-white/20 transition-colors active:scale-95"
                    >
                        <X className="w-4 h-4" />
                    </button>
                    <span className="text-sm font-semibold text-white/50">Treino guiado</span>
                </div>
                {phase !== 'done' && (
                    <span className="text-xs bg-white/10 text-white/40 px-3 py-1 rounded-full">
                        {exIdx + 1} / {exercises.length}
                    </span>
                )}
            </div>

            {/* Progress bar */}
            {phase !== 'done' && (
                <div className="h-0.5 bg-white/10 flex-shrink-0">
                    <div
                        className="h-full bg-blue-500 transition-all duration-700"
                        style={{ width: `${Math.round((exIdx / exercises.length) * 100)}%` }}
                    />
                </div>
            )}

            {/* Content */}
            <div className="flex-1 flex flex-col items-center justify-center p-6 gap-6 overflow-y-auto">

                {phase === 'exercise' && ex && (
                    <>
                        {ex.workoutLabel && (
                            <span className="text-xs bg-blue-500/20 text-blue-300 px-3 py-1 rounded-full font-medium">
                                {ex.workoutLabel}
                            </span>
                        )}
                        <div className="text-center">
                            <h2 className="text-2xl font-bold leading-tight">{ex.name}</h2>
                            <p className="text-white/40 text-sm mt-1.5">Série {setNum} de {totalSets}</p>
                        </div>

                        <div className="flex gap-3">
                            <div className="bg-white/5 rounded-2xl px-5 py-3 text-center min-w-[72px]">
                                <p className="text-[10px] text-white/30 uppercase tracking-wide mb-1">Séries</p>
                                <p className="text-3xl font-bold text-blue-400">{ex.sets}</p>
                            </div>
                            <div className="bg-white/5 rounded-2xl px-5 py-3 text-center min-w-[72px]">
                                <p className="text-[10px] text-white/30 uppercase tracking-wide mb-1">Reps</p>
                                <p className="text-3xl font-bold text-green-400">{ex.repetitions}</p>
                            </div>
                            {ex.weightInKg != null && (
                                <div className="bg-white/5 rounded-2xl px-5 py-3 text-center min-w-[72px]">
                                    <p className="text-[10px] text-white/30 uppercase tracking-wide mb-1">Peso</p>
                                    <p className="text-3xl font-bold text-orange-400">{ex.weightInKg}kg</p>
                                </div>
                            )}
                        </div>

                        {exStats && (exStats.lastWeight != null || exStats.prWeight != null) && (
                            <div className="flex gap-2 flex-wrap justify-center">
                                {exStats.lastWeight != null && (
                                    <span className="text-xs bg-white/10 text-white/50 px-3 py-1 rounded-full">
                                        Última: {exStats.lastWeight}kg × {exStats.lastReps}
                                    </span>
                                )}
                                {exStats.prWeight != null && (
                                    <span className="text-xs bg-yellow-500/20 text-yellow-300 px-3 py-1 rounded-full">
                                        🏆 PR: {exStats.prWeight}kg × {exStats.prReps}
                                    </span>
                                )}
                            </div>
                        )}

                        <button
                            onClick={() => setPhase('logging')}
                            className="w-full max-w-xs py-4 bg-blue-600 text-white text-lg font-bold rounded-3xl hover:bg-blue-500 active:scale-95 transition-all shadow-lg shadow-blue-500/20"
                        >
                            Registrar série
                        </button>

                        <button
                            onClick={skipExercise}
                            className="flex items-center gap-1.5 text-xs text-white/25 hover:text-white/50 transition-colors"
                        >
                            <SkipForward className="w-3.5 h-3.5" />
                            Pular exercício
                        </button>
                    </>
                )}

                {phase === 'logging' && ex && (
                    <>
                        <div className="text-center">
                            <h2 className="text-xl font-bold">{ex.name}</h2>
                            <p className="text-white/40 text-sm mt-1">
                                Série {setNum} de {totalSets} — O que você fez?
                            </p>
                        </div>

                        <div className="w-full max-w-xs space-y-3">
                            <div>
                                <label className="text-xs text-white/30 uppercase tracking-wide block mb-2">
                                    Peso real (kg)
                                </label>
                                <input
                                    type="number"
                                    inputMode="decimal"
                                    min={0}
                                    step={0.5}
                                    value={logWeight}
                                    onChange={e => setLogWeight(e.target.value)}
                                    placeholder="—"
                                    autoFocus
                                    className="w-full bg-white/10 border border-white/15 rounded-2xl px-4 py-3.5 text-white text-center text-3xl font-bold placeholder-white/15 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                            </div>
                            <div>
                                <label className="text-xs text-white/30 uppercase tracking-wide block mb-2">
                                    Repetições
                                </label>
                                <input
                                    type="number"
                                    inputMode="numeric"
                                    min={1}
                                    value={logReps}
                                    onChange={e => setLogReps(e.target.value)}
                                    className="w-full bg-white/10 border border-white/15 rounded-2xl px-4 py-3.5 text-white text-center text-3xl font-bold placeholder-white/15 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
                            </div>
                        </div>

                        <div className="w-full max-w-xs flex gap-3">
                            <button
                                onClick={() => setPhase('exercise')}
                                className="flex-1 py-3 rounded-2xl text-sm font-medium bg-white/10 text-white/50 hover:bg-white/15 transition-colors active:scale-95"
                            >
                                Voltar
                            </button>
                            <button
                                onClick={handleLog}
                                disabled={isLogging || !logReps}
                                className="flex-1 py-3 rounded-2xl text-sm font-bold bg-green-600 text-white hover:bg-green-500 disabled:opacity-50 transition-colors active:scale-95"
                            >
                                {isLogging ? '...' : 'Confirmar ✓'}
                            </button>
                        </div>
                    </>
                )}

                {phase === 'resting' && (
                    <>
                        <div className="text-center space-y-1">
                            <p className="text-sm font-semibold text-white/50">Descanse</p>
                            {!isLastSet ? (
                                <p className="text-xs text-white/25">Próxima: Série {setNum + 1} de {totalSets}</p>
                            ) : isLastExercise ? (
                                <p className="text-xs text-green-400 font-medium">Último exercício! 🎉</p>
                            ) : (
                                <p className="text-xs text-white/25">Próximo: {exercises[exIdx + 1]?.name}</p>
                            )}
                        </div>

                        <div className="relative w-48 h-48">
                            <svg className="w-full h-full -rotate-90" viewBox="0 0 100 100">
                                <circle
                                    cx="50" cy="50" r="44"
                                    fill="none"
                                    stroke="rgba(255,255,255,0.06)"
                                    strokeWidth="7"
                                />
                                <circle
                                    cx="50" cy="50" r="44"
                                    fill="none"
                                    stroke="#3b82f6"
                                    strokeWidth="7"
                                    strokeLinecap="round"
                                    strokeDasharray={circumference}
                                    strokeDashoffset={ringOffset}
                                    className="transition-all duration-1000 ease-linear"
                                />
                            </svg>
                            <div className="absolute inset-0 flex flex-col items-center justify-center">
                                <span className="text-6xl font-bold tabular-nums leading-none">{restSecs}</span>
                                <span className="text-xs text-white/30 mt-2 uppercase tracking-wide">seg</span>
                            </div>
                        </div>

                        <button
                            onClick={skipRest}
                            className="flex items-center gap-1.5 text-xs text-white/25 hover:text-white/55 transition-colors"
                        >
                            <SkipForward className="w-3.5 h-3.5" />
                            Pular descanso
                        </button>
                    </>
                )}

                {phase === 'done' && (
                    <>
                        <div className="text-center">
                            <div className="text-6xl mb-3">🎉</div>
                            <h2 className="text-2xl font-bold">Treino concluído!</h2>
                            <p className="text-white/40 text-sm mt-1">Ótimo trabalho hoje!</p>
                        </div>

                        <div className="grid grid-cols-3 gap-3 w-full max-w-xs">
                            <div className="bg-white/5 rounded-2xl p-4 text-center">
                                <p className="text-3xl font-bold text-blue-400">{completedExercises}</p>
                                <p className="text-[10px] text-white/30 uppercase tracking-wide mt-1.5">Exerc.</p>
                            </div>
                            <div className="bg-white/5 rounded-2xl p-4 text-center">
                                <p className="text-3xl font-bold text-green-400">{sessionLogs.length}</p>
                                <p className="text-[10px] text-white/30 uppercase tracking-wide mt-1.5">Séries</p>
                            </div>
                            <div className="bg-white/5 rounded-2xl p-4 text-center">
                                <p className="text-3xl font-bold text-orange-400">{totalTonnage.toFixed(0)}</p>
                                <p className="text-[10px] text-white/30 uppercase tracking-wide mt-1.5">kg vol.</p>
                            </div>
                        </div>

                        {prCount > 0 && (
                            <div className="flex items-center gap-2 bg-yellow-500/10 text-yellow-300 px-4 py-2.5 rounded-2xl text-sm font-medium">
                                <Trophy className="w-4 h-4 flex-shrink-0" />
                                <span>
                                    {prCount === 1 ? '1 novo recorde pessoal!' : `${prCount} novos recordes pessoais!`}
                                </span>
                            </div>
                        )}

                        <button
                            onClick={onClose}
                            className="w-full max-w-xs py-4 bg-blue-600 text-white text-base font-bold rounded-3xl hover:bg-blue-500 active:scale-95 transition-all shadow-lg shadow-blue-500/20"
                        >
                            Fechar
                        </button>
                    </>
                )}
            </div>
        </div>
    );
}
