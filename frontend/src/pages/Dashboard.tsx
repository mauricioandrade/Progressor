import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import {
    Dumbbell, Scale, Activity, Users, PenLine, ChevronRight,
    Trophy, CalendarDays, UtensilsCrossed, Droplets, Pencil, Check, X, Play, Salad, Camera,
    Bell, UserCheck, UserX, Target
} from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { ContributionGraph } from '../components/ContributionGraph';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

interface UserProfile { fullName: string; email: string; hasAvatar: boolean; }

interface WorkoutExercise { name: string; tonnage: number | null; workoutLabel?: string; scheduledDays?: string; }
interface TodayExercise { name: string; workoutLabel?: string; }
interface Measurement { weight: number | null; }
interface Student { id: string; }
interface PersonalRecord {
    id: string; exerciseName: string; actualWeight: number | null;
    actualReps: number; tonnageAchieved: number; completedAt: string;
}
interface MealPlan { id: string; items: { proteinG: number; carbsG: number; fatG: number; caloriesKcal: number }[] }
interface WaterIntake { dailyWaterGoal: number; currentWaterIntake: number; }

interface ConnectionInvite {
    id: string;
    professionalId: string;
    professionalName: string;
    professionalRole: 'COACH' | 'NUTRI';
    status: string;
    createdAt: string;
}

const tile = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm p-5 transition-all duration-300';

const MACRO_COLORS = { protein: '#f43f5e', carbs: '#3b82f6', fat: '#f59e0b' };

function MacroRing({ protein, carbs, fat, calories }: { protein: number; carbs: number; fat: number; calories: number }) {
    const { t } = useTranslation();
    const total = protein + carbs + fat;
    const data = [
        { name: t('nutrition.protein'), value: protein, color: MACRO_COLORS.protein },
        { name: t('nutrition.carbs'), value: carbs, color: MACRO_COLORS.carbs },
        { name: t('nutrition.fat'), value: fat, color: MACRO_COLORS.fat },
    ].filter(d => d.value > 0);

    if (total === 0) {
        return (
            <div className="flex flex-col items-center justify-center h-28 text-gray-400 gap-1">
                <UtensilsCrossed className="w-8 h-8 text-gray-300 dark:text-gray-700" />
                <p className="text-xs">{t('nutrition.no_diet')}</p>
            </div>
        );
    }

    return (
        <div className="flex items-center gap-4">
            <div className="relative w-28 h-28 shrink-0">
                <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                        <Pie data={data} dataKey="value" innerRadius={34} outerRadius={54} strokeWidth={0} startAngle={90} endAngle={-270}>
                            {data.map((e, i) => <Cell key={i} fill={e.color} />)}
                        </Pie>
                        <Tooltip contentStyle={{ fontSize: '10px', borderRadius: '8px', border: 'none', background: 'rgba(0,0,0,0.8)', color: '#fff' }} formatter={(v: number) => [`${v.toFixed(1)}g`, '']} />
                    </PieChart>
                </ResponsiveContainer>
                <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                    <p className="text-xs font-bold text-emerald-500">{calories.toFixed(0)}</p>
                    <p className="text-[9px] text-gray-400">kcal</p>
                </div>
            </div>
            <div className="flex-1 space-y-1.5">
                {data.map(m => (
                    <div key={m.name}>
                        <div className="flex justify-between text-[10px] mb-0.5">
                            <span className="text-gray-500 dark:text-gray-400">{m.name}</span>
                            <span className="font-semibold" style={{ color: m.color }}>{m.value.toFixed(1)}g</span>
                        </div>
                        <div className="h-1 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden">
                            <div className="h-full rounded-full" style={{ width: `${(m.value / total) * 100}%`, background: m.color }} />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

function WaterTile({ water, onAdd, onSetGoal }: { water: WaterIntake; onAdd: (n: number) => void; onSetGoal: (n: number) => void }) {
    const { t } = useTranslation();
    const [editingGoal, setEditingGoal] = useState(false);
    const [goalInput, setGoalInput] = useState('');
    const pct = water.dailyWaterGoal > 0 ? Math.min((water.currentWaterIntake / water.dailyWaterGoal) * 100, 100) : 0;

    function submitGoal() {
        const v = parseInt(goalInput, 10);
        if (v > 0) { onSetGoal(v); setEditingGoal(false); setGoalInput(''); }
    }

    return (
        <div className={tile}>
            <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                    <Droplets className="w-4 h-4 text-blue-400" />
                    <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('water.title')}</span>
                </div>
                <button onClick={() => { setEditingGoal(v => !v); setGoalInput(water.dailyWaterGoal > 0 ? String(water.dailyWaterGoal) : ''); }} className="w-6 h-6 flex items-center justify-center rounded-full bg-black/5 dark:bg-white/10 text-gray-400 hover:text-blue-400 transition-colors">
                    <Pencil className="w-3 h-3" />
                </button>
            </div>

            {editingGoal ? (
                <div className="flex gap-1.5 items-center mb-3">
                    <input type="number" min={1} value={goalInput} onChange={e => setGoalInput(e.target.value)} placeholder={t('water.set_goal_placeholder')} className="flex-1 px-2.5 py-1.5 border border-white/20 rounded-xl bg-gray-800 text-gray-100 placeholder-gray-500 text-xs focus:ring-2 focus:ring-blue-500 outline-none" onKeyDown={e => e.key === 'Enter' && submitGoal()} />
                    <button onClick={submitGoal} className="w-7 h-7 flex items-center justify-center rounded-xl bg-blue-500 text-white"><Check className="w-3.5 h-3.5" /></button>
                    <button onClick={() => setEditingGoal(false)} className="w-7 h-7 flex items-center justify-center rounded-xl bg-black/10 dark:bg-white/10 text-gray-400"><X className="w-3.5 h-3.5" /></button>
                </div>
            ) : (
                <>
                    <div className="flex items-end justify-between mb-1.5">
                        <span className="text-xl font-bold text-blue-400">{water.currentWaterIntake}ml</span>
                        <span className="text-xs text-gray-400 pb-0.5">/ {water.dailyWaterGoal > 0 ? `${water.dailyWaterGoal}ml` : t('water.no_goal')}</span>
                    </div>
                    <div className="h-2 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden mb-3">
                        <div className="h-full bg-blue-400 rounded-full transition-all duration-500" style={{ width: `${pct}%` }} />
                    </div>
                </>
            )}

            <div className="flex gap-2">
                <button onClick={() => onAdd(250)} className="flex-1 py-2 bg-blue-500/10 text-blue-500 font-semibold text-xs rounded-xl hover:bg-blue-500/20 active:scale-95 transition-all">{t('water.add_250')}</button>
                <button onClick={() => onAdd(500)} className="flex-1 py-2 bg-blue-500/10 text-blue-500 font-semibold text-xs rounded-xl hover:bg-blue-500/20 active:scale-95 transition-all">{t('water.add_500')}</button>
            </div>
        </div>
    );
}

function PendingInvitesCard() {
    const { t } = useTranslation();
    const [invites, setInvites] = useState<ConnectionInvite[]>([]);
    const [responding, setResponding] = useState<string | null>(null);

    useEffect(() => {
        api.get<ConnectionInvite[]>('/connections/pending')
            .then(r => setInvites(r.data))
            .catch(() => {});
    }, []);

    async function respond(requestId: string, accepted: boolean) {
        setResponding(requestId);
        try {
            await api.post('/connections/respond', { requestId, accepted });
            setInvites(prev => prev.filter(i => i.id !== requestId));
            toast.success(accepted ? 'Convite aceito!' : 'Convite recusado.');
        } catch {
            toast.error('Erro ao responder convite.');
        } finally {
            setResponding(null);
        }
    }

    if (invites.length === 0) return null;

    return (
        <div className={`${tile} border-amber-200/60 dark:border-amber-700/40 bg-amber-50/80 dark:bg-amber-900/20`}>
            <div className="flex items-center gap-2 mb-3">
                <div className="relative">
                    <Bell className="w-4 h-4 text-amber-600 dark:text-amber-400" />
                    <span className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full flex items-center justify-center text-[8px] text-white font-bold">
                        {invites.length}
                    </span>
                </div>
                <span className="text-xs font-semibold text-amber-700 dark:text-amber-400 uppercase tracking-wide">
                    Convites pendentes
                </span>
            </div>
            <div className="space-y-2">
                {invites.map(invite => (
                    <div
                        key={invite.id}
                        className="flex items-center justify-between gap-3 p-3 bg-white/70 dark:bg-slate-800/50 rounded-2xl border border-black/5 dark:border-white/5"
                    >
                        <div className="flex items-center gap-3 min-w-0">
                            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-indigo-400 to-blue-500 flex items-center justify-center text-white text-sm font-bold shrink-0">
                                {invite.professionalName.charAt(0).toUpperCase()}
                            </div>
                            <div className="min-w-0">
                                <p className="text-xs font-semibold text-gray-800 dark:text-gray-100 truncate">
                                    {invite.professionalRole === 'COACH' ? 'Personal Trainer' : 'Nutricionista'}{' '}
                                    <span className="font-bold">{invite.professionalName}</span>
                                </p>
                                <p className="text-[11px] text-gray-500 dark:text-gray-400">
                                    {invite.professionalRole === 'COACH'
                                        ? 'quer gerenciar os seus treinos'
                                        : 'quer gerenciar a sua dieta'}
                                </p>
                            </div>
                        </div>
                        <div className="flex items-center gap-1.5 shrink-0">
                            <button
                                onClick={() => respond(invite.id, false)}
                                disabled={responding === invite.id}
                                className="w-8 h-8 flex items-center justify-center rounded-xl bg-red-50 dark:bg-red-900/20 text-red-500 hover:bg-red-100 dark:hover:bg-red-900/40 disabled:opacity-50 transition-colors active:scale-95"
                                title="Recusar"
                            >
                                <UserX className="w-4 h-4" />
                            </button>
                            <button
                                onClick={() => respond(invite.id, true)}
                                disabled={responding === invite.id}
                                className="w-8 h-8 flex items-center justify-center rounded-xl bg-green-50 dark:bg-green-900/20 text-green-600 hover:bg-green-100 dark:hover:bg-green-900/40 disabled:opacity-50 transition-colors active:scale-95"
                                title="Aceitar"
                            >
                                <UserCheck className="w-4 h-4" />
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

const WEEK_DAYS = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'] as const;
const WEEK_DAY_LABELS: Record<string, string> = { MON: 'Seg', TUE: 'Ter', WED: 'Qua', THU: 'Qui', FRI: 'Sex', SAT: 'Sáb', SUN: 'Dom' };

function getTodayCode(): string {
    const map = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    return map[new Date().getDay()];
}

function StudentDashboard() {
    const { t } = useTranslation();
    const [exercises, setExercises] = useState<WorkoutExercise[]>([]);
    const [todayExercises, setTodayExercises] = useState<TodayExercise[]>([]);
    const [measurements, setMeasurements] = useState<Measurement[]>([]);
    const [prs, setPrs] = useState<PersonalRecord[]>([]);
    const [checkInDates, setCheckInDates] = useState<string[]>([]);
    const [mealPlan, setMealPlan] = useState<MealPlan | null>(null);
    const [water, setWater] = useState<WaterIntake>({ dailyWaterGoal: 0, currentWaterIntake: 0 });
    const [weightGoal, setWeightGoal] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        Promise.all([
            api.get('/workouts/my').then(r => setExercises(r.data)).catch(() => {}),
            api.get('/workouts/today').then(r => setTodayExercises(r.data)).catch(() => {}),
            api.get('/measurements/my').then(r => setMeasurements(r.data)).catch(() => {}),
            api.get('/workouts/prs').then(r => setPrs(r.data)).catch(() => {}),
            api.get('/checkins/my').then(r => setCheckInDates(r.data)).catch(() => {}),
            api.get('/nutrition/meal-plans/my').then(r => setMealPlan(r.data)).catch(() => {}),
            api.get('/nutrition/water').then(r => setWater(r.data)).catch(() => {}),
            api.get('/measurements/my/weight-goal').then(r => setWeightGoal(r.data.weightGoal ?? null)).catch(() => {}),
        ]).finally(() => setIsLoading(false));
    }, []);

    const todayCode = getTodayCode();

    const latestWeight = measurements.length > 0 ? measurements[measurements.length - 1].weight : null;
    const totalProtein = mealPlan?.items.reduce((s, i) => s + i.proteinG, 0) ?? 0;
    const totalCarbs = mealPlan?.items.reduce((s, i) => s + i.carbsG, 0) ?? 0;
    const totalFat = mealPlan?.items.reduce((s, i) => s + i.fatG, 0) ?? 0;
    const totalCalories = mealPlan?.items.reduce((s, i) => s + i.caloriesKcal, 0) ?? 0;

    async function handleAddWater(amount: number) {
        try {
            const r = await api.patch('/nutrition/water/intake', { amount });
            setWater(r.data);
            toast.success(t('toast.water_logged'));
        } catch {
            toast.error(t('toast.error_generic'));
        }
    }

    async function handleSetGoal(goal: number) {
        try {
            const r = await api.patch('/nutrition/water/goal', { goal });
            setWater(r.data);
        } catch {
            toast.error(t('toast.error_generic'));
        }
    }

    const [editingGoal, setEditingGoal] = useState(false);
    const [goalInput, setGoalInput] = useState('');
    const [isSavingGoal, setIsSavingGoal] = useState(false);

    async function handleSaveGoal() {
        const val = parseFloat(goalInput);
        if (!val || val <= 0) return;
        setIsSavingGoal(true);
        try {
            await api.put('/measurements/my/weight-goal', { goal: val });
            setWeightGoal(val);
            setEditingGoal(false);
            toast.success(t('toast.weight_goal_saved'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsSavingGoal(false);
        }
    }

    const initialWeight = measurements.length > 0 ? measurements[0].weight : null;
    const weightRange = initialWeight !== null && weightGoal !== null ? Math.abs(weightGoal - initialWeight) : 0;
    const weightMoved = initialWeight !== null && latestWeight !== null ? Math.abs(latestWeight - initialWeight) : 0;
    const weightProgress = weightRange > 0 ? Math.min((weightMoved / weightRange) * 100, 100) : 0;
    const isGoalClose = latestWeight !== null && weightGoal !== null && Math.abs(latestWeight - weightGoal) <= 2;
    const progressBarColor = isGoalClose ? 'bg-green-500' : weightProgress >= 60 ? 'bg-emerald-400' : 'bg-blue-400';

    if (isLoading) return <div className="p-12 text-center text-gray-400">...</div>;

    return (
        <div className="space-y-4">
            {/* Pending invitations notification */}
            <PendingInvitesCard />

            {/* Bento row 1 — 3 stat tiles */}
            <div className="grid grid-cols-3 gap-3 md:gap-4">
                <div className={`${tile} flex flex-col gap-1`}>
                    <div className="w-9 h-9 rounded-2xl flex items-center justify-center bg-purple-50/80 dark:bg-purple-900/30 mb-1">
                        <Dumbbell className="w-4 h-4 text-purple-600 dark:text-purple-400" />
                    </div>
                    <p className="text-[10px] text-gray-400 uppercase tracking-wide">{t('dashboard.exercise_count')}</p>
                    <p className="text-lg font-bold text-gray-800 dark:text-gray-100">{exercises.length}</p>
                </div>
                <div className={`${tile} flex flex-col gap-1`}>
                    <div className="w-9 h-9 rounded-2xl flex items-center justify-center bg-blue-50/80 dark:bg-blue-900/30 mb-1">
                        <Scale className="w-4 h-4 text-blue-600 dark:text-blue-400" />
                    </div>
                    <p className="text-[10px] text-gray-400 uppercase tracking-wide">{t('dashboard.current_weight')}</p>
                    <p className="text-lg font-bold text-gray-800 dark:text-gray-100">{latestWeight !== null ? `${latestWeight}kg` : '—'}</p>
                </div>
                <div className={`${tile} flex flex-col gap-1`}>
                    <div className="w-9 h-9 rounded-2xl flex items-center justify-center bg-yellow-50/80 dark:bg-yellow-900/30 mb-1">
                        <Trophy className="w-4 h-4 text-yellow-600 dark:text-yellow-400" />
                    </div>
                    <p className="text-[10px] text-gray-400 uppercase tracking-wide">{t('dashboard.personal_records')}</p>
                    <p className="text-lg font-bold text-gray-800 dark:text-gray-100">{prs.length}</p>
                </div>
            </div>

            {/* Weight goal progress tile */}
            {(latestWeight !== null || weightGoal !== null) && (
                <div className={tile}>
                    <div className="flex items-center justify-between mb-3">
                        <div className="flex items-center gap-2">
                            <Target className="w-4 h-4 text-violet-400" />
                            <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('weight_goal.title')}</span>
                        </div>
                        {!editingGoal && (
                            <button onClick={() => { setEditingGoal(true); setGoalInput(weightGoal?.toString() ?? ''); }} className="w-6 h-6 flex items-center justify-center rounded-full bg-black/5 dark:bg-white/10 text-gray-400 hover:text-violet-400 transition-colors">
                                <Pencil className="w-3 h-3" />
                            </button>
                        )}
                    </div>
                    {editingGoal ? (
                        <div className="flex gap-1.5 items-center mb-3">
                            <input type="number" min={1} step={0.1} value={goalInput} onChange={e => setGoalInput(e.target.value)} placeholder={t('weight_goal.placeholder')} className="flex-1 px-2.5 py-1.5 border border-black/10 dark:border-white/20 rounded-xl bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 text-xs focus:ring-2 focus:ring-violet-500 outline-none" onKeyDown={e => { if (e.key === 'Enter') handleSaveGoal(); }} />
                            <button onClick={handleSaveGoal} disabled={isSavingGoal} className="w-7 h-7 flex items-center justify-center rounded-xl bg-violet-500 text-white disabled:opacity-50"><Check className="w-3.5 h-3.5" /></button>
                            <button onClick={() => setEditingGoal(false)} className="w-7 h-7 flex items-center justify-center rounded-xl bg-black/10 dark:bg-white/10 text-gray-400"><X className="w-3.5 h-3.5" /></button>
                        </div>
                    ) : (
                        <div className="flex items-end justify-between mb-2">
                            {initialWeight !== null && (
                                <div>
                                    <p className="text-[10px] text-gray-400 mb-0.5">{t('dashboard.initial_weight')}</p>
                                    <p className="text-sm font-semibold text-gray-500 dark:text-gray-400">{initialWeight} kg</p>
                                </div>
                            )}
                            <div>
                                <p className="text-[10px] text-gray-400 mb-0.5">{t('weight_goal.current')}</p>
                                <p className="text-lg font-bold text-gray-800 dark:text-gray-100">{latestWeight !== null ? `${latestWeight} kg` : '—'}</p>
                            </div>
                            <div className="text-right">
                                <p className="text-[10px] text-gray-400 mb-0.5">{t('weight_goal.goal')}</p>
                                <p className="text-lg font-bold text-violet-500 dark:text-violet-400">{weightGoal !== null ? `${weightGoal} kg` : '—'}</p>
                            </div>
                        </div>
                    )}
                    {!editingGoal && latestWeight !== null && weightGoal !== null && (
                        <>
                            <div className="h-2.5 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden">
                                <div
                                    className={`h-full rounded-full transition-all duration-700 ${progressBarColor}`}
                                    style={{ width: `${Math.max(weightProgress, 3)}%` }}
                                />
                            </div>
                            {isGoalClose && (
                                <p className="text-xs text-green-600 dark:text-green-400 mt-1.5 font-medium">Você está próximo da sua meta!</p>
                            )}
                        </>
                    )}
                </div>
            )}

            {/* Bento row 2 — Training + Hydration (2 col) OR 1 col mobile */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 md:gap-4">
                {/* Training tile — weekly schedule */}
                <div className={tile}>
                    <div className="flex items-center gap-2 mb-3">
                        <CalendarDays className="w-4 h-4 text-purple-500" />
                        <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('dashboard.weekly_schedule')}</span>
                    </div>

                    {/* 7-day strip */}
                    <div className="flex gap-1 mb-3">
                        {WEEK_DAYS.map(day => {
                            const isToday = day === todayCode;
                            const hasWorkout = exercises.some(e => e.scheduledDays?.includes(day));
                            return (
                                <div
                                    key={day}
                                    className={`flex-1 flex flex-col items-center py-1.5 rounded-xl text-[9px] font-bold transition-colors ${
                                        isToday
                                            ? 'bg-purple-600 text-white'
                                            : hasWorkout
                                                ? 'bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400'
                                                : 'bg-black/5 dark:bg-white/5 text-gray-400'
                                    }`}
                                >
                                    {WEEK_DAY_LABELS[day]}
                                    <div className={`w-1 h-1 rounded-full mt-0.5 ${hasWorkout ? (isToday ? 'bg-white' : 'bg-purple-500') : 'bg-transparent'}`} />
                                </div>
                            );
                        })}
                    </div>

                    {/* Today's workout */}
                    {todayExercises.length > 0 ? (
                        <div className="mb-3">
                            <p className="text-[10px] text-purple-500 font-semibold uppercase tracking-wide mb-1">{t('dashboard.today_workout')}</p>
                            <p className="text-sm font-semibold text-gray-800 dark:text-gray-100 truncate">{todayExercises[0]?.workoutLabel || todayExercises[0]?.name}</p>
                            <p className="text-xs text-gray-400">{todayExercises.length} {todayExercises.length === 1 ? t('dashboard.exercises_count', { count: todayExercises.length }) : t('dashboard.exercises_count_plural', { count: todayExercises.length })}</p>
                        </div>
                    ) : (
                        <p className="text-xs text-gray-400 mb-3">{t('dashboard.no_today_workout')}</p>
                    )}

                    <Link to="/workouts" className="flex items-center justify-center gap-2 w-full py-2 bg-purple-500/10 text-purple-600 dark:text-purple-400 font-semibold text-xs rounded-xl hover:bg-purple-500/20 active:scale-95 transition-all">
                        <Play className="w-3.5 h-3.5" />
                        {t('dashboard.view_workout')}
                    </Link>
                </div>

                {/* Hydration tile */}
                <WaterTile water={water} onAdd={handleAddWater} onSetGoal={handleSetGoal} />
            </div>

            {/* Bento row 3 — Nutrition macros (full width on mobile, 2/3 on lg) */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-3 md:gap-4">
                <div className={`${tile} lg:col-span-2`}>
                    <div className="flex items-center gap-2 mb-3">
                        <Salad className="w-4 h-4 text-emerald-500" />
                        <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('nutrition.my_diet_title')}</span>
                    </div>
                    <MacroRing protein={totalProtein} carbs={totalCarbs} fat={totalFat} calories={totalCalories} />
                </div>

                {/* Personal Records tile */}
                <div className={`${tile} overflow-hidden`}>
                    <div className="flex items-center gap-2 mb-3">
                        <Trophy className="w-4 h-4 text-yellow-500" />
                        <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('dashboard.personal_records')}</span>
                    </div>
                    {prs.length === 0 ? (
                        <p className="text-xs text-gray-400">{t('dashboard.no_prs')}</p>
                    ) : (
                        <div className="space-y-2 max-h-32 overflow-y-auto">
                            {prs.slice(0, 4).map(pr => (
                                <div key={pr.id} className="flex items-center justify-between gap-2">
                                    <p className="text-xs font-medium text-gray-700 dark:text-gray-300 truncate">{pr.exerciseName}</p>
                                    <p className="text-xs font-bold text-yellow-600 dark:text-yellow-400 shrink-0">{pr.tonnageAchieved.toFixed(0)}kg</p>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Bento row 4 — Training frequency */}
            <div className={tile}>
                <div className="flex items-center gap-2 mb-4">
                    <CalendarDays className="w-4 h-4 text-green-500" />
                    <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest">{t('dashboard.training_frequency')}</h3>
                </div>
                {checkInDates.length === 0 ? (
                    <p className="text-sm text-gray-400">{t('dashboard.no_frequency_data')}</p>
                ) : (
                    <div className="overflow-x-auto">
                        <ContributionGraph dates={checkInDates} />
                    </div>
                )}
            </div>

            {/* Quick actions */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <Link to="/measurements/log" className={`${tile} flex items-center justify-between group hover:border-green-300 dark:hover:border-green-700 !p-4`}>
                    <div className="flex items-center gap-3">
                        <PenLine className="w-5 h-5 text-green-500" />
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('dashboard.log_measurements')}</span>
                    </div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-green-500 transition-colors" />
                </Link>
                <Link to="/measurements" className={`${tile} flex items-center justify-between group hover:border-blue-300 dark:hover:border-blue-700 !p-4`}>
                    <div className="flex items-center gap-3">
                        <Activity className="w-5 h-5 text-blue-500" />
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.measurements')}</span>
                    </div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-blue-500 transition-colors" />
                </Link>
                <Link to="/progress" className={`${tile} flex items-center justify-between group hover:border-violet-300 dark:hover:border-violet-700 !p-4`}>
                    <div className="flex items-center gap-3">
                        <Camera className="w-5 h-5 text-violet-500" />
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.visual_progress')}</span>
                    </div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-violet-500 transition-colors" />
                </Link>
            </div>
        </div>
    );
}

function NutritionistDashboard() {
    const { t } = useTranslation();
    const [patients, setPatients] = useState<Student[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        api.get('/users/my-students/nutritionist').then(r => setPatients(r.data)).catch(() => {}).finally(() => setIsLoading(false));
    }, []);

    if (isLoading) return <div className="p-12 text-center text-gray-400">...</div>;

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className={`${tile} flex items-center gap-4`}>
                    <div className="w-12 h-12 rounded-2xl flex items-center justify-center bg-teal-50/80 dark:bg-teal-900/30">
                        <Users className="w-5 h-5 text-teal-600 dark:text-teal-400" />
                    </div>
                    <div>
                        <p className="text-xs text-gray-400 uppercase tracking-wide mb-0.5">{t('dashboard.student_count')}</p>
                        <p className="text-xl font-bold text-gray-800 dark:text-gray-100">{patients.length}</p>
                    </div>
                </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Link to="/diet/patients" className={`${tile} flex items-center justify-between group hover:border-teal-300 dark:hover:border-teal-700 !p-4`}>
                    <div className="flex items-center gap-3"><Users className="w-5 h-5 text-teal-500" /><span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.my_patients')}</span></div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-teal-500 transition-colors" />
                </Link>
                <Link to="/diet/builder" className={`${tile} flex items-center justify-between group hover:border-blue-300 dark:hover:border-blue-700 !p-4`}>
                    <div className="flex items-center gap-3"><UtensilsCrossed className="w-5 h-5 text-blue-500" /><span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.diet_builder')}</span></div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-blue-500 transition-colors" />
                </Link>
            </div>
        </div>
    );
}

function TrainerDashboard() {
    const { t } = useTranslation();
    const [students, setStudents] = useState<Student[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        api.get('/users/students').then(r => setStudents(r.data)).catch(() => {}).finally(() => setIsLoading(false));
    }, []);

    if (isLoading) return <div className="p-12 text-center text-gray-400">...</div>;

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className={`${tile} flex items-center gap-4`}>
                    <div className="w-12 h-12 rounded-2xl flex items-center justify-center bg-indigo-50/80 dark:bg-indigo-900/30">
                        <Users className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                    </div>
                    <div>
                        <p className="text-xs text-gray-400 uppercase tracking-wide mb-0.5">{t('dashboard.student_count')}</p>
                        <p className="text-xl font-bold text-gray-800 dark:text-gray-100">{students.length}</p>
                    </div>
                </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Link to="/students" className={`${tile} flex items-center justify-between group hover:border-indigo-300 dark:hover:border-indigo-700 !p-4`}>
                    <div className="flex items-center gap-3"><Users className="w-5 h-5 text-indigo-500" /><span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.students')}</span></div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-indigo-500 transition-colors" />
                </Link>
                <Link to="/workouts/new" className={`${tile} flex items-center justify-between group hover:border-blue-300 dark:hover:border-blue-700 !p-4`}>
                    <div className="flex items-center gap-3"><Dumbbell className="w-5 h-5 text-blue-500" /><span className="text-sm font-medium text-gray-700 dark:text-gray-200">{t('sidebar.create_workout')}</span></div>
                    <ChevronRight className="w-4 h-4 text-gray-400 group-hover:text-blue-500 transition-colors" />
                </Link>
            </div>
        </div>
    );
}

export function Dashboard() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const [profile, setProfile] = useState<UserProfile | null>(null);
    const [avatarUrl, setAvatarUrl] = useState<string | null>(null);

    useEffect(() => {
        api.get('/users/me').then(r => setProfile(r.data)).catch(() => {});
        api.get('/users/me/avatar', { responseType: 'blob' })
            .then(r => setAvatarUrl(URL.createObjectURL(r.data)))
            .catch(() => {});
    }, []);

    if (!user) return null;

    const firstName = profile?.fullName.split(' ')[0] ?? '';
    const initials = profile?.fullName
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase() ?? '?';

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0">
                <header className="bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border-b border-black/5 dark:border-white/[0.07] px-5 py-4 sticky top-0 z-20 flex items-center justify-between gap-4">
                    <div className="min-w-0">
                        <p className="text-[10px] font-semibold text-gray-400 uppercase tracking-widest mb-0.5">{t('sidebar.dashboard')}</p>
                        {firstName ? (
                            <h1 className="text-lg font-bold text-gray-900 dark:text-white truncate">
                                {t('dashboard.welcome', { name: firstName })}
                            </h1>
                        ) : (
                            <div className="h-6 w-48 bg-black/5 dark:bg-white/5 rounded-lg animate-pulse" />
                        )}
                    </div>
                    <div className="w-10 h-10 rounded-full overflow-hidden bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center shadow-sm shrink-0">
                        {avatarUrl ? (
                            <img src={avatarUrl} alt="avatar" className="w-full h-full object-cover" />
                        ) : (
                            <span className="text-sm font-bold text-white">{initials}</span>
                        )}
                    </div>
                </header>
                <main className="p-4 md:p-6 max-w-3xl pb-24 md:pb-6">
                    {user.role === 'STUDENT' && <StudentDashboard />}
                    {user.role === 'PERSONALTRAINER' && <TrainerDashboard />}
                    {user.role === 'NUTRITIONIST' && <NutritionistDashboard />}
                </main>
            </div>
        </div>
    );
}
