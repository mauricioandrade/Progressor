import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
    Camera, Trash2, Loader2, ImageOff, SplitSquareHorizontal, X,
    MessageSquare, Send, ChevronDown, ChevronUp, Users
} from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { BeforeAfterSlider } from '../components/BeforeAfterSlider';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

interface PatientSummary { id: string; name: string; }

interface ProgressPhoto {
    id: string;
    takenAt: string;
    description: string;
    photoBase64: string;
    professionalFeedback: string | null;
    professionalName: string | null;
    studentNotes: string | null;
}

const tile =
    'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

function formatDate(iso: string) {
    return new Date(iso).toLocaleDateString(undefined, {
        year: 'numeric', month: 'short', day: 'numeric',
    });
}

interface FeedbackPanelProps {
    photo: ProgressPhoto;
    isProfessional: boolean;
    onFeedbackSaved: (updated: ProgressPhoto) => void;
}

function FeedbackPanel({ photo, isProfessional, onFeedbackSaved }: FeedbackPanelProps) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [draft, setDraft] = useState('');
    const [saving, setSaving] = useState(false);

    async function handleSend() {
        if (!draft.trim()) return;
        setSaving(true);
        try {
            const r = await api.patch(`/progress-photos/${photo.id}/feedback`, { feedback: draft.trim() });
            onFeedbackSaved(r.data);
            setDraft('');
            toast.success(t('progress.feedback_saved'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setSaving(false);
        }
    }

    const hasFeedback = !!photo.professionalFeedback;

    return (
        <div className="border-t border-black/5 dark:border-white/[0.06]">
            <button
                onClick={() => setOpen(v => !v)}
                className="w-full flex items-center justify-between px-3 py-2 text-[11px] font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-colors"
            >
                <span className="flex items-center gap-1.5">
                    <MessageSquare className="w-3.5 h-3.5" />
                    {hasFeedback ? t('progress.feedback_label') : t('progress.feedback_none')}
                    {hasFeedback && (
                        <span className="w-1.5 h-1.5 rounded-full bg-violet-500" />
                    )}
                </span>
                {open ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
            </button>

            {open && (
                <div className="px-3 pb-3 space-y-2">
                    {hasFeedback && (
                        <div className="rounded-xl bg-violet-50 dark:bg-violet-900/20 border border-violet-100 dark:border-violet-800/40 p-2.5">
                            <p className="text-[10px] font-bold text-violet-600 dark:text-violet-400 mb-0.5">
                                {photo.professionalName}
                            </p>
                            <p className="text-xs text-gray-700 dark:text-gray-300 leading-relaxed">
                                {photo.professionalFeedback}
                            </p>
                        </div>
                    )}

                    {isProfessional && (
                        <div className="flex gap-1.5">
                            <input
                                type="text"
                                value={draft}
                                onChange={e => setDraft(e.target.value)}
                                placeholder={t('progress.feedback_placeholder')}
                                className="flex-1 px-2.5 py-1.5 text-xs border border-black/10 dark:border-white/10 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:ring-2 focus:ring-violet-500 outline-none"
                                onKeyDown={e => e.key === 'Enter' && !e.shiftKey && handleSend()}
                            />
                            <button
                                onClick={handleSend}
                                disabled={saving || !draft.trim()}
                                className="w-8 h-8 flex items-center justify-center rounded-xl bg-violet-600 text-white hover:bg-violet-700 disabled:opacity-50 active:scale-90 transition-all shrink-0"
                            >
                                {saving
                                    ? <Loader2 className="w-3.5 h-3.5 animate-spin" />
                                    : <Send className="w-3.5 h-3.5" />}
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

interface StudentNotesPanelProps {
    photo: ProgressPhoto;
    onNotesSaved: (updated: ProgressPhoto) => void;
}

function StudentNotesPanel({ photo, onNotesSaved }: StudentNotesPanelProps) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [draft, setDraft] = useState(photo.studentNotes ?? '');
    const [saving, setSaving] = useState(false);

    async function handleSave() {
        setSaving(true);
        try {
            const r = await api.patch(`/progress-photos/${photo.id}/student-notes`, { notes: draft.trim() });
            onNotesSaved(r.data);
            toast.success(t('progress.student_notes_saved'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setSaving(false);
        }
    }

    const hasNotes = !!photo.studentNotes;

    return (
        <div className="border-t border-black/5 dark:border-white/[0.06]">
            <button
                onClick={() => setOpen(v => !v)}
                className="w-full flex items-center justify-between px-3 py-2 text-[11px] font-semibold text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-colors"
            >
                <span className="flex items-center gap-1.5">
                    <Send className="w-3.5 h-3.5" />
                    {t('progress.student_notes_label')}
                    {hasNotes && <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />}
                </span>
                {open ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
            </button>

            {open && (
                <div className="px-3 pb-3 space-y-2">
                    <textarea
                        value={draft}
                        onChange={e => setDraft(e.target.value)}
                        placeholder={t('progress.student_notes_placeholder')}
                        rows={3}
                        className="w-full px-2.5 py-1.5 text-xs border border-black/10 dark:border-white/10 rounded-xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 focus:ring-2 focus:ring-emerald-500 outline-none resize-none"
                    />
                    <button
                        onClick={handleSave}
                        disabled={saving}
                        className="w-full flex items-center justify-center gap-1.5 py-1.5 bg-emerald-600 text-white text-xs font-semibold rounded-xl hover:bg-emerald-700 disabled:opacity-50 active:scale-95 transition-all"
                    >
                        {saving
                            ? <Loader2 className="w-3.5 h-3.5 animate-spin" />
                            : <Send className="w-3.5 h-3.5" />}
                        Save
                    </button>
                </div>
            )}
        </div>
    );
}

export function VisualProgressPage() {
    const { t } = useTranslation();
    const { user } = useAuth();
    const fileInputRef = useRef<HTMLInputElement>(null);

    const [photos, setPhotos] = useState<ProgressPhoto[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isUploading, setIsUploading] = useState(false);
    const [description, setDescription] = useState('');
    const [takenAt, setTakenAt] = useState('');

    const [compareA, setCompareA] = useState<ProgressPhoto | null>(null);
    const [compareB, setCompareB] = useState<ProgressPhoto | null>(null);
    const [showSlider, setShowSlider] = useState(false);

    const isStudent = user?.role === 'STUDENT';
    const isProfessional = user?.role === 'PERSONALTRAINER' || user?.role === 'NUTRITIONIST';

    const [patients, setPatients] = useState<PatientSummary[]>([]);
    const [selectedPatientId, setSelectedPatientId] = useState('');

    useEffect(() => {
        if (!isStudent) { setIsLoading(false); return; }
        api.get('/progress-photos/my')
            .then(r => setPhotos(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }, [isStudent]);

    useEffect(() => {
        if (!isProfessional) return;
        const endpoint = user?.role === 'NUTRITIONIST'
            ? '/users/my-students/nutritionist'
            : '/users/students';
        api.get(endpoint).then(r => setPatients(r.data)).catch(() => {});
    }, [isProfessional, user?.role]);

    useEffect(() => {
        if (!isProfessional || !selectedPatientId) return;
        setIsLoading(true);
        setPhotos([]);
        setCompareA(null);
        setCompareB(null);
        setShowSlider(false);
        api.get(`/progress-photos/student/${selectedPatientId}`)
            .then(r => setPhotos(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));
    }, [isProfessional, selectedPatientId]);

    if (!user) return null;

    async function handleUpload(e: React.ChangeEvent<HTMLInputElement>) {
        const file = e.target.files?.[0];
        if (!file) return;
        if (file.size > 5 * 1024 * 1024) {
            toast.error(t('progress.error_size'));
            return;
        }
        setIsUploading(true);
        const form = new FormData();
        form.append('file', file);
        if (description.trim()) form.append('description', description.trim());
        if (takenAt) form.append('takenAt', takenAt);
        try {
            const r = await api.post('/progress-photos', form, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            setPhotos(prev => [r.data, ...prev]);
            setDescription('');
            setTakenAt('');
            toast.success(t('progress.upload_success'));
        } catch {
            toast.error(t('toast.error_generic'));
        } finally {
            setIsUploading(false);
            if (fileInputRef.current) fileInputRef.current.value = '';
        }
    }

    async function handleDelete(photoId: string) {
        try {
            await api.delete(`/progress-photos/${photoId}`);
            setPhotos(prev => prev.filter(p => p.id !== photoId));
            if (compareA?.id === photoId) setCompareA(null);
            if (compareB?.id === photoId) setCompareB(null);
            setShowSlider(false);
            toast.success(t('progress.delete_success'));
        } catch {
            toast.error(t('toast.error_generic'));
        }
    }

    function updatePhoto(updated: ProgressPhoto) {
        setPhotos(prev => prev.map(p => p.id === updated.id ? updated : p));
    }

    function handleSelectCompare(photo: ProgressPhoto) {
        if (!compareA || (compareA && compareB)) {
            setCompareA(photo);
            setCompareB(null);
            setShowSlider(false);
        } else if (compareA.id === photo.id) {
            setCompareA(null);
        } else {
            setCompareB(photo);
        }
    }

    const bothSelected = compareA && compareB;

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0">
                <header className="bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border-b border-black/5 dark:border-white/[0.07] p-5 sticky top-0 z-20">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                        {t('progress.title')}
                    </h2>
                    <p className="text-xs text-gray-400 mt-0.5">{t('progress.subtitle')}</p>
                </header>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">

                    {/* Professional: student selector */}
                    {isProfessional && (
                        <div className={`${tile} p-5 space-y-3`}>
                            <div className="flex items-center gap-2 mb-1">
                                <Users className="w-4 h-4 text-violet-500" />
                                <span className="text-xs font-semibold text-gray-400 uppercase tracking-widest">
                                    {t('progress.select_student_label')}
                                </span>
                            </div>
                            <select
                                value={selectedPatientId}
                                onChange={e => setSelectedPatientId(e.target.value)}
                                className="w-full px-4 py-2.5 border border-black/15 dark:border-white/10 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-violet-500 outline-none text-sm appearance-none"
                            >
                                <option value="">{t('progress.select_student_placeholder')}</option>
                                {patients.map(p => (
                                    <option key={p.id} value={p.id}>{p.name}</option>
                                ))}
                            </select>
                            {!selectedPatientId && (
                                <p className="text-xs text-gray-400 flex items-center gap-1.5">
                                    <MessageSquare className="w-3.5 h-3.5 shrink-0" />
                                    {t('progress.professional_notice')}
                                </p>
                            )}
                        </div>
                    )}

                    {/* Upload card — students only */}
                    {isStudent && (
                        <div className={`${tile} p-5`}>
                            <h3 className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-4">
                                {t('progress.upload_section')}
                            </h3>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                                <div>
                                    <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                        {t('progress.description_label')}
                                    </label>
                                    <input
                                        type="text"
                                        value={description}
                                        onChange={e => setDescription(e.target.value)}
                                        placeholder={t('progress.description_placeholder')}
                                        className="w-full px-4 py-2.5 border border-black/15 dark:border-white/10 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 text-sm focus:ring-2 focus:ring-violet-500 outline-none"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                        {t('progress.date_label')}
                                    </label>
                                    <input
                                        type="date"
                                        value={takenAt}
                                        onChange={e => setTakenAt(e.target.value)}
                                        className="w-full px-4 py-2.5 border border-black/15 dark:border-white/10 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 text-sm focus:ring-2 focus:ring-violet-500 outline-none"
                                    />
                                </div>
                            </div>
                            <button
                                onClick={() => fileInputRef.current?.click()}
                                disabled={isUploading}
                                className="w-full flex items-center justify-center gap-2 py-3 bg-violet-600 text-white font-semibold text-sm rounded-2xl hover:bg-violet-700 disabled:opacity-50 active:scale-95 transition-all"
                            >
                                {isUploading
                                    ? <Loader2 className="w-4 h-4 animate-spin" />
                                    : <Camera className="w-4 h-4" />}
                                {isUploading ? t('progress.uploading') : t('progress.upload_button')}
                            </button>
                            <input
                                ref={fileInputRef}
                                type="file"
                                accept="image/jpeg,image/png,image/webp"
                                className="hidden"
                                onChange={handleUpload}
                            />
                        </div>
                    )}

                    {/* Compare selection bar — students only */}
                    {isStudent && photos.length >= 2 && (
                        <div className={`${tile} p-4 flex items-center gap-3`}>
                            <SplitSquareHorizontal className="w-4 h-4 text-violet-500 shrink-0" />
                            <p className="text-xs text-gray-500 dark:text-gray-400 flex-1">
                                {!compareA
                                    ? t('progress.compare_hint_first')
                                    : !compareB
                                    ? t('progress.compare_hint_second')
                                    : t('progress.compare_ready')}
                            </p>
                            {bothSelected && (
                                <button
                                    onClick={() => setShowSlider(true)}
                                    className="shrink-0 flex items-center gap-1.5 px-3 py-1.5 bg-violet-600 text-white text-xs font-bold rounded-xl hover:bg-violet-700 active:scale-95 transition-all"
                                >
                                    <SplitSquareHorizontal className="w-3.5 h-3.5" />
                                    {t('progress.compare_button')}
                                </button>
                            )}
                            {compareA && (
                                <button
                                    onClick={() => { setCompareA(null); setCompareB(null); setShowSlider(false); }}
                                    className="shrink-0 w-7 h-7 flex items-center justify-center rounded-full bg-black/5 dark:bg-white/10 text-gray-400 hover:text-red-500 transition-colors"
                                >
                                    <X className="w-3.5 h-3.5" />
                                </button>
                            )}
                        </div>
                    )}

                    {/* Before/After Slider */}
                    {showSlider && compareA && compareB && (
                        <div className={`${tile} p-4 space-y-3`}>
                            <div className="flex items-center justify-between">
                                <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-widest">
                                    {t('progress.comparison_title')}
                                </span>
                                <button
                                    onClick={() => setShowSlider(false)}
                                    className="w-7 h-7 flex items-center justify-center rounded-full bg-black/5 dark:bg-white/10 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 transition-colors"
                                >
                                    <X className="w-3.5 h-3.5" />
                                </button>
                            </div>
                            <BeforeAfterSlider
                                beforeSrc={`data:image/jpeg;base64,${compareA.photoBase64}`}
                                afterSrc={`data:image/jpeg;base64,${compareB.photoBase64}`}
                                beforeLabel={formatDate(compareA.takenAt)}
                                afterLabel={formatDate(compareB.takenAt)}
                            />
                            <p className="text-[11px] text-gray-400 text-center">{t('progress.slider_hint')}</p>
                        </div>
                    )}

                    {/* Gallery */}
                    {isLoading && (isStudent || selectedPatientId) ? (
                        <div className="flex items-center justify-center py-12">
                            <Loader2 className="w-6 h-6 text-gray-400 animate-spin" />
                        </div>
                    ) : (isStudent || selectedPatientId) && photos.length === 0 && !isLoading ? (
                        <div className={`${tile} p-10 flex flex-col items-center gap-3 text-gray-400`}>
                            <ImageOff className="w-10 h-10 text-gray-300 dark:text-gray-700" />
                            <p className="text-sm">{t('progress.no_photos')}</p>
                        </div>
                    ) : photos.length > 0 ? (
                        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                            {photos.map(photo => {
                                const isA = compareA?.id === photo.id;
                                const isB = compareB?.id === photo.id;
                                const isSelected = isA || isB;
                                return (
                                    <div
                                        key={photo.id}
                                        className={`rounded-2xl overflow-hidden bg-white dark:bg-slate-800 border transition-all duration-200 ${
                                            isSelected
                                                ? 'border-violet-500 ring-2 ring-violet-500 ring-offset-2 dark:ring-offset-slate-950'
                                                : 'border-black/5 dark:border-white/[0.07]'
                                        }`}
                                    >
                                        {/* Photo area */}
                                        <div
                                            className="relative cursor-pointer"
                                            style={{ aspectRatio: '3 / 4' }}
                                            onClick={() => isStudent && handleSelectCompare(photo)}
                                        >
                                            <img
                                                src={`data:image/jpeg;base64,${photo.photoBase64}`}
                                                alt={photo.description || formatDate(photo.takenAt)}
                                                className="w-full h-full object-cover"
                                            />
                                            <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-2.5">
                                                <p className="text-[11px] font-semibold text-white">
                                                    {formatDate(photo.takenAt)}
                                                </p>
                                                {photo.description && (
                                                    <p className="text-[10px] text-white/70 truncate">
                                                        {photo.description}
                                                    </p>
                                                )}
                                            </div>
                                            {isSelected && (
                                                <div className="absolute top-2 left-2 px-2 py-0.5 rounded-lg bg-violet-600 text-white text-[10px] font-bold">
                                                    {isA ? t('progress.label_before') : t('progress.label_after')}
                                                </div>
                                            )}
                                            {isStudent && (
                                                <button
                                                    onClick={e => { e.stopPropagation(); handleDelete(photo.id); }}
                                                    className="absolute top-2 right-2 w-7 h-7 flex items-center justify-center rounded-full bg-black/50 text-white/80 hover:text-red-400 hover:bg-black/70 transition-colors active:scale-90"
                                                >
                                                    <Trash2 className="w-3.5 h-3.5" />
                                                </button>
                                            )}
                                        </div>

                                        {/* Feedback panel */}
                                        <FeedbackPanel
                                            photo={photo}
                                            isProfessional={isProfessional}
                                            onFeedbackSaved={updatePhoto}
                                        />
                                        {/* Student notes panel */}
                                        {isStudent && (
                                            <StudentNotesPanel
                                                photo={photo}
                                                onNotesSaved={updatePhoto}
                                            />
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    ) : null}
                </main>
            </div>
        </div>
    );
}
