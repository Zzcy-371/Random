import client from './client';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  nickname?: string;
  email?: string;
}

export interface UserVO {
  id: number;
  username: string;
  nickname?: string;
  email?: string;
  avatarUrl?: string;
  createdAt: string;
}

export const authApi = {
  register: (data: RegisterRequest) => client.post('/auth/register', data),
  login: (data: LoginRequest) => client.post('/auth/login', data),
  getMe: () => client.get('/auth/me'),
  updateMe: (data: Partial<RegisterRequest>) => client.put('/auth/me', data),
};
