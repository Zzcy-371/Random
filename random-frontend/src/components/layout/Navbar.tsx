import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User, Shuffle } from 'lucide-react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const location = useLocation();

  return (
    <nav className="bg-white border-b border-gray-100 sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2 text-indigo-600 font-bold text-xl">
            <Shuffle size={24} />
            <span>Random</span>
          </Link>

          <div className="flex items-center gap-6">
            {['eating', 'drinking', 'playing', 'staying', 'other'].map((slug) => (
              <Link
                key={slug}
                to={`/${slug}`}
                className={`text-sm font-medium transition-colors ${
                  location.pathname === `/${slug}`
                    ? 'text-indigo-600'
                    : 'text-gray-500 hover:text-gray-900'
                }`}
              >
                {{ eating: '吃', drinking: '喝', playing: '玩', staying: '住', other: '其他' }[slug]}
              </Link>
            ))}
          </div>

          <div className="flex items-center gap-3">
            <Link to="/history" className="text-sm text-gray-500 hover:text-gray-900">历史</Link>
            <Link to="/preferences" className="text-sm text-gray-500 hover:text-gray-900">偏好</Link>
            {user && (
              <div className="flex items-center gap-2 ml-2">
                <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center">
                  <User size={16} className="text-indigo-600" />
                </div>
                <span className="text-sm text-gray-700">{user.nickname || user.username}</span>
                <button onClick={logout} className="p-1 text-gray-400 hover:text-red-500" title="退出登录">
                  <LogOut size={18} />
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
