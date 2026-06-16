import { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { ActivityIndicator, View } from 'react-native';
import { getUser } from '../lib/auth';
import type { UserInfo } from '../../../shared/types/user';
import { LoginScreen } from '../screens/LoginScreen';
import { RegisterScreen } from '../screens/RegisterScreen';
import { DashboardScreen } from '../screens/DashboardScreen';
import { WorkoutScreen } from '../screens/WorkoutScreen';
import { ProgressPhotosScreen } from '../screens/ProgressPhotosScreen';

export type RootStackParamList = {
    Login: undefined;
    Register: undefined;
    Dashboard: { user: UserInfo };
    Workout: { user: UserInfo };
    ProgressPhotos: { user: UserInfo };
};

const Stack = createNativeStackNavigator<RootStackParamList>();

export function AppNavigator() {
    const [initialUser, setInitialUser] = useState<UserInfo | null | undefined>(undefined);

    useEffect(() => {
        getUser().then(setInitialUser);
    }, []);

    if (initialUser === undefined) {
        return (
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
                <ActivityIndicator size="large" color="#3b82f6" />
            </View>
        );
    }

    return (
        <NavigationContainer>
            <Stack.Navigator screenOptions={{ headerShown: false }}>
                {initialUser ? (
                    <>
                        <Stack.Screen name="Dashboard" component={DashboardScreen} initialParams={{ user: initialUser }} />
                        <Stack.Screen name="Workout" component={WorkoutScreen} />
                        <Stack.Screen name="ProgressPhotos" component={ProgressPhotosScreen} />
                    </>
                ) : (
                    <>
                        <Stack.Screen name="Login" component={LoginScreen} />
                        <Stack.Screen name="Register" component={RegisterScreen} />
                    </>
                )}
            </Stack.Navigator>
        </NavigationContainer>
    );
}
