export interface MealItem {
    id: string;
    foodName: string;
    mealTime: 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
    quantityGrams: number;
    proteinG: number;
    carbsG: number;
    fatG: number;
    caloriesKcal: number;
}

export interface MealPlan {
    id: string;
    name: string;
    goal: string;
    items: MealItem[];
    cheatMeal: boolean;
}

export interface WaterIntake {
    dailyWaterGoal: number;
    currentWaterIntake: number;
}
