export interface WorkoutExercise {
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

export interface ExerciseLog {
    id: string;
    exerciseId: string;
    actualWeight: number | null;
    actualReps: number;
    completedAt: string;
    tonnageAchieved: number;
}

export interface PersonalRecord {
    id: string;
    exerciseName: string;
    actualWeight: number | null;
    actualReps: number;
    tonnageAchieved: number;
    completedAt: string;
}

export interface WorkoutPlan {
    id: string;
    name: string;
    createdAt: string;
}

export interface LogWorkoutRequest {
    exerciseId: string;
    actualWeight: number | null;
    actualReps: number;
}
