import { useCallback, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Search, Plus, Trash2, Loader2, UtensilsCrossed, SearchX } from 'lucide-react';
import axios from 'axios';
import { Sidebar } from '../components/Sidebar';
import { useAuth } from '../hooks/useAuth';
import { api } from '../services/api';
import toast from 'react-hot-toast';

interface Patient { id: string; name: string; email: string; }

interface FoodResult {
    foodId: string;
    name: string;
    brandName?: string | null;
    caloriesKcal: number;
    proteinG: number;
    carbsG: number;
    fatG: number;
    description: string;
}

interface MealEntry {
    tempId: string;
    name: string;
    foodDescription: string;
    baseCalories: number;
    baseProtein: number;
    baseCarbs: number;
    baseFat: number;
    quantity: number;
    baseUnit: string;
}

type MealTime = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
const MEAL_TIMES: MealTime[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];
const GOALS = ['BULKING', 'CUTTING', 'MAINTENANCE'];

const MACRO_COLORS = {
    calories: 'text-emerald-500',
    caloriesBg: 'bg-emerald-50 dark:bg-emerald-900/20',
    protein: 'text-rose-500',
    proteinBg: 'bg-rose-50 dark:bg-rose-900/20',
    carbs: 'text-blue-500',
    carbsBg: 'bg-blue-50 dark:bg-blue-900/20',
    fat: 'text-amber-500',
    fatBg: 'bg-amber-50 dark:bg-amber-900/20',
};

const glassCard = 'bg-white/80 dark:bg-slate-800/60 backdrop-blur-xl border border-black/5 dark:border-white/[0.07] rounded-3xl shadow-sm';
const inputClass = 'w-full px-4 py-2.5 border border-black/15 dark:border-white/20 rounded-2xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 placeholder-gray-400 dark:placeholder-gray-500 focus:ring-2 focus:ring-blue-500 outline-none text-sm';
const selectClass = 'w-full px-4 py-2.5 border border-black/15 dark:border-white/20 rounded-2xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 focus:ring-2 focus:ring-blue-500 outline-none text-sm appearance-none';

function scaled(entry: MealEntry) {
    const ratio = entry.quantity / 100;
    return {
        calories: entry.baseCalories * ratio,
        protein: entry.baseProtein * ratio,
        carbs: entry.baseCarbs * ratio,
        fat: entry.baseFat * ratio,
    };
}

function mealTotals(entries: MealEntry[]) {
    return entries.reduce(
        (acc, e) => {
            const s = scaled(e);
            return { calories: acc.calories + s.calories, protein: acc.protein + s.protein, carbs: acc.carbs + s.carbs, fat: acc.fat + s.fat };
        },
        { calories: 0, protein: 0, carbs: 0, fat: 0 }
    );
}

interface MacroPillProps { label: string; value: number; unit?: string; colorClass: string; bgClass: string; }
function MacroPill({ label, value, unit = 'g', colorClass, bgClass }: MacroPillProps) {
    return (
        <div className={`${bgClass} rounded-2xl px-3 py-2 text-center`}>
            <p className="text-[10px] text-gray-400 uppercase tracking-wide">{label}</p>
            <p className={`text-sm font-bold ${colorClass}`}>{value.toFixed(1)}<span className="text-[10px] font-normal ml-0.5">{unit}</span></p>
        </div>
    );
}

export function NutritionistDietBuilder() {
    const { t } = useTranslation();
    const { user } = useAuth();

    const [patients, setPatients] = useState<Patient[]>([]);
    const [selectedPatient, setSelectedPatient] = useState('');
    const [planName, setPlanName] = useState('');
    const [goal, setGoal] = useState('CUTTING');
    const [activeTab, setActiveTab] = useState<MealTime>('BREAKFAST');
    const [meals, setMeals] = useState<Record<MealTime, MealEntry[]>>({
        BREAKFAST: [], LUNCH: [], DINNER: [], SNACK: [],
    });

    const [foodQuery, setFoodQuery] = useState('');
    const [foodResults, setFoodResults] = useState<FoodResult[]>([]);
    const [isSearchingFood, setIsSearchingFood] = useState(false);
    const [searchPerformed, setSearchPerformed] = useState(false);

    const [planId, setPlanId] = useState<string | null>(null);
    const [cheatMeal, setCheatMeal] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [saveSuccess, setSaveSuccess] = useState('');
    const [saveError, setSaveError] = useState('');

    const searchRef = useRef<HTMLInputElement>(null);
    const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    useEffect(() => {
        api.get('/users/my-students/nutritionist').then(r => setPatients(r.data)).catch(() => {});
    }, []);

    // Load existing plan when patient is selected
    useEffect(() => {
        if (!selectedPatient) {
            setPlanId(null);
            setMeals({ BREAKFAST: [], LUNCH: [], DINNER: [], SNACK: [] });
            setPlanName('');
            setGoal('CUTTING');
            setCheatMeal(false);
            return;
        }
        api.get(`/nutrition/meal-plans/student/${selectedPatient}`)
            .then(r => {
                const plan = r.data;
                if (!plan) return;
                setPlanId(plan.id);
                setPlanName(plan.name ?? '');
                setGoal(plan.goal ?? 'CUTTING');
                setCheatMeal(plan.cheatMeal ?? false);
                const newMeals: Record<MealTime, MealEntry[]> = { BREAKFAST: [], LUNCH: [], DINNER: [], SNACK: [] };
                for (const item of (plan.items ?? [])) {
                    const mt = item.mealTime as MealTime;
                    if (!MEAL_TIMES.includes(mt)) continue;
                    const ratio = item.quantity > 0 ? 100 / item.quantity : 1;
                    newMeals[mt].push({
                        tempId: item.id,
                        name: item.name,
                        foodDescription: item.foodDescription ?? '',
                        baseCalories: item.caloriesKcal * ratio,
                        baseProtein: item.proteinG * ratio,
                        baseCarbs: item.carbsG * ratio,
                        baseFat: item.fatG * ratio,
                        quantity: item.quantity,
                        baseUnit: item.baseUnit ?? 'g',
                    });
                }
                setMeals(newMeals);
            })
            .catch(() => { setPlanId(null); });
    }, [selectedPatient]);

    const doFoodSearch = useCallback(async (query: string) => {
        if (!query.trim()) return;
        setIsSearchingFood(true);
        setFoodResults([]);
        setSearchPerformed(false);
        try {
            const r = await api.get(`/nutrition/foods/search?q=${encodeURIComponent(query)}`);
            setFoodResults(r.data ?? []);
            setSearchPerformed(true);
        } catch (err) {
            setSearchPerformed(true);
            if (axios.isAxiosError(err) && err.response?.data?.message) {
                toast.error(err.response.data.message);
            } else {
                toast.error(t('toast.food_error'));
            }
        } finally {
            setIsSearchingFood(false);
        }
    }, [t]);

    // Debounced real-time search
    useEffect(() => {
        if (foodQuery.trim().length < 2) {
            setFoodResults([]);
            setSearchPerformed(false);
            return;
        }
        if (debounceRef.current) clearTimeout(debounceRef.current);
        debounceRef.current = setTimeout(() => doFoodSearch(foodQuery), 500);
        return () => { if (debounceRef.current) clearTimeout(debounceRef.current); };
    }, [foodQuery, doFoodSearch]);

    if (!user) return null;

    function mealLabel(mt: MealTime): string {
        const map: Record<MealTime, string> = {
            BREAKFAST: t('nutrition.meal_breakfast'),
            LUNCH: t('nutrition.meal_lunch'),
            DINNER: t('nutrition.meal_dinner'),
            SNACK: t('nutrition.meal_snack'),
        };
        return map[mt];
    }

    function goalLabel(g: string): string {
        const map: Record<string, string> = {
            BULKING: t('nutrition.goal_bulking'),
            CUTTING: t('nutrition.goal_cutting'),
            MAINTENANCE: t('nutrition.goal_maintenance'),
        };
        return map[g] ?? g;
    }

    async function handleFoodSearch(e: React.FormEvent) {
        e.preventDefault();
        if (!foodQuery.trim()) return;
        if (debounceRef.current) clearTimeout(debounceRef.current);
        await doFoodSearch(foodQuery);
    }

    function addFood(food: FoodResult) {
        const entry: MealEntry = {
            tempId: `${Date.now()}-${Math.random()}`,
            name: food.name,
            foodDescription: food.description,
            baseCalories: food.caloriesKcal,
            baseProtein: food.proteinG,
            baseCarbs: food.carbsG,
            baseFat: food.fatG,
            quantity: 100,
            baseUnit: 'g',
        };
        setMeals(prev => ({ ...prev, [activeTab]: [...prev[activeTab], entry] }));
        setFoodResults([]);
        setFoodQuery('');
        setSearchPerformed(false);
        searchRef.current?.focus();
    }

    function updateQuantity(mealTime: MealTime, tempId: string, qty: number) {
        setMeals(prev => ({
            ...prev,
            [mealTime]: prev[mealTime].map(e => e.tempId === tempId ? { ...e, quantity: qty } : e),
        }));
    }

    function removeEntry(mealTime: MealTime, tempId: string) {
        setMeals(prev => ({ ...prev, [mealTime]: prev[mealTime].filter(e => e.tempId !== tempId) }));
    }

    const allEntries = MEAL_TIMES.flatMap(mt => meals[mt]);
    const daily = mealTotals(allEntries);

    async function handleSave() {
        setSaveError('');
        setSaveSuccess('');
        if (!selectedPatient) { setSaveError(t('nutrition.error_no_patient')); return; }
        if (allEntries.length === 0) { setSaveError(t('nutrition.error_no_items')); return; }
        setIsSaving(true);
        try {
            const items = MEAL_TIMES.flatMap(mealTime =>
                meals[mealTime].map(e => {
                    const s = scaled(e);
                    return {
                        mealTime,
                        name: e.name,
                        foodDescription: e.foodDescription,
                        caloriesKcal: s.calories,
                        proteinG: s.protein,
                        carbsG: s.carbs,
                        fatG: s.fat,
                        quantity: e.quantity,
                        baseUnit: e.baseUnit,
                    };
                })
            );
            const payload = {
                studentId: selectedPatient,
                name: planName || t('nutrition.my_diet_title'),
                goal,
                items,
                cheatMeal,
            };
            if (planId) {
                await api.put(`/nutrition/meal-plans/${planId}`, payload);
            } else {
                const res = await api.post('/nutrition/meal-plans', payload);
                setPlanId(res.data?.id ?? null);
            }
            setSaveSuccess(t('nutrition.save_success'));
            toast.success(t('toast.plan_saved'));
            if (!planId) {
                setMeals({ BREAKFAST: [], LUNCH: [], DINNER: [], SNACK: [] });
                setPlanName('');
                setCheatMeal(false);
            }
        } catch {
            setSaveError(t('login.errors.server_error'));
        } finally {
            setIsSaving(false);
        }
    }

    const tabColors: Record<MealTime, string> = {
        BREAKFAST: 'from-orange-400 to-amber-500',
        LUNCH: 'from-green-400 to-teal-500',
        DINNER: 'from-blue-400 to-indigo-500',
        SNACK: 'from-purple-400 to-pink-500',
    };

    return (
        <div className="flex min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-slate-950 dark:to-slate-900">
            <Sidebar role={user.role} />
            <div className="flex-1 min-w-0 flex flex-col">
                <header className="bg-white/70 dark:bg-slate-900/80 backdrop-blur-xl border-b border-black/5 dark:border-white/10 p-5 sticky top-0 z-20">
                    <h2 className="text-lg font-semibold text-gray-800 dark:text-gray-100">
                        {t('nutrition.diet_builder_title')}
                    </h2>
                </header>

                {/* Sticky daily summary bar */}
                <div className="bg-white/80 dark:bg-slate-900/90 backdrop-blur-xl border-b border-black/5 dark:border-white/10 px-6 py-3 sticky top-[73px] z-10">
                    <div className="max-w-3xl flex items-center gap-4">
                        <span className="text-xs font-semibold text-gray-400 uppercase tracking-wide shrink-0">{t('nutrition.daily_summary')}</span>
                        <div className="flex gap-2 flex-1">
                            <MacroPill label={t('nutrition.total_calories')} value={daily.calories} unit="kcal" colorClass={MACRO_COLORS.calories} bgClass={MACRO_COLORS.caloriesBg} />
                            <MacroPill label={t('nutrition.protein')} value={daily.protein} colorClass={MACRO_COLORS.protein} bgClass={MACRO_COLORS.proteinBg} />
                            <MacroPill label={t('nutrition.carbs')} value={daily.carbs} colorClass={MACRO_COLORS.carbs} bgClass={MACRO_COLORS.carbsBg} />
                            <MacroPill label={t('nutrition.fat')} value={daily.fat} colorClass={MACRO_COLORS.fat} bgClass={MACRO_COLORS.fatBg} />
                        </div>
                    </div>
                </div>

                <main className="p-4 md:p-6 max-w-3xl space-y-5 pb-24 md:pb-6">
                    {/* Meta: patient, name, goal */}
                    <div className={`${glassCard} p-6 grid grid-cols-1 sm:grid-cols-4 gap-4`}>
                        <div>
                            <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                {t('nutrition.select_patient')}
                            </label>
                            <select value={selectedPatient} onChange={e => setSelectedPatient(e.target.value)} className={selectClass}>
                                <option value="">{t('nutrition.select_patient_placeholder')}</option>
                                {patients.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                {t('nutrition.plan_name_label')}
                            </label>
                            <input
                                type="text"
                                value={planName}
                                onChange={e => setPlanName(e.target.value)}
                                placeholder={t('nutrition.plan_name_placeholder')}
                                className={inputClass}
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                {t('nutrition.diet_goal')}
                            </label>
                            <select value={goal} onChange={e => setGoal(e.target.value)} className={selectClass}>
                                {GOALS.map(g => <option key={g} value={g}>{goalLabel(g)}</option>)}
                            </select>
                        </div>
                        <div className="flex flex-col justify-center">
                            <label className="block text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wide mb-1.5">
                                {t('nutrition.cheat_meal_label')}
                            </label>
                            <button
                                type="button"
                                onClick={() => setCheatMeal(v => !v)}
                                title={t('nutrition.cheat_meal_tooltip')}
                                className={`flex items-center gap-2 px-4 py-2.5 rounded-2xl border font-semibold text-sm transition-colors ${
                                    cheatMeal
                                        ? 'bg-orange-500 border-orange-500 text-white'
                                        : 'bg-white dark:bg-gray-800 border-black/15 dark:border-white/20 text-gray-500 dark:text-gray-400'
                                }`}
                            >
                                🍔 {cheatMeal ? 'On' : 'Off'}
                            </button>
                        </div>
                    </div>

                    {/* Meal tabs */}
                    <div className="flex gap-2">
                        {MEAL_TIMES.map(mt => (
                            <button
                                key={mt}
                                onClick={() => { setActiveTab(mt); setFoodResults([]); setFoodQuery(''); setSearchPerformed(false); }}
                                className={`flex-1 py-2.5 rounded-2xl text-xs font-bold transition-all active:scale-95 flex flex-col items-center gap-0.5 ${
                                    activeTab === mt
                                        ? `bg-gradient-to-br ${tabColors[mt]} text-white shadow-md`
                                        : 'bg-white/70 dark:bg-slate-800/40 text-gray-500 dark:text-gray-400 border border-black/5 dark:border-white/[0.07] hover:bg-black/5 dark:hover:bg-white/5'
                                }`}
                            >
                                {mealLabel(mt)}
                                <span className={`text-[10px] font-normal ${activeTab === mt ? 'text-white/80' : 'text-gray-400'}`}>
                                    {meals[mt].length > 0 ? `${meals[mt].length} items` : '—'}
                                </span>
                            </button>
                        ))}
                    </div>

                    {/* Active meal panel */}
                    <div className={`${glassCard} p-5 space-y-4`}>
                        {/* Food search */}
                        <form onSubmit={handleFoodSearch} className="flex gap-2">
                            <input
                                ref={searchRef}
                                type="text"
                                value={foodQuery}
                                onChange={e => setFoodQuery(e.target.value)}
                                placeholder={t('nutrition.food_search_placeholder')}
                                className={inputClass}
                            />
                            <button
                                type="submit"
                                disabled={isSearchingFood}
                                className="shrink-0 flex items-center gap-2 px-4 py-2.5 bg-blue-600 text-white text-sm font-semibold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95"
                            >
                                {isSearchingFood ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                                {t('nutrition.food_search_button')}
                            </button>
                        </form>

                        {/* Search results */}
                        {foodResults.length > 0 && (
                            <div className="space-y-1.5 max-h-64 overflow-y-auto pr-1">
                                {foodResults.map(food => (
                                    <div key={food.foodId} className="flex items-center justify-between p-3 rounded-2xl bg-white dark:bg-gray-800 border border-black/10 dark:border-white/15 gap-3 shadow-sm">
                                        <div className="min-w-0">
                                            <div className="flex items-center gap-2 flex-wrap">
                                                <p className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate">{food.name}</p>
                                                {food.brandName && (
                                                    <span className="ml-2 text-xs px-2 py-0.5 bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400 rounded-full">
                                                        {food.brandName}
                                                    </span>
                                                )}
                                            </div>
                                            <p className="text-[11px] text-gray-500 dark:text-gray-400 mt-0.5">
                                                per 100g ·{' '}
                                                <span className={MACRO_COLORS.calories}>{food.caloriesKcal.toFixed(0)} kcal</span>
                                                {' · '}
                                                <span className={MACRO_COLORS.protein}>P {food.proteinG.toFixed(1)}g</span>
                                                {' · '}
                                                <span className={MACRO_COLORS.carbs}>C {food.carbsG.toFixed(1)}g</span>
                                                {' · '}
                                                <span className={MACRO_COLORS.fat}>F {food.fatG.toFixed(1)}g</span>
                                            </p>
                                        </div>
                                        <button
                                            onClick={() => addFood(food)}
                                            className="shrink-0 flex items-center gap-1 px-3 py-1.5 bg-blue-600 text-white text-xs font-bold rounded-xl hover:bg-blue-700 transition-colors active:scale-95"
                                        >
                                            <Plus className="w-3.5 h-3.5" />
                                            {t('nutrition.add_to_plan')}
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Ingredient list */}
                        {meals[activeTab].length > 0 && (
                            <div className="space-y-2">
                                {meals[activeTab].map(entry => {
                                    const s = scaled(entry);
                                    return (
                                        <div key={entry.tempId} className="rounded-2xl border border-black/5 dark:border-white/10 bg-white/60 dark:bg-white/5 overflow-hidden">
                                            <div className="flex items-center justify-between px-4 pt-3 pb-2 gap-3">
                                                <p className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate flex-1">{entry.name}</p>
                                                <button
                                                    onClick={() => removeEntry(activeTab, entry.tempId)}
                                                    className="shrink-0 w-7 h-7 flex items-center justify-center rounded-full bg-red-50 dark:bg-red-900/20 text-red-500 hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors active:scale-95"
                                                >
                                                    <Trash2 className="w-3.5 h-3.5" />
                                                </button>
                                            </div>
                                            <div className="px-4 pb-3 flex items-center gap-3">
                                                <div className="flex items-center gap-1.5 shrink-0">
                                                    <label className="text-xs text-gray-400 shrink-0">{t('nutrition.quantity_label')}</label>
                                                    <input
                                                        type="number"
                                                        min={1}
                                                        step={1}
                                                        value={entry.quantity}
                                                        onChange={e => updateQuantity(activeTab, entry.tempId, Number(e.target.value) || 1)}
                                                        className="w-20 px-2.5 py-1.5 border border-black/15 dark:border-white/20 rounded-xl bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100 text-sm text-center focus:ring-2 focus:ring-blue-500 outline-none"
                                                    />
                                                </div>
                                                <div className="flex gap-2 flex-1">
                                                    <span className={`text-xs font-semibold ${MACRO_COLORS.calories}`}>{s.calories.toFixed(0)} kcal</span>
                                                    <span className={`text-xs font-semibold ${MACRO_COLORS.protein}`}>P {s.protein.toFixed(1)}g</span>
                                                    <span className={`text-xs font-semibold ${MACRO_COLORS.carbs}`}>C {s.carbs.toFixed(1)}g</span>
                                                    <span className={`text-xs font-semibold ${MACRO_COLORS.fat}`}>F {s.fat.toFixed(1)}g</span>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })}

                                {/* Meal total */}
                                <div className="flex justify-end pt-1 gap-3">
                                    {(() => {
                                        const t_ = mealTotals(meals[activeTab]);
                                        return (
                                            <>
                                                <span className="text-xs text-gray-400">{t('nutrition.meal_totals')}:</span>
                                                <span className={`text-xs font-bold ${MACRO_COLORS.calories}`}>{t_.calories.toFixed(0)} kcal</span>
                                                <span className={`text-xs font-bold ${MACRO_COLORS.protein}`}>P {t_.protein.toFixed(1)}g</span>
                                                <span className={`text-xs font-bold ${MACRO_COLORS.carbs}`}>C {t_.carbs.toFixed(1)}g</span>
                                                <span className={`text-xs font-bold ${MACRO_COLORS.fat}`}>F {t_.fat.toFixed(1)}g</span>
                                            </>
                                        );
                                    })()}
                                </div>
                            </div>
                        )}

                        {meals[activeTab].length === 0 && foodResults.length === 0 && !isSearchingFood && (
                            <div className="py-6 text-center text-gray-400 text-sm flex flex-col items-center gap-2">
                                {searchPerformed ? (
                                    <>
                                        <SearchX className="w-8 h-8 text-gray-300 dark:text-gray-700" />
                                        <p className="text-gray-500 dark:text-gray-400">{t('nutrition.no_search_results')}</p>
                                    </>
                                ) : (
                                    <>
                                        <UtensilsCrossed className="w-8 h-8 text-gray-300 dark:text-gray-700" />
                                        <p>{t('nutrition.food_search_hint')}</p>
                                    </>
                                )}
                            </div>
                        )}
                    </div>

                    {/* Feedback */}
                    {saveSuccess && <p className="text-sm text-emerald-600 dark:text-emerald-400 text-center">{saveSuccess}</p>}
                    {saveError && <p className="text-sm text-red-500 text-center">{saveError}</p>}

                    {/* Save */}
                    <button
                        onClick={handleSave}
                        disabled={isSaving}
                        className="w-full py-3 bg-blue-600 text-white font-bold rounded-2xl hover:bg-blue-700 disabled:opacity-50 transition-colors active:scale-95 flex items-center justify-center gap-2 text-sm"
                    >
                        {isSaving && <Loader2 className="w-4 h-4 animate-spin" />}
                        {isSaving ? '...' : t('nutrition.save_plan')}
                    </button>
                </main>
            </div>
        </div>
    );
}
