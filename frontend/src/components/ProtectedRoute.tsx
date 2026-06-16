import { Navigate, Outlet } from 'react-router-dom';
import { getAuthState } from '../hooks/useAuth';

export function ProtectedRoute() {
    const { signed } = getAuthState();

    if (!signed) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}
