import { useEffect, useState, useRef } from 'react';
import {
    View, Text, ScrollView, TouchableOpacity, StyleSheet,
    ActivityIndicator, Alert, TextInput, Modal,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { api } from '../lib/api';
import type { WorkoutExercise } from '../../../shared/types/workout';
import type { RootStackParamList } from '../navigation/AppNavigator';

type Props = NativeStackScreenProps<RootStackParamList, 'Workout'>;

export function WorkoutScreen({ navigation, route }: Props) {
    const { user } = route.params;
    const [exercises, setExercises] = useState<WorkoutExercise[]>([]);
    const [loading, setLoading] = useState(true);
    const [logModal, setLogModal] = useState<WorkoutExercise | null>(null);
    const [actualWeight, setActualWeight] = useState('');
    const [actualReps, setActualReps] = useState('');
    const [logging, setLogging] = useState(false);

    // Rest timer
    const [restSeconds, setRestSeconds] = useState(0);
    const [timerRunning, setTimerRunning] = useState(false);
    const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);

    useEffect(() => {
        api.get<WorkoutExercise[]>('/workouts/today')
            .then((r) => setExercises(r.data))
            .catch(() => Alert.alert('Erro', 'Não foi possível carregar o treino.'))
            .finally(() => setLoading(false));
    }, []);

    function startRestTimer(seconds: number) {
        if (timerRef.current) clearInterval(timerRef.current);
        setRestSeconds(seconds);
        setTimerRunning(true);
        timerRef.current = setInterval(() => {
            setRestSeconds((prev) => {
                if (prev <= 1) {
                    clearInterval(timerRef.current!);
                    setTimerRunning(false);
                    return 0;
                }
                return prev - 1;
            });
        }, 1000);
    }

    useEffect(() => () => { if (timerRef.current) clearInterval(timerRef.current); }, []);

    function openLog(ex: WorkoutExercise) {
        setLogModal(ex);
        setActualWeight(ex.weightInKg?.toString() ?? '');
        setActualReps(ex.repetitions.toString());
    }

    async function submitLog() {
        if (!logModal) return;
        const reps = parseInt(actualReps, 10);
        if (!reps || reps < 1) {
            Alert.alert('Atenção', 'Informe as repetições realizadas.');
            return;
        }
        setLogging(true);
        try {
            await api.post('/workouts/log', {
                exerciseId: logModal.id,
                actualWeight: actualWeight ? parseFloat(actualWeight) : null,
                actualReps: reps,
            });
            setLogModal(null);
            const restTime = logModal.restTime ?? 60;
            startRestTimer(restTime);
            Alert.alert('Série registrada!', `Descansando ${restTime}s...`);
        } catch {
            Alert.alert('Erro', 'Não foi possível registrar a série.');
        } finally {
            setLogging(false);
        }
    }

    const timerColor = restSeconds > 0 ? '#3b82f6' : '#10b981';
    const mm = String(Math.floor(restSeconds / 60)).padStart(2, '0');
    const ss = String(restSeconds % 60).padStart(2, '0');

    if (loading) {
        return (
            <View style={s.center}>
                <ActivityIndicator size="large" color="#3b82f6" />
            </View>
        );
    }

    return (
        <View style={{ flex: 1, backgroundColor: '#f1f5f9' }}>
            <View style={s.header}>
                <TouchableOpacity onPress={() => navigation.goBack()}>
                    <Text style={s.back}>← Voltar</Text>
                </TouchableOpacity>
                <Text style={s.title}>Treino de hoje</Text>
                <View style={{ width: 60 }} />
            </View>

            {/* Rest timer */}
            {timerRunning && (
                <View style={s.timerBanner}>
                    <Text style={[s.timerText, { color: timerColor }]}>
                        Descanso: {mm}:{ss}
                    </Text>
                    <TouchableOpacity onPress={() => { clearInterval(timerRef.current!); setTimerRunning(false); setRestSeconds(0); }}>
                        <Text style={s.timerStop}>Pular</Text>
                    </TouchableOpacity>
                </View>
            )}

            <ScrollView contentContainerStyle={s.list}>
                {exercises.length === 0 ? (
                    <Text style={s.empty}>Nenhum exercício agendado para hoje.</Text>
                ) : (
                    exercises.map((ex) => (
                        <View key={ex.id} style={s.card}>
                            <View style={s.cardTop}>
                                <Text style={s.exerciseName}>{ex.name}</Text>
                                <Text style={s.exerciseMeta}>
                                    {ex.sets} séries × {ex.repetitions} reps
                                    {ex.weightInKg ? ` @ ${ex.weightInKg}kg` : ''}
                                </Text>
                            </View>
                            <TouchableOpacity style={s.logButton} onPress={() => openLog(ex)}>
                                <Text style={s.logButtonText}>Registrar série</Text>
                            </TouchableOpacity>
                        </View>
                    ))
                )}
                <View style={{ height: 32 }} />
            </ScrollView>

            {/* Log modal */}
            <Modal visible={!!logModal} transparent animationType="slide">
                <View style={s.modalOverlay}>
                    <View style={s.modalCard}>
                        <Text style={s.modalTitle}>{logModal?.name}</Text>
                        <Text style={s.modalSub}>
                            Planejado: {logModal?.sets}x{logModal?.repetitions}
                            {logModal?.weightInKg ? ` @ ${logModal.weightInKg}kg` : ''}
                        </Text>

                        <Text style={s.inputLabel}>Peso realizado (kg)</Text>
                        <TextInput
                            style={s.input}
                            keyboardType="decimal-pad"
                            placeholder="Ex: 60"
                            placeholderTextColor="#9ca3af"
                            value={actualWeight}
                            onChangeText={setActualWeight}
                        />
                        <Text style={s.inputLabel}>Repetições realizadas</Text>
                        <TextInput
                            style={s.input}
                            keyboardType="number-pad"
                            placeholder="Ex: 10"
                            placeholderTextColor="#9ca3af"
                            value={actualReps}
                            onChangeText={setActualReps}
                        />

                        <View style={s.modalButtons}>
                            <TouchableOpacity style={s.cancelButton} onPress={() => setLogModal(null)}>
                                <Text style={s.cancelText}>Cancelar</Text>
                            </TouchableOpacity>
                            <TouchableOpacity style={s.confirmButton} onPress={submitLog} disabled={logging}>
                                {logging ? <ActivityIndicator color="#fff" /> : <Text style={s.confirmText}>Salvar</Text>}
                            </TouchableOpacity>
                        </View>
                    </View>
                </View>
            </Modal>
        </View>
    );
}

const s = StyleSheet.create({
    center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 16, paddingTop: 56, paddingBottom: 12, backgroundColor: '#fff' },
    back: { color: '#3b82f6', fontSize: 15, fontWeight: '600', width: 60 },
    title: { fontSize: 16, fontWeight: '700', color: '#111827' },
    timerBanner: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#eff6ff', paddingHorizontal: 20, paddingVertical: 10 },
    timerText: { fontSize: 20, fontWeight: '800' },
    timerStop: { color: '#6b7280', fontSize: 13, fontWeight: '600' },
    list: { padding: 16 },
    empty: { color: '#9ca3af', fontSize: 14, textAlign: 'center', marginTop: 40 },
    card: { backgroundColor: '#fff', borderRadius: 16, padding: 16, marginBottom: 12, shadowColor: '#000', shadowOpacity: 0.05, shadowRadius: 6, elevation: 2 },
    cardTop: { marginBottom: 12 },
    exerciseName: { fontSize: 16, fontWeight: '700', color: '#111827', marginBottom: 4 },
    exerciseMeta: { fontSize: 13, color: '#6b7280' },
    logButton: { backgroundColor: '#3b82f6', borderRadius: 10, paddingVertical: 10, alignItems: 'center' },
    logButtonText: { color: '#fff', fontWeight: '600', fontSize: 14 },
    modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.4)', justifyContent: 'flex-end' },
    modalCard: { backgroundColor: '#fff', borderTopLeftRadius: 24, borderTopRightRadius: 24, padding: 24 },
    modalTitle: { fontSize: 18, fontWeight: '800', color: '#111827', marginBottom: 4 },
    modalSub: { fontSize: 13, color: '#6b7280', marginBottom: 20 },
    inputLabel: { fontSize: 13, fontWeight: '600', color: '#374151', marginBottom: 6 },
    input: { borderWidth: 1, borderColor: '#e5e7eb', borderRadius: 12, paddingHorizontal: 14, paddingVertical: 10, fontSize: 15, color: '#111827', marginBottom: 14, backgroundColor: '#f9fafb' },
    modalButtons: { flexDirection: 'row', gap: 12, marginTop: 4 },
    cancelButton: { flex: 1, borderWidth: 1, borderColor: '#e5e7eb', borderRadius: 12, paddingVertical: 12, alignItems: 'center' },
    cancelText: { color: '#6b7280', fontWeight: '600' },
    confirmButton: { flex: 1, backgroundColor: '#3b82f6', borderRadius: 12, paddingVertical: 12, alignItems: 'center' },
    confirmText: { color: '#fff', fontWeight: '700' },
});
