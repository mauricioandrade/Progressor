import { useEffect, useState, useCallback } from 'react';
import {
    View, Text, ScrollView, TouchableOpacity, StyleSheet,
    ActivityIndicator, Alert, RefreshControl, Platform,
} from 'react-native';
import * as Notifications from 'expo-notifications';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { api } from '../lib/api';
import { clearAuth } from '../lib/auth';
import type { WorkoutExercise } from '../../../shared/types/workout';
import type { RootStackParamList } from '../navigation/AppNavigator';

type Props = NativeStackScreenProps<RootStackParamList, 'Dashboard'>;

interface WaterIntake {
    totalMl: number;
    goalMl: number;
}

export function DashboardScreen({ navigation, route }: Props) {
    const { user } = route.params;
    const [todayExercises, setTodayExercises] = useState<WorkoutExercise[]>([]);
    const [water, setWater] = useState<WaterIntake | null>(null);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [loggingWater, setLoggingWater] = useState(false);

    async function fetchData() {
        try {
            const [exRes, waterRes] = await Promise.allSettled([
                api.get<WorkoutExercise[]>('/workouts/today'),
                api.get<WaterIntake>('/water/today'),
            ]);
            if (exRes.status === 'fulfilled') setTodayExercises(exRes.value.data);
            if (waterRes.status === 'fulfilled') setWater(waterRes.value.data);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }

    useEffect(() => {
        fetchData();
        registerPushToken();
    }, []);

    async function registerPushToken() {
        if (Platform.OS === 'web') return;
        try {
            const { status } = await Notifications.requestPermissionsAsync();
            if (status !== 'granted') return;
            const tokenData = await Notifications.getExpoPushTokenAsync();
            await api.post('/users/push-token', { pushToken: tokenData.data });
        } catch {
            // push token registration is best-effort
        }
    }

    const onRefresh = useCallback(() => {
        setRefreshing(true);
        fetchData();
    }, []);

    async function logWater(ml: number) {
        setLoggingWater(true);
        try {
            await api.post('/water', { amountMl: ml });
            const res = await api.get<WaterIntake>('/water/today');
            setWater(res.data);
        } catch {
            Alert.alert('Erro', 'Não foi possível registrar água.');
        } finally {
            setLoggingWater(false);
        }
    }

    async function handleLogout() {
        await clearAuth();
        navigation.reset({ index: 0, routes: [{ name: 'Login' }] });
    }

    const waterPct = water ? Math.min((water.totalMl / water.goalMl) * 100, 100) : 0;

    if (loading) {
        return (
            <View style={s.center}>
                <ActivityIndicator size="large" color="#3b82f6" />
            </View>
        );
    }

    return (
        <ScrollView
            style={s.container}
            refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        >
            <View style={s.header}>
                <View>
                    <Text style={s.greeting}>Olá 👋</Text>
                    <Text style={s.email}>{user.email}</Text>
                </View>
                <TouchableOpacity onPress={handleLogout}>
                    <Text style={s.logoutText}>Sair</Text>
                </TouchableOpacity>
            </View>

            {/* Today's workout */}
            <View style={s.card}>
                <Text style={s.cardTitle}>Treino de hoje</Text>
                {todayExercises.length === 0 ? (
                    <Text style={s.empty}>Nenhum exercício agendado para hoje.</Text>
                ) : (
                    <>
                        {todayExercises.slice(0, 3).map((ex) => (
                            <View key={ex.id} style={s.exerciseRow}>
                                <Text style={s.exerciseName}>{ex.name}</Text>
                                <Text style={s.exerciseMeta}>{ex.sets}x{ex.repetitions}</Text>
                            </View>
                        ))}
                        {todayExercises.length > 3 && (
                            <Text style={s.moreText}>+{todayExercises.length - 3} exercícios</Text>
                        )}
                        <TouchableOpacity
                            style={s.primaryButton}
                            onPress={() => navigation.navigate('Workout', { user })}
                        >
                            <Text style={s.primaryButtonText}>Ver treino completo</Text>
                        </TouchableOpacity>
                    </>
                )}
            </View>

            {/* Water intake */}
            <View style={s.card}>
                <Text style={s.cardTitle}>Hidratação</Text>
                <View style={s.waterBar}>
                    <View style={[s.waterFill, { width: `${waterPct}%` as any }]} />
                </View>
                <Text style={s.waterLabel}>
                    {water ? `${water.totalMl} ml / ${water.goalMl} ml` : '— ml'}
                </Text>
                <View style={s.waterButtons}>
                    {[150, 200, 300, 500].map((ml) => (
                        <TouchableOpacity
                            key={ml}
                            style={s.waterButton}
                            onPress={() => logWater(ml)}
                            disabled={loggingWater}
                        >
                            <Text style={s.waterButtonText}>+{ml}ml</Text>
                        </TouchableOpacity>
                    ))}
                </View>
            </View>

            {/* Quick actions */}
            <View style={s.card}>
                <Text style={s.cardTitle}>Acesso rápido</Text>
                <TouchableOpacity
                    style={s.actionRow}
                    onPress={() => navigation.navigate('ProgressPhotos', { user })}
                >
                    <Text style={s.actionText}>Fotos de progresso →</Text>
                </TouchableOpacity>
            </View>

            <View style={{ height: 32 }} />
        </ScrollView>
    );
}

const s = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f1f5f9' },
    center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 20, paddingTop: 56 },
    greeting: { fontSize: 22, fontWeight: '800', color: '#111827' },
    email: { fontSize: 13, color: '#6b7280', marginTop: 2 },
    logoutText: { color: '#ef4444', fontWeight: '600', fontSize: 14 },
    card: { backgroundColor: '#fff', borderRadius: 20, marginHorizontal: 16, marginBottom: 16, padding: 20, shadowColor: '#000', shadowOpacity: 0.05, shadowRadius: 8, elevation: 2 },
    cardTitle: { fontSize: 16, fontWeight: '700', color: '#111827', marginBottom: 14 },
    empty: { color: '#9ca3af', fontSize: 14, textAlign: 'center', paddingVertical: 8 },
    exerciseRow: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 6, borderBottomWidth: 1, borderBottomColor: '#f3f4f6' },
    exerciseName: { fontSize: 14, color: '#374151', flex: 1 },
    exerciseMeta: { fontSize: 14, color: '#6b7280', fontWeight: '600' },
    moreText: { fontSize: 13, color: '#9ca3af', marginTop: 8 },
    primaryButton: { backgroundColor: '#3b82f6', borderRadius: 12, paddingVertical: 12, alignItems: 'center', marginTop: 14 },
    primaryButtonText: { color: '#fff', fontWeight: '700', fontSize: 14 },
    waterBar: { height: 10, backgroundColor: '#e5e7eb', borderRadius: 5, overflow: 'hidden', marginBottom: 8 },
    waterFill: { height: '100%', backgroundColor: '#3b82f6', borderRadius: 5 },
    waterLabel: { fontSize: 13, color: '#6b7280', textAlign: 'center', marginBottom: 12 },
    waterButtons: { flexDirection: 'row', justifyContent: 'space-between' },
    waterButton: { backgroundColor: '#eff6ff', borderRadius: 10, paddingVertical: 8, paddingHorizontal: 12 },
    waterButtonText: { color: '#3b82f6', fontWeight: '600', fontSize: 13 },
    actionRow: { paddingVertical: 10 },
    actionText: { color: '#3b82f6', fontSize: 14, fontWeight: '600' },
});
