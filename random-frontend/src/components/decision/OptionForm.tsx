import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { OptionCreateRequest, OptionVO } from '../../api/optionApi';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import Tag from '../ui/Tag';
import { MODULE_TAGS } from '../../utils/constants';
import { useState } from 'react';

const schema = z.object({
  name: z.string().min(1, '名称不能为空').max(200),
  description: z.string().max(500).optional(),
  tags: z.string().optional(),
});

type FormData = z.infer<typeof schema>;

interface OptionFormProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: OptionCreateRequest) => Promise<void>;
  categorySlug: string;
  editOption?: OptionVO | null;
}

export default function OptionForm({ isOpen, onClose, onSubmit, categorySlug, editOption }: OptionFormProps) {
  const [selectedTags, setSelectedTags] = useState<string[]>(
    editOption?.tags || []
  );
  const defaultTags = MODULE_TAGS[categorySlug] || [];

  const { register, handleSubmit, formState: { errors, isSubmitting }, reset } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: editOption?.name || '',
      description: editOption?.description || '',
      tags: editOption?.tags?.join(', ') || '',
    },
  });

  const handleFormSubmit = async (data: FormData) => {
    await onSubmit({
      name: data.name,
      description: data.description,
      tags: [...new Set([...selectedTags, ...(data.tags?.split(',').map(t => t.trim()).filter(Boolean) || [])])].join(', '),
    });
    reset();
    setSelectedTags([]);
    onClose();
  };

  const toggleTag = (tag: string) => {
    setSelectedTags(prev =>
      prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]
    );
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={editOption ? '编辑选项' : '添加选项'}>
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">名称 *</label>
          <input
            {...register('name')}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            placeholder="输入选项名称"
          />
          {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">描述</label>
          <textarea
            {...register('description')}
            rows={3}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            placeholder="输入选项描述（可选）"
          />
        </div>

        {defaultTags.length > 0 && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">快速标签</label>
            <div className="flex flex-wrap gap-1.5">
              {defaultTags.map((tag) => (
                <Tag
                  key={tag}
                  label={tag}
                  selected={selectedTags.includes(tag)}
                  onClick={() => toggleTag(tag)}
                />
              ))}
            </div>
          </div>
        )}

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">自定义标签</label>
          <input
            {...register('tags')}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent"
            placeholder="用逗号分隔，如：健康, 低卡"
          />
        </div>

        <div className="flex gap-2 pt-2">
          <Button type="button" variant="secondary" onClick={onClose} className="flex-1">取消</Button>
          <Button type="submit" loading={isSubmitting} className="flex-1">
            {editOption ? '保存' : '添加'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
