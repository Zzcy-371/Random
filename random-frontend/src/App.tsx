import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import MainLayout from './components/layout/MainLayout';
import ProtectedRoute from './components/shared/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import EatingPage from './pages/EatingPage';
import DrinkingPage from './pages/DrinkingPage';
import PlayingPage from './pages/PlayingPage';
import StayingPage from './pages/StayingPage';
import OtherPage from './pages/OtherPage';
import HistoryPage from './pages/HistoryPage';
import PreferencesPage from './pages/PreferencesPage';
import ProfilePage from './pages/ProfilePage';

export default function App() {
  const { loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600" />
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
        <Route path="/" element={<HomePage />} />
        <Route path="/eating" element={<EatingPage />} />
        <Route path="/drinking" element={<DrinkingPage />} />
        <Route path="/playing" element={<PlayingPage />} />
        <Route path="/staying" element={<StayingPage />} />
        <Route path="/other" element={<OtherPage />} />
        <Route path="/history" element={<HistoryPage />} />
        <Route path="/preferences" element={<PreferencesPage />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
