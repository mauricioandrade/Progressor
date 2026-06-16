import { useEffect, useState, useCallback } from 'react';
import {
    View, Text, FlatList, TouchableOpacity, StyleSheet,
    ActivityIndicator, Alert, Image, RefreshControl, Dimensions,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { api } from '../lib/api';
import type { RootStackParamList } from '../navigation/AppNavigator';

type Props = NativeStackScreenProps<RootStackParamList, 'ProgressPhotos'>;

interface ProgressPhoto {
    id: string;
    imageUrl: string;
    takenAt: string;
    notes: string | null;
}

const COLS = 2;
const ITEM_SIZE = (Dimensions.get('window').width - 48) / COLS;

export function ProgressPhotosScreen({ navigation }: Props) {
    const [photos, setPhotos] = useState<ProgressPhoto[]>([]);
    const [loading, setLoading] = useState(true);
    const [uploading, setUploading] = useState(false);
    const [refreshing, setRefreshing] = useState(false);

    async function fetchPhotos() {
        try {
            const res = await api.get<ProgressPhoto[]>('/progress/photos');
            setPhotos(res.data);
        } catch {
            Alert.alert('Erro', 'Não foi possível carregar as fotos.');
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }

    useEffect(() => { fetchPhotos(); }, []);

    const onRefresh = useCallback(() => {
        setRefreshing(true);
        fetchPhotos();
    }, []);

    async function pickAndUpload() {
        const perm = await ImagePicker.requestMediaLibraryPermissionsAsync();
        if (!perm.granted) {
            const cam = await ImagePicker.requestCameraPermissionsAsync();
            if (!cam.granted) {
                Alert.alert('Permissão negada', 'Permita acesso à câmera ou galeria nas configurações.');
                return;
            }
        }

        Alert.alert('Adicionar foto', 'Escolha a origem', [
            {
                text: 'Câmera',
                onPress: async () => {
                    const result = await ImagePicker.launchCameraAsync({
                        mediaTypes: ['images'],
                        quality: 0.8,
                        allowsEditing: true,
                        aspect: [3, 4],
                    });
                    if (!result.canceled) await upload(result.assets[0]);
                },
            },
            {
                text: 'Galeria',
                onPress: async () => {
                    const result = await ImagePicker.launchImageLibraryAsync({
                        mediaTypes: ['images'],
                        quality: 0.8,
                        allowsEditing: true,
                        aspect: [3, 4],
                    });
                    if (!result.canceled) await upload(result.assets[0]);
                },
            },
            { text: 'Cancelar', style: 'cancel' },
        ]);
    }

    async function upload(asset: ImagePicker.ImagePickerAsset) {
        setUploading(true);
        try {
            const form = new FormData();
            form.append('photo', {
                uri: asset.uri,
                name: 'progress.jpg',
                type: 'image/jpeg',
            } as any);
            await api.post('/progress/photos', form, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            await fetchPhotos();
        } catch {
            Alert.alert('Erro', 'Não foi possível enviar a foto.');
        } finally {
            setUploading(false);
        }
    }

    function formatDate(iso: string) {
        const d = new Date(iso);
        return d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' });
    }

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
                <Text style={s.title}>Fotos de progresso</Text>
                <TouchableOpacity onPress={pickAndUpload} disabled={uploading}>
                    {uploading ? (
                        <ActivityIndicator color="#3b82f6" />
                    ) : (
                        <Text style={s.addBtn}>+ Adicionar</Text>
                    )}
                </TouchableOpacity>
            </View>

            {photos.length === 0 ? (
                <View style={s.center}>
                    <Text style={s.emptyTitle}>Nenhuma foto ainda</Text>
                    <Text style={s.emptySubtitle}>Registre sua evolução com fotos periódicas.</Text>
                    <TouchableOpacity style={s.ctaButton} onPress={pickAndUpload}>
                        <Text style={s.ctaText}>Adicionar primeira foto</Text>
                    </TouchableOpacity>
                </View>
            ) : (
                <FlatList
                    data={photos}
                    keyExtractor={(item) => item.id}
                    numColumns={COLS}
                    contentContainerStyle={s.grid}
                    refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                    renderItem={({ item }) => (
                        <View style={s.photoContainer}>
                            <Image
                                source={{ uri: item.imageUrl }}
                                style={s.photo}
                                resizeMode="cover"
                            />
                            <Text style={s.photoDate}>{formatDate(item.takenAt)}</Text>
                        </View>
                    )}
                />
            )}
        </View>
    );
}

const s = StyleSheet.create({
    center: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 24 },
    header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: 16, paddingTop: 56, paddingBottom: 12, backgroundColor: '#fff' },
    back: { color: '#3b82f6', fontSize: 15, fontWeight: '600' },
    title: { fontSize: 16, fontWeight: '700', color: '#111827' },
    addBtn: { color: '#3b82f6', fontSize: 14, fontWeight: '700' },
    emptyTitle: { fontSize: 18, fontWeight: '700', color: '#374151', marginBottom: 8 },
    emptySubtitle: { fontSize: 14, color: '#9ca3af', textAlign: 'center', marginBottom: 24 },
    ctaButton: { backgroundColor: '#3b82f6', borderRadius: 14, paddingVertical: 12, paddingHorizontal: 24 },
    ctaText: { color: '#fff', fontWeight: '700', fontSize: 15 },
    grid: { padding: 16 },
    photoContainer: { flex: 1, margin: 4 },
    photo: { width: ITEM_SIZE, height: ITEM_SIZE * (4 / 3), borderRadius: 12 },
    photoDate: { fontSize: 11, color: '#6b7280', textAlign: 'center', marginTop: 4 },
});
