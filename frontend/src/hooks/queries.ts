import { useQuery } from '@tanstack/react-query';
import { api, fetchConversations, fetchMessages } from '../services/api';
import type {
  WorkoutExercise, Measurement, PersonalRecord, MealPlan,
  WaterIntake, UserProfile, ConnectionInvite, ExerciseStats,
  Student, Patient, ProfessionalDashboardStudent,
  ChatMessage, ConversationSummary,
} from '../types/api';

export { useQueryClient } from '@tanstack/react-query';
export type { WorkoutExercise, Measurement, PersonalRecord, MealPlan, WaterIntake, UserProfile, ConnectionInvite, ExerciseStats, Student, Patient, ProfessionalDashboardStudent, ChatMessage, ConversationSummary } from '../types/api';

export const QK = {
  conversations:      ['chat', 'conversations'],
  messages:           (partnerId: string) => ['chat', 'messages', partnerId],
  myWorkouts:           ['workouts', 'my'],
  myPRs:                ['workouts', 'prs'],
  exerciseStats:        ['workouts', 'exercise-stats'],
  myMeasurements:       ['measurements', 'my'],
  myWeightGoal:         ['measurements', 'weight-goal'],
  myCheckIns:           ['checkins', 'my'],
  myMealPlan:           ['nutrition', 'meal-plan'],
  myWater:              ['nutrition', 'water'],
  myProfile:            ['users', 'me'],
  myAvatar:             ['users', 'me', 'avatar'],
  students:             ['users', 'students'],
  nutritionistStudents: ['users', 'nutritionist', 'students'],
  pendingConnections:   ['connections', 'pending'],
  professionalCount:    (endpoint: string) => ['professional', 'count', endpoint],
  professionalDashboard: ['professional', 'dashboard'],
} as const;

export function useMyWorkouts() {
  return useQuery<WorkoutExercise[]>({
    queryKey: QK.myWorkouts,
    queryFn: () => api.get<WorkoutExercise[]>('/workouts/my').then(r => r.data),
  });
}

export function useMyPRs() {
  return useQuery<PersonalRecord[]>({
    queryKey: QK.myPRs,
    queryFn: () => api.get<PersonalRecord[]>('/workouts/prs').then(r => r.data),
  });
}

export function useExerciseStats(enabled: boolean) {
  return useQuery<ExerciseStats[]>({
    queryKey: QK.exerciseStats,
    queryFn: () => api.get<ExerciseStats[]>('/workouts/exercise-stats').then(r => r.data),
    enabled,
  });
}

export function useMyMeasurements() {
  return useQuery<Measurement[]>({
    queryKey: QK.myMeasurements,
    queryFn: () => api.get<Measurement[]>('/measurements/my').then(r => r.data),
  });
}

export function useMyWeightGoal() {
  return useQuery<number | null>({
    queryKey: QK.myWeightGoal,
    queryFn: () => api.get('/measurements/my/weight-goal').then(r => r.data.weightGoal ?? null),
  });
}

export function useMyCheckIns() {
  return useQuery<string[]>({
    queryKey: QK.myCheckIns,
    queryFn: () => api.get<string[]>('/checkins/my').then(r => r.data),
  });
}

export function useMyMealPlan() {
  return useQuery<MealPlan | null>({
    queryKey: QK.myMealPlan,
    queryFn: () => api.get<MealPlan>('/nutrition/meal-plans/my').then(r => r.data).catch(() => null),
  });
}

export function useMyWater() {
  return useQuery<WaterIntake>({
    queryKey: QK.myWater,
    queryFn: () => api.get<WaterIntake>('/nutrition/water').then(r => r.data),
    placeholderData: { dailyWaterGoal: 0, currentWaterIntake: 0 },
  });
}

export function useMyProfile() {
  return useQuery<UserProfile | null>({
    queryKey: QK.myProfile,
    queryFn: () => api.get<UserProfile>('/users/me').then(r => r.data).catch(() => null),
  });
}

export function useMyAvatar() {
  return useQuery<string | null>({
    queryKey: QK.myAvatar,
    queryFn: () =>
      api.get('/users/me/avatar', { responseType: 'blob' })
        .then(r => URL.createObjectURL(r.data))
        .catch(() => null),
    staleTime: 1000 * 60 * 30,
  });
}

export function useStudents() {
  return useQuery<Student[]>({
    queryKey: QK.students,
    queryFn: () => api.get<Student[]>('/users/students').then(r => r.data),
  });
}

export function useNutritionistStudents() {
  return useQuery<Patient[]>({
    queryKey: QK.nutritionistStudents,
    queryFn: () => api.get<Patient[]>('/users/my-students/nutritionist').then(r => r.data),
  });
}

export function usePendingConnections() {
  return useQuery<ConnectionInvite[]>({
    queryKey: QK.pendingConnections,
    queryFn: () => api.get<ConnectionInvite[]>('/connections/pending').then(r => r.data),
  });
}

export function useProfessionalCount(endpoint: string) {
  return useQuery<number>({
    queryKey: QK.professionalCount(endpoint),
    queryFn: () => api.get(endpoint).then(r => (r.data.length as number)),
  });
}

export function useProfessionalDashboard() {
  return useQuery<ProfessionalDashboardStudent[]>({
    queryKey: QK.professionalDashboard,
    queryFn: () => api.get<ProfessionalDashboardStudent[]>('/professional/dashboard').then(r => r.data),
  });
}

export function useConversations() {
  return useQuery<ConversationSummary[]>({
    queryKey: QK.conversations,
    queryFn: fetchConversations,
    refetchInterval: 30_000,
    refetchIntervalInBackground: false,
  });
}

export function useMessages(partnerId: string) {
  return useQuery<ChatMessage[]>({
    queryKey: QK.messages(partnerId),
    queryFn: () => fetchMessages(partnerId),
    refetchInterval: 15_000,
    refetchIntervalInBackground: false,
    enabled: !!partnerId,
  });
}
