import { useState } from 'react';
import { feedbackApi } from '../../api/feedbackApi';
import Button from '../ui/Button';
import { ThumbsUp, ThumbsDown, Minus } from 'lucide-react';
import { motion } from 'framer-motion';

interface FeedbackPanelProps {
  decisionId: number;
  onSubmitted?: () => void;
}

export default function FeedbackPanel({ decisionId, onSubmitted }: FeedbackPanelProps) {
  const [rating, setRating] = useState<number | null>(null);
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (rating === null) return;
    setLoading(true);
    try {
      await feedbackApi.submit(decisionId, { rating });
      setSubmitted(true);
      onSubmitted?.();
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  };

  if (submitted) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center py-4 text-green-600 font-medium"
      >
        感谢你的反馈！
      </motion.div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-gray-50 rounded-xl p-4 mt-4"
    >
      <p className="text-sm text-gray-600 text-center mb-3">对这个结果满意吗？</p>
      <div className="flex justify-center gap-4 mb-3">
        <button
          onClick={() => setRating(1)}
          className={`p-3 rounded-full transition-colors ${
            rating === 1 ? 'bg-red-100 text-red-600' : 'bg-gray-200 text-gray-500 hover:bg-red-50'
          }`}
        >
          <ThumbsDown size={20} />
        </button>
        <button
          onClick={() => setRating(2)}
          className={`p-3 rounded-full transition-colors ${
            rating === 2 ? 'bg-yellow-100 text-yellow-600' : 'bg-gray-200 text-gray-500 hover:bg-yellow-50'
          }`}
        >
          <Minus size={20} />
        </button>
        <button
          onClick={() => setRating(3)}
          className={`p-3 rounded-full transition-colors ${
            rating === 3 ? 'bg-green-100 text-green-600' : 'bg-gray-200 text-gray-500 hover:bg-green-50'
          }`}
        >
          <ThumbsUp size={20} />
        </button>
      </div>
      <Button
        onClick={handleSubmit}
        disabled={rating === null}
        loading={loading}
        size="sm"
        className="w-full"
      >
        提交反馈
      </Button>
    </motion.div>
  );
}
