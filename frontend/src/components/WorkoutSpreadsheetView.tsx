import { Fragment, useEffect, useState } from 'react';
import { LayoutGrid, Loader2, ChevronDown, ChevronUp } from 'lucide-react';
import { api } from '../services/api';

interface ExerciseLog {
    id: string;
    exerciseId: string;
    actualWeight: number | null;
    actualReps: number;
    completedAt: string;
    tonnageAchieved: number;
}

interface ExerciseRow {
    id: string;
    name: string;
    sets: number;
    repetitions: number;
    weightInKg: number | null;
    measurementType: string;
}

interface BlockSection {
    id: string;
    name: string;
    position: number;
    exercises: ExerciseRow[];
}

interface PlanData {
    id: string;
    name: string;
    createdAt: string;
    blocks: BlockSection[];
}

function formatWeight(log: ExerciseLog | undefined): string {
    if (!log) return '—';
    if (log.actualWeight) return `${log.actualWeight}kg`;
    return `${log.actualReps}rep`;
}

function formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
}

interface Props {
    studentId?: string;
}

export function WorkoutSpreadsheetView({ studentId }: Props) {
    const [plans, setPlans] = useState<PlanData[]>([]);
    const [history, setHistory] = useState<Record<string, ExerciseLog[]>>({});
    const [isLoading, setIsLoading] = useState(true);
    const [expandedPlans, setExpandedPlans] = useState<Record<string, boolean>>({});

    useEffect(() => {
        const url = studentId
            ? `/workouts/plans/student/${studentId}`
            : '/workouts/plans/my';

        api.get<PlanData[]>(url)
            .then(async r => {
                const data = r.data;
                setPlans(data);
                if (data.length > 0) {
                    setExpandedPlans({ [data[0].id]: true });
                }

                const allExercises = data.flatMap(p => (p.blocks ?? []).flatMap(b => (b.exercises ?? [])));
                if (allExercises.length === 0) return;

                const results = await Promise.all(
                    allExercises.map(ex =>
                        api.get<ExerciseLog[]>(`/workouts/history/${ex.id}`)
                            .then(h => ({ id: ex.id, logs: h.data }))
                            .catch(() => ({ id: ex.id, logs: [] as ExerciseLog[] }))
                    )
                );
                const map: Record<string, ExerciseLog[]> = {};
                results.forEach(r => { map[r.id] = r.logs; });
                setHistory(map);
            })
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }, [studentId]);

    function togglePlan(planId: string) {
        setExpandedPlans(prev => ({ ...prev, [planId]: !prev[planId] }));
    }

    if (isLoading) {
        return (
            <div className="flex items-center justify-center py-16">
                <Loader2 className="w-6 h-6 animate-spin text-blue-500" />
            </div>
        );
    }

    if (plans.length === 0) {
        return (
            <div className="bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl p-10 text-center">
                <LayoutGrid className="w-10 h-10 mx-auto mb-3 text-gray-300 dark:text-gray-600" />
                <p className="text-sm font-medium text-gray-500 dark:text-gray-400">
                    Nenhum plano de treino criado ainda.
                </p>
                <p className="text-xs text-gray-400 mt-1">
                    O seu personal trainer deve criar um plano organizado em blocos para aparecer aqui.
                </p>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            {plans.map(plan => (
                <div
                    key={plan.id}
                    className="bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm overflow-hidden"
                >
                    {/* Plan header */}
                    <button
                        onClick={() => togglePlan(plan.id)}
                        className="w-full flex items-center justify-between px-5 py-4 hover:bg-black/[0.02] dark:hover:bg-white/[0.03] transition-colors"
                    >
                        <div className="flex items-center gap-3">
                            <div className="w-8 h-8 rounded-xl bg-blue-50 dark:bg-blue-900/30 flex items-center justify-center">
                                <LayoutGrid className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                            </div>
                            <div className="text-left">
                                <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">{plan.name}</p>
                                <p className="text-[11px] text-gray-400">
                                    {(plan.blocks ?? []).length} bloco{(plan.blocks ?? []).length !== 1 ? 's' : ''} ·{' '}
                                    {(plan.blocks ?? []).reduce((s, b) => s + (b.exercises ?? []).length, 0)} exercício{(plan.blocks ?? []).reduce((s, b) => s + (b.exercises ?? []).length, 0) !== 1 ? 's' : ''}
                                </p>
                            </div>
                        </div>
                        {expandedPlans[plan.id]
                            ? <ChevronUp className="w-4 h-4 text-gray-400" />
                            : <ChevronDown className="w-4 h-4 text-gray-400" />}
                    </button>

                    {expandedPlans[plan.id] && (
                        <div className="overflow-x-auto border-t border-black/5 dark:border-white/[0.07]">
                            <table className="w-full min-w-[640px] text-sm">
                                {/* Table header */}
                                <thead>
                                    <tr className="bg-gray-50/80 dark:bg-slate-700/30">
                                        <th className="text-left px-5 py-2.5 text-[11px] font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide w-48 sticky left-0 bg-gray-50/80 dark:bg-slate-700/30 z-10">
                                            Exercício
                                        </th>
                                        <th className="text-center px-3 py-2.5 text-[11px] font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide w-24">
                                            Séries × Rep
                                        </th>
                                        <th className="text-center px-3 py-2.5 text-[11px] font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide w-20">
                                            Carga
                                        </th>
                                        {[1, 2, 3, 4].map(w => (
                                            <th key={w} className="text-center px-3 py-2.5 text-[11px] font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide min-w-[80px]">
                                                Semana {w}
                                            </th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {(plan.blocks ?? []).map(block => (
                                        <Fragment key={block.id}>
                                            {/* Block header row */}
                                            <tr className="bg-indigo-50/60 dark:bg-indigo-900/20">
                                                <td
                                                    colSpan={7}
                                                    className="px-5 py-2 text-[11px] font-bold text-indigo-700 dark:text-indigo-400 uppercase tracking-widest"
                                                >
                                                    ▸ {block.name}
                                                </td>
                                            </tr>

                                            {/* Exercise rows */}
                                            {(block.exercises ?? []).length === 0 ? (
                                                <tr>
                                                    <td colSpan={7} className="px-5 py-3 text-xs text-gray-400 italic">
                                                        Nenhum exercício neste bloco.
                                                    </td>
                                                </tr>
                                            ) : (
                                                (block.exercises ?? []).map((ex, rowIdx) => {
                                                    const logs = history[ex.id] ?? [];
                                                    // Sort ascending by date; take last 4
                                                    const sorted = [...logs].sort((a, b) =>
                                                        new Date(a.completedAt).getTime() - new Date(b.completedAt).getTime()
                                                    );
                                                    const last4 = sorted.slice(-4);
                                                    // Pad to 4 with undefined
                                                    while (last4.length < 4) last4.unshift(undefined as any);

                                                    const isEven = rowIdx % 2 === 0;
                                                    const rowBg = isEven
                                                        ? 'bg-white/60 dark:bg-slate-800/40'
                                                        : 'bg-gray-50/60 dark:bg-slate-700/20';

                                                    return (
                                                        <tr key={ex.id} className={`${rowBg} group hover:bg-blue-50/40 dark:hover:bg-blue-900/10 transition-colors`}>
                                                            {/* Exercise name — sticky */}
                                                            <td className={`px-5 py-3 text-xs font-medium text-gray-800 dark:text-gray-100 sticky left-0 ${rowBg} group-hover:bg-blue-50/40 dark:group-hover:bg-blue-900/10 z-10`}>
                                                                {ex.name}
                                                            </td>

                                                            {/* Sets × Reps */}
                                                            <td className="px-3 py-3 text-xs text-center font-semibold text-gray-700 dark:text-gray-300">
                                                                {ex.sets} × {ex.repetitions}
                                                            </td>

                                                            {/* Planned weight */}
                                                            <td className="px-3 py-3 text-xs text-center font-semibold text-blue-600 dark:text-blue-400">
                                                                {ex.weightInKg ? `${ex.weightInKg}kg` : '—'}
                                                            </td>

                                                            {/* Week 1-4 from history */}
                                                            {last4.map((log, wi) => {
                                                                const isLatest = wi === 3 && log;
                                                                return (
                                                                    <td
                                                                        key={wi}
                                                                        className="px-3 py-3 text-xs text-center"
                                                                    >
                                                                        {log ? (
                                                                            <div className="flex flex-col items-center gap-0.5">
                                                                                <span className={`font-semibold ${isLatest ? 'text-emerald-600 dark:text-emerald-400' : 'text-gray-600 dark:text-gray-300'}`}>
                                                                                    {formatWeight(log)}
                                                                                </span>
                                                                                <span className="text-[9px] text-gray-400">{formatDate(log.completedAt)}</span>
                                                                            </div>
                                                                        ) : (
                                                                            <span className="text-gray-300 dark:text-gray-600">—</span>
                                                                        )}
                                                                    </td>
                                                                );
                                                            })}
                                                        </tr>
                                                    );
                                                })
                                            )}
                                        </Fragment>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
}
