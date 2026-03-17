import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

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
        weight: '', bodyFatPercentage: '', rightBicep: '', leftBicep: '',
        chest: '', waist: '', abdomen: '', hips: '',
        leftThigh: '', rightThigh: '', rightCalf: '', leftCalf: ''
    };
}

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

export function StudentSelfAssessment() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState<FormData>(emptyForm());
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

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

        const payload = {
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
            await api.post('/measurements/my', payload);
            setSuccessMessage(t('self_assessment.success'));
            toast.success(t('toast.measurement_saved'));
            setForm(emptyForm());
            setTimeout(() => navigate('/measurements'), 1500);
        } catch {
            setErrorMessage(t('self_assessment.error'));
        } finally {
            setIsSubmitting(false);
        }
    }

    const inputClass = 'w-full border border-white/30 dark:border-white/10 bg-white/50 dark:bg-white/5 text-gray-900 dark:text-gray-100 rounded-2xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 backdrop-blur-sm';
    const labelClass = 'block text-xs font-medium text-gray-500 dark:text-gray-400 mb-1';

    const circumferenceFields: { key: keyof FormData; label: string }[] = [
        { key: 'rightBicep', label: t('self_assessment.right_bicep') },
        { key: 'leftBicep', label: t('self_assessment.left_bicep') },
        { key: 'chest', label: t('self_assessment.chest') },
        { key: 'waist', label: t('self_assessment.waist') },
        { key: 'abdomen', label: t('self_assessment.abdomen') },
        { key: 'hips', label: t('self_assessment.hips') },
        { key: 'rightThigh', label: t('self_assessment.right_thigh') },
        { key: 'leftThigh', label: t('self_assessment.left_thigh') },
        { key: 'rightCalf', label: t('self_assessment.right_calf') },
        { key: 'leftCalf', label: t('self_assessment.left_calf') },
    ];

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/60 dark:bg-slate-900/70 backdrop-blur-xl border-b border-white/20 dark:border-white/10 p-5 sticky top-0 z-10">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                        {t('self_assessment.title')}
                    </h2>
                </header>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">
                    {successMessage && (
                        <div className="p-3 bg-green-50/80 border border-green-200/60 text-green-700 rounded-2xl text-sm backdrop-blur-sm">
                            {successMessage}
                        </div>
                    )}
                    {errorMessage && (
                        <div className="p-3 bg-red-50/80 border border-red-200/60 text-red-700 rounded-2xl text-sm backdrop-blur-sm">
                            {errorMessage}
                        </div>
                    )}

                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                            {t('self_assessment.body_composition')}
                        </h3>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className={labelClass}>{t('self_assessment.weight')}</label>
                                <input type="number" min={0} step={0.1} value={form.weight} onChange={e => update('weight', e.target.value)} className={inputClass} />
                            </div>
                            <div>
                                <label className={labelClass}>{t('self_assessment.body_fat')}</label>
                                <input type="number" min={0} max={100} step={0.1} value={form.bodyFatPercentage} onChange={e => update('bodyFatPercentage', e.target.value)} className={inputClass} />
                            </div>
                        </div>
                    </div>

                    <div className={`${glassCard} p-6`}>
                        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                            {t('self_assessment.circumferences')}
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
                        className="w-full py-3.5 bg-blue-600 text-white font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors"
                    >
                        {t('self_assessment.submit')}
                    </button>
                </main>
            </div>
        </div>
    );
}
