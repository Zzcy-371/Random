import { useEffect, useState } from 'react';
import { decisionApi, DecisionVO } from '../api/decisionApi';
import Card from '../components/ui/Card';
import Tag from '../components/ui/Tag';
import EmptyState from '../components/ui/EmptyState';
import Button from '../components/ui/Button';
import { Clock, ChevronDown } from 'lucide-react';
import { motion } from 'framer-motion';

export default function HistoryPage() {
  const [decisions, setDecisions] = useState<DecisionVO[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(true);

  const loadHistory = async (p: number) => {
    try {
      const res = await decisionApi.getHistory(p, 20);
      const data = res.data.data;
      setDecisions(prev => p === 0 ? data.content : [...prev, ...data.content]);
      setHasMore(!data.last);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory(0);
  }, []);

  const loadMore = () => {
    const nextPage = page + 1;
    setPage(nextPage);
    loadHistory(nextPage);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600" />
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">决策历史</h1>

      {decisions.length === 0 ? (
        <EmptyState
          icon={<Clock size={48} />}
          title="暂无决策记录"
          description="去各个模块做一次随机决策吧！"
        />
      ) : (
        <div className="space-y-3">
          {decisions.map((decision, index) => (
            <motion.div
              key={decision.decisionId}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.03 }}
            >
              <Card className="p-4">
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-gray-900">{decision.chosenOption.name}</h3>
                    <p className="text-xs text-gray-400 mt-1">
                      {decision.chosenOption.categoryName} · {new Date(decision.decidedAt).toLocaleString('zh-CN')}
                    </p>
                  </div>
                  {decision.chosenOption.tags.length > 0 && (
                    <div className="flex gap-1">
                      {decision.chosenOption.tags.slice(0, 3).map(tag => (
                        <Tag key={tag} label={tag} />
                      ))}
                    </div>
                  )}
                </div>
              </Card>
            </motion.div>
          ))}

          {hasMore && (
            <div className="text-center pt-4">
              <Button variant="ghost" onClick={loadMore}>
                <ChevronDown size={16} className="mr-1" /> 加载更多
              </Button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
