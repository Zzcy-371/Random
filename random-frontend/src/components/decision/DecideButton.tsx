import Button from '../ui/Button';
import { Shuffle } from 'lucide-react';

interface DecideButtonProps {
  onClick: () => void;
  disabled?: boolean;
  loading?: boolean;
}

export default function DecideButton({ onClick, disabled, loading }: DecideButtonProps) {
  return (
    <div className="flex justify-center py-6">
      <Button
        onClick={onClick}
        disabled={disabled}
        loading={loading}
        size="lg"
        className="px-8 py-4 text-lg rounded-2xl shadow-lg hover:shadow-xl transition-shadow"
      >
        <Shuffle size={20} className="mr-2" />
        随机决定！
      </Button>
    </div>
  );
}
