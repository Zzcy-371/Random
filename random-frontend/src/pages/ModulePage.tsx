import { useState, useEffect, useCallback } from 'react';
import { optionApi, OptionVO, OptionCreateRequest } from '../api/optionApi';
import { decisionApi, DecisionVO } from '../api/decisionApi';
import { CATEGORY_SLUG_TO_ID, CATEGORY_DISPLAY_NAMES } from '../utils/constants';
import OptionList from '../components/decision/OptionList';
import OptionForm from '../components/decision/OptionForm';
import DecideButton from '../components/decision/DecideButton';
import DecisionResult from '../components/decision/DecisionResult';
import FeedbackPanel from '../components/decision/FeedbackPanel';
import SlotMachine from '../components/decision/SlotMachine';
import AiPanel from '../components/decision/AiPanel';
import Button from '../components/ui/Button';
import { Plus } from 'lucide-react';
import { motion } from 'framer-motion';

interface ModulePageProps {
  categorySlug: string;
}

export default function ModulePage({ categorySlug }: ModulePageProps) {
  const categoryId = CATEGORY_SLUG_TO_ID[categorySlug];
  const displayName = CATEGORY_DISPLAY_NAMES[categorySlug];

  const [options, setOptions] = useState<OptionVO[]>([]);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editOption, setEditOption] = useState<OptionVO | null>(null);
  const [decision, setDecision] = useState<DecisionVO | null>(null);
  const [isDeciding, setIsDeciding] = useState(false);
  const [showSlot, setShowSlot] = useState(false);
  const [showFeedback, setShowFeedback] = useState(false);
  const [hint, setHint] = useState('');

  const loadOptions = useCallback(async () => {
    try {
      const res = await optionApi.list(categoryId);
      const opts: OptionVO[] = res.data.data;
      setOptions(opts);
      setSelectedIds(prev => {
        const ids = new Set(opts.map(o => o.id));
        return new Set([...prev].filter(id => ids.has(id)));
      });
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, [categoryId]);

  useEffect(() => {
    loadOptions();
  }, [loadOptions]);

  const handleToggleSelect = (id: number) => {
    setSelectedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const handleSelectAll = () => setSelectedIds(new Set(options.map(o => o.id)));
  const handleDeselectAll = () => setSelectedIds(new Set());

  const handleAddSuggestion = async (name: string) => {
    await optionApi.create(categoryId, { name });
    loadOptions();
  };

  const handleAdd = async (data: OptionCreateRequest) => {
    await optionApi.create(categoryId, data);
    loadOptions();
  };

  const handleUpdate = async (data: OptionCreateRequest) => {
    if (!editOption) return;
    await optionApi.update(editOption.id, data);
    setEditOption(null);
    loadOptions();
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定删除该选项？')) return;
    await optionApi.delete(id);
    loadOptions();
  };

  const handleDecide = async () => {
    if (selectedIds.size < 2) {
      setHint('请至少选择2个选项进行随机决策');
      return;
    }
    setHint('');
    setIsDeciding(true);
    setShowSlot(true);
    setDecision(null);
    setShowFeedback(false);

    try {
      const res = await decisionApi.decide(categoryId, { optionIds: Array.from(selectedIds) });
      setDecision(res.data.data);
    } catch {
      setIsDeciding(false);
      setShowSlot(false);
    }
  };

  const handleSlotComplete = useCallback(() => {
    setIsDeciding(false);
    setShowFeedback(true);
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600" />
      </div>
    );
  }

  const selectedOptions = options.filter(o => selectedIds.has(o.id));

  return (
    <div className="max-w-4xl mx-auto">
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">{displayName}</h1>
          <Button onClick={() => { setEditOption(null); setShowForm(true); }}>
            <Plus size={16} className="mr-1" /> 添加选项
          </Button>
        </div>

        {/* AI Panel */}
        <AiPanel categorySlug={categorySlug} optionCount={options.length} onAddSuggestion={handleAddSuggestion} />

        {/* Options */}
        <OptionList
          options={options}
          onEdit={(opt) => { setEditOption(opt); setShowForm(true); }}
          onDelete={handleDelete}
          onAdd={() => { setEditOption(null); setShowForm(true); }}
          selectable
          selectedIds={selectedIds}
          onToggleSelect={handleToggleSelect}
          onSelectAll={handleSelectAll}
          onDeselectAll={handleDeselectAll}
        />

        {/* Hint */}
        {hint && (
          <p className="text-center text-sm text-amber-600 mt-3">{hint}</p>
        )}

        {/* Decide Button */}
        {options.length >= 2 && (
          <DecideButton onClick={handleDecide} loading={isDeciding} />
        )}

        {/* Slot Machine */}
        {showSlot && decision && (
          <SlotMachine
            options={selectedOptions}
            result={decision.chosenOption}
            isSpinning={isDeciding}
            onComplete={handleSlotComplete}
          />
        )}

        {/* Decision Result */}
        {decision && !isDeciding && (
          <DecisionResult decision={decision} />
        )}

        {/* Feedback */}
        {showFeedback && decision && !isDeciding && (
          <FeedbackPanel decisionId={decision.decisionId} />
        )}
      </motion.div>

      {/* Option Form Modal */}
      <OptionForm
        isOpen={showForm}
        onClose={() => { setShowForm(false); setEditOption(null); }}
        onSubmit={editOption ? handleUpdate : handleAdd}
        categorySlug={categorySlug}
        editOption={editOption}
      />
    </div>
  );
}
