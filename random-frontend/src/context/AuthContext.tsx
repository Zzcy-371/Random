import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authApi, UserVO } from '../api/authApi';

interface AuthState {
  user: UserVO | null;
  token: string | null;
  loading: boolean;
}

interface AuthContextType extends AuthState {
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, password: string, nickname?: string, email?: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: localStorage.getItem('token'),
    loading: true,
  });

  const loadUser = useCallback(async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      setState({ user: null, token: null, loading: false });
      return;
    }
    try {
      const res = await authApi.getMe();
      setState({ user: res.data.data, token, loading: false });
    } catch {
      localStorage.removeItem('token');
      setState({ user: null, token: null, loading: false });
    }
  }, []);

  useEffect(() => {
    loadUser();
  }, [loadUser]);

  const login = async (username: string, password: string) => {
    const res = await authApi.login({ username, password });
    const { token, user } = res.data.data;
    localStorage.setItem('token', token);
    setState({ user, token, loading: false });
  };

  const register = async (username: string, password: string, nickname?: string, email?: string) => {
    const res = await authApi.register({ username, password, nickname, email });
    const { token, user } = res.data.data;
    localStorage.setItem('token', token);
    setState({ user, token, loading: false });
  };

  const logout = () => {
    localStorage.removeItem('token');
    setState({ user: null, token: null, loading: false });
  };

  return (
    <AuthContext.Provider value={{ ...state, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
}
