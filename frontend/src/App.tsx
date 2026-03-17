import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { Login } from './pages/Login';
import { SignUp } from './pages/SignUp';
import { ForgotPasswordPage } from './pages/ForgotPasswordPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { Dashboard } from './pages/Dashboard';
import { Students } from './pages/Students';
import { WorkoutBuilder } from './pages/WorkoutBuilder';
import { WorkoutView } from './pages/WorkoutView';
import { MeasurementsPage } from './pages/MeasurementsPage';
import { AssessmentForm } from './pages/AssessmentForm';
import { StudentSelfAssessment } from './pages/StudentSelfAssessment';
import { NutritionistPatients } from './pages/NutritionistPatients';
import { NutritionistDietBuilder } from './pages/NutritionistDietBuilder';
import { StudentDietPage } from './pages/StudentDietPage';
import { SettingsPage } from './pages/SettingsPage';
import { VisualProgressPage } from './pages/VisualProgressPage';
import { StudentDetailPage } from './pages/StudentDetailPage';
import { ProtectedRoute } from './components/ProtectedRoute';
import { RestTimerProvider } from './contexts/RestTimerContext';
import { RestTimerPill } from './components/RestTimerPill';

function App() {
  return (
    <RestTimerProvider>
      <BrowserRouter>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              borderRadius: '16px',
              background: 'rgba(17,17,17,0.92)',
              color: '#f9fafb',
              backdropFilter: 'blur(12px)',
              border: '1px solid rgba(255,255,255,0.08)',
              fontSize: '14px',
              fontWeight: '500',
              padding: '12px 16px',
            },
            success: {
              iconTheme: { primary: '#22c55e', secondary: '#fff' },
            },
            error: {
              iconTheme: { primary: '#ef4444', secondary: '#fff' },
            },
          }}
        />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/students" element={<Students />} />
            <Route path="/workouts/new" element={<WorkoutBuilder />} />
            <Route path="/workouts" element={<WorkoutView />} />
            <Route path="/measurements" element={<MeasurementsPage />} />
            <Route path="/assessments/new" element={<AssessmentForm />} />
            <Route path="/measurements/log" element={<StudentSelfAssessment />} />
            <Route path="/diet/patients" element={<NutritionistPatients />} />
            <Route path="/diet/builder" element={<NutritionistDietBuilder />} />
            <Route path="/my-diet" element={<StudentDietPage />} />
            <Route path="/settings" element={<SettingsPage />} />
            <Route path="/progress" element={<VisualProgressPage />} />
            <Route path="/professional/student/:studentId" element={<StudentDetailPage />} />
          </Route>

          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
        <RestTimerPill />
      </BrowserRouter>
    </RestTimerProvider>
  );
}

export default App;
