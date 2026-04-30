import { useState } from 'react';
import { aiApi, AiRecommendationVO } from '../../api/aiApi';
import { CATEGORY_SLUG_TO_ID } from '../../utils/constants';
import Card from '../ui/Card';
import Button from '../ui/Button';
import { Sparkles, Brain, Lightbulb, Plus } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

interface AiPanelProps {
  categorySlug: string;
  onAddSuggestion?: (name: string) => void;
}

export default function AiPanel({ categorySlug, onAddSuggestion }: AiPanelProps) {
  const categoryId = CATEGORY_SLUG_TO_ID[categorySlug];
  const [recommendation, setRecommendation] = useState<AiRecommendationVO | null>(null);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [analysis, setAnalysis] = useState<string>('');
  const [loading, setLoading] = useState<'recommend' | 'suggest' | 'analyze' | null>(null);

  const handleRecommend = async () => {
    setLoading('recommend');
    try {
      const res = await aiApi.getRecommendation(categoryId);
      setRecommendation(res.data.data);
    } catch {
      setRecommendation({ category: '', recommendation: 'AI服务暂时不可用' });
    } finally {
      setLoading(null);
    }
  };

  const handleSuggest = async () => {
    setLoading('suggest');
    try {
      const res = await aiApi.suggestOptions(categoryId);
      setSuggestions(res.data.data || []);
    } catch {
      setSuggestions([]);
    } finally {
      setLoading(null);
    }
  };

  const handleAnalyze = async () => {
    setLoading('analyze');
    try {
      const res = await aiApi.analyzePreferences(categoryId);
      setAnalysis(res.data.data || '暂无分析数据');
    } catch {
      setAnalysis('分析服务暂时不可用');
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="space-y-4">
      {/* AI Action Buttons */}
      <div className="flex flex-wrap gap-2">
        <Button variant="secondary" size="sm" onClick={handleRecommend} loading={loading === 'recommend'}>
          <Sparkles size={14} className="mr-1" /> AI 智能推荐
        </Button>
        <Button variant="secondary" size="sm" onClick={handleSuggest} loading={loading === 'suggest'}>
          <Lightbulb size={14} className="mr-1" /> AI 建议新选项
        </Button>
        <Button variant="secondary" size="sm" onClick={handleAnalyze} loading={loading === 'analyze'}>
          <Brain size={14} className="mr-1" /> AI 偏好分析
        </Button>
      </div>

      {/* AI Recommendation */}
      <AnimatePresence>
        {recommendation && (
          <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <Card className="p-4 bg-gradient-to-r from-amber-50 to-orange-50 border-amber-200">
              <div className="flex items-start gap-2">
                <Sparkles size={18} className="text-amber-500 mt-0.5 shrink-0" />
                <div>
                  <h4 className="font-medium text-amber-800 mb-1">AI 推荐</h4>
                  <p className="text-sm text-amber-700">{recommendation.recommendation}</p>
                  {recommendation.explanation && (
                    <p className="text-xs text-amber-600 mt-2 italic">{recommendation.explanation}</p>
                  )}
                </div>
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>

      {/* AI Suggestions */}
      <AnimatePresence>
        {suggestions.length > 0 && (
          <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <Card className="p-4 bg-gradient-to-r from-green-50 to-emerald-50 border-green-200">
              <div className="flex items-start gap-2">
                <Lightbulb size={18} className="text-green-500 mt-0.5 shrink-0" />
                <div className="flex-1">
                  <h4 className="font-medium text-green-800 mb-2">AI 建议的新选项</h4>
                  <div className="space-y-1.5">
                    {suggestions.map((s, i) => (
                      <div key={i} className="flex items-center justify-between bg-white rounded-lg px-3 py-1.5">
                        <span className="text-sm text-green-700">{s}</span>
                        {onAddSuggestion && (
                          <button
                            onClick={() => onAddSuggestion(s)}
                            className="text-green-500 hover:text-green-700 p-0.5"
                            title="添加为选项"
                          >
                            <Plus size={14} />
                          </button>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>

      {/* AI Analysis */}
      <AnimatePresence>
        {analysis && (
          <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}>
            <Card className="p-4 bg-gradient-to-r from-purple-50 to-indigo-50 border-purple-200">
              <div className="flex items-start gap-2">
                <Brain size={18} className="text-purple-500 mt-0.5 shrink-0" />
                <div>
                  <h4 className="font-medium text-purple-800 mb-1">AI 偏好分析</h4>
                  <p className="text-sm text-purple-700">{analysis}</p>
                </div>
              </div>
            </Card>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
