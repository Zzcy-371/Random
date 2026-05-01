import { useAuth } from '../context/AuthContext';
import Card from '../components/ui/Card';
import { User, Mail, Calendar } from 'lucide-react';
import { formatBeijingDate } from '../utils/formatDate';

export default function ProfilePage() {
  const { user } = useAuth();

  if (!user) return null;

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">个人资料</h1>

      <Card className="p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center">
            <User size={32} className="text-indigo-600" />
          </div>
          <div>
            <h2 className="text-xl font-semibold text-gray-900">{user.nickname || user.username}</h2>
            <p className="text-gray-500">@{user.username}</p>
          </div>
        </div>

        <div className="space-y-4">
          <div className="flex items-center gap-3 text-gray-600">
            <User size={18} />
            <span>用户名：{user.username}</span>
          </div>
          {user.email && (
            <div className="flex items-center gap-3 text-gray-600">
              <Mail size={18} />
              <span>邮箱：{user.email}</span>
            </div>
          )}
          <div className="flex items-center gap-3 text-gray-600">
            <Calendar size={18} />
            <span>注册时间：{formatBeijingDate(user.createdAt)}</span>
          </div>
        </div>
      </Card>
    </div>
  );
}
