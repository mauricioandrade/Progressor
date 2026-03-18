import { useEffect, useRef, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
    ArrowLeft, Dumbbell, Apple, Droplets, Camera, Ruler,
    MessageSquare, Send, ChevronDown, ChevronUp, Loader2, FileText,
    Pencil, Trash2, ExternalLink, Play, Target, Check, X, History, Plus,
} from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

type Tab = 'workout' | 'diet' | 'measurements' | 'photos' | 'history';

interface WorkoutExercise {
    id: string;
    name: string;
    sets: number;
    repetitions: number;
    measurementType: string;
    weightInKg: number | null;
    tonnage: number | null;
    videoUrl: string | null;
    restTime: number | null;
    workoutLabel?: string;
    scheduledDays?: string;
}

interface MealItem {
    id: string;
    mealTime: string;
    name: string;
    foodDescription: string;
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
    cheatMeal: boolean;
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

interface MealPlanSummary {
    id: string;
    name: string;
    goal: string;
    cheatMeal: boolean;
}

interface EditableItem {
    tempId: string;
    mealTime: string;
    name: string;
    foodDescription: string;
    caloriesKcal: number | string;
    proteinG: number | string;
    carbsG: number | string;
    fatG: number | string;
    quantity: number | string;
    baseUnit: string;
    // Per-unit ratios (stored on load / confirm) for proportional macro recalculation
    kcalPerUnit: number;
    protPerUnit: number;
    carbPerUnit: number;
    fatPerUnit: number;
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

function getEmbedUrl(url: string): string | null {
    const ytMatch = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&\n?#]+)/);
    if (ytMatch) return `https://www.youtube.com/embed/${ytMatch[1]}?autoplay=1`;
    const vimeoMatch = url.match(/vimeo\.com\/(\d+)/);
    if (vimeoMatch) return `https://player.vimeo.com/video/${vimeoMatch[1]}?autoplay=1`;
    const driveMatch = url.match(/drive\.google\.com\/file\/d\/([^/]+)/);
    if (driveMatch) return `https://drive.google.com/file/d/${driveMatch[1]}/preview`;
    return null;
}

const DAY_PT: Record<string, string> = {
    MON: 'SEG', TUE: 'TER', WED: 'QUA', THU: 'QUI', FRI: 'SEX', SAT: 'SÁB', SUN: 'DOM',
};

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

    const [deletingExerciseId, setDeletingExerciseId] = useState<string | null>(null);
    const [editExerciseModal, setEditExerciseModal] = useState<WorkoutExercise | null>(null);
    const [editExerciseForm, setEditExerciseForm] = useState<Partial<WorkoutExercise> & { scheduledDaysText?: string }>({});
    const [isSavingExercise, setIsSavingExercise] = useState(false);
    const [expandedVideoId, setExpandedVideoId] = useState<string | null>(null);

    const [weightGoal, setWeightGoal] = useState<number | null>(null);
    const [editingWeightGoal, setEditingWeightGoal] = useState(false);
    const [weightGoalInput, setWeightGoalInput] = useState('');
    const [isSavingWeightGoal, setIsSavingWeightGoal] = useState(false);

    const [mealPlanHistory, setMealPlanHistory] = useState<MealPlanSummary[]>([]);
    const [checkinHistory, setCheckinHistory] = useState<string[]>([]);
    const [editMealPlanModal, setEditMealPlanModal] = useState<{ id: string; name: string; goal: string; cheatMeal: boolean } | null>(null);
    const [editMealPlanForm, setEditMealPlanForm] = useState<{ name: string; goal: string; cheatMeal: boolean; items: EditableItem[] }>({ name: '', goal: 'BULKING', cheatMeal: false, items: [] });
    const [isSavingMealPlan, setIsSavingMealPlan] = useState(false);
    const [isFetchingPlanForEdit, setIsFetchingPlanForEdit] = useState(false);
    const [deletingMealPlanId, setDeletingMealPlanId] = useState<string | null>(null);
    const [newMealTimeSelect, setNewMealTimeSelect] = useState<string | null>(null);
    const [mealDrafts, setMealDrafts] = useState<Record<string, EditableItem>>({});
    const [openAddForms, setOpenAddForms] = useState<Set<string>>(new Set());
    const [editingItemTempId, setEditingItemTempId] = useState<string | null>(null);

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
                const [exercisesRes, goalRes] = await Promise.allSettled([
                    api.get(`/workouts/student/${studentId}`),
                    api.get(`/measurements/weight-goal/student/${studentId}`),
                ]);
                if (exercisesRes.status === 'fulfilled') setExercises(exercisesRes.value.data);
                if (goalRes.status === 'fulfilled') setWeightGoal(goalRes.value.data.weightGoal ?? null);
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
            } else if (tab === 'history') {
                const [historyRes, checkinsRes] = await Promise.allSettled([
                    api.get(`/nutrition/meal-plans/history/${studentId}`),
                    api.get(`/checkins/student/${studentId}`),
                ]);
                if (historyRes.status === 'fulfilled') setMealPlanHistory(historyRes.value.data);
                if (checkinsRes.status === 'fulfilled') setCheckinHistory(checkinsRes.value.data);
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

    async function handleSaveWeightGoal() {
        const val = parseFloat(weightGoalInput);
        if (!val || val <= 0 || !studentId) return;
        setIsSavingWeightGoal(true);
        try {
            await api.put(`/measurements/weight-goal/${studentId}`, { goal: val });
            setWeightGoal(val);
            setEditingWeightGoal(false);
            toast.success(t('toast.weight_goal_saved'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsSavingWeightGoal(false);
        }
    }

    async function openEditMealPlanModal(plan: { id: string; name: string; goal: string; cheatMeal: boolean }) {
        setEditMealPlanModal(plan);
        setEditMealPlanForm({ name: plan.name, goal: plan.goal, cheatMeal: plan.cheatMeal, items: [] });
        setNewMealTimeSelect(null);
        setMealDrafts({});
        setOpenAddForms(new Set());
        setEditingItemTempId(null);
        setIsFetchingPlanForEdit(true);
        try {
            const res = await api.get(`/nutrition/meal-plans/student/${studentId}`);
            const fresh: MealPlan = res.data;
            setMealPlan(fresh);
            const items: EditableItem[] = (fresh.items ?? []).map(item => ({
                tempId: item.id,
                mealTime: item.mealTime,
                name: item.name,
                foodDescription: item.foodDescription ?? '',
                caloriesKcal: item.caloriesKcal,
                proteinG: item.proteinG,
                carbsG: item.carbsG,
                fatG: item.fatG,
                quantity: item.quantity,
                baseUnit: item.baseUnit,
                ...perUnitRatios(item.quantity, item.caloriesKcal, item.proteinG, item.carbsG, item.fatG),
            }));
            setEditMealPlanForm({ name: fresh.name, goal: fresh.goal, cheatMeal: fresh.cheatMeal, items });
        } catch {
            // fall back to cached state
            const items: EditableItem[] = (mealPlan?.items ?? []).map(item => ({
                tempId: item.id,
                mealTime: item.mealTime,
                name: item.name,
                foodDescription: item.foodDescription ?? '',
                caloriesKcal: item.caloriesKcal,
                proteinG: item.proteinG,
                carbsG: item.carbsG,
                fatG: item.fatG,
                quantity: item.quantity,
                baseUnit: item.baseUnit,
                ...perUnitRatios(item.quantity, item.caloriesKcal, item.proteinG, item.carbsG, item.fatG),
            }));
            setEditMealPlanForm({ name: plan.name, goal: plan.goal, cheatMeal: plan.cheatMeal, items });
        } finally {
            setIsFetchingPlanForEdit(false);
        }
    }

    function emptyDraftItem(mealTime: string): EditableItem {
        return { tempId: '', mealTime, name: '', foodDescription: '', caloriesKcal: '', proteinG: '', carbsG: '', fatG: '', quantity: '', baseUnit: 'g', kcalPerUnit: 0, protPerUnit: 0, carbPerUnit: 0, fatPerUnit: 0 };
    }

    function perUnitRatios(qty: number | string, kcal: number | string, prot: number | string, carb: number | string, fat: number | string) {
        const q = Number(qty) || 0;
        if (q === 0) return { kcalPerUnit: 0, protPerUnit: 0, carbPerUnit: 0, fatPerUnit: 0 };
        return {
            kcalPerUnit: (Number(kcal) || 0) / q,
            protPerUnit: (Number(prot) || 0) / q,
            carbPerUnit: (Number(carb) || 0) / q,
            fatPerUnit:  (Number(fat)  || 0) / q,
        };
    }

    function openMealAddForm(mt: string) {
        setOpenAddForms(prev => new Set([...prev, mt]));
        setMealDrafts(prev => ({ ...prev, [mt]: prev[mt] ?? emptyDraftItem(mt) }));
        setNewMealTimeSelect(null);
    }

    function closeMealAddForm(mt: string) {
        setOpenAddForms(prev => { const s = new Set(prev); s.delete(mt); return s; });
        setMealDrafts(prev => { const d = { ...prev }; delete d[mt]; return d; });
    }

    function updateDraftField(mt: string, field: keyof EditableItem, value: string) {
        setMealDrafts(prev => ({ ...prev, [mt]: { ...(prev[mt] ?? emptyDraftItem(mt)), [field]: value } }));
    }

    function confirmDraftItem(mt: string) {
        const draft = mealDrafts[mt];
        if (!draft?.name?.trim()) return;
        const ratios = perUnitRatios(draft.quantity, draft.caloriesKcal, draft.proteinG, draft.carbsG, draft.fatG);
        setEditMealPlanForm(f => ({
            ...f,
            items: [...f.items, { ...draft, tempId: Math.random().toString(36).slice(2), ...ratios }],
        }));
        setMealDrafts(prev => ({ ...prev, [mt]: emptyDraftItem(mt) }));
    }

    function updateEditItem(tempId: string, field: keyof EditableItem, value: string | number) {
        setEditMealPlanForm(f => ({
            ...f,
            items: f.items.map(i => {
                if (i.tempId !== tempId) return i;
                const updated = { ...i, [field]: value };
                // When quantity changes and we have per-unit ratios, recalculate macros proportionally
                if (field === 'quantity') {
                    const qty = Number(value) || 0;
                    const hasRatios = i.kcalPerUnit > 0 || i.protPerUnit > 0 || i.carbPerUnit > 0 || i.fatPerUnit > 0;
                    if (hasRatios && qty > 0) {
                        updated.caloriesKcal = parseFloat((qty * i.kcalPerUnit).toFixed(1));
                        updated.proteinG     = parseFloat((qty * i.protPerUnit).toFixed(1));
                        updated.carbsG       = parseFloat((qty * i.carbPerUnit).toFixed(1));
                        updated.fatG         = parseFloat((qty * i.fatPerUnit).toFixed(1));
                    }
                }
                return updated;
            }),
        }));
    }

    function removeEditItem(tempId: string) {
        setEditMealPlanForm(f => ({ ...f, items: f.items.filter(i => i.tempId !== tempId) }));
    }

    async function handleSaveMealPlan() {
        if (!editMealPlanModal) return;
        setIsSavingMealPlan(true);
        try {
            const payload = {
                name: editMealPlanForm.name,
                goal: editMealPlanForm.goal,
                cheatMeal: editMealPlanForm.cheatMeal,
                items: editMealPlanForm.items.map(i => ({
                    mealTime: i.mealTime,
                    name: i.name,
                    foodDescription: i.foodDescription,
                    caloriesKcal: Number(i.caloriesKcal) || 0,
                    proteinG: Number(i.proteinG) || 0,
                    carbsG: Number(i.carbsG) || 0,
                    fatG: Number(i.fatG) || 0,
                    quantity: Number(i.quantity) || 0,
                    baseUnit: i.baseUnit || 'g',
                })),
            };
            await api.put(`/nutrition/meal-plans/${editMealPlanModal.id}`, payload);
            const res = await api.get(`/nutrition/meal-plans/student/${studentId}`);
            setMealPlan(res.data);
            setEditMealPlanModal(null);
            toast.success(t('toast.meal_plan_updated'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsSavingMealPlan(false);
        }
    }

    async function handleDeleteMealPlan(id: string) {
        if (!confirm(t('meal_plan.edit_title') + '?')) return;
        setDeletingMealPlanId(id);
        try {
            await api.delete(`/nutrition/meal-plans/${id}`);
            setMealPlan(null);
            toast.success(t('toast.meal_plan_deleted'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setDeletingMealPlanId(null);
        }
    }

    async function handleDeleteExercise(id: string) {
        if (!confirm(t('edit_exercise.title') + '?')) return;
        setDeletingExerciseId(id);
        try {
            await api.delete(`/workouts/${id}`);
            setExercises(prev => prev.filter(e => e.id !== id));
            toast.success(t('toast.exercise_deleted'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setDeletingExerciseId(null);
        }
    }

    function openEditExerciseModal(ex: WorkoutExercise) {
        setEditExerciseModal(ex);
        setEditExerciseForm({ ...ex, scheduledDaysText: ex.scheduledDays ?? '' });
    }

    async function handleSaveExercise() {
        if (!editExerciseModal) return;
        setIsSavingExercise(true);
        try {
            const scheduledDays = editExerciseForm.scheduledDaysText
                ? editExerciseForm.scheduledDaysText.split(',').map(d => d.trim()).filter(Boolean)
                : [];
            const { data } = await api.put(`/workouts/${editExerciseModal.id}`, {
                name: editExerciseForm.name,
                sets: Number(editExerciseForm.sets),
                repetitions: Number(editExerciseForm.repetitions),
                measurementType: editExerciseForm.measurementType,
                weightInKg: editExerciseForm.weightInKg ?? null,
                videoUrl: editExerciseForm.videoUrl ?? null,
                restTime: editExerciseForm.restTime ?? null,
                workoutLabel: editExerciseForm.workoutLabel ?? null,
                scheduledDays: scheduledDays.length > 0 ? scheduledDays : null,
            });
            setExercises(prev => prev.map(e => e.id === editExerciseModal.id ? data : e));
            setEditExerciseModal(null);
            toast.success(t('toast.exercise_updated'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsSavingExercise(false);
        }
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
        { key: 'history', icon: <History className="w-4 h-4" />, label: t('student_detail.tab_history') },
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

    const inputClass = 'w-full border border-black/10 dark:border-white/10 bg-white/50 dark:bg-white/5 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500';

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />

            {editExerciseModal && (
                <div
                    className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-4"
                    onClick={() => !isSavingExercise && setEditExerciseModal(null)}
                >
                    <div
                        className="w-full max-w-sm bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-black/5 dark:border-white/10 p-6 space-y-3"
                        onClick={e => e.stopPropagation()}
                    >
                        <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">{t('edit_exercise.title')}</h3>
                        <div className="space-y-2">
                            <input className={inputClass} placeholder={t('edit_exercise.name')} value={editExerciseForm.name ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, name: e.target.value }))} />
                            <div className="grid grid-cols-2 gap-2">
                                <input className={inputClass} type="number" min={1} placeholder={t('edit_exercise.sets')} value={editExerciseForm.sets ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, sets: Number(e.target.value) }))} />
                                <input className={inputClass} type="number" min={1} placeholder={t('edit_exercise.reps')} value={editExerciseForm.repetitions ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, repetitions: Number(e.target.value) }))} />
                            </div>
                            <select className={inputClass} value={editExerciseForm.measurementType ?? 'WEIGHT'} onChange={e => setEditExerciseForm(f => ({ ...f, measurementType: e.target.value }))}>
                                <option value="WEIGHT">{t('edit_exercise.type_weight')}</option>
                                <option value="BODYWEIGHT">{t('edit_exercise.type_bodyweight')}</option>
                                <option value="SPEED">{t('edit_exercise.type_speed')}</option>
                                <option value="TIME">{t('edit_exercise.type_time')}</option>
                            </select>
                            <input className={inputClass} type="number" min={0} step={0.5} placeholder={t('edit_exercise.weight')} value={editExerciseForm.weightInKg ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, weightInKg: e.target.value ? Number(e.target.value) : null }))} />
                            <input className={inputClass} placeholder={t('edit_exercise.video_url')} value={editExerciseForm.videoUrl ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, videoUrl: e.target.value }))} />
                            <input className={inputClass} type="number" min={0} placeholder={t('edit_exercise.rest_time')} value={editExerciseForm.restTime ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, restTime: e.target.value ? Number(e.target.value) : null }))} />
                            <input className={inputClass} placeholder={t('edit_exercise.label')} value={editExerciseForm.workoutLabel ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, workoutLabel: e.target.value }))} />
                            <input className={inputClass} placeholder={t('edit_exercise.days')} value={editExerciseForm.scheduledDaysText ?? ''} onChange={e => setEditExerciseForm(f => ({ ...f, scheduledDaysText: e.target.value }))} />
                        </div>
                        <div className="flex gap-2 pt-1">
                            <button onClick={() => setEditExerciseModal(null)} className="flex-1 py-2.5 rounded-2xl text-sm font-medium bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95">
                                {t('common.cancel')}
                            </button>
                            <button onClick={handleSaveExercise} disabled={isSavingExercise || !editExerciseForm.name} className="flex-1 py-2.5 rounded-2xl text-sm font-semibold bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95">
                                {isSavingExercise ? '...' : t('edit_exercise.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {editMealPlanModal && (() => {
                // Shared input class – solid white/dark, no transparency
                const mIn = 'w-full border border-gray-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-400 rounded-xl px-2.5 py-1.5 text-xs focus:outline-none focus:ring-2 focus:ring-blue-500';

                // Derived totals – recalculate on every render (no useMemo needed)
                const totals = editMealPlanForm.items.reduce(
                    (acc, i) => ({
                        kcal: acc.kcal + (Number(i.caloriesKcal) || 0),
                        prot: acc.prot + (Number(i.proteinG) || 0),
                        carb: acc.carb + (Number(i.carbsG) || 0),
                        fat:  acc.fat  + (Number(i.fatG)  || 0),
                    }),
                    { kcal: 0, prot: 0, carb: 0, fat: 0 }
                );

                // All meal times to render: ordered first, then any extras
                const allMealTimes = [
                    ...MEAL_ORDER,
                    ...[...new Set([
                        ...editMealPlanForm.items.map(i => i.mealTime),
                        ...Array.from(openAddForms),
                    ])].filter(mt => !MEAL_ORDER.includes(mt)),
                ];

                return (
                    <div
                        className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-4"
                        onClick={() => !isSavingMealPlan && setEditMealPlanModal(null)}
                    >
                        <div
                            className="w-full max-w-lg max-h-[92vh] flex flex-col bg-white dark:bg-slate-900 rounded-3xl shadow-2xl border border-black/5 dark:border-white/10 overflow-hidden"
                            onClick={e => e.stopPropagation()}
                        >
                            {/* ── Header ── */}
                            <div className="px-6 pt-5 pb-4 border-b border-black/5 dark:border-white/10 shrink-0 flex items-center justify-between">
                                <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">{t('meal_plan.edit_title')}</h3>
                                {isFetchingPlanForEdit && <Loader2 className="w-4 h-4 text-blue-500 animate-spin" />}
                            </div>

                            {/* ── Live Macro Totals (always visible, updates on every keystroke) ── */}
                            <div className="px-6 py-3 bg-slate-50 dark:bg-slate-800/60 border-b border-black/5 dark:border-white/10 shrink-0">
                                <p className="text-[10px] font-semibold uppercase tracking-widest text-gray-400 mb-2">{t('meal_plan.totals_label')}</p>
                                <div className="grid grid-cols-4 gap-2 text-center">
                                    <div>
                                        <p className="text-sm font-bold text-emerald-500">{totals.kcal.toFixed(0)}</p>
                                        <p className="text-[10px] text-gray-400">kcal</p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-bold text-rose-500">{totals.prot.toFixed(1)}g</p>
                                        <p className="text-[10px] text-gray-400">{t('nutrition.protein')}</p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-bold text-blue-500">{totals.carb.toFixed(1)}g</p>
                                        <p className="text-[10px] text-gray-400">{t('nutrition.carbs')}</p>
                                    </div>
                                    <div>
                                        <p className="text-sm font-bold text-amber-500">{totals.fat.toFixed(1)}g</p>
                                        <p className="text-[10px] text-gray-400">{t('nutrition.fat')}</p>
                                    </div>
                                </div>
                            </div>

                            {/* ── Scrollable Body ── */}
                            <div className="flex-1 overflow-y-auto px-6 py-4 space-y-5">

                                {/* Plan meta */}
                                <div className="space-y-2">
                                    <input
                                        className="w-full border border-gray-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 placeholder-gray-400 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        placeholder={t('meal_plan.name_label')}
                                        value={editMealPlanForm.name}
                                        onChange={e => setEditMealPlanForm(f => ({ ...f, name: e.target.value }))}
                                    />
                                    <select
                                        className="w-full border border-gray-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                        value={editMealPlanForm.goal}
                                        onChange={e => setEditMealPlanForm(f => ({ ...f, goal: e.target.value }))}
                                    >
                                        <option value="BULKING">{t('history.goal_bulking')}</option>
                                        <option value="CUTTING">{t('history.goal_cutting')}</option>
                                        <option value="MAINTENANCE">{t('history.goal_maintenance')}</option>
                                    </select>
                                    <label className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-300 cursor-pointer select-none">
                                        <input
                                            type="checkbox"
                                            checked={editMealPlanForm.cheatMeal}
                                            onChange={e => setEditMealPlanForm(f => ({ ...f, cheatMeal: e.target.checked }))}
                                            className="w-4 h-4 rounded accent-blue-500"
                                        />
                                        {t('meal_plan.cheat_meal')}
                                    </label>
                                </div>

                                {/* Meal groups */}
                                {allMealTimes.map(mt => {
                                    const groupItems = editMealPlanForm.items.filter(i => i.mealTime === mt);
                                    const isAddOpen = openAddForms.has(mt);
                                    if (groupItems.length === 0 && !isAddOpen) return null;

                                    const groupKcal = groupItems.reduce((s, i) => s + (Number(i.caloriesKcal) || 0), 0);
                                    const draft = mealDrafts[mt] ?? emptyDraftItem(mt);

                                    return (
                                        <div key={mt} className="rounded-2xl border border-gray-100 dark:border-slate-700/60 overflow-hidden">
                                            {/* Group header */}
                                            <div className="flex items-center justify-between px-4 py-2.5 bg-gray-50 dark:bg-slate-800/50">
                                                <div className="flex items-center gap-2">
                                                    <span className="text-xs font-semibold uppercase tracking-wide text-gray-700 dark:text-gray-300">
                                                        {mealTimeLabel(mt)}
                                                    </span>
                                                    <span className="text-[10px] font-medium text-emerald-500 tabular-nums">
                                                        {groupKcal.toFixed(0)} kcal
                                                    </span>
                                                </div>
                                                {!isAddOpen && (
                                                    <button
                                                        onClick={() => openMealAddForm(mt)}
                                                        className="flex items-center gap-1 text-xs font-medium text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300 transition-colors"
                                                    >
                                                        <Plus className="w-3 h-3" />
                                                        {t('meal_plan.add_food_btn')}
                                                    </button>
                                                )}
                                            </div>

                                            {/* Existing items – compact read-only rows with optional inline edit */}
                                            {groupItems.length > 0 && (
                                                <div className="divide-y divide-gray-100 dark:divide-slate-700/50">
                                                    {groupItems.map(item => {
                                                        const isEditing = editingItemTempId === item.tempId;
                                                        return (
                                                            <div key={item.tempId}>
                                                                {/* Compact summary row */}
                                                                <div className="flex items-center gap-2 px-4 py-2.5">
                                                                    <div className="flex-1 min-w-0">
                                                                        <p className="text-xs font-medium text-gray-800 dark:text-gray-100 truncate">{item.name || <span className="italic text-gray-400">{t('meal_plan.item_name')}</span>}</p>
                                                                        <p className="text-[10px] text-gray-400 mt-0.5">
                                                                            {item.quantity ? `${item.quantity}${item.baseUnit} · ` : ''}
                                                                            <span className="text-rose-400">{Number(item.proteinG).toFixed(1)}P</span>
                                                                            {' · '}
                                                                            <span className="text-blue-400">{Number(item.carbsG).toFixed(1)}C</span>
                                                                            {' · '}
                                                                            <span className="text-amber-400">{Number(item.fatG).toFixed(1)}G</span>
                                                                        </p>
                                                                    </div>
                                                                    <span className="text-xs font-bold text-emerald-500 shrink-0 tabular-nums">
                                                                        {Number(item.caloriesKcal).toFixed(0)} kcal
                                                                    </span>
                                                                    <button
                                                                        onClick={() => setEditingItemTempId(isEditing ? null : item.tempId)}
                                                                        className={`w-6 h-6 rounded-lg flex items-center justify-center transition-colors shrink-0 ${isEditing ? 'bg-blue-100 dark:bg-blue-900/40 text-blue-600 dark:text-blue-400' : 'bg-gray-100 dark:bg-slate-700 text-gray-400 hover:text-gray-600 dark:hover:text-gray-200'}`}
                                                                        title={t('meal_plan.edit_item')}
                                                                    >
                                                                        <Pencil className="w-3 h-3" />
                                                                    </button>
                                                                    <button
                                                                        onClick={() => { removeEditItem(item.tempId); if (editingItemTempId === item.tempId) setEditingItemTempId(null); }}
                                                                        className="w-6 h-6 rounded-lg bg-red-50 dark:bg-red-900/20 text-red-400 flex items-center justify-center hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors shrink-0"
                                                                        title={t('meal_plan.remove_item')}
                                                                    >
                                                                        <Trash2 className="w-3 h-3" />
                                                                    </button>
                                                                </div>

                                                                {/* Inline edit panel (shown when pencil is toggled) */}
                                                                {isEditing && (
                                                                    <div className="px-4 pb-3 space-y-2 bg-gray-50/60 dark:bg-slate-800/30">
                                                                        <input
                                                                            className={`${mIn} w-full`}
                                                                            placeholder={t('meal_plan.item_name')}
                                                                            value={String(item.name)}
                                                                            onChange={e => updateEditItem(item.tempId, 'name', e.target.value)}
                                                                            autoFocus
                                                                        />
                                                                        <div className="grid grid-cols-2 gap-2">
                                                                            <input className={mIn} type="number" min={0} placeholder={t('meal_plan.item_qty')} value={String(item.quantity)} onChange={e => updateEditItem(item.tempId, 'quantity', e.target.value)} />
                                                                            <input className={mIn} placeholder={t('meal_plan.item_unit')} value={String(item.baseUnit)} onChange={e => updateEditItem(item.tempId, 'baseUnit', e.target.value)} />
                                                                        </div>
                                                                        {(item.kcalPerUnit > 0 || item.protPerUnit > 0) && (
                                                                            <p className="text-[9px] text-blue-500 dark:text-blue-400">
                                                                                ↻ {t('meal_plan.auto_recalc_hint')}
                                                                            </p>
                                                                        )}
                                                                        <div className="grid grid-cols-4 gap-1.5">
                                                                            {[
                                                                                { field: 'caloriesKcal' as keyof EditableItem, label: 'kcal', cls: 'text-gray-400' },
                                                                                { field: 'proteinG' as keyof EditableItem, label: t('meal_plan.item_prot'), cls: 'text-rose-400' },
                                                                                { field: 'carbsG' as keyof EditableItem, label: t('meal_plan.item_carb'), cls: 'text-blue-400' },
                                                                                { field: 'fatG' as keyof EditableItem, label: t('meal_plan.item_fat'), cls: 'text-amber-400' },
                                                                            ].map(({ field, label, cls }) => (
                                                                                <div key={field}>
                                                                                    <p className={`text-[9px] mb-0.5 text-center ${cls}`}>{label}</p>
                                                                                    <input
                                                                                        className={`${mIn} text-center`}
                                                                                        type="number"
                                                                                        min={0}
                                                                                        placeholder="0"
                                                                                        value={String(item[field])}
                                                                                        onChange={e => updateEditItem(item.tempId, field, e.target.value)}
                                                                                    />
                                                                                </div>
                                                                            ))}
                                                                        </div>
                                                                        <button
                                                                            onClick={() => setEditingItemTempId(null)}
                                                                            className="w-full py-1.5 rounded-xl text-xs font-medium bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-900/40 transition-colors"
                                                                        >
                                                                            {t('meal_plan.done_editing')}
                                                                        </button>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        );
                                                    })}
                                                </div>
                                            )}

                                            {/* ── Inline Add-Food Form ── stays open until user clicks X */}
                                            {isAddOpen && (
                                                <div className="px-4 py-3 space-y-2 bg-blue-50/40 dark:bg-blue-900/10 border-t border-blue-100 dark:border-blue-800/30">
                                                    <p className="text-[10px] font-semibold uppercase tracking-widest text-blue-600 dark:text-blue-400">
                                                        {t('meal_plan.add_food_form_title')}
                                                    </p>
                                                    <input
                                                        className={`${mIn} w-full`}
                                                        placeholder={t('meal_plan.item_name')}
                                                        value={draft.name}
                                                        onChange={e => updateDraftField(mt, 'name', e.target.value)}
                                                        autoFocus
                                                        onKeyDown={e => { if (e.key === 'Enter') confirmDraftItem(mt); }}
                                                    />
                                                    <div className="grid grid-cols-2 gap-2">
                                                        <input
                                                            className={mIn}
                                                            type="number"
                                                            min={0}
                                                            placeholder={t('meal_plan.item_qty')}
                                                            value={draft.quantity}
                                                            onChange={e => updateDraftField(mt, 'quantity', e.target.value)}
                                                        />
                                                        <input
                                                            className={mIn}
                                                            placeholder={t('meal_plan.item_unit')}
                                                            value={draft.baseUnit}
                                                            onChange={e => updateDraftField(mt, 'baseUnit', e.target.value)}
                                                        />
                                                    </div>
                                                    <div className="grid grid-cols-4 gap-1.5">
                                                        {[
                                                            { field: 'caloriesKcal' as keyof EditableItem, label: 'kcal', cls: 'text-gray-400' },
                                                            { field: 'proteinG'     as keyof EditableItem, label: t('meal_plan.item_prot'), cls: 'text-rose-400' },
                                                            { field: 'carbsG'       as keyof EditableItem, label: t('meal_plan.item_carb'), cls: 'text-blue-400' },
                                                            { field: 'fatG'         as keyof EditableItem, label: t('meal_plan.item_fat'), cls: 'text-amber-400' },
                                                        ].map(({ field, label, cls }) => (
                                                            <div key={field}>
                                                                <p className={`text-[9px] mb-0.5 text-center ${cls}`}>{label}</p>
                                                                <input
                                                                    className={`${mIn} text-center`}
                                                                    type="number"
                                                                    min={0}
                                                                    placeholder="0"
                                                                    value={String(draft[field])}
                                                                    onChange={e => updateDraftField(mt, field, e.target.value)}
                                                                />
                                                            </div>
                                                        ))}
                                                    </div>
                                                    <div className="flex gap-2 pt-1">
                                                        <button
                                                            onClick={() => confirmDraftItem(mt)}
                                                            disabled={!draft.name.trim()}
                                                            className="flex-1 py-2 rounded-xl text-xs font-semibold bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-40 transition-colors active:scale-95 flex items-center justify-center gap-1.5"
                                                        >
                                                            <Plus className="w-3.5 h-3.5" />
                                                            {t('meal_plan.add_food_btn')}
                                                        </button>
                                                        <button
                                                            onClick={() => closeMealAddForm(mt)}
                                                            className="w-9 h-9 rounded-xl bg-gray-100 dark:bg-slate-700 text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-slate-600 transition-colors active:scale-95 shrink-0"
                                                            title={t('common.cancel')}
                                                        >
                                                            <X className="w-4 h-4" />
                                                        </button>
                                                    </div>
                                                    {groupItems.length > 0 && (
                                                        <p className="text-[10px] text-center text-blue-500/70 dark:text-blue-400/60">
                                                            {t('meal_plan.keep_adding_hint')}
                                                        </p>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}

                                {/* ── Add new meal group ── */}
                                {newMealTimeSelect === null ? (
                                    <button
                                        onClick={() => setNewMealTimeSelect('BREAKFAST')}
                                        className="w-full py-3 rounded-2xl border-2 border-dashed border-gray-200 dark:border-slate-700 text-sm text-gray-400 hover:border-blue-400 hover:text-blue-500 dark:hover:border-blue-500 dark:hover:text-blue-400 flex items-center justify-center gap-2 transition-colors"
                                    >
                                        <Plus className="w-4 h-4" />
                                        {t('meal_plan.add_meal')}
                                    </button>
                                ) : (
                                    <div className="flex gap-2">
                                        <select
                                            className="flex-1 border border-gray-200 dark:border-slate-600 bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 rounded-xl px-2.5 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                            value={newMealTimeSelect}
                                            onChange={e => setNewMealTimeSelect(e.target.value)}
                                        >
                                            {MEAL_ORDER.map(mt => (
                                                <option key={mt} value={mt}>{mealTimeLabel(mt)}</option>
                                            ))}
                                        </select>
                                        <button
                                            onClick={() => openMealAddForm(newMealTimeSelect)}
                                            className="px-4 py-2 rounded-xl text-sm font-medium bg-blue-600 text-white hover:bg-blue-700 transition-colors active:scale-95 shrink-0"
                                        >
                                            {t('common.add')}
                                        </button>
                                        <button
                                            onClick={() => setNewMealTimeSelect(null)}
                                            className="w-9 h-9 rounded-xl bg-gray-100 dark:bg-slate-800 text-gray-400 flex items-center justify-center hover:bg-gray-200 transition-colors active:scale-95 shrink-0"
                                        >
                                            <X className="w-4 h-4" />
                                        </button>
                                    </div>
                                )}
                            </div>

                            {/* ── Footer ── */}
                            <div className="px-6 pb-5 pt-4 border-t border-black/5 dark:border-white/10 flex gap-2 shrink-0">
                                <button
                                    onClick={() => setEditMealPlanModal(null)}
                                    className="flex-1 py-2.5 rounded-2xl text-sm font-medium bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                >
                                    {t('common.cancel')}
                                </button>
                                <button
                                    onClick={handleSaveMealPlan}
                                    disabled={isSavingMealPlan || !editMealPlanForm.name || isFetchingPlanForEdit}
                                    className="flex-1 py-2.5 rounded-2xl text-sm font-semibold bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                                >
                                    {isSavingMealPlan ? <Loader2 className="w-4 h-4 animate-spin mx-auto" /> : t('meal_plan.save')}
                                </button>
                            </div>
                        </div>
                    </div>
                );
            })()}

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
                                        ? 'border-blue-600 text-blue-600 dark:text-blue-400'
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
                        <>
                            {/* Weight Goal Card */}
                            <div className={`${glassCard} p-5`}>
                                <div className="flex items-center justify-between mb-3">
                                    <div className="flex items-center gap-2">
                                        <Target className="w-4 h-4 text-violet-400" />
                                        <span className="text-sm font-semibold text-gray-800 dark:text-gray-100">{t('weight_goal.title')}</span>
                                    </div>
                                    {!editingWeightGoal && (
                                        <button
                                            onClick={() => { setEditingWeightGoal(true); setWeightGoalInput(weightGoal?.toString() ?? ''); }}
                                            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-slate-700 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors active:scale-95"
                                        >
                                            <Pencil className="w-3 h-3" />
                                        </button>
                                    )}
                                </div>
                                {editingWeightGoal ? (
                                    <div className="flex items-center gap-2">
                                        <input
                                            type="number"
                                            min={1}
                                            step={0.1}
                                            className={`${inputClass} flex-1`}
                                            placeholder={t('weight_goal.placeholder')}
                                            value={weightGoalInput}
                                            onChange={e => setWeightGoalInput(e.target.value)}
                                            autoFocus
                                        />
                                        <button
                                            onClick={handleSaveWeightGoal}
                                            disabled={isSavingWeightGoal || !weightGoalInput}
                                            className="w-8 h-8 rounded-full bg-violet-500 text-white flex items-center justify-center hover:bg-violet-600 disabled:opacity-50 transition-colors active:scale-95 shrink-0"
                                        >
                                            {isSavingWeightGoal ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Check className="w-3.5 h-3.5" />}
                                        </button>
                                        <button
                                            onClick={() => setEditingWeightGoal(false)}
                                            className="w-8 h-8 rounded-full bg-gray-100 dark:bg-slate-700 text-gray-500 flex items-center justify-center hover:bg-gray-200 transition-colors active:scale-95 shrink-0"
                                        >
                                            <X className="w-3.5 h-3.5" />
                                        </button>
                                    </div>
                                ) : weightGoal != null ? (
                                    <div className="flex items-baseline gap-1.5">
                                        <span className="text-2xl font-bold text-violet-500 dark:text-violet-400">{weightGoal}</span>
                                        <span className="text-sm text-gray-400">{t('weight_goal.unit')}</span>
                                    </div>
                                ) : (
                                    <p className="text-sm text-gray-400 text-center py-1">{t('weight_goal.no_goal')}</p>
                                )}
                            </div>

                            {exercises.length === 0 ? (
                                <div className={`${glassCard} p-10 text-center text-gray-400 text-sm`}>
                                    {t('student_detail.no_workout')}
                                </div>
                            ) : (
                                exercises.map((ex, idx) => {
                                    const embedUrl = ex.videoUrl ? getEmbedUrl(ex.videoUrl) : null;
                                    const isExpanded = expandedVideoId === ex.id;
                                    return (
                                    <div key={ex.id} className={`${glassCard} p-5`}>
                                        <div className="flex items-start justify-between mb-3">
                                            <div>
                                                <span className="text-xs text-gray-400">#{idx + 1}</span>
                                                <h3 className="text-sm font-semibold text-gray-900 dark:text-gray-100 mt-0.5">{ex.name}</h3>
                                                {ex.workoutLabel && (
                                                    <span className="text-xs text-violet-500 dark:text-violet-400">{ex.workoutLabel}</span>
                                                )}
                                            </div>
                                            <div className="flex items-center gap-1.5 shrink-0">
                                                {ex.tonnage != null && ex.tonnage > 0 && (
                                                    <div className="bg-purple-50/80 dark:bg-purple-900/30 text-purple-600 dark:text-purple-300 text-xs font-semibold px-3 py-1 rounded-full">
                                                        {ex.tonnage.toFixed(1)} kg
                                                    </div>
                                                )}
                                                {ex.videoUrl && (
                                                    <button
                                                        onClick={() => setExpandedVideoId(isExpanded ? null : ex.id)}
                                                        className={`w-8 h-8 rounded-full flex items-center justify-center transition-colors active:scale-95 ${
                                                            isExpanded
                                                                ? 'bg-blue-600 text-white hover:bg-blue-700'
                                                                : 'bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-900/50'
                                                        }`}
                                                        title={isExpanded ? 'Fechar vídeo' : t('workout_view.watch_video')}
                                                    >
                                                        <Play className="w-3.5 h-3.5" />
                                                    </button>
                                                )}
                                                {!isNutritionist && (
                                                    <>
                                                        <button
                                                            onClick={() => openEditExerciseModal(ex)}
                                                            className="w-8 h-8 rounded-full bg-gray-100 dark:bg-slate-700 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors active:scale-95"
                                                            title="Edit exercise"
                                                        >
                                                            <Pencil className="w-3.5 h-3.5" />
                                                        </button>
                                                        <button
                                                            onClick={() => handleDeleteExercise(ex.id)}
                                                            disabled={deletingExerciseId === ex.id}
                                                            className="w-8 h-8 rounded-full bg-red-50 dark:bg-red-900/30 text-red-500 dark:text-red-400 flex items-center justify-center hover:bg-red-100 dark:hover:bg-red-900/50 disabled:opacity-50 transition-colors active:scale-95"
                                                            title="Delete exercise"
                                                        >
                                                            {deletingExerciseId === ex.id ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Trash2 className="w-3.5 h-3.5" />}
                                                        </button>
                                                    </>
                                                )}
                                            </div>
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
                                            <p className="mt-2 text-xs text-gray-400">
                                                {ex.scheduledDays.split(',').map(d => DAY_PT[d.trim()] ?? d.trim()).join(' · ')}
                                            </p>
                                        )}
                                        {isExpanded && ex.videoUrl && (
                                            <div className="mt-3 space-y-2">
                                                <div className="rounded-2xl overflow-hidden aspect-video bg-black">
                                                    <iframe
                                                        src={embedUrl ?? ex.videoUrl}
                                                        className="w-full h-full"
                                                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                                        allowFullScreen
                                                        title={ex.name}
                                                    />
                                                </div>
                                                {!embedUrl && (
                                                    <a
                                                        href={ex.videoUrl}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="flex items-center justify-center gap-2 w-full py-2 bg-gray-50 dark:bg-slate-800 text-gray-600 dark:text-gray-300 rounded-2xl text-sm font-medium hover:bg-gray-100 dark:hover:bg-slate-700 transition-colors active:scale-95"
                                                    >
                                                        <ExternalLink className="w-4 h-4" />
                                                        Abrir vídeo externamente
                                                    </a>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                    );
                                })
                            )}
                        </>
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
                                            <div className="flex items-center gap-1.5">
                                                {mealPlan.goal && (
                                                    <span className={`text-xs font-semibold px-3 py-1 rounded-full ${GOAL_COLORS[mealPlan.goal] ?? 'bg-gray-100 text-gray-600'}`}>
                                                        {goalLabel(mealPlan.goal)}
                                                    </span>
                                                )}
                                                {isNutritionist && (
                                                    <>
                                                        <button
                                                            onClick={() => openEditMealPlanModal({ id: mealPlan.id, name: mealPlan.name, goal: mealPlan.goal, cheatMeal: mealPlan.cheatMeal })}
                                                            className="w-7 h-7 rounded-full bg-gray-100 dark:bg-slate-700 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 transition-colors active:scale-95"
                                                        >
                                                            <Pencil className="w-3 h-3" />
                                                        </button>
                                                        <button
                                                            onClick={() => handleDeleteMealPlan(mealPlan.id)}
                                                            disabled={deletingMealPlanId === mealPlan.id}
                                                            className="w-7 h-7 rounded-full bg-red-50 dark:bg-red-900/30 text-red-500 flex items-center justify-center hover:bg-red-100 transition-colors active:scale-95 disabled:opacity-50"
                                                        >
                                                            {deletingMealPlanId === mealPlan.id ? <Loader2 className="w-3 h-3 animate-spin" /> : <Trash2 className="w-3 h-3" />}
                                                        </button>
                                                    </>
                                                )}
                                            </div>
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

                    {/* HISTORY TAB */}
                    {activeTab === 'history' && !isLoading && (
                        <>
                            {/* Diet History */}
                            <div className={`${glassCard} p-5`}>
                                <div className="flex items-center gap-2 mb-4">
                                    <Apple className="w-4 h-4 text-blue-400" />
                                    <span className="text-sm font-semibold text-gray-800 dark:text-gray-100">{t('history.meal_plan_history')}</span>
                                </div>
                                {mealPlanHistory.length === 0 ? (
                                    <p className="text-sm text-gray-400">{t('history.no_meal_plans')}</p>
                                ) : (
                                    <div className="space-y-2">
                                        {mealPlanHistory.map((plan, idx) => (
                                            <div key={plan.id} className={`flex items-center justify-between p-3 rounded-2xl ${idx === 0 ? 'bg-blue-50/60 dark:bg-blue-900/20' : 'bg-black/5 dark:bg-white/5'}`}>
                                                <div>
                                                    <p className="text-sm font-medium text-gray-800 dark:text-gray-100">{plan.name}</p>
                                                    <p className="text-xs text-gray-400">{goalLabel(plan.goal)}{plan.cheatMeal ? ' · Refeição Livre' : ''}</p>
                                                </div>
                                                {idx === 0 && <span className="text-xs text-blue-600 dark:text-blue-400 font-semibold">Atual</span>}
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>

                            {/* Check-in History */}
                            <div className={`${glassCard} p-5`}>
                                <div className="flex items-center gap-2 mb-4">
                                    <History className="w-4 h-4 text-green-400" />
                                    <span className="text-sm font-semibold text-gray-800 dark:text-gray-100">{t('history.checkin_history')}</span>
                                </div>
                                {checkinHistory.length === 0 ? (
                                    <p className="text-sm text-gray-400">{t('history.no_checkins')}</p>
                                ) : (
                                    <div className="flex flex-wrap gap-1.5">
                                        {checkinHistory.slice().reverse().slice(0, 30).map((date, i) => (
                                            <span key={i} className="text-xs bg-green-50/60 dark:bg-green-900/20 text-green-700 dark:text-green-300 px-2.5 py-1 rounded-full">
                                                {new Date(date).toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })}
                                            </span>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </>
                    )}
                </main>
            </div>
        </div>
    );
}
