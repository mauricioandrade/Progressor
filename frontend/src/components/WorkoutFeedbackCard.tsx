import { useState } from 'react';
import { Star, MessageSquare, CheckCircle } from 'lucide-react';
import { api } from '../services/api';
import toast from 'react-hot-toast';
import { glassCard } from '../styles/shared';

interface FeedbackResponse {
    id: string;
    rating: number;
    comment: string | null;
    feedbackDate: string;
}

interface Props {
    existingFeedback: FeedbackResponse | null;
    onSubmitted: (f: FeedbackResponse) => void;
}

export function WorkoutFeedbackCard({ existingFeedback, onSubmitted }: Props) {
    const [rating, setRating] = useState(existingFeedback?.rating ?? 0);
    const [hoveredStar, setHoveredStar] = useState(0);
    const [comment, setComment] = useState(existingFeedback?.comment ?? '');
    const [submitting, setSubmitting] = useState(false);
    const [showComment, setShowComment] = useState(false);

    const already = existingFeedback != null;

    const STAR_LABELS = ['', 'Péssimo 😩', 'Ruim 😕', 'Ok 😐', 'Bom 😊', 'Excelente 🔥'];

    async function handleSubmit() {
        if (rating === 0) return;
        setSubmitting(true);
        try {
            const { data } = await api.post<FeedbackResponse>('/workouts/feedback', { rating, comment: comment.trim() || null });
            onSubmitted(data);
            toast.success('Feedback enviado!');
        } catch (e: unknown) {
            const status = (e as { response?: { status?: number } })?.response?.status;
            if (status === 409 || status === 500) {
                toast.error('Você já enviou feedback hoje.');
            } else {
                toast.error('Erro ao enviar feedback.');
            }
        } finally {
            setSubmitting(false);
        }
    }

    if (already) {
        return (
            <div className={`${glassCard} p-4`}>
                <div className="flex items-center gap-2 mb-2">
                    <CheckCircle className="w-4 h-4 text-green-500" />
                    <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">
                        Feedback de hoje
                    </span>
                </div>
                <div className="flex items-center gap-1">
                    {[1, 2, 3, 4, 5].map(s => (
                        <Star
                            key={s}
                            className={`w-5 h-5 ${s <= existingFeedback.rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300 dark:text-gray-600'}`}
                        />
                    ))}
                    <span className="ml-2 text-sm font-semibold text-gray-700 dark:text-gray-300">
                        {STAR_LABELS[existingFeedback.rating]}
                    </span>
                </div>
                {existingFeedback.comment && (
                    <p className="mt-2 text-xs text-gray-500 dark:text-gray-400 italic">"{existingFeedback.comment}"</p>
                )}
            </div>
        );
    }

    return (
        <div className={`${glassCard} p-4 space-y-3`}>
            <div className="flex items-center gap-2">
                <Star className="w-4 h-4 text-yellow-500" />
                <span className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide">
                    Como foi o treino de hoje?
                </span>
            </div>
            <div className="flex items-center gap-1.5">
                {[1, 2, 3, 4, 5].map(s => (
                    <button
                        key={s}
                        onClick={() => setRating(s)}
                        onMouseEnter={() => setHoveredStar(s)}
                        onMouseLeave={() => setHoveredStar(0)}
                        className="transition-transform active:scale-90"
                        aria-label={`${s} estrelas`}
                    >
                        <Star
                            className={`w-7 h-7 transition-colors ${s <= (hoveredStar || rating) ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300 dark:text-gray-600'}`}
                        />
                    </button>
                ))}
                {(hoveredStar || rating) > 0 && (
                    <span className="ml-2 text-sm text-gray-600 dark:text-gray-400 font-medium">
                        {STAR_LABELS[hoveredStar || rating]}
                    </span>
                )}
            </div>

            {rating > 0 && (
                <>
                    {!showComment ? (
                        <button
                            onClick={() => setShowComment(true)}
                            className="flex items-center gap-1.5 text-xs text-blue-500 hover:text-blue-600 transition-colors"
                        >
                            <MessageSquare className="w-3.5 h-3.5" />
                            Adicionar comentário (opcional)
                        </button>
                    ) : (
                        <textarea
                            value={comment}
                            onChange={e => setComment(e.target.value)}
                            placeholder="Como se sentiu? Algo a melhorar?"
                            rows={2}
                            maxLength={300}
                            className="w-full px-3 py-2 text-sm border border-black/10 dark:border-white/10 rounded-2xl bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:ring-2 focus:ring-blue-500 outline-none resize-none"
                        />
                    )}
                    <button
                        onClick={handleSubmit}
                        disabled={submitting}
                        className="w-full py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                    >
                        {submitting ? '...' : 'Enviar feedback'}
                    </button>
                </>
            )}
        </div>
    );
}
