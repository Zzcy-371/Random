import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { OptionVO } from '../../api/optionApi';

interface SlotMachineProps {
  options: OptionVO[];
  result: OptionVO | null;
  isSpinning: boolean;
  onComplete: () => void;
}

export default function SlotMachine({ options, result, isSpinning, onComplete }: SlotMachineProps) {
  const [displayOption, setDisplayOption] = useState<OptionVO | null>(null);
  const [phase, setPhase] = useState<'idle' | 'spinning' | 'slowing' | 'done'>('idle');
  const intervalRef = useRef<number | null>(null);
  const indexRef = useRef(0);

  useEffect(() => {
    if (!isSpinning) {
      setPhase('idle');
      return;
    }

    setPhase('spinning');
    let interval = 50;
    const startTime = Date.now();

    const tick = () => {
      indexRef.current = (indexRef.current + 1) % options.length;
      setDisplayOption(options[indexRef.current]);
    };

    intervalRef.current = window.setInterval(tick, interval);

    // Phase transitions
    const slowTimer = setTimeout(() => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      setPhase('slowing');
      interval = 200;
      intervalRef.current = window.setInterval(tick, interval);
    }, 1500);

    const stopTimer = setTimeout(() => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      setPhase('done');
      setDisplayOption(result);
      onComplete();
    }, 2500);

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
      clearTimeout(slowTimer);
      clearTimeout(stopTimer);
    };
  }, [isSpinning, options, result, onComplete]);

  if (!isSpinning && phase === 'idle') {
    return null;
  }

  return (
    <div className="flex flex-col items-center py-8">
      <div className="relative w-64 h-24 overflow-hidden rounded-xl bg-gradient-to-r from-indigo-500 to-purple-600 shadow-lg">
        <div className="absolute inset-0 flex items-center justify-center">
          <AnimatePresence mode="wait">
            {displayOption && (
              <motion.div
                key={displayOption.id}
                initial={{ y: 30, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                exit={{ y: -30, opacity: 0 }}
                transition={{ duration: phase === 'slowing' ? 0.3 : 0.1 }}
                className="text-white text-xl font-bold text-center px-4 truncate w-full"
              >
                {displayOption.name}
              </motion.div>
            )}
          </AnimatePresence>
        </div>
        {/* Decorative lines */}
        <div className="absolute top-0 left-0 right-0 h-1 bg-yellow-400" />
        <div className="absolute bottom-0 left-0 right-0 h-1 bg-yellow-400" />
      </div>

      {phase === 'done' && result && (
        <motion.div
          initial={{ scale: 0, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ type: 'spring', stiffness: 200, damping: 15 }}
          className="mt-6 text-center"
        >
          <p className="text-sm text-gray-500 mb-1">随机选择结果</p>
          <h3 className="text-2xl font-bold text-indigo-600">{result.name}</h3>
          {result.description && (
            <p className="text-gray-500 mt-1">{result.description}</p>
          )}
        </motion.div>
      )}
    </div>
  );
}
