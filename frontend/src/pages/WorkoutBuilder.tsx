import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

interface Student {
    id: string;
    name: string;
    email: string;
}

interface ExerciseForm {
    name: string;
    sets: number;
    repetitions: number;
    measurementType: 'WEIGHT' | 'BODYWEIGHT' | 'SPEED' | 'TIME';
    weightInKg: string;
    speed: string;
    timeInSeconds: string;
    cadence: string;
    videoUrl: string;
    restTime: string;
    workoutLabel: string;
    scheduledDays: string[];
}

const ALL_DAYS = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'] as const;
function emptyExercise(): ExerciseForm {
    return {
        name: '',
        sets: 1,
        repetitions: 1,
        measurementType: 'WEIGHT',
        weightInKg: '',
        speed: '',
        timeInSeconds: '',
        cadence: '',
        videoUrl: '',
        restTime: '90',
        workoutLabel: '',
        scheduledDays: [],
    };
}

function calcTonnage(ex: ExerciseForm): number | null {
    if ((ex.measurementType === 'WEIGHT' || ex.measurementType === 'BODYWEIGHT') && ex.weightInKg) {
        return ex.sets * ex.repetitions * Number(ex.weightInKg);
    }
    return null;
}

export function WorkoutBuilder() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const [students, setStudents] = useState<Student[]>([]);
    const [selectedStudentId, setSelectedStudentId] = useState('');
    const [exercises, setExercises] = useState<ExerciseForm[]>([emptyExercise()]);
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        api.get('/users/students').then(r => setStudents(r.data)).catch(() => {});
    }, []);

    if (!user) return null;

    function updateExercise(index: number, field: keyof ExerciseForm, value: string | number | string[]) {
        setExercises(prev => prev.map((ex, i) => i === index ? { ...ex, [field]: value } : ex));
    }

    function toggleDay(index: number, day: string) {
        setExercises(prev => prev.map((ex, i) => {
            if (i !== index) return ex;
            const days = ex.scheduledDays.includes(day)
                ? ex.scheduledDays.filter(d => d !== day)
                : [...ex.scheduledDays, day];
            return { ...ex, scheduledDays: days };
        }));
    }

    function addExercise() {
        setExercises(prev => [...prev, emptyExercise()]);
    }

    function removeExercise(index: number) {
        setExercises(prev => prev.filter((_, i) => i !== index));
    }

    const totalTonnage = exercises.reduce((sum, ex) => {
        const t = calcTonnage(ex);
        return t !== null ? sum + t : sum;
    }, 0);

    async function handleSubmit() {
        setErrorMessage('');
        setSuccessMessage('');

        if (!selectedStudentId) {
            setErrorMessage(t('workout_builder.error_no_student'));
            return;
        }
        if (exercises.length === 0) {
            setErrorMessage(t('workout_builder.error_no_exercises'));
            return;
        }

        const payload = {
            studentId: selectedStudentId,
            exercises: exercises.map(ex => ({
                name: ex.name,
                sets: Number(ex.sets),
                repetitions: Number(ex.repetitions),
                measurementType: ex.measurementType,
                weightInKg: ex.weightInKg ? Number(ex.weightInKg) : null,
                speed: ex.speed ? Number(ex.speed) : null,
                timeInSeconds: ex.timeInSeconds ? Number(ex.timeInSeconds) : null,
                cadence: ex.cadence || null,
                videoUrl: ex.videoUrl || null,
                restTime: ex.restTime ? Number(ex.restTime) : null,
                workoutLabel: ex.workoutLabel || null,
                scheduledDays: ex.scheduledDays.length > 0 ? ex.scheduledDays : null
            }))
        };

        try {
            setIsSubmitting(true);
            await api.post('/workouts', payload);
            setSuccessMessage(t('workout_builder.success'));
            toast.success(t('toast.workout_saved'));
            setExercises([emptyExercise()]);
            setSelectedStudentId('');
        } catch {
            setErrorMessage(t('workout_builder.error_no_exercises'));
        } finally {
            setIsSubmitting(false);
        }
    }

    const typeLabels: Record<string, string> = {
        WEIGHT: t('workout_builder.type_weight'),
        BODYWEIGHT: t('workout_builder.type_bodyweight'),
        SPEED: t('workout_builder.type_speed'),
        TIME: t('workout_builder.type_time')
    };

    const inputClass = "w-full border border-white/30 dark:border-white/10 bg-white/50 dark:bg-white/5 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 backdrop-blur-sm";
    const labelClass = "block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1";

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0">
                <header className="bg-white/60 dark:bg-slate-900/70 backdrop-blur-xl border-b border-white/20 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                            {t('workout_builder.title')}
                        </h2>
                        {totalTonnage > 0 && (
                            <div className="text-sm font-medium text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/30 px-4 py-1.5 rounded-full">
                                {t('workout_builder.total_estimated_tonnage')}: <strong>{totalTonnage.toFixed(1)} kg</strong>
                            </div>
                        )}
                    </div>
                </header>

                <main className="p-4 md:p-8 max-w-3xl pb-24 md:pb-8">
                    {successMessage && (
                        <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-2xl text-sm">
                            {successMessage}
                        </div>
                    )}
                    {errorMessage && (
                        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-2xl text-sm">
                            {errorMessage}
                        </div>
                    )}

                    <div className="bg-white dark:bg-slate-900 rounded-3xl shadow-sm border border-gray-200 dark:border-slate-800 p-6 mb-6">
                        <label className={labelClass}>{t('workout_builder.select_student')}</label>
                        <select
                            value={selectedStudentId}
                            onChange={e => setSelectedStudentId(e.target.value)}
                            className={inputClass}
                        >
                            <option value="">{t('workout_builder.select_student_placeholder')}</option>
                            {students.map(s => (
                                <option key={s.id} value={s.id}>{s.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="space-y-4">
                        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest">
                            {t('workout_builder.exercises_title')}
                        </h3>

                        {exercises.map((ex, idx) => {
                            const tonnage = calcTonnage(ex);
                            return (
                                <div key={idx} className="bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm p-6">
                                    <div className="flex justify-between items-center mb-4">
                                        <div className="flex items-center gap-3">
                                            <span className="w-7 h-7 flex items-center justify-center bg-blue-50 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 text-xs font-bold rounded-full">
                                                {idx + 1}
                                            </span>
                                            {tonnage !== null && (
                                                <span className="text-xs font-medium text-blue-500 bg-blue-50 dark:bg-blue-900/20 px-2 py-0.5 rounded-full">
                                                    {t('workout_builder.estimated_tonnage')}: {tonnage.toFixed(1)} kg
                                                </span>
                                            )}
                                        </div>
                                        {exercises.length > 1 && (
                                            <button
                                                onClick={() => removeExercise(idx)}
                                                className="text-xs text-red-400 hover:text-red-600"
                                            >
                                                {t('workout_builder.remove')}
                                            </button>
                                        )}
                                    </div>

                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="col-span-2">
                                            <label className={labelClass}>{t('workout_builder.exercise_name')}</label>
                                            <input type="text" value={ex.name} onChange={e => updateExercise(idx, 'name', e.target.value)} className={inputClass} />
                                        </div>

                                        <div>
                                            <label className={labelClass}>{t('workout_builder.sets')}</label>
                                            <input type="number" min={1} value={ex.sets} onChange={e => updateExercise(idx, 'sets', Number(e.target.value))} className={inputClass} />
                                        </div>

                                        <div>
                                            <label className={labelClass}>{t('workout_builder.repetitions')}</label>
                                            <input type="number" min={1} value={ex.repetitions} onChange={e => updateExercise(idx, 'repetitions', Number(e.target.value))} className={inputClass} />
                                        </div>

                                        <div className="col-span-2">
                                            <label className={labelClass}>{t('workout_builder.measurement_type')}</label>
                                            <select value={ex.measurementType} onChange={e => updateExercise(idx, 'measurementType', e.target.value)} className={inputClass}>
                                                {Object.entries(typeLabels).map(([val, label]) => (
                                                    <option key={val} value={val}>{label}</option>
                                                ))}
                                            </select>
                                        </div>

                                        {(ex.measurementType === 'WEIGHT' || ex.measurementType === 'BODYWEIGHT') && (
                                            <div>
                                                <label className={labelClass}>{t('workout_builder.weight_kg')}</label>
                                                <input type="number" min={0} step={0.5} value={ex.weightInKg} onChange={e => updateExercise(idx, 'weightInKg', e.target.value)} className={inputClass} />
                                            </div>
                                        )}

                                        {ex.measurementType === 'SPEED' && (
                                            <div>
                                                <label className={labelClass}>{t('workout_builder.speed')}</label>
                                                <input type="number" min={0} step={0.1} value={ex.speed} onChange={e => updateExercise(idx, 'speed', e.target.value)} className={inputClass} />
                                            </div>
                                        )}

                                        {(ex.measurementType === 'TIME' || ex.measurementType === 'SPEED') && (
                                            <div>
                                                <label className={labelClass}>{t('workout_builder.time_seconds')}</label>
                                                <input type="number" min={1} value={ex.timeInSeconds} onChange={e => updateExercise(idx, 'timeInSeconds', e.target.value)} className={inputClass} />
                                            </div>
                                        )}

                                        <div>
                                            <label className={labelClass}>{t('workout_builder.cadence')}</label>
                                            <input type="text" value={ex.cadence} onChange={e => updateExercise(idx, 'cadence', e.target.value)} className={inputClass} />
                                        </div>

                                        <div className="col-span-2">
                                            <label className={labelClass}>{t('workout_builder.video_url')}</label>
                                            <input type="url" value={ex.videoUrl} onChange={e => updateExercise(idx, 'videoUrl', e.target.value)} className={inputClass} placeholder="https://www.youtube.com/watch?v=..." />
                                        </div>

                                        <div>
                                            <label className={labelClass}>{t('rest_timer.rest_time_label')} (s)</label>
                                            <input type="number" min={0} step={15} value={ex.restTime} onChange={e => updateExercise(idx, 'restTime', e.target.value)} className={inputClass} />
                                        </div>

                                        <div className="col-span-2">
                                            <label className={labelClass}>{t('workout_builder.workout_label')}</label>
                                            <input type="text" value={ex.workoutLabel} onChange={e => updateExercise(idx, 'workoutLabel', e.target.value)} placeholder={t('workout_builder.workout_label_placeholder')} className={inputClass} />
                                        </div>

                                        <div className="col-span-2">
                                            <label className={labelClass}>{t('workout_builder.scheduled_days')}</label>
                                            <div className="flex gap-1.5 flex-wrap mt-1">
                                                {ALL_DAYS.map(day => (
                                                    <button
                                                        key={day}
                                                        type="button"
                                                        onClick={() => toggleDay(idx, day)}
                                                        className={`px-2.5 py-1 rounded-xl text-xs font-bold transition-colors ${
                                                            ex.scheduledDays.includes(day)
                                                                ? 'bg-blue-600 text-white'
                                                                : 'bg-white/50 dark:bg-white/5 border border-white/30 dark:border-white/10 text-gray-500 dark:text-gray-400 hover:bg-blue-50 dark:hover:bg-blue-900/20'
                                                        }`}
                                                    >
                                                        {t(`workout_builder.days_${day.toLowerCase()}` as any)}
                                                    </button>
                                                ))}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}

                        <button
                            onClick={addExercise}
                            className="w-full py-3 border-2 border-dashed border-gray-200 dark:border-slate-700 rounded-2xl text-sm text-gray-400 hover:border-blue-400 hover:text-blue-500 transition-colors"
                        >
                            + {t('workout_builder.add_exercise')}
                        </button>
                    </div>

                    <div className="mt-6">
                        <button
                            onClick={handleSubmit}
                            disabled={isSubmitting}
                            className="w-full py-3 bg-blue-600 text-white font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors"
                        >
                            {t('workout_builder.submit')}
                        </button>
                    </div>
                </main>
            </div>
        </div>
    );
}
