import { OptionVO } from '../../api/optionApi';
import Card from '../ui/Card';
import Tag from '../ui/Tag';
import Button from '../ui/Button';
import EmptyState from '../ui/EmptyState';
import { Edit2, Trash2, Plus } from 'lucide-react';
import { motion } from 'framer-motion';

interface OptionListProps {
  options: OptionVO[];
  onEdit: (option: OptionVO) => void;
  onDelete: (id: number) => void;
  onAdd: () => void;
}

export default function OptionList({ options, onEdit, onDelete, onAdd }: OptionListProps) {
  if (options.length === 0) {
    return (
      <EmptyState
        title="还没有选项"
        description="添加一些选项，让随机决策帮你做选择！"
        action={
          <Button onClick={onAdd} size="sm">
            <Plus size={16} className="mr-1" /> 添加选项
          </Button>
        }
      />
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
      {options.map((option, index) => (
        <motion.div
          key={option.id}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: index * 0.05 }}
        >
          <Card className="p-4" hoverable>
            <div className="flex items-start justify-between mb-2">
              <h3 className="font-medium text-gray-900 truncate flex-1">{option.name}</h3>
              <div className="flex gap-1 ml-2">
                <button onClick={() => onEdit(option)} className="p-1 text-gray-400 hover:text-indigo-600">
                  <Edit2 size={14} />
                </button>
                <button onClick={() => onDelete(option.id)} className="p-1 text-gray-400 hover:text-red-500">
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
            {option.description && (
              <p className="text-sm text-gray-500 mb-2 line-clamp-2">{option.description}</p>
            )}
            {option.tags.length > 0 && (
              <div className="flex flex-wrap gap-1">
                {option.tags.map((tag) => (
                  <Tag key={tag} label={tag} />
                ))}
              </div>
            )}
          </Card>
        </motion.div>
      ))}
    </div>
  );
}
