interface TagProps {
  label: string;
  selected?: boolean;
  onClick?: () => void;
  removable?: boolean;
  onRemove?: () => void;
}

export default function Tag({ label, selected, onClick, removable, onRemove }: TagProps) {
  return (
    <span
      className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium transition-colors ${
        selected
          ? 'bg-indigo-100 text-indigo-700 border border-indigo-300'
          : 'bg-gray-100 text-gray-600 border border-gray-200'
      } ${onClick ? 'cursor-pointer hover:bg-indigo-50' : ''}`}
      onClick={onClick}
    >
      {label}
      {removable && (
        <button onClick={(e) => { e.stopPropagation(); onRemove?.(); }} className="ml-0.5 hover:text-red-500">
          ×
        </button>
      )}
    </span>
  );
}
