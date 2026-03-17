import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PenLine, Download, Loader2 } from 'lucide-react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

interface Measurement {
    id: string;
    recordedAt: string;
    weight: number | null;
    bodyFatPercentage: number | null;
    rightBicep: number | null;
    leftBicep: number | null;
    chest: number | null;
    waist: number | null;
    abdomen: number | null;
    hips: number | null;
    leftThigh: number | null;
    rightThigh: number | null;
    rightCalf: number | null;
    leftCalf: number | null;
}

const CHART_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#f97316', '#84cc16'];

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

export function MeasurementsPage() {
    const { t, i18n } = useTranslation();
    const { user } = useAuth();
    const [measurements, setMeasurements] = useState<Measurement[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isDownloading, setIsDownloading] = useState(false);

    useEffect(() => {
        api.get('/measurements/my')
            .then(r => setMeasurements(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }, []);

    if (!user) return null;

    async function handleDownloadProgress() {
        setIsDownloading(true);
        try {
            const response = await api.get('/reports/progress/my', { responseType: 'blob', headers: { 'Accept-Language': i18n.language } });
            const url = URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const a = document.createElement('a');
            a.href = url;
            a.download = 'progress_report.pdf';
            a.click();
            URL.revokeObjectURL(url);
        } catch {
        } finally {
            setIsDownloading(false);
        }
    }

    const compositionData = measurements.map(m => ({
        date: m.recordedAt,
        [t('measurements.weight')]: m.weight,
        [t('measurements.body_fat')]: m.bodyFatPercentage,
    }));

    const circumferenceData = measurements.map(m => ({
        date: m.recordedAt,
        [t('measurements.chest')]: m.chest,
        [t('measurements.waist')]: m.waist,
        [t('measurements.abdomen')]: m.abdomen,
        [t('measurements.hips')]: m.hips,
        [t('measurements.right_bicep')]: m.rightBicep,
        [t('measurements.left_bicep')]: m.leftBicep,
    }));

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/60 dark:bg-slate-900/70 backdrop-blur-xl border-b border-white/20 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                            {t('measurements.title')}
                        </h2>
                        <div className="flex items-center gap-2">
                            {measurements.length >= 2 && (
                                <button
                                    onClick={handleDownloadProgress}
                                    disabled={isDownloading}
                                    className="flex items-center gap-2 bg-gray-100 dark:bg-slate-800 hover:bg-gray-200 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-200 text-sm font-medium px-4 py-2 rounded-2xl disabled:opacity-50 transition-colors active:scale-95"
                                >
                                    {isDownloading
                                        ? <Loader2 className="w-4 h-4 animate-spin" />
                                        : <Download className="w-4 h-4" />}
                                    {isDownloading ? t('reports.generating') : t('reports.download_progress')}
                                </button>
                            )}
                            <Link
                                to="/measurements/log"
                                className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-4 py-2 rounded-2xl transition-colors"
                            >
                                <PenLine className="w-4 h-4" />
                                {t('measurements.log_button')}
                            </Link>
                        </div>
                    </div>
                </header>

                <main className="p-4 md:p-6 space-y-5 pb-24 md:pb-6">
                    {isLoading ? (
                        <div className="p-12 text-center text-gray-400">...</div>
                    ) : measurements.length === 0 ? (
                        <div className={`${glassCard} p-10 text-center`}>
                            <p className="text-gray-500 mb-4">{t('measurements.no_data')}</p>
                            <Link
                                to="/measurements/log"
                                className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium px-5 py-2.5 rounded-2xl transition-colors"
                            >
                                <PenLine className="w-4 h-4" />
                                {t('measurements.log_button')}
                            </Link>
                        </div>
                    ) : (
                        <>
                            <div className={`${glassCard} p-6`}>
                                <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                                    {t('measurements.chart_title')}
                                </h3>
                                <ResponsiveContainer width="100%" height={260}>
                                    <LineChart data={compositionData}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb20" />
                                        <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                                        <YAxis tick={{ fontSize: 11 }} />
                                        <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', backdropFilter: 'blur(8px)' }} />
                                        <Legend />
                                        <Line type="monotone" dataKey={t('measurements.weight')} stroke="#3b82f6" dot={{ r: 4 }} strokeWidth={2} />
                                        <Line type="monotone" dataKey={t('measurements.body_fat')} stroke="#f97316" dot={{ r: 4 }} strokeWidth={2} />
                                    </LineChart>
                                </ResponsiveContainer>
                            </div>

                            <div className={`${glassCard} p-6`}>
                                <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest mb-4">
                                    {t('measurements.circumference_chart_title')}
                                </h3>
                                <ResponsiveContainer width="100%" height={260}>
                                    <LineChart data={circumferenceData}>
                                        <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb20" />
                                        <XAxis dataKey="date" tick={{ fontSize: 11 }} />
                                        <YAxis tick={{ fontSize: 11 }} />
                                        <Tooltip contentStyle={{ borderRadius: '12px', border: 'none', backdropFilter: 'blur(8px)' }} />
                                        <Legend />
                                        {[
                                            t('measurements.chest'), t('measurements.waist'), t('measurements.abdomen'),
                                            t('measurements.hips'), t('measurements.right_bicep'), t('measurements.left_bicep')
                                        ].map((key, i) => (
                                            <Line key={key} type="monotone" dataKey={key} stroke={CHART_COLORS[i % CHART_COLORS.length]} dot={{ r: 3 }} strokeWidth={2} />
                                        ))}
                                    </LineChart>
                                </ResponsiveContainer>
                            </div>

                            <div className={`${glassCard} overflow-hidden`}>
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left">
                                        <thead>
                                            <tr className="border-b border-white/20 dark:border-white/10">
                                                {[
                                                    t('measurements.date'), t('measurements.weight'),
                                                    t('measurements.body_fat'), t('measurements.chest'),
                                                    t('measurements.waist'), t('measurements.abdomen'),
                                                    t('measurements.hips'), t('measurements.right_bicep'),
                                                    t('measurements.left_bicep')
                                                ].map(h => (
                                                    <th key={h} className="px-5 py-3.5 text-xs font-semibold text-gray-400 uppercase tracking-wide whitespace-nowrap">{h}</th>
                                                ))}
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {measurements.map((m, i) => (
                                                <tr key={m.id} className={i % 2 === 0 ? '' : 'bg-white/10 dark:bg-white/5'}>
                                                    <td className="px-5 py-4 text-sm font-medium text-gray-700 dark:text-gray-200 whitespace-nowrap">{m.recordedAt}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.weight ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.bodyFatPercentage ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.chest ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.waist ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.abdomen ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.hips ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.rightBicep ?? '—'}</td>
                                                    <td className="px-5 py-4 text-sm text-gray-600 dark:text-gray-300">{m.leftBicep ?? '—'}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </>
                    )}
                </main>
            </div>
        </div>
    );
}
