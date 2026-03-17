import { useEffect, useRef, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
    ArrowLeft, Dumbbell, Apple, Droplets, Camera, Ruler,
    MessageSquare, Send, ChevronDown, ChevronUp, Loader2, FileText,
    Pencil, Trash2,
} from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

type Tab = 'workout' | 'diet' | 'measurements' | 'photos';

interface WorkoutExercise {
    id: string;
    name: string;
    sets: number;
    repetitions: number;
    measurementType: string;
    weightInKg: number | null;
    tonnage: number | null;
    restTime: number | null;
    workoutLabel?: string;
    scheduledDays?: string;
}

interface MealItem {
    id: string;
    mealTime: string;
    name: string;
    caloriesKcal: number;
    proteinG: number;
    carbsG: number;
    fatG: number;
    quantity: number;
    baseUnit: string;
}

interface MealPlan {
    id: string;
    name: string;
    goal: string;
    items: MealItem[];
}

interface WaterIntake {
    dailyWaterGoal: number;
    currentWaterIntake: number;
}

interface Measurement {
    id: string;
    recordedAt: string;
    weight: number | null;
    bodyFatPercentage: number | null;
    chest: number | null;
    waist: number | null;
    abdomen: number | null;
    hips: number | null;
    rightBicep: number | null;
    leftBicep: number | null;
}

interface ProgressPhoto {
    id: string;
    takenAt: string;
    description: string;
    photoBase64: string;
    professionalFeedback: string | null;
    professionalId: string | null;
    professionalName: string | null;
    professionalRole: string | null;
    studentNotes: string | null;
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

const MEAL_ORDER = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

const GOAL_COLORS: Record<string, string> = {
    BULKING: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300',
    CUTTING: 'bg-rose-100 text-rose-700 dark:bg-rose-900/30 dark:text-rose-300',
    MAINTENANCE: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
};

function formatDate(iso: string) {
    return new Date(iso).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}

export function StudentDetailPage() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const { studentId } = useParams<{ studentId: string }>();
    const location = useLocation();
    const navigate = useNavigate();

    const studentName: string = (location.state as any)?.studentName ?? t('student_detail.student');
    const isNutritionist = user?.role === 'NUTRITIONIST';
    const defaultTab: Tab = isNutritionist ? 'diet' : 'workout';

    const [activeTab, setActiveTab] = useState<Tab>(defaultTab);
    const [isLoading, setIsLoading] = useState(false);
    const tabLoadedRef = useRef<Set<Tab>>(new Set());

    const [exercises, setExercises] = useState<WorkoutExercise[]>([]);
    const [mealPlan, setMealPlan] = useState<MealPlan | null>(null);
    const [water, setWater] = useState<WaterIntake | null>(null);
    const [measurements, setMeasurements] = useState<Measurement[]>([]);
    const [photos, setPhotos] = useState<ProgressPhoto[]>([]);

    const [feedbackDraft, setFeedbackDraft] = useState<Record<string, string>>({});
    const [savingFeedback, setSavingFeedback] = useState<string | null>(null);
    const [deletingFeedback, setDeletingFeedback] = useState<string | null>(null);
    const [editingFeedbackId, setEditingFeedbackId] = useState<string | null>(null);
    const [openFeedback, setOpenFeedback] = useState<Record<string, boolean>>({});

    useEffect(() => {
        if (!studentId) return;
        if (tabLoadedRef.current.has(activeTab)) return;
        tabLoadedRef.current.add(activeTab);
        loadTab(activeTab);
    }, [activeTab, studentId]);

    async function loadTab(tab: Tab) {
        setIsLoading(true);
        try {
            if (tab === 'workout') {
                const r = await api.get(`/workouts/student/${studentId}`);
                setExercises(r.data);
            } else if (tab === 'diet') {
                const [planRes, waterRes] = await Promise.allSettled([
                    api.get(`/nutrition/meal-plans/student/${studentId}`),
                    api.get(`/nutrition/water/student/${studentId}`),
                ]);
                if (planRes.status === 'fulfilled') setMealPlan(planRes.value.data);
                if (waterRes.status === 'fulfilled') setWater(waterRes.value.data);
            } else if (tab === 'measurements') {
                const r = await api.get(`/measurements/student/${studentId}`);
                setMeasurements(r.data);
            } else if (tab === 'photos') {
                const r = await api.get(`/progress-photos/student/${studentId}`);
                setPhotos(r.data);
            }
        } catch {
        } finally {
            setIsLoading(false);
        }
    }

    async function handleSendFeedback(photoId: string) {
        const feedback = feedbackDraft[photoId]?.trim();
        if (!feedback) return;
        setSavingFeedback(photoId);
        try {
            const r = await api.patch(`/progress-photos/${photoId}/feedback`, { feedback });
            setPhotos(prev => prev.map(p => p.id === photoId ? r.data : p));
            setFeedbackDraft(prev => ({ ...prev, [photoId]: '' }));
            setEditingFeedbackId(null);
            toast.success(t('progress.feedback_saved'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setSavingFeedback(null);
        }
    }

    async function handleDeleteFeedback(photoId: string) {
        setDeletingFeedback(photoId);
        try {
            const r = await api.delete(`/progress-photos/${photoId}/feedback`);
            setPhotos(prev => prev.map(p => p.id === photoId ? r.data : p));
            toast.success(t('progress.feedback_deleted'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setDeletingFeedback(null);
        }
    }

    function startEditFeedback(photoId: string, currentText: string) {
        setFeedbackDraft(prev => ({ ...prev, [photoId]: currentText }));
        setEditingFeedbackId(photoId);
        setOpenFeedback(prev => ({ ...prev, [photoId]: true }));
    }

    function mealTimeLabel(mt: string): string {
        const map: Record<string, string> = {
            BREAKFAST: t('nutrition.meal_breakfast'),
            LUNCH: t('nutrition.meal_lunch'),
            DINNER: t('nutrition.meal_dinner'),
            SNACK: t('nutrition.meal_snack'),
        };
        return map[mt] ?? mt;
    }

    function goalLabel(goal: string): string {
        const map: Record<string, string> = {
            BULKING: t('nutrition.goal_bulking'),
            CUTTING: t('nutrition.goal_cutting'),
            MAINTENANCE: t('nutrition.goal_maintenance'),
        };
        return map[goal] ?? goal;
    }

    if (!user) return null;

    const tabs: { key: Tab; icon: React.ReactNode; label: string }[] = [
        { key: 'workout', icon: <Dumbbell className="w-4 h-4" />, label: t('student_detail.tab_workout') },
        { key: 'diet', icon: <Apple className="w-4 h-4" />, label: t('student_detail.tab_diet') },
        { key: 'measurements', icon: <Ruler className="w-4 h-4" />, label: t('student_detail.tab_measurements') },
        { key: 'photos', icon: <Camera className="w-4 h-4" />, label: t('student_detail.tab_photos') },
    ];

    const waterPct = water && water.dailyWaterGoal > 0
        ? Math.min((water.currentWaterIntake / water.dailyWaterGoal) * 100, 100)
        : 0;

    const grouped = mealPlan
        ? MEAL_ORDER.reduce((acc, mt) => {
            const items = mealPlan.items.filter(i => i.mealTime === mt);
            if (items.length > 0) acc[mt] = items;
            return acc;
        }, {} as Record<string, MealItem[]>)
        : {};

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0">
                <header className="bg-white/70 dark:bg-slate-800/60 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center gap-3">
                        <button
                            onClick={() => navigate(-1)}
                            className="w-9 h-9 flex items-center justify-center rounded-2xl bg-black/5 dark:bg-white/10 text-gray-600 dark:text-gray-400 hover:bg-black/10 dark:hover:bg-white/15 transition-colors active:scale-95 shrink-0"
                        >
                            <ArrowLeft className="w-4 h-4" />
                        </button>
                        <div>
                            <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100 leading-tight">{studentName}</h2>
                            <p className="text-xs text-gray-400">{t('student_detail.subtitle')}</p>
                        </div>
                    </div>
                </header>

                <div className="bg-white/50 dark:bg-slate-800/40 backdrop-blur-sm border-b border-black/5 dark:border-white/10 px-4 sticky top-[77px] z-10">
                    <div className="flex gap-1 overflow-x-auto scrollbar-none">
                        {tabs.map(tab => (
                            <button
                                key={tab.key}
                                onClick={() => setActiveTab(tab.key)}
                                className={`flex items-center gap-1.5 px-4 py-3.5 text-sm font-medium transition-colors whitespace-nowrap border-b-2 -mb-px ${
                                    activeTab === tab.key
                                        ? 'border-violet-500 text-violet-600 dark:text-violet-400'
                                        : 'border-transparent text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                                }`}
                            >
                                {tab.icon}
                                {tab.label}
                            </button>
                        ))}
                    </div>
                </div>

                <main className="p-4 md:p-6 max-w-2xl space-y-4 pb-24 md:pb-6">
                    {isLoading && (
                        <div className="flex items-center justify-center py-16">
                            <Loader2 className="w-6 h-6 text-gray-400 animate-spin" />
                        </div>
                    )}

                    {/* WORKOUT TAB */}
                    {activeTab === 'workout' && !isLoading && (
                        exercises.length === 0 ? (
                            <div className={`${glassCard} p-10 text-center text-gray-400 text-sm`}>
                                {t('student_detail.no_workout')}
                            </div>
                        ) : (
                            exercises.map((ex, idx) => (
                                <div key={ex.id} className={`${glassCard} p-5`}>
                                    <div className="flex items-start justify-between mb-3">
                                        <div>
                                            <span className="text-xs text-gray-400">#{idx + 1}</span>
                                            <h3 className="text-sm font-semibold text-gray-900 dark:text-gray-100 mt-0.5">{ex.name}</h3>
                                            {ex.workoutLabel && (
                                                <span className="text-xs text-violet-500 dark:text-violet-400">{ex.workoutLabel}</span>
                                            )}
                                        </div>
                                        {ex.tonnage != null && ex.tonnage > 0 && (
                                            <div className="bg-purple-50/80 dark:bg-purple-900/30 text-purple-600 dark:text-purple-300 text-xs font-semibold px-3 py-1 rounded-full shrink-0">
                                                {ex.tonnage.toFixed(1)} kg
                                            </div>
                                        )}
                                    </div>
                                    <div className="grid grid-cols-3 gap-2">
                                        <div className="bg-blue-50/60 dark:bg-blue-900/20 rounded-2xl p-2.5 text-center">
                                            <p className="text-[10px] text-blue-400 uppercase tracking-wide mb-0.5">{t('workout_view.sets')}</p>
                                            <p className="text-base font-bold text-blue-600 dark:text-blue-400">{ex.sets}</p>
                                        </div>
                                        <div className="bg-green-50/60 dark:bg-green-900/20 rounded-2xl p-2.5 text-center">
                                            <p className="text-[10px] text-green-400 uppercase tracking-wide mb-0.5">{t('workout_view.repetitions')}</p>
                                            <p className="text-base font-bold text-green-600 dark:text-green-400">{ex.repetitions}</p>
                                        </div>
                                        <div className="bg-orange-50/60 dark:bg-orange-900/20 rounded-2xl p-2.5 text-center">
                                            <p className="text-[10px] text-orange-400 uppercase tracking-wide mb-0.5">{t('workout_view.weight')}</p>
                                            <p className="text-base font-bold text-orange-600 dark:text-orange-400">{ex.weightInKg ?? '—'}</p>
                                        </div>
                                    </div>
                                    {ex.scheduledDays && (
                                        <p className="mt-2 text-xs text-gray-400">{ex.scheduledDays}</p>
                                    )}
                                </div>
                            ))
                        )
                    )}

                    {/* DIET TAB */}
                    {activeTab === 'diet' && !isLoading && (
                        <>
                            {water && (
                                <div className={`${glassCard} p-5`}>
                                    <div className="flex items-center gap-2 mb-3">
                                        <Droplets className="w-4 h-4 text-blue-400" />
                                        <span className="text-sm font-semibold text-gray-800 dark:text-gray-100">{t('water.title')}</span>
                                    </div>
                                    <div className="flex items-end justify-between mb-2">
                                        <span className="text-xl font-bold text-blue-400">{water.currentWaterIntake}ml</span>
                                        <span className="text-sm text-gray-400 pb-0.5">
                                            {water.dailyWaterGoal > 0 ? `/ ${water.dailyWaterGoal}ml` : t('water.no_goal')}
                                        </span>
                                    </div>
                                    <div className="h-2.5 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden">
                                        <div
                                            className="h-full bg-blue-400 rounded-full transition-all duration-500"
                                            style={{ width: `${waterPct}%` }}
                                        />
                                    </div>
                                    {water.dailyWaterGoal > 0 && (
                                        <p className="text-[11px] text-gray-400 text-center mt-1">{waterPct.toFixed(0)}%</p>
                                    )}
                                </div>
                            )}

                            {!mealPlan ? (
                                <div className={`${glassCard} p-10 text-center text-gray-400 text-sm`}>
                                    {t('student_detail.no_diet')}
                                </div>
                            ) : (
                                <>
                                    <div className={`${glassCard} p-5`}>
                                        <div className="flex items-start justify-between mb-4">
                                            <div>
                                                <p className="text-xs text-gray-400 mb-0.5">{t('nutrition.my_diet_title')}</p>
                                                <h3 className="text-base font-bold text-gray-900 dark:text-gray-100">{mealPlan.name}</h3>
                                            </div>
                                            {mealPlan.goal && (
                                                <span className={`text-xs font-semibold px-3 py-1 rounded-full ${GOAL_COLORS[mealPlan.goal] ?? 'bg-gray-100 text-gray-600'}`}>
                                                    {goalLabel(mealPlan.goal)}
                                                </span>
                                            )}
                                        </div>
                                        <div className="grid grid-cols-4 gap-2 text-center">
                                            <div>
                                                <p className="text-xs text-gray-400">{t('nutrition.total_calories')}</p>
                                                <p className="text-lg font-bold text-emerald-500">{mealPlan.items.reduce((s, i) => s + i.caloriesKcal, 0).toFixed(0)}</p>
                                                <p className="text-[10px] text-gray-400">kcal</p>
                                            </div>
                                            <div>
                                                <p className="text-xs text-gray-400">{t('nutrition.protein')}</p>
                                                <p className="text-lg font-bold text-rose-500">{mealPlan.items.reduce((s, i) => s + i.proteinG, 0).toFixed(1)}</p>
                                                <p className="text-[10px] text-gray-400">g</p>
                                            </div>
                                            <div>
                                                <p className="text-xs text-gray-400">{t('nutrition.carbs')}</p>
                                                <p className="text-lg font-bold text-blue-500">{mealPlan.items.reduce((s, i) => s + i.carbsG, 0).toFixed(1)}</p>
                                                <p className="text-[10px] text-gray-400">g</p>
                                            </div>
                                            <div>
                                                <p className="text-xs text-gray-400">{t('nutrition.fat')}</p>
                                                <p className="text-lg font-bold text-amber-500">{mealPlan.items.reduce((s, i) => s + i.fatG, 0).toFixed(1)}</p>
                                                <p className="text-[10px] text-gray-400">g</p>
                                            </div>
                                        </div>
                                    </div>

                                    {Object.entries(grouped).map(([mealTime, items]) => (
                                        <div key={mealTime} className={`${glassCard} overflow-hidden`}>
                                            <div className="px-5 py-4 border-b border-black/5 dark:border-white/5">
                                                <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">{mealTimeLabel(mealTime)}</p>
                                            </div>
                                            <div className="divide-y divide-black/5 dark:divide-white/5">
                                                {items.map(item => (
                                                    <div key={item.id} className="px-5 py-3 flex items-center justify-between gap-3">
                                                        <div className="flex-1 min-w-0">
                                                            <p className="text-sm font-medium text-gray-800 dark:text-gray-100 truncate">{item.name}</p>
                                                            <p className="text-xs text-gray-400 mt-0.5">
                                                                {item.quantity != null ? `${item.quantity}${item.baseUnit ?? 'g'} · ` : ''}
                                                                <span className="text-rose-500">{item.proteinG.toFixed(1)}g P</span>
                                                                {' · '}
                                                                <span className="text-blue-500">{item.carbsG.toFixed(1)}g C</span>
                                                                {' · '}
                                                                <span className="text-amber-500">{item.fatG.toFixed(1)}g F</span>
                                                            </p>
                                                        </div>
                                                        <div className="text-right shrink-0">
                                                            <p className="text-sm font-bold text-emerald-500">{item.caloriesKcal.toFixed(0)}</p>
                                                            <p className="text-[10px] text-gray-400">kcal</p>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    ))}
                                </>
                            )}
                        </>
                    )}

                    {/* MEASUREMENTS TAB */}
                    {activeTab === 'measurements' && !isLoading && (
                        measurements.length === 0 ? (
                            <div className={`${glassCard} p-10 text-center text-gray-400 text-sm`}>
                                {t('student_detail.no_measurements')}
                            </div>
                        ) : (
                            <>
                                {(() => {
                                    const latest = [...measurements].reverse()[0];
                                    const rows = [
                                        { label: t('measurements.weight'), value: latest.weight, unit: 'kg' },
                                        { label: t('measurements.body_fat'), value: latest.bodyFatPercentage, unit: '%' },
                                        { label: t('measurements.chest'), value: latest.chest, unit: 'cm' },
                                        { label: t('measurements.waist'), value: latest.waist, unit: 'cm' },
                                        { label: t('measurements.abdomen'), value: latest.abdomen, unit: 'cm' },
                                        { label: t('measurements.hips'), value: latest.hips, unit: 'cm' },
                                        { label: t('measurements.right_bicep'), value: latest.rightBicep, unit: 'cm' },
                                        { label: t('measurements.left_bicep'), value: latest.leftBicep, unit: 'cm' },
                                    ].filter(row => row.value != null);
                                    return (
                                        <div className={`${glassCard} p-5`}>
                                            <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-3">
                                                {t('student_detail.latest_assessment')} — {latest.recordedAt}
                                            </p>
                                            <div className="grid grid-cols-2 gap-3">
                                                {rows.map(row => (
                                                    <div key={row.label} className="bg-black/[0.02] dark:bg-white/5 rounded-2xl p-3">
                                                        <p className="text-xs text-gray-400">{row.label}</p>
                                                        <p className="text-lg font-bold text-gray-900 dark:text-gray-100">
                                                            {row.value}
                                                            <span className="text-xs text-gray-400 font-normal ml-1">{row.unit}</span>
                                                        </p>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    );
                                })()}

                                <div className={`${glassCard} overflow-hidden`}>
                                    <div className="overflow-x-auto">
                                        <table className="w-full text-left">
                                            <thead>
                                                <tr className="border-b border-black/5 dark:border-white/10">
                                                    {[
                                                        t('measurements.date'),
                                                        t('measurements.weight'),
                                                        t('measurements.body_fat'),
                                                        t('measurements.waist'),
                                                        t('measurements.chest'),
                                                    ].map(h => (
                                                        <th key={h} className="px-4 py-3 text-xs font-semibold text-gray-400 uppercase tracking-wide whitespace-nowrap">{h}</th>
                                                    ))}
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {[...measurements].reverse().map((m, i) => (
                                                    <tr key={m.id} className={i % 2 !== 0 ? 'bg-black/[0.02] dark:bg-white/5' : ''}>
                                                        <td className="px-4 py-3 text-sm font-medium text-gray-700 dark:text-gray-200 whitespace-nowrap">{m.recordedAt}</td>
                                                        <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{m.weight ?? '—'}</td>
                                                        <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{m.bodyFatPercentage ?? '—'}</td>
                                                        <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{m.waist ?? '—'}</td>
                                                        <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{m.chest ?? '—'}</td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </>
                        )
                    )}

                    {/* PHOTOS TAB */}
                    {activeTab === 'photos' && !isLoading && (
                        photos.length === 0 ? (
                            <div className={`${glassCard} p-10 text-center text-gray-400 text-sm`}>
                                {t('student_detail.no_photos')}
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                {photos.map(photo => {
                                    const isOpen = openFeedback[photo.id] ?? false;
                                    const draft = feedbackDraft[photo.id] ?? '';
                                    const hasNotes = !!photo.studentNotes;
                                    const hasFeedback = !!photo.professionalFeedback;
                                    return (
                                        <div key={photo.id} className="rounded-3xl border border-black/5 dark:border-white/[0.07] bg-white dark:bg-slate-800 shadow-sm">
                                            {/* Image wrapper — own overflow context so it doesn't clip the panel below */}
                                            <div className="rounded-t-2xl overflow-hidden relative" style={{ aspectRatio: '3 / 4' }}>
                                                <img
                                                    src={`data:image/jpeg;base64,${photo.photoBase64}`}
                                                    alt={photo.description || formatDate(photo.takenAt)}
                                                    className="w-full h-full object-cover"
                                                />
                                                <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-3">
                                                    <p className="text-xs font-semibold text-white">{formatDate(photo.takenAt)}</p>
                                                    {photo.description && (
                                                        <p className="text-[11px] text-white/70 truncate mt-0.5">{photo.description}</p>
                                                    )}
                                                </div>
                                            </div>

                                            {/* Toggle button */}
                                            <div className="border-t border-black/5 dark:border-white/[0.06]">
                                                <button
                                                    onClick={() => setOpenFeedback(prev => ({ ...prev, [photo.id]: !isOpen }))}
                                                    className="w-full flex items-center justify-between px-4 py-2.5 text-xs font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-colors"
                                                >
                                                    <span className="flex items-center gap-2">
                                                        <MessageSquare className="w-3.5 h-3.5 shrink-0" />
                                                        <span>{hasFeedback ? t('progress.feedback_label') : t('progress.feedback_none')}</span>
                                                        {hasNotes && <span className="w-2 h-2 rounded-full bg-amber-400 shrink-0" title={t('progress.student_notes_label')} />}
                                                        {hasFeedback && <span className="w-2 h-2 rounded-full bg-violet-500 shrink-0" />}
                                                    </span>
                                                    {isOpen ? <ChevronUp className="w-3.5 h-3.5 shrink-0" /> : <ChevronDown className="w-3.5 h-3.5 shrink-0" />}
                                                </button>

                                                {isOpen && (
                                                    <div className="px-4 pb-4 space-y-3">
                                                        {/* Student notes — shown FIRST so professional sees the request before responding */}
                                                        {hasNotes && (
                                                            <div className="rounded-2xl bg-amber-50 dark:bg-amber-950/50 border border-amber-200/70 dark:border-amber-700/40 p-3">
                                                                <div className="flex items-center gap-1.5 mb-1.5">
                                                                    <FileText className="w-3.5 h-3.5 text-amber-500 shrink-0" />
                                                                    <p className="text-[11px] font-bold text-amber-600 dark:text-amber-400 uppercase tracking-wide">
                                                                        {t('progress.student_notes_label')}
                                                                    </p>
                                                                </div>
                                                                <p className="text-sm text-amber-900 dark:text-amber-100 leading-relaxed break-words">
                                                                    {photo.studentNotes}
                                                                </p>
                                                            </div>
                                                        )}

                                                        {/* Professional feedback bubble */}
                                                        {hasFeedback && editingFeedbackId !== photo.id && (
                                                            <div className="rounded-2xl bg-violet-50 dark:bg-violet-950/60 border border-violet-100 dark:border-violet-800/40 p-3">
                                                                <div className="flex items-start justify-between gap-2 mb-2">
                                                                    <div className="flex items-center gap-2 flex-wrap">
                                                                        <p className="text-[11px] font-bold text-violet-600 dark:text-violet-400">{photo.professionalName}</p>
                                                                        {photo.professionalRole === 'NUTRITIONIST' ? (
                                                                            <span className="flex items-center gap-1 px-1.5 py-0.5 bg-green-100 dark:bg-green-900/40 text-green-700 dark:text-green-300 text-[10px] font-bold rounded-full">
                                                                                <Apple className="w-2.5 h-2.5" />
                                                                                Nutri
                                                                            </span>
                                                                        ) : photo.professionalRole === 'PERSONALTRAINER' ? (
                                                                            <span className="flex items-center gap-1 px-1.5 py-0.5 bg-blue-100 dark:bg-blue-900/40 text-blue-700 dark:text-blue-300 text-[10px] font-bold rounded-full">
                                                                                <Dumbbell className="w-2.5 h-2.5" />
                                                                                Personal
                                                                            </span>
                                                                        ) : null}
                                                                    </div>
                                                                    {/* Edit / delete — only visible to the comment author */}
                                                                    {user.id && photo.professionalId === user.id && (
                                                                        <div className="flex items-center gap-1 shrink-0">
                                                                            <button
                                                                                onClick={() => startEditFeedback(photo.id, photo.professionalFeedback!)}
                                                                                className="w-6 h-6 flex items-center justify-center rounded-lg text-violet-400 hover:text-violet-600 hover:bg-violet-100 dark:hover:bg-violet-900/40 transition-colors"
                                                                                title={t('progress.edit_feedback')}
                                                                            >
                                                                                <Pencil className="w-3 h-3" />
                                                                            </button>
                                                                            <button
                                                                                onClick={() => handleDeleteFeedback(photo.id)}
                                                                                disabled={deletingFeedback === photo.id}
                                                                                className="w-6 h-6 flex items-center justify-center rounded-lg text-red-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30 transition-colors disabled:opacity-50"
                                                                                title={t('progress.delete_feedback')}
                                                                            >
                                                                                {deletingFeedback === photo.id
                                                                                    ? <Loader2 className="w-3 h-3 animate-spin" />
                                                                                    : <Trash2 className="w-3 h-3" />}
                                                                            </button>
                                                                        </div>
                                                                    )}
                                                                </div>
                                                                <p className="text-sm text-gray-700 dark:text-gray-200 leading-relaxed break-words">
                                                                    {photo.professionalFeedback}
                                                                </p>
                                                            </div>
                                                        )}

                                                        {/* Feedback input — shown when writing new or editing existing */}
                                                        {(!hasFeedback || editingFeedbackId === photo.id) && (
                                                            <div className="flex items-center gap-2 pt-1">
                                                                {isNutritionist ? (
                                                                    <span className="flex items-center gap-1 px-2 py-1 bg-green-100 dark:bg-green-900/40 text-green-700 dark:text-green-300 text-[10px] font-bold rounded-full shrink-0">
                                                                        <Apple className="w-3 h-3" />
                                                                        Nutri
                                                                    </span>
                                                                ) : (
                                                                    <span className="flex items-center gap-1 px-2 py-1 bg-blue-100 dark:bg-blue-900/40 text-blue-700 dark:text-blue-300 text-[10px] font-bold rounded-full shrink-0">
                                                                        <Dumbbell className="w-3 h-3" />
                                                                        Personal
                                                                    </span>
                                                                )}
                                                                <input
                                                                    type="text"
                                                                    value={draft}
                                                                    onChange={e => setFeedbackDraft(prev => ({ ...prev, [photo.id]: e.target.value }))}
                                                                    placeholder={t('progress.feedback_placeholder')}
                                                                    className="flex-1 min-w-0 px-3 py-2 text-sm border border-black/10 dark:border-white/10 rounded-2xl bg-white dark:bg-slate-900 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:ring-2 focus:ring-violet-500 outline-none"
                                                                    onKeyDown={e => e.key === 'Enter' && !e.shiftKey && handleSendFeedback(photo.id)}
                                                                    autoFocus={editingFeedbackId === photo.id}
                                                                />
                                                                {editingFeedbackId === photo.id && (
                                                                    <button
                                                                        onClick={() => { setEditingFeedbackId(null); setFeedbackDraft(prev => ({ ...prev, [photo.id]: '' })); }}
                                                                        className="w-9 h-9 flex items-center justify-center rounded-2xl bg-gray-200 dark:bg-slate-700 text-gray-600 dark:text-gray-300 hover:bg-gray-300 dark:hover:bg-slate-600 transition-all shrink-0 text-xs font-bold"
                                                                        title={t('common.cancel')}
                                                                    >
                                                                        ✕
                                                                    </button>
                                                                )}
                                                                <button
                                                                    onClick={() => handleSendFeedback(photo.id)}
                                                                    disabled={savingFeedback === photo.id || !draft.trim()}
                                                                    className="w-9 h-9 flex items-center justify-center rounded-2xl bg-violet-600 text-white hover:bg-violet-700 disabled:opacity-50 active:scale-90 transition-all shrink-0"
                                                                >
                                                                    {savingFeedback === photo.id
                                                                        ? <Loader2 className="w-4 h-4 animate-spin" />
                                                                        : <Send className="w-4 h-4" />}
                                                                </button>
                                                            </div>
                                                        )}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )
                    )}
                </main>
            </div>
        </div>
    );
}
