import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Play, X, CheckCircle, Timer, Download, Loader2, LayoutGrid, List, Pencil, Trash2, ExternalLink } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { useRestTimer } from '../contexts/RestTimerContext';
import { api } from '../services/api';
import { WorkoutSpreadsheetView } from '../components/WorkoutSpreadsheetView';
import toast from 'react-hot-toast';

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

interface TodayExercise {
    id: string;
    name: string;
    sets: number;
    repetitions: number;
    workoutLabel?: string;
}

interface ExerciseLog {
    id: string;
    exerciseId: string;
    actualWeight: number | null;
    actualReps: number;
    completedAt: string;
    tonnageAchieved: number;
    personalRecord: boolean;
}

function getEmbedUrl(url: string): string | null {
    const ytMatch = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&\n?#]+)/);
    if (ytMatch) return `https://www.youtube.com/embed/${ytMatch[1]}?autoplay=1`;
    const driveMatch = url.match(/drive\.google\.com\/file\/d\/([^/]+)/);
    if (driveMatch) return `https://drive.google.com/file/d/${driveMatch[1]}/preview`;
    return null;
}

function getYoutubeThumbnail(url: string): string | null {
    const ytMatch = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&\n?#]+)/);
    if (ytMatch) return `https://img.youtube.com/vi/${ytMatch[1]}/hqdefault.jpg`;
    return null;
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';
const inputClass = 'w-full border border-black/10 dark:border-white/10 bg-white/50 dark:bg-white/5 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500';

export function WorkoutView() {
    const { t, i18n } = useTranslation();
    const { user } = useAuth();
    const { start: startTimer } = useRestTimer();
    const [exercises, setExercises] = useState<WorkoutExercise[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [modalUrl, setModalUrl] = useState<string | null>(null);

    const [logModal, setLogModal] = useState<WorkoutExercise | null>(null);
    const [logWeight, setLogWeight] = useState('');
    const [logReps, setLogReps] = useState('');
    const [isLogging, setIsLogging] = useState(false);
    const [logResult, setLogResult] = useState<ExerciseLog | null>(null);

    const [prMap, setPrMap] = useState<Record<string, boolean>>({});
    const [historyData, setHistoryData] = useState<Record<string, ExerciseLog[]>>({});
    const [showHistory, setShowHistory] = useState<Record<string, boolean>>({});
    const [isDownloading, setIsDownloading] = useState(false);
    const [todayExercises, setTodayExercises] = useState<TodayExercise[]>([]);
    const [viewMode, setViewMode] = useState<'card' | 'spreadsheet'>('card');

    const [deletingId, setDeletingId] = useState<string | null>(null);
    const [editModal, setEditModal] = useState<WorkoutExercise | null>(null);
    const [editForm, setEditForm] = useState<Partial<WorkoutExercise> & { scheduledDaysArr?: string[] }>({});
    const [isSavingEdit, setIsSavingEdit] = useState(false);

    function loadExercises() {
        setIsLoading(true);
        api.get('/workouts/my')
            .then(r => setExercises(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }

    useEffect(() => {
        loadExercises();
        api.get('/workouts/today').then(r => setTodayExercises(r.data)).catch(() => {});
    }, []);

    async function handleDeleteExercise(id: string) {
        if (!confirm(t('edit_exercise.title') + '?')) return;
        setDeletingId(id);
        try {
            await api.delete(`/workouts/${id}`);
            setExercises(prev => prev.filter(e => e.id !== id));
            toast.success(t('toast.exercise_deleted'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setDeletingId(null);
        }
    }

    const ALL_EDIT_DAYS = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'] as const;
    const EDIT_DAY_LABELS: Record<string, string> = {
        MON: 'SEG', TUE: 'TER', WED: 'QUA', THU: 'QUI', FRI: 'SEX', SAT: 'SÁB', SUN: 'DOM',
    };

    function openEditModal(ex: WorkoutExercise) {
        setEditModal(ex);
        const daysArr = ex.scheduledDays
            ? String(ex.scheduledDays).split(',').map(d => d.trim()).filter(Boolean)
            : [];
        setEditForm({ ...ex, scheduledDaysArr: daysArr });
    }

    async function handleSaveEdit() {
        if (!editModal) return;
        setIsSavingEdit(true);
        try {
            const scheduledDays = editForm.scheduledDaysArr ?? [];
            const { data } = await api.put(`/workouts/${editModal.id}`, {
                name: editForm.name,
                sets: Number(editForm.sets),
                repetitions: Number(editForm.repetitions),
                measurementType: editForm.measurementType,
                weightInKg: editForm.weightInKg ?? null,
                videoUrl: editForm.videoUrl ?? null,
                restTime: editForm.restTime ?? null,
                workoutLabel: editForm.workoutLabel ?? null,
                scheduledDays: scheduledDays.length > 0 ? scheduledDays : null,
            });
            setExercises(prev => prev.map(e => e.id === editModal.id ? data : e));
            setEditModal(null);
            toast.success(t('toast.exercise_updated'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsSavingEdit(false);
        }
    }

    if (!user) return null;

    function openLogModal(ex: WorkoutExercise) {
        setLogModal(ex);
        setLogWeight(ex.weightInKg ? String(ex.weightInKg) : '');
        setLogReps(String(ex.repetitions));
        setLogResult(null);
    }

    async function handleLog() {
        if (!logModal) return;
        setIsLogging(true);
        try {
            const { data } = await api.post<ExerciseLog>('/workouts/log', {
                exerciseId: logModal.id,
                exerciseName: logModal.name,
                actualWeight: logWeight ? Number(logWeight) : null,
                actualReps: Number(logReps)
            });
            setLogResult(data);
            if (data.personalRecord) {
                setPrMap(prev => ({ ...prev, [logModal.id]: true }));
            }
            api.get<ExerciseLog[]>(`/workouts/history/${logModal.id}`)
                .then(r => setHistoryData(prev => ({ ...prev, [logModal.id]: r.data })))
                .catch(() => {});
        } catch {
        } finally {
            setIsLogging(false);
        }
    }

    function handleLogSuccess() {
        if (!logModal) return;
        const restSecs = logModal.restTime ?? 90;
        setLogModal(null);
        startTimer(restSecs, logModal.name);
    }

    function toggleHistory(exerciseId: string) {
        const next = !showHistory[exerciseId];
        setShowHistory(prev => ({ ...prev, [exerciseId]: next }));
        if (next && !historyData[exerciseId]) {
            api.get<ExerciseLog[]>(`/workouts/history/${exerciseId}`)
                .then(r => setHistoryData(prev => ({ ...prev, [exerciseId]: r.data })))
                .catch(() => {});
        }
    }

    const totalVolume = exercises.reduce((sum, ex) => ex.tonnage ? sum + ex.tonnage : sum, 0);

    async function handleDownloadPdf() {
        setIsDownloading(true);
        try {
            const response = await api.get('/reports/workouts/my', { responseType: 'blob', headers: { 'Accept-Language': i18n.language } });
            const url = URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const a = document.createElement('a');
            a.href = url;
            a.download = 'workout_sheet.pdf';
            a.click();
            URL.revokeObjectURL(url);
        } catch {
        } finally {
            setIsDownloading(false);
        }
    }

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />

            {modalUrl && (
                <div
                    className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4"
                    onClick={() => setModalUrl(null)}
                >
                    <div
                        className="relative w-full max-w-3xl aspect-video rounded-3xl overflow-hidden shadow-2xl"
                        onClick={e => e.stopPropagation()}
                    >
                        <button
                            onClick={() => setModalUrl(null)}
                            className="absolute top-3 right-3 z-10 w-8 h-8 flex items-center justify-center bg-black/60 hover:bg-black/80 rounded-full text-white transition-colors active:scale-95"
                        >
                            <X className="w-4 h-4" />
                        </button>
                        <iframe
                            src={modalUrl}
                            title="Exercise video"
                            className="w-full h-full"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                        />
                    </div>
                </div>
            )}

            {logModal && (
                <div
                    className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-4"
                    onClick={() => !isLogging && setLogModal(null)}
                >
                    <div
                        className="w-full max-w-sm bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-black/5 dark:border-white/10 p-6 space-y-4"
                        onClick={e => e.stopPropagation()}
                    >
                        {logResult ? (
                            <div className="text-center space-y-3 py-2">
                                {logResult.personalRecord ? (
                                    <>
                                        <div className="text-4xl animate-bounce">🏆</div>
                                        <p className="text-lg font-bold text-yellow-600 dark:text-yellow-400">
                                            {t('workout_log.new_record')}
                                        </p>
                                        <p className="text-sm text-gray-500">{logResult.tonnageAchieved.toFixed(1)} kg {t('workout_view.tonnage')}</p>
                                    </>
                                ) : (
                                    <>
                                        <CheckCircle className="w-10 h-10 text-green-500 mx-auto" />
                                        <p className="text-base font-semibold text-gray-800 dark:text-gray-100">
                                            {t('workout_log.log_success')}
                                        </p>
                                        <p className="text-sm text-gray-500">{logResult.tonnageAchieved.toFixed(1)} kg {t('workout_view.tonnage')}</p>
                                    </>
                                )}
                                <button
                                    onClick={handleLogSuccess}
                                    className="mt-2 w-full py-2.5 bg-blue-600 text-white rounded-2xl text-sm font-semibold hover:bg-blue-700 transition-colors active:scale-95"
                                >
                                    {t('rest_timer.start_rest')} ({logModal?.restTime ?? 90}s)
                                </button>
                                <button
                                    onClick={() => setLogModal(null)}
                                    className="w-full py-2 bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 rounded-2xl text-sm font-medium hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                >
                                    {t('workout_log.close')}
                                </button>
                            </div>
                        ) : (
                            <>
                                <div>
                                    <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">{t('workout_log.confirm_execution')}</h3>
                                    <p className="text-sm text-gray-500 mt-0.5">{logModal.name}</p>
                                </div>
                                <div className="grid grid-cols-2 gap-3">
                                    <div>
                                        <label className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">{t('workout_log.actual_weight')} (kg)</label>
                                        <input
                                            type="number"
                                            min={0}
                                            step={0.5}
                                            value={logWeight}
                                            onChange={e => setLogWeight(e.target.value)}
                                            className={inputClass}
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1">{t('workout_log.actual_reps')}</label>
                                        <input
                                            type="number"
                                            min={1}
                                            value={logReps}
                                            onChange={e => setLogReps(e.target.value)}
                                            className={inputClass}
                                        />
                                    </div>
                                </div>
                                <div className="flex gap-2 pt-1">
                                    <button
                                        onClick={() => setLogModal(null)}
                                        className="flex-1 py-2.5 rounded-2xl text-sm font-medium bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                    >
                                        {t('workout_log.close')}
                                    </button>
                                    <button
                                        onClick={handleLog}
                                        disabled={isLogging || !logReps}
                                        className="flex-1 py-2.5 rounded-2xl text-sm font-semibold bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                                    >
                                        {isLogging ? '...' : t('workout_log.log_set')}
                                    </button>
                                </div>
                            </>
                        )}
                    </div>
                </div>
            )}

            {editModal && (
                <div
                    className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-4"
                    onClick={() => !isSavingEdit && setEditModal(null)}
                >
                    <div
                        className="w-full max-w-sm bg-white/90 dark:bg-slate-900/90 backdrop-blur-xl rounded-3xl shadow-2xl border border-black/5 dark:border-white/10 p-6 space-y-3"
                        onClick={e => e.stopPropagation()}
                    >
                        <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">{t('edit_exercise.title')}</h3>
                        <div className="space-y-2">
                            <input className={inputClass} placeholder={t('edit_exercise.name')} value={editForm.name ?? ''} onChange={e => setEditForm(f => ({ ...f, name: e.target.value }))} />
                            <div className="grid grid-cols-2 gap-2">
                                <input className={inputClass} type="number" min={1} placeholder={t('edit_exercise.sets')} value={editForm.sets ?? ''} onChange={e => setEditForm(f => ({ ...f, sets: Number(e.target.value) }))} />
                                <input className={inputClass} type="number" min={1} placeholder={t('edit_exercise.reps')} value={editForm.repetitions ?? ''} onChange={e => setEditForm(f => ({ ...f, repetitions: Number(e.target.value) }))} />
                            </div>
                            <select className={inputClass} value={editForm.measurementType ?? 'WEIGHT'} onChange={e => setEditForm(f => ({ ...f, measurementType: e.target.value }))}>
                                <option value="WEIGHT">{t('edit_exercise.type_weight')}</option>
                                <option value="BODYWEIGHT">{t('edit_exercise.type_bodyweight')}</option>
                                <option value="SPEED">{t('edit_exercise.type_speed')}</option>
                                <option value="TIME">{t('edit_exercise.type_time')}</option>
                            </select>
                            <input className={inputClass} type="number" min={0} step={0.5} placeholder={t('edit_exercise.weight')} value={editForm.weightInKg ?? ''} onChange={e => setEditForm(f => ({ ...f, weightInKg: e.target.value ? Number(e.target.value) : null }))} />
                            <input className={inputClass} placeholder={t('edit_exercise.video_url')} value={editForm.videoUrl ?? ''} onChange={e => setEditForm(f => ({ ...f, videoUrl: e.target.value }))} />
                            <input className={inputClass} type="number" min={0} placeholder={t('edit_exercise.rest_time')} value={editForm.restTime ?? ''} onChange={e => setEditForm(f => ({ ...f, restTime: e.target.value ? Number(e.target.value) : null }))} />
                            <input className={inputClass} placeholder={t('edit_exercise.label')} value={editForm.workoutLabel ?? ''} onChange={e => setEditForm(f => ({ ...f, workoutLabel: e.target.value }))} />
                            <div>
                                <p className="text-xs font-medium text-gray-500 dark:text-gray-400 mb-1.5">{t('edit_exercise.days')}</p>
                                <div className="flex gap-1">
                                    {ALL_EDIT_DAYS.map(day => {
                                        const selected = (editForm.scheduledDaysArr ?? []).includes(day);
                                        return (
                                            <button
                                                key={day}
                                                type="button"
                                                onClick={() => setEditForm(f => {
                                                    const arr = f.scheduledDaysArr ?? [];
                                                    return {
                                                        ...f,
                                                        scheduledDaysArr: selected
                                                            ? arr.filter(d => d !== day)
                                                            : [...arr, day],
                                                    };
                                                })}
                                                className={`flex-1 py-1.5 rounded-xl text-[10px] font-bold transition-all active:scale-95 ${
                                                    selected
                                                        ? 'bg-blue-600 text-white'
                                                        : 'bg-black/5 dark:bg-white/10 text-gray-500 dark:text-gray-400'
                                                }`}
                                            >
                                                {EDIT_DAY_LABELS[day]}
                                            </button>
                                        );
                                    })}
                                </div>
                            </div>
                        </div>
                        <div className="flex gap-2 pt-1">
                            <button onClick={() => setEditModal(null)} className="flex-1 py-2.5 rounded-2xl text-sm font-medium bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95">
                                {t('common.cancel')}
                            </button>
                            <button onClick={handleSaveEdit} disabled={isSavingEdit || !editForm.name} className="flex-1 py-2.5 rounded-2xl text-sm font-semibold bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95">
                                {isSavingEdit ? '...' : t('edit_exercise.save')}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <div className="flex-1 min-w-0">
                <header className="bg-white/70 dark:bg-slate-800/60 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                            {t('workout_view.title')}
                        </h2>
                        <div className="flex items-center gap-2">
                            {totalVolume > 0 && viewMode === 'card' && (
                                <div className="flex items-center gap-2 bg-purple-100/80 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300 px-4 py-1.5 rounded-full text-sm font-medium">
                                    <span>{t('workout_view.total_volume')}</span>
                                    <strong>{totalVolume.toFixed(1)} kg</strong>
                                </div>
                            )}

                            {/* View toggle */}
                            <div className="flex items-center bg-black/5 dark:bg-white/10 rounded-2xl p-1 gap-0.5">
                                <button
                                    onClick={() => setViewMode('card')}
                                    className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                                        viewMode === 'card'
                                            ? 'bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 shadow-sm'
                                            : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                                    }`}
                                >
                                    <List className="w-3.5 h-3.5" />
                                    Cards
                                </button>
                                <button
                                    onClick={() => setViewMode('spreadsheet')}
                                    className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold transition-all ${
                                        viewMode === 'spreadsheet'
                                            ? 'bg-white dark:bg-slate-700 text-gray-900 dark:text-gray-100 shadow-sm'
                                            : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                                    }`}
                                >
                                    <LayoutGrid className="w-3.5 h-3.5" />
                                    Planilha
                                </button>
                            </div>

                            {exercises.length > 0 && viewMode === 'card' && (
                                <button
                                    onClick={handleDownloadPdf}
                                    disabled={isDownloading}
                                    className="flex items-center gap-1.5 px-3 py-1.5 bg-gray-100 dark:bg-slate-800 text-gray-600 dark:text-gray-300 text-sm font-medium rounded-2xl hover:bg-gray-200 dark:hover:bg-gray-700 disabled:opacity-50 transition-colors active:scale-95"
                                >
                                    {isDownloading
                                        ? <Loader2 className="w-4 h-4 animate-spin" />
                                        : <Download className="w-4 h-4" />}
                                    {isDownloading ? t('reports.generating') : t('reports.download_workout')}
                                </button>
                            )}
                        </div>
                    </div>
                </header>

                <main className="p-4 md:p-6 max-w-4xl space-y-4 pb-24 md:pb-6">

                    {/* Spreadsheet view */}
                    {viewMode === 'spreadsheet' && (
                        <WorkoutSpreadsheetView />
                    )}

                    {viewMode === 'card' && (
                    <>
                    {/* Today's Workout highlight */}
                    <div className={`${glassCard} p-5`}>
                        <div className="flex items-center gap-2 mb-3">
                            <Timer className="w-4 h-4 text-purple-500" />
                            <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">{t('dashboard.today_workout')}</span>
                        </div>
                        {todayExercises.length === 0 ? (
                            <p className="text-sm text-gray-400">{t('dashboard.no_today_workout')}</p>
                        ) : (
                            <div className="space-y-1.5">
                                {todayExercises.map((ex, i) => (
                                    <div key={i} className="flex items-center gap-3 py-1">
                                        <span className="w-5 h-5 flex items-center justify-center bg-purple-100 dark:bg-purple-900/30 text-purple-600 text-[10px] font-bold rounded-full">{i + 1}</span>
                                        <div>
                                            <p className="text-sm font-medium text-gray-800 dark:text-gray-100">{ex.name}</p>
                                            <p className="text-[11px] text-gray-400">{ex.sets} × {ex.repetitions}</p>
                                        </div>
                                        {ex.workoutLabel && (
                                            <span className="ml-auto text-[10px] bg-purple-50 dark:bg-purple-900/20 text-purple-600 dark:text-purple-400 px-2 py-0.5 rounded-full font-medium">{ex.workoutLabel}</span>
                                        )}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                    {isLoading ? (
                        <div className="p-12 text-center text-gray-400">...</div>
                    ) : exercises.length === 0 ? (
                        <div className={`${glassCard} p-10 text-center text-gray-500`}>
                            {t('workout_view.no_workouts')}
                        </div>
                    ) : (
                        exercises.map((ex, idx) => {
                            const embedUrl = ex.videoUrl ? getEmbedUrl(ex.videoUrl) : null;
                            const thumbnail = ex.videoUrl ? getYoutubeThumbnail(ex.videoUrl) : null;
                            const hasPr = prMap[ex.id];
                            const history = historyData[ex.id] ?? [];
                            const chartData = history.map(h => ({
                                date: new Date(h.completedAt).toLocaleDateString(),
                                weight: h.actualWeight ?? 0
                            }));

                            return (
                                <div key={ex.id} className={glassCard + ' overflow-hidden'}>
                                    {thumbnail && (
                                        <div
                                            className="relative w-full h-44 cursor-pointer group"
                                            onClick={() => embedUrl && setModalUrl(embedUrl)}
                                        >
                                            <img src={thumbnail} alt={ex.name} className="w-full h-full object-cover" />
                                            <div className="absolute inset-0 bg-black/30 group-hover:bg-black/50 transition-colors flex items-center justify-center">
                                                <div className="w-14 h-14 rounded-full bg-white/20 backdrop-blur-sm border border-white/40 flex items-center justify-center group-hover:scale-110 transition-transform">
                                                    <Play className="w-6 h-6 text-white fill-white ml-1" />
                                                </div>
                                            </div>
                                            <div className="absolute bottom-3 left-3">
                                                <span className="text-xs bg-black/60 text-white px-2 py-0.5 rounded-full">{t('workout_view.watch_video')}</span>
                                            </div>
                                        </div>
                                    )}
                                    {embedUrl && !thumbnail && (
                                        <div
                                            className="relative w-full h-44 bg-gray-900 cursor-pointer flex items-center justify-center group"
                                            onClick={() => setModalUrl(embedUrl)}
                                        >
                                            <div className="w-14 h-14 rounded-full bg-white/20 backdrop-blur-sm border border-white/40 flex items-center justify-center group-hover:scale-110 transition-transform">
                                                <Play className="w-6 h-6 text-white fill-white ml-1" />
                                            </div>
                                        </div>
                                    )}

                                    <div className="p-5">
                                        <div className="flex items-start justify-between mb-4">
                                            <div>
                                                <span className="text-xs text-gray-400 font-medium">#{idx + 1}</span>
                                                <div className="flex items-center gap-2 mt-0.5">
                                                    <h3 className="text-base font-semibold text-gray-900 dark:text-gray-100">{ex.name}</h3>
                                                    {hasPr && (
                                                        <span className="text-xs bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 px-2 py-0.5 rounded-full font-medium">
                                                            🏆 PR
                                                        </span>
                                                    )}
                                                </div>
                                                {ex.restTime && (
                                                    <span className="text-xs text-gray-400 mt-0.5 flex items-center gap-1">
                                                        <Timer className="w-3 h-3" />
                                                        {ex.restTime}s {t('rest_timer.rest_label')}
                                                    </span>
                                                )}
                                            </div>
                                            <div className="flex items-center gap-2">
                                                {ex.tonnage !== null && ex.tonnage > 0 && (
                                                    <div className="bg-purple-50/80 dark:bg-purple-900/30 text-purple-600 dark:text-purple-300 text-xs font-semibold px-3 py-1 rounded-full">
                                                        {ex.tonnage.toFixed(1)} kg
                                                    </div>
                                                )}
                                                {ex.videoUrl && !embedUrl && (
                                                    <a
                                                        href={ex.videoUrl}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                        className="w-8 h-8 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                                        title="Watch video"
                                                    >
                                                        <ExternalLink className="w-4 h-4" />
                                                    </a>
                                                )}
                                                {user.role === 'PERSONALTRAINER' && (
                                                    <>
                                                        <button
                                                            onClick={() => openEditModal(ex)}
                                                            className="w-8 h-8 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                                            title="Edit exercise"
                                                        >
                                                            <Pencil className="w-4 h-4" />
                                                        </button>
                                                        <button
                                                            onClick={() => handleDeleteExercise(ex.id)}
                                                            disabled={deletingId === ex.id}
                                                            className="w-8 h-8 rounded-full bg-red-50 dark:bg-red-900/30 text-red-500 dark:text-red-400 flex items-center justify-center hover:bg-red-100 dark:hover:bg-red-900/50 disabled:opacity-50 transition-colors active:scale-95"
                                                            title="Delete exercise"
                                                        >
                                                            {deletingId === ex.id ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
                                                        </button>
                                                    </>
                                                )}
                                                <button
                                                    onClick={() => startTimer(ex.restTime ?? 90, ex.name)}
                                                    className="w-8 h-8 rounded-full bg-gray-100 dark:bg-slate-800 text-gray-500 dark:text-gray-400 flex items-center justify-center hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors active:scale-95"
                                                    title={t('rest_timer.start_rest')}
                                                >
                                                    <Timer className="w-4 h-4" />
                                                </button>
                                                <button
                                                    onClick={() => openLogModal(ex)}
                                                    className="w-8 h-8 rounded-full bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 flex items-center justify-center hover:bg-blue-100 dark:hover:bg-blue-900/50 transition-colors active:scale-95"
                                                    title={t('workout_log.log_set')}
                                                >
                                                    <CheckCircle className="w-4 h-4" />
                                                </button>
                                            </div>
                                        </div>

                                        <div className="grid grid-cols-3 gap-3">
                                            <div className="bg-blue-50/60 dark:bg-blue-900/20 rounded-2xl p-3 text-center">
                                                <p className="text-xs text-blue-400 dark:text-blue-500 uppercase tracking-wide mb-1">{t('workout_view.sets')}</p>
                                                <p className="text-lg font-bold text-blue-600 dark:text-blue-400">{ex.sets}</p>
                                            </div>
                                            <div className="bg-green-50/60 dark:bg-green-900/20 rounded-2xl p-3 text-center">
                                                <p className="text-xs text-green-400 dark:text-green-500 uppercase tracking-wide mb-1">{t('workout_view.repetitions')}</p>
                                                <p className="text-lg font-bold text-green-600 dark:text-green-400">{ex.repetitions}</p>
                                            </div>
                                            <div className="bg-orange-50/60 dark:bg-orange-900/20 rounded-2xl p-3 text-center">
                                                <p className="text-xs text-orange-400 dark:text-orange-500 uppercase tracking-wide mb-1">{t('workout_view.weight')}</p>
                                                <p className="text-lg font-bold text-orange-600 dark:text-orange-400">{ex.weightInKg ?? '—'}</p>
                                            </div>
                                        </div>

                                        <button
                                            onClick={() => toggleHistory(ex.id)}
                                            className="mt-3 w-full text-xs text-gray-400 hover:text-blue-500 transition-colors text-center py-1 active:scale-95"
                                        >
                                            {showHistory[ex.id] ? '▲' : '▼'} {t('workout_log.strength_chart')}
                                        </button>

                                        {showHistory[ex.id] && (
                                            <div className="mt-2">
                                                {chartData.length < 2 ? (
                                                    <p className="text-xs text-gray-400 text-center py-3">{t('workout_log.no_history')}</p>
                                                ) : (
                                                    <ResponsiveContainer width="100%" height={80}>
                                                        <LineChart data={chartData}>
                                                            <XAxis dataKey="date" hide />
                                                            <YAxis hide domain={['auto', 'auto']} />
                                                            <Tooltip
                                                                contentStyle={{ fontSize: '11px', borderRadius: '8px', border: 'none', background: 'rgba(0,0,0,0.75)', color: '#fff' }}
                                                                formatter={(v: number) => [`${v} kg`, '']}
                                                            />
                                                            <Line type="monotone" dataKey="weight" stroke="#3b82f6" strokeWidth={2} dot={{ r: 3 }} />
                                                        </LineChart>
                                                    </ResponsiveContainer>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            );
                        })
                    )}
                    </> /* end card view */
                    )}
                </main>
            </div>
        </div>
    );
}
