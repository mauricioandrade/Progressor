import { useState } from 'react';
import {
    View, Text, TextInput, TouchableOpacity, StyleSheet,
    KeyboardAvoidingView, Platform, ActivityIndicator, Alert, ScrollView,
} from 'react-native';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { api } from '../lib/api';
import { saveToken, saveUser } from '../lib/auth';
import type { UserInfo } from '../../../shared/types/user';
import type { RootStackParamList } from '../navigation/AppNavigator';

type Props = NativeStackScreenProps<RootStackParamList, 'Register'>;

export function RegisterScreen({ navigation }: Props) {
    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);

    async function handleRegister() {
        if (!fullName || !email || !password) {
            Alert.alert('Atenção', 'Preencha todos os campos.');
            return;
        }
        if (password.length < 6) {
            Alert.alert('Atenção', 'Senha deve ter ao menos 6 caracteres.');
            return;
        }
        setLoading(true);
        try {
            const res = await api.post<{ token: string } & UserInfo>('/auth/register', {
                fullName,
                email,
                password,
                role: 'STUDENT',
            });
            const { token, ...userInfo } = res.data as any;
            if (token) await saveToken(token);
            await saveUser(userInfo as UserInfo);
            navigation.reset({ index: 0, routes: [{ name: 'Dashboard', params: { user: userInfo } }] });
        } catch (err: any) {
            const status = err?.response?.status;
            if (status === 409) {
                Alert.alert('Erro', 'E-mail já cadastrado.');
            } else {
                Alert.alert('Erro', 'Não foi possível criar a conta.');
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <KeyboardAvoidingView style={s.container} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
            <ScrollView contentContainerStyle={s.scroll} keyboardShouldPersistTaps="handled">
                <View style={s.card}>
                    <Text style={s.title}>Criar conta</Text>
                    <Text style={s.subtitle}>Comece sua jornada agora</Text>

                    <TextInput
                        style={s.input}
                        placeholder="Nome completo"
                        placeholderTextColor="#9ca3af"
                        autoCapitalize="words"
                        value={fullName}
                        onChangeText={setFullName}
                    />
                    <TextInput
                        style={s.input}
                        placeholder="E-mail"
                        placeholderTextColor="#9ca3af"
                        keyboardType="email-address"
                        autoCapitalize="none"
                        value={email}
                        onChangeText={setEmail}
                    />
                    <TextInput
                        style={s.input}
                        placeholder="Senha (mín. 6 caracteres)"
                        placeholderTextColor="#9ca3af"
                        secureTextEntry
                        value={password}
                        onChangeText={setPassword}
                        onSubmitEditing={handleRegister}
                    />

                    <TouchableOpacity style={s.button} onPress={handleRegister} disabled={loading}>
                        {loading ? (
                            <ActivityIndicator color="#fff" />
                        ) : (
                            <Text style={s.buttonText}>Cadastrar</Text>
                        )}
                    </TouchableOpacity>

                    <TouchableOpacity style={s.link} onPress={() => navigation.goBack()}>
                        <Text style={s.linkText}>Já tem conta? Entrar</Text>
                    </TouchableOpacity>
                </View>
            </ScrollView>
        </KeyboardAvoidingView>
    );
}

const s = StyleSheet.create({
    container: { flex: 1, backgroundColor: '#f1f5f9' },
    scroll: { flexGrow: 1, justifyContent: 'center', padding: 20 },
    card: { backgroundColor: '#fff', borderRadius: 24, padding: 28, shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 16, elevation: 4 },
    title: { fontSize: 28, fontWeight: '800', color: '#111827', textAlign: 'center', marginBottom: 4 },
    subtitle: { fontSize: 14, color: '#6b7280', textAlign: 'center', marginBottom: 28 },
    input: { borderWidth: 1, borderColor: '#e5e7eb', borderRadius: 16, paddingHorizontal: 16, paddingVertical: 12, fontSize: 15, color: '#111827', marginBottom: 12, backgroundColor: '#f9fafb' },
    button: { backgroundColor: '#3b82f6', borderRadius: 16, paddingVertical: 14, alignItems: 'center', marginTop: 4 },
    buttonText: { color: '#fff', fontWeight: '700', fontSize: 16 },
    link: { marginTop: 20, alignItems: 'center' },
    linkText: { color: '#3b82f6', fontSize: 14, fontWeight: '500' },
});
