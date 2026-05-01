import { OptionVO } from '../../api/optionApi';
import Card from '../ui/Card';
import Tag from '../ui/Tag';
import Button from '../ui/Button';
import EmptyState from '../ui/EmptyState';
import { Edit2, Trash2, Plus, CheckSquare, Square } from 'lucide-react';
import { motion } from 'framer-motion';

interface OptionListProps {
  options: OptionVO[];
  onEdit: (option: OptionVO) => void;
  onDelete: (id: number) => void;
  onAdd: () => void;
  selectable?: boolean;
  selectedIds?: Set<number>;
  onToggleSelect?: (id: number) => void;
  onSelectAll?: () => void;
  onDeselectAll?: () => void;
}

export default function OptionList({
  options, onEdit, onDelete, onAdd,
  selectable = false, selectedIds, onToggleSelect, onSelectAll, onDeselectAll,
}: OptionListProps) {
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

  const allSelected = selectedIds && options.every(o => selectedIds.has(o.id));

  return (
    <div>
      {selectable && (
        <div className="flex items-center gap-2 mb-3">
          <button
            onClick={allSelected ? onDeselectAll : onSelectAll}
            className="flex items-center gap-1 text-sm text-indigo-600 hover:text-indigo-800"
          >
            {allSelected ? <CheckSquare size={16} /> : <Square size={16} />}
            {allSelected ? '全不选' : '全选'}
          </button>
          {selectedIds && (
            <span className="text-xs text-gray-400">
              已选 {selectedIds.size}/{options.length}
            </span>
          )}
        </div>
      )}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
        {options.map((option, index) => {
          const isSelected = selectedIds?.has(option.id) ?? true;
          return (
            <motion.div
              key={option.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
            >
              <Card
                className={`p-4 transition-all ${selectable && isSelected ? 'ring-2 ring-indigo-500 bg-indigo-50/50' : ''}`}
                hoverable
              >
                <div className="flex items-start justify-between mb-2">
                  <div className="flex items-center gap-2 flex-1 min-w-0">
                    {selectable && (
                      <button
                        onClick={() => onToggleSelect?.(option.id)}
                        className={`shrink-0 ${isSelected ? 'text-indigo-600' : 'text-gray-300'}`}
                      >
                        {isSelected ? <CheckSquare size={18} /> : <Square size={18} />}
                      </button>
                    )}
                    <h3 className="font-medium text-gray-900 truncate">{option.name}</h3>
                  </div>
                  <div className="flex gap-1 ml-2 shrink-0">
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
          );
        })}
      </div>
    </div>
  );
}
