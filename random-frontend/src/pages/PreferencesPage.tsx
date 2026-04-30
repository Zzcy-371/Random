import { useEffect, useState } from 'react';
import { recommendationApi, PreferenceVO } from '../api/recommendationApi';
import { CATEGORY_SLUG_TO_ID, CATEGORY_DISPLAY_NAMES } from '../utils/constants';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import { motion } from 'framer-motion';

export default function PreferencesPage() {
  const [activeTab, setActiveTab] = useState('eating');
  const [preferences, setPreferences] = useState<PreferenceVO[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const tabs = Object.entries(CATEGORY_DISPLAY_NAMES);

  useEffect(() => {
    const catId = CATEGORY_SLUG_TO_ID[activeTab];
    setLoading(true);
    recommendationApi.getPreferences(catId).then(res => {
      setPreferences(res.data.data);
    }).catch(() => {
      setPreferences([]);
    }).finally(() => setLoading(false));
  }, [activeTab]);

  const handleWeightChange = (tag: string, value: number) => {
    setPreferences(prev =>
      prev.map(p => p.tag === tag ? { ...p, weight: value } : p)
    );
  };

  const handleSave = async () => {
    const catId = CATEGORY_SLUG_TO_ID[activeTab];
    setSaving(true);
    try {
      const res = await recommendationApi.updatePreferences(catId, preferences);
      setPreferences(res.data.data);
    } catch {
      // ignore
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">偏好设置</h1>

      {/* Tabs */}
      <div className="flex gap-2 mb-6 overflow-x-auto pb-2">
        {tabs.map(([slug, name]) => (
          <button
            key={slug}
            onClick={() => setActiveTab(slug)}
            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-colors ${
              activeTab === slug
                ? 'bg-indigo-600 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {name}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-20">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600" />
        </div>
      ) : preferences.length === 0 ? (
        <Card className="p-8 text-center text-gray-500">
          <p>暂无偏好数据</p>
          <p className="text-sm text-gray-400 mt-1">使用随机决策并提交反馈后，系统会自动学习你的偏好</p>
        </Card>
      ) : (
        <div className="space-y-4">
          {preferences.map((pref, index) => (
            <motion.div
              key={pref.tag}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <Card className="p-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="font-medium text-gray-700">{pref.tag}</span>
                  <span className="text-sm text-indigo-600 font-mono">
                    {(pref.weight * 100).toFixed(0)}%
                  </span>
                </div>
                <input
                  type="range"
                  min="5"
                  max="95"
                  value={Math.round(pref.weight * 100)}
                  onChange={e => handleWeightChange(pref.tag, parseInt(e.target.value) / 100)}
                  className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                />
                <div className="flex justify-between text-xs text-gray-400 mt-1">
                  <span>不喜欢</span>
                  <span>喜欢</span>
                </div>
              </Card>
            </motion.div>
          ))}

          <Button onClick={handleSave} loading={saving} className="w-full mt-4">
            保存偏好
          </Button>
        </div>
      )}
    </div>
  );
}
