export interface WorkoutExercise {
  id: string;
  name: string;
  sets: number;
  repetitions: number;
  measurementType: string;
  weightInKg?: number | null;
  speed?: number | null;
  timeInSeconds?: number | null;
  cadence?: string | null;
  tonnage?: number | null;
  videoUrl?: string | null;
  restTime?: number | null;
  workoutLabel?: string | null;
  scheduledDays?: string | null;
  blockId?: string | null;
  studentId?: string | null;
}

export interface Measurement { weight: number | null; recordedAt?: string; }

export interface PersonalRecord {
  id: string;
  exerciseName: string;
  actualWeight: number | null;
  actualReps: number;
  tonnageAchieved: number;
  completedAt: string;
}

export interface MealItem {
  proteinG: number;
  carbsG: number;
  fatG: number;
  caloriesKcal: number;
  name?: string;
  mealTime?: string;
  quantity?: number;
  baseUnit?: string;
}

export interface MealPlan {
  id: string;
  studentId?: string;
  name?: string;
  goal?: string;
  items: MealItem[];
}

export interface WaterIntake { dailyWaterGoal: number; currentWaterIntake: number; }

export interface UserProfile { fullName: string; email: string; hasAvatar: boolean; }

export interface ConnectionInvite {
  id: string;
  professionalId: string;
  professionalName: string;
  professionalRole: 'COACH' | 'NUTRI';
  status: string;
  createdAt: string;
}

export interface ExerciseStats {
  exerciseId: string;
  lastWeight: number | null;
  lastReps: number | null;
  prWeight: number | null;
  prReps: number | null;
}

export interface Student {
  id: string;
  name: string;
  email: string;
  lastCheckIn?: string | null;
}

export interface Patient {
  id: string;
  name: string;
  email: string;
}

export interface ChatMessage {
  id: string;
  senderId: string;
  receiverId: string;
  content: string | null;
  hasImage: boolean;
  sentAt: string;
  readAt: string | null;
}

export interface ConversationSummary {
  partnerId: string;
  partnerName: string;
  lastMessageContent: string | null;
  lastMessageAt: string;
  unreadCount: number;
}

export interface ProfessionalDashboardStudent {
  studentId: string;
  studentName: string;
  studentEmail: string;
  lastCheckIn: string | null;
  checkInsLast7Days: number | null;
  lastFeedbackRating: number | null;
  lastFeedbackComment: string | null;
  lastFeedbackDate: string | null;
  lastFoodLogDate: string | null;
  todayAdherencePct: number | null;
}
