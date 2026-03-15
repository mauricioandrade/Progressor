import { Navigate, Outlet } from 'react-router-dom';

export function ProtectedRoute() {
    const token = localStorage.getItem('@Progressor:token');

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}