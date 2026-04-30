import { DecisionVO } from '../../api/decisionApi';
import { OptionVO } from '../../api/optionApi';
import Card from '../ui/Card';
import Tag from '../ui/Tag';
import { motion } from 'framer-motion';

interface DecisionResultProps {
  decision: DecisionVO;
}

export default function DecisionResult({ decision }: DecisionResultProps) {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      className="mt-6"
    >
      <Card className="p-6 bg-gradient-to-br from-indigo-50 to-purple-50 border-indigo-200">
        <div className="text-center mb-4">
          <p className="text-sm text-indigo-500 mb-1">随机选择结果</p>
          <h2 className="text-2xl font-bold text-indigo-700">{decision.chosenOption.name}</h2>
          {decision.chosenOption.description && (
            <p className="text-gray-600 mt-1">{decision.chosenOption.description}</p>
          )}
          {decision.chosenOption.tags.length > 0 && (
            <div className="flex flex-wrap justify-center gap-1.5 mt-3">
              {decision.chosenOption.tags.map((tag) => (
                <Tag key={tag} label={tag} selected />
              ))}
            </div>
          )}
        </div>

        {decision.alternates.length > 0 && (
          <div className="border-t border-indigo-100 pt-4 mt-4">
            <p className="text-xs text-gray-500 text-center mb-2">备选方案</p>
            <div className="flex justify-center gap-3">
              {decision.alternates.map((alt) => (
                <span key={alt.id} className="text-sm text-gray-600 bg-white px-3 py-1 rounded-full">
                  {alt.name}
                </span>
              ))}
            </div>
          </div>
        )}
      </Card>
    </motion.div>
  );
}
