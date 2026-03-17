import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

interface Student {
    id: string;
    name: string;
}

interface FormData {
    weight: string;
    bodyFatPercentage: string;
    rightBicep: string;
    leftBicep: string;
    chest: string;
    waist: string;
    abdomen: string;
    hips: string;
    leftThigh: string;
    rightThigh: string;
    rightCalf: string;
    leftCalf: string;
}

function emptyForm(): FormData {
    return {
        weight: '', bodyFatPercentage: '',
        rightBicep: '', leftBicep: '', chest: '', waist: '',
        abdomen: '', hips: '', leftThigh: '', rightThigh: '',
        rightCalf: '', leftCalf: ''
    };
}

export function AssessmentForm() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const [students, setStudents] = useState<Student[]>([]);
    const [selectedStudentId, setSelectedStudentId] = useState('');
    const [form, setForm] = useState<FormData>(emptyForm());
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        api.get('/users/students').then(r => setStudents(r.data)).catch(() => {});
    }, []);

    if (!user) return null;

    function update(field: keyof FormData, value: string) {
        setForm(prev => ({ ...prev, [field]: value }));
    }

    function toNum(val: string): number | null {
        return val !== '' ? Number(val) : null;
    }

    async function handleSubmit() {
        setErrorMessage('');
        setSuccessMessage('');

        if (!selectedStudentId) {
            setErrorMessage(t('assessment_form.error_no_student'));
            return;
        }

        const payload = {
            studentId: selectedStudentId,
            weight: toNum(form.weight),
            bodyFatPercentage: toNum(form.bodyFatPercentage),
            rightBicep: toNum(form.rightBicep),
            leftBicep: toNum(form.leftBicep),
            chest: toNum(form.chest),
            waist: toNum(form.waist),
            abdomen: toNum(form.abdomen),
            hips: toNum(form.hips),
            leftThigh: toNum(form.leftThigh),
            rightThigh: toNum(form.rightThigh),
            rightCalf: toNum(form.rightCalf),
            leftCalf: toNum(form.leftCalf)
        };

        try {
            setIsSubmitting(true);
            await api.post('/measurements', payload);
            setSuccessMessage(t('assessment_form.success'));
            setForm(emptyForm());
            setSelectedStudentId('');
        } catch {
            setErrorMessage(t('assessment_form.error_generic'));
        } finally {
            setIsSubmitting(false);
        }
    }

    const inputClass = "w-full border border-white/30 dark:border-white/10 bg-white/50 dark:bg-white/5 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 backdrop-blur-sm";
    const labelClass = "block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1";
    const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

    const circumferenceFields: { key: keyof FormData; label: string }[] = [
        { key: 'rightBicep', label: t('assessment_form.right_bicep') },
        { key: 'leftBicep', label: t('assessment_form.left_bicep') },
        { key: 'chest', label: t('assessment_form.chest') },
        { key: 'waist', label: t('assessment_form.waist') },
        { key: 'abdomen', label: t('assessment_form.abdomen') },
        { key: 'hips', label: t('assessment_form.hips') },
        { key: 'rightThigh', label: t('assessment_form.right_thigh') },
        { key: 'leftThigh', label: t('assessment_form.left_thigh') },
        { key: 'rightCalf', label: t('assessment_form.right_calf') },
        { key: 'leftCalf', label: t('assessment_form.left_calf') },
    ];

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/60 dark:bg-slate-900/70 backdrop-blur-xl border-b border-white/20 dark:border-white/10 p-5 sticky top-0 z-10">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                        {t('assessment_form.title')}
                    </h2>
                </header>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">
                    {successMessage && (
                        <div className="p-3 bg-green-50 border border-green-200 text-green-700 rounded-2xl text-sm">
                            {successMessage}
                        </div>
                    )}
                    {errorMessage && (
                        <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-2xl text-sm">
                            {errorMessage}
                        </div>
                    )}

                    <div className={`${glassCard} p-6`}>
                        <label className={labelClass}>{t('assessment_form.select_student')}</label>
                        <select
                            value={selectedStudentId}
                            onChange={e => setSelectedStudentId(e.target.value)}
                            className={inputClass}
                        >
                            <option value="">{t('assessment_form.select_student_placeholder')}</option>
                            {students.map(s => (
                                <option key={s.id} value={s.id}>{s.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                            {t('assessment_form.body_composition')}
                        </h3>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className={labelClass}>{t('assessment_form.weight')}</label>
                                <input type="number" min={0} step={0.1} value={form.weight} onChange={e => update('weight', e.target.value)} className={inputClass} />
                            </div>
                            <div>
                                <label className={labelClass}>{t('assessment_form.body_fat')}</label>
                                <input type="number" min={0} max={100} step={0.1} value={form.bodyFatPercentage} onChange={e => update('bodyFatPercentage', e.target.value)} className={inputClass} />
                            </div>
                        </div>
                    </div>

                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                            {t('assessment_form.circumferences')}
                        </h3>
                        <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
                            {circumferenceFields.map(({ key, label }) => (
                                <div key={key}>
                                    <label className={labelClass}>{label}</label>
                                    <input type="number" min={0} step={0.1} value={form[key]} onChange={e => update(key, e.target.value)} className={inputClass} />
                                </div>
                            ))}
                        </div>
                    </div>

                    <button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        className="w-full py-3 bg-blue-600 text-white font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors"
                    >
                        {t('assessment_form.submit')}
                    </button>
                </main>
            </div>
        </div>
    );
}
