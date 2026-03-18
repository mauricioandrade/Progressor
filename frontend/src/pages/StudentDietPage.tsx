import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';
import { Download, Droplets, Loader2, Pencil, Check, X } from 'lucide-react';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';

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
    items: MealItem[];
    cheatMeal: boolean;
}

interface WaterIntake {
    dailyWaterGoal: number;
    currentWaterIntake: number;
}

const MEAL_ORDER = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

const MACRO_COLORS = {
    protein: '#f43f5e',
    carbs:   '#3b82f6',
    fat:     '#f59e0b',
};

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';

const GOAL_COLORS: Record<string, string> = {
    BULKING:     'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300',
    CUTTING:     'bg-rose-100 text-rose-700 dark:bg-rose-900/30 dark:text-rose-300',
    MAINTENANCE: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
};

function MacroDonut({ protein, carbs, fat, calories }: { protein: number; carbs: number; fat: number; calories: number }) {
    const { t } = useTranslation();
    const total = protein + carbs + fat;
    const data = [
        { name: t('nutrition.protein'), value: protein, color: MACRO_COLORS.protein },
        { name: t('nutrition.carbs'),   value: carbs,   color: MACRO_COLORS.carbs   },
        { name: t('nutrition.fat'),     value: fat,     color: MACRO_COLORS.fat     },
    ].filter(d => d.value > 0);

    return (
        <div className="flex items-center gap-6">
            <div className="relative w-40 h-40 shrink-0">
                <ResponsiveContainer width="100%" height="100%">
                    <PieChart>
                        <Pie
                            data={data}
                            dataKey="value"
                            innerRadius={46}
                            outerRadius={70}
                            strokeWidth={0}
                            startAngle={90}
                            endAngle={-270}
                        >
                            {data.map((entry, i) => (
                                <Cell key={i} fill={entry.color} />
                            ))}
                        </Pie>
                        <Tooltip
                            contentStyle={{ fontSize: '11px', borderRadius: '10px', border: 'none', background: 'rgba(0,0,0,0.8)', color: '#fff' }}
                            formatter={(value: any) => [value != null ? `${Number(value).toFixed(1)}g` : '', '']}
                        />
                    </PieChart>
                </ResponsiveContainer>
                <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                    <p className="text-xs text-gray-400">{t('nutrition.total_calories')}</p>
                    <p className="text-lg font-bold text-emerald-500">{calories.toFixed(0)}</p>
                    <p className="text-[10px] text-gray-400">kcal</p>
                </div>
            </div>
            <div className="space-y-2.5 flex-1">
                {data.map(macro => (
                    <div key={macro.name}>
                        <div className="flex justify-between items-center mb-1">
                            <div className="flex items-center gap-1.5">
                                <div className="w-2.5 h-2.5 rounded-full" style={{ background: macro.color }} />
                                <span className="text-xs font-medium text-gray-700 dark:text-gray-300">{macro.name}</span>
                            </div>
                            <span className="text-xs font-semibold text-gray-700 dark:text-gray-200">{macro.value.toFixed(1)}g</span>
                        </div>
                        <div className="h-1.5 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden">
                            <div
                                className="h-full rounded-full transition-all"
                                style={{ width: `${total > 0 ? (macro.value / total) * 100 : 0}%`, background: macro.color }}
                            />
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

function HydrationCard({
    water,
    onAdd,
    onSetGoal,
}: {
    water: WaterIntake;
    onAdd: (amount: number) => Promise<void>;
    onSetGoal: (goal: number) => Promise<void>;
}) {
    const { t } = useTranslation();
    const [editingGoal, setEditingGoal] = useState(false);
    const [goalInput, setGoalInput] = useState('');
    const [adding, setAdding] = useState(false);

    const pct = water.dailyWaterGoal > 0
        ? Math.min((water.currentWaterIntake / water.dailyWaterGoal) * 100, 100)
        : 0;

    async function handleAdd(amount: number) {
        setAdding(true);
        await onAdd(amount);
        setAdding(false);
    }

    async function handleGoalSubmit() {
        const val = parseInt(goalInput, 10);
        if (!val || val < 1) return;
        await onSetGoal(val);
        setEditingGoal(false);
        setGoalInput('');
    }

    return (
        <div className={`${glassCard} p-5`}>
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-2">
                    <Droplets className="w-5 h-5 text-blue-400" />
                    <h3 className="text-sm font-semibold text-gray-800 dark:text-gray-100">{t('water.title')}</h3>
                </div>
                <button
                    onClick={() => { setEditingGoal(v => !v); setGoalInput(water.dailyWaterGoal > 0 ? String(water.dailyWaterGoal) : ''); }}
                    className="w-7 h-7 flex items-center justify-center rounded-full bg-black/5 dark:bg-white/10 text-gray-400 hover:text-blue-400 transition-colors"
                >
                    <Pencil className="w-3.5 h-3.5" />
                </button>
            </div>

            {editingGoal && (
                <div className="mb-4 flex gap-2 items-center">
                    <input
                        type="number"
                        min={1}
                        value={goalInput}
                        onChange={e => setGoalInput(e.target.value)}
                        placeholder={t('water.set_goal_placeholder')}
                        className="flex-1 px-3 py-2 border border-white/20 rounded-xl bg-gray-900 text-gray-100 placeholder-gray-500 text-sm focus:ring-2 focus:ring-blue-500 outline-none"
                        onKeyDown={e => e.key === 'Enter' && handleGoalSubmit()}
                    />
                    <button onClick={handleGoalSubmit} className="w-8 h-8 flex items-center justify-center rounded-xl bg-blue-500 text-white hover:bg-blue-600 transition-colors">
                        <Check className="w-4 h-4" />
                    </button>
                    <button onClick={() => setEditingGoal(false)} className="w-8 h-8 flex items-center justify-center rounded-xl bg-black/10 dark:bg-white/10 text-gray-400 hover:text-red-400 transition-colors">
                        <X className="w-4 h-4" />
                    </button>
                </div>
            )}

            {!editingGoal && (
                <>
                    <div className="flex items-end justify-between mb-2">
                        <span className="text-2xl font-bold text-blue-400">{water.currentWaterIntake}ml</span>
                        <span className="text-sm text-gray-400 pb-0.5">
                            {water.dailyWaterGoal > 0 ? `/ ${water.dailyWaterGoal}ml` : t('water.no_goal')}
                        </span>
                    </div>

                    <div className="h-3 bg-black/5 dark:bg-white/10 rounded-full overflow-hidden mb-4">
                        <div
                            className="h-full bg-blue-400 rounded-full transition-all duration-500"
                            style={{ width: `${pct}%` }}
                        />
                    </div>

                    {water.dailyWaterGoal > 0 && (
                        <p className="text-[11px] text-gray-400 text-center mb-3">
                            {pct.toFixed(0)}% — {t('water.formula_hint')}
                        </p>
                    )}

                    <div className="flex gap-2">
                        <button
                            disabled={adding}
                            onClick={() => handleAdd(250)}
                            className="flex-1 py-2.5 bg-blue-500/10 text-blue-500 font-semibold text-sm rounded-xl hover:bg-blue-500/20 active:scale-95 transition-all disabled:opacity-50"
                        >
                            {t('water.add_250')}
                        </button>
                        <button
                            disabled={adding}
                            onClick={() => handleAdd(500)}
                            className="flex-1 py-2.5 bg-blue-500/10 text-blue-500 font-semibold text-sm rounded-xl hover:bg-blue-500/20 active:scale-95 transition-all disabled:opacity-50"
                        >
                            {t('water.add_500')}
                        </button>
                    </div>
                </>
            )}
        </div>
    );
}

export function StudentDietPage() {
    const { t, i18n } = useTranslation();
    const { user } = useAuth();
    const [plan, setPlan] = useState<MealPlan | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isDownloading, setIsDownloading] = useState(false);
    const [water, setWater] = useState<WaterIntake>({ dailyWaterGoal: 0, currentWaterIntake: 0 });

    useEffect(() => {
        api.get('/nutrition/meal-plans/my')
            .then(r => setPlan(r.data))
            .catch(() => {})
            .finally(() => setIsLoading(false));

        api.get('/nutrition/water')
            .then(r => setWater(r.data))
            .catch(() => {});
    }, []);

    if (!user) return null;

    async function handleAddWater(amount: number) {
        const r = await api.patch('/nutrition/water/intake', { amount });
        setWater(r.data);
    }

    async function handleSetGoal(goal: number) {
        const r = await api.patch('/nutrition/water/goal', { goal });
        setWater(r.data);
    }

    async function handleDownloadDiet() {
        setIsDownloading(true);
        try {
            const response = await api.get('/reports/meal-plan/my', {
                responseType: 'blob',
                headers: { 'Accept-Language': i18n.language }
            });
            const url = URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const a = document.createElement('a');
            a.href = url;
            a.download = 'meal_plan.pdf';
            a.click();
            URL.revokeObjectURL(url);
        } catch {
        } finally {
            setIsDownloading(false);
        }
    }

    function mealTimeLabel(mt: string): string {
        const map: Record<string, string> = {
            BREAKFAST: t('nutrition.meal_breakfast'),
            LUNCH:     t('nutrition.meal_lunch'),
            DINNER:    t('nutrition.meal_dinner'),
            SNACK:     t('nutrition.meal_snack'),
        };
        return map[mt] ?? mt;
    }

    function goalLabel(goal: string): string {
        const map: Record<string, string> = {
            BULKING:     t('nutrition.goal_bulking'),
            CUTTING:     t('nutrition.goal_cutting'),
            MAINTENANCE: t('nutrition.goal_maintenance'),
        };
        return map[goal] ?? goal;
    }

    const grouped = plan
        ? MEAL_ORDER.reduce((acc, mt) => {
            const items = plan.items.filter(i => i.mealTime === mt);
            if (items.length > 0) acc[mt] = items;
            return acc;
        }, {} as Record<string, MealItem[]>)
        : {};

    const totalProtein  = plan?.items.reduce((s, i) => s + i.proteinG,    0) ?? 0;
    const totalCarbs    = plan?.items.reduce((s, i) => s + i.carbsG,      0) ?? 0;
    const totalFat      = plan?.items.reduce((s, i) => s + i.fatG,        0) ?? 0;
    const totalCalories = plan?.items.reduce((s, i) => s + i.caloriesKcal, 0) ?? 0;

    const mealColors: Record<string, string> = {
        BREAKFAST: 'from-orange-400 to-amber-500',
        LUNCH:     'from-green-400 to-teal-500',
        DINNER:    'from-blue-400 to-indigo-500',
        SNACK:     'from-purple-400 to-pink-500',
    };

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1">
                <header className="bg-white/70 dark:bg-slate-800/60 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-10">
                    <div className="flex items-center justify-between">
                        <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                            {t('nutrition.my_diet_title')}
                        </h2>
                        {plan && (
                            <button
                                onClick={handleDownloadDiet}
                                disabled={isDownloading}
                                className="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white text-sm font-semibold rounded-xl hover:bg-purple-700 disabled:opacity-50 transition-colors"
                            >
                                {isDownloading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Download className="w-4 h-4" />}
                                {isDownloading ? t('reports.generating') : t('reports.download_diet')}
                            </button>
                        )}
                    </div>
                </header>

                <main className="p-4 md:p-6 max-w-2xl space-y-5 pb-24 md:pb-6">
                    <HydrationCard water={water} onAdd={handleAddWater} onSetGoal={handleSetGoal} />

                    {isLoading ? (
                        <div className="p-12 text-center text-gray-400">...</div>
                    ) : !plan ? (
                        <div className={`${glassCard} p-10 text-center text-gray-500 text-sm`}>
                            {t('nutrition.no_diet')}
                        </div>
                    ) : (
                        <>
                            {plan.cheatMeal && (
                                <div className="rounded-3xl bg-gradient-to-r from-orange-500 to-amber-500 p-5 text-white flex items-start gap-3">
                                    <span className="text-2xl leading-none">🍔</span>
                                    <div>
                                        <p className="font-bold text-sm">{t('nutrition.cheat_meal_banner_title')}</p>
                                        <p className="text-xs text-white/80 mt-0.5">{t('nutrition.cheat_meal_banner_body')}</p>
                                    </div>
                                </div>
                            )}

                            <div className={`${glassCard} p-6`}>
                                <div className="flex items-start justify-between mb-5">
                                    <div>
                                        <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest mb-1">
                                            {t('nutrition.my_diet_title')}
                                        </p>
                                        <h3 className="text-lg font-bold text-gray-900 dark:text-gray-100">{plan.name}</h3>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        {plan.cheatMeal && (
                                            <span className="text-xs font-semibold px-3 py-1 rounded-full bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-300">
                                                🍔 Cheat
                                            </span>
                                        )}
                                        {plan.goal && (
                                            <span className={`text-xs font-semibold px-3 py-1 rounded-full ${GOAL_COLORS[plan.goal] ?? 'bg-gray-100 text-gray-600'}`}>
                                                {goalLabel(plan.goal)}
                                            </span>
                                        )}
                                    </div>
                                </div>

                                {plan.cheatMeal ? (
                                    <div className="py-6 text-center text-gray-400 text-sm">
                                        <p className="text-3xl mb-2">🎉</p>
                                        <p>{t('nutrition.cheat_meal_banner_title')}</p>
                                    </div>
                                ) : (
                                    <MacroDonut protein={totalProtein} carbs={totalCarbs} fat={totalFat} calories={totalCalories} />
                                )}

                                <div className={`mt-4 pt-4 border-t border-black/5 dark:border-white/5 grid grid-cols-4 gap-2 text-center${plan.cheatMeal ? ' opacity-30 pointer-events-none select-none' : ''}`}>
                                    <div>
                                        <p className="text-xs text-gray-400">{t('nutrition.total_calories')}</p>
                                        <p className="text-lg font-bold text-emerald-500">{totalCalories.toFixed(0)}</p>
                                        <p className="text-[10px] text-gray-400">kcal</p>
                                    </div>
                                    <div>
                                        <p className="text-xs text-gray-400">{t('nutrition.protein')}</p>
                                        <p className="text-lg font-bold text-rose-500">{totalProtein.toFixed(1)}</p>
                                        <p className="text-[10px] text-gray-400">g</p>
                                    </div>
                                    <div>
                                        <p className="text-xs text-gray-400">{t('nutrition.carbs')}</p>
                                        <p className="text-lg font-bold text-blue-500">{totalCarbs.toFixed(1)}</p>
                                        <p className="text-[10px] text-gray-400">g</p>
                                    </div>
                                    <div>
                                        <p className="text-xs text-gray-400">{t('nutrition.fat')}</p>
                                        <p className="text-lg font-bold text-amber-500">{totalFat.toFixed(1)}</p>
                                        <p className="text-[10px] text-gray-400">g</p>
                                    </div>
                                </div>
                            </div>

                            {Object.entries(grouped).map(([mealTime, items]) => {
                                const mealCalories = items.reduce((s, i) => s + i.caloriesKcal, 0);
                                const mealProtein  = items.reduce((s, i) => s + i.proteinG, 0);
                                const mealCarbs    = items.reduce((s, i) => s + i.carbsG, 0);
                                const mealFat      = items.reduce((s, i) => s + i.fatG, 0);
                                return (
                                    <div key={mealTime} className={`${glassCard} overflow-hidden`}>
                                        <div className="px-5 py-4 flex items-center gap-3">
                                            <div className={`w-8 h-8 rounded-xl bg-gradient-to-br ${mealColors[mealTime] ?? 'from-gray-400 to-gray-500'} flex items-center justify-center text-white text-sm font-bold`}>
                                                {mealTimeLabel(mealTime).charAt(0)}
                                            </div>
                                            <div className="flex-1">
                                                <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">{mealTimeLabel(mealTime)}</p>
                                                <p className="text-xs text-gray-400">{mealCalories.toFixed(0)} kcal</p>
                                            </div>
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

                                        <div className="px-5 py-3 bg-black/2 dark:bg-white/2 border-t border-black/5 dark:border-white/5 flex justify-between text-xs text-gray-500 dark:text-gray-400">
                                            <span className="font-semibold">{t('nutrition.meal_totals')}</span>
                                            <span>
                                                <span className="text-rose-500 font-medium">{mealProtein.toFixed(1)}g P</span>
                                                {' · '}
                                                <span className="text-blue-500 font-medium">{mealCarbs.toFixed(1)}g C</span>
                                                {' · '}
                                                <span className="text-amber-500 font-medium">{mealFat.toFixed(1)}g F</span>
                                                {' · '}
                                                <span className="text-emerald-500 font-medium">{mealCalories.toFixed(0)} kcal</span>
                                            </span>
                                        </div>
                                    </div>
                                );
                            })}
                        </>
                    )}
                </main>
            </div>
        </div>
    );
}
