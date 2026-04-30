import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { recommendationApi, DailyRecommendationVO } from '../api/recommendationApi';
import Card from '../components/ui/Card';
import { Utensils, Coffee, Gamepad, Bed, Shuffle, Sparkles } from 'lucide-react';
import { motion } from 'framer-motion';

const MODULES = [
  { slug: 'eating', name: '吃', icon: Utensils, color: 'from-orange-400 to-red-500' },
  { slug: 'drinking', name: '喝', icon: Coffee, color: 'from-amber-400 to-orange-500' },
  { slug: 'playing', name: '玩', icon: Gamepad, color: 'from-green-400 to-emerald-500' },
  { slug: 'staying', name: '住', icon: Bed, color: 'from-blue-400 to-indigo-500' },
  { slug: 'other', name: '其他', icon: Shuffle, color: 'from-purple-400 to-pink-500' },
];

export default function HomePage() {
  const [recommendation, setRecommendation] = useState<DailyRecommendationVO | null>(null);

  useEffect(() => {
    recommendationApi.getDaily().then(res => {
      setRecommendation(res.data.data);
    }).catch(() => {});
  }, []);

  return (
    <div className="space-y-8">
      {/* Welcome */}
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">今天想做什么？</h1>
        <p className="text-gray-500">选择一个模块，让随机决策帮你做选择</p>
      </motion.div>

      {/* Module Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-4">
        {MODULES.map((mod, index) => (
          <motion.div
            key={mod.slug}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <Link to={`/${mod.slug}`}>
              <Card className="p-6 text-center hover:shadow-lg transition-shadow" hoverable>
                <div className={`inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-gradient-to-br ${mod.color} text-white mb-3`}>
                  <mod.icon size={28} />
                </div>
                <h3 className="font-semibold text-gray-900">{mod.name}</h3>
              </Card>
            </Link>
          </motion.div>
        ))}
      </div>

      {/* Daily Recommendations */}
      {recommendation && Object.keys(recommendation.recommendationsByCategory).length > 0 && (
        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
          <div className="flex items-center gap-2 mb-4">
            <Sparkles size={20} className="text-yellow-500" />
            <h2 className="text-xl font-semibold text-gray-900">{recommendation.timePeriod}推荐</h2>
          </div>
          <p className="text-sm text-gray-500 mb-4">{recommendation.message}</p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {Object.entries(recommendation.recommendationsByCategory).map(([category, options]) => (
              <Card key={category} className="p-4">
                <h3 className="font-medium text-gray-700 mb-2">{category}</h3>
                <ul className="space-y-1">
                  {options.slice(0, 3).map(opt => (
                    <li key={opt.id} className="text-sm text-gray-600 flex items-center gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-indigo-400" />
                      {opt.name}
                    </li>
                  ))}
                </ul>
              </Card>
            ))}
          </div>
        </motion.div>
      )}
    </div>
  );
}
