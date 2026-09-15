import * as React from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { X, CheckCircle2, AlertCircle, AlertTriangle, Info } from 'lucide-react';
import { cn } from '@/lib/utils';

const toastVariants = cva(
  'pointer-events-auto relative flex w-full items-center justify-between gap-3 rounded-lg border px-4 py-3 text-sm shadow-lg transition-all',
  {
    variants: {
      variant: {
        default: 'bg-white text-slate-900 border-slate-200',
        success: 'bg-emerald-50 text-emerald-800 border-emerald-200',
        destructive: 'bg-red-50 text-red-800 border-red-200',
        warning: 'bg-amber-50 text-amber-800 border-amber-200',
        info: 'bg-blue-50 text-blue-800 border-blue-200',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
);

interface Toast {
  id: string;
  variant: 'default' | 'success' | 'destructive' | 'warning' | 'info';
  title?: string;
  message: string;
  duration?: number;
}

interface ToastContextType {
  toasts: Toast[];
  addToast: (toast: Omit<Toast, 'id'>) => void;
  removeToast: (id: string) => void;
}

const ToastContext = React.createContext<ToastContextType>({
  toasts: [],
  addToast: () => {},
  removeToast: () => {},
});

export const useToast = () => {
  const context = React.useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = React.useState<Toast[]>([]);

  const addToast = React.useCallback((toast: Omit<Toast, 'id'>) => {
    const id = Math.random().toString(36).substring(2, 9);
    const duration = toast.duration ?? 4000;
    setToasts((prev) => [...prev, { ...toast, id, duration }]);

    if (duration > 0) {
      setTimeout(() => {
        setToasts((prev) => prev.filter((t) => t.id !== id));
      }, duration);
    }
  }, []);

  const removeToast = React.useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast }}>
      {children}
      <ToastContainer toasts={toasts} removeToast={removeToast} />
    </ToastContext.Provider>
  );
};

const ToastContainer: React.FC<{ toasts: Toast[]; removeToast: (id: string) => void }> = ({
  toasts,
  removeToast,
}) => {
  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-[100] flex flex-col gap-2 max-w-sm w-full pointer-events-none">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} onClose={() => removeToast(toast.id)} />
      ))}
    </div>
  );
};

const ToastItem: React.FC<{ toast: Toast; onClose: () => void }> = ({ toast, onClose }) => {
  const icons = {
    default: null,
    success: <CheckCircle2 className="h-4 w-4 text-emerald-500 shrink-0" />,
    destructive: <AlertCircle className="h-4 w-4 text-red-500 shrink-0" />,
    warning: <AlertTriangle className="h-4 w-4 text-amber-500 shrink-0" />,
    info: <Info className="h-4 w-4 text-blue-500 shrink-0" />,
  };

  return (
    <div
      className={cn(
        toastVariants({ variant: toast.variant }),
        'pointer-events-auto animate-in slide-in-from-bottom-5 fade-in duration-300'
      )}
    >
      <div className="flex items-center gap-3 flex-1 min-w-0">
        {icons[toast.variant]}
        <div className="flex-1 min-w-0">
          {toast.title && <p className="font-medium text-sm">{toast.title}</p>}
          <p className={cn('text-sm', toast.title ? 'text-slate-600 mt-0.5' : '')}>{toast.message}</p>
        </div>
      </div>
      <button
        onClick={onClose}
        className="shrink-0 p-1 rounded-md hover:bg-black/5 transition-colors"
      >
        <X className="h-3.5 w-3.5 text-slate-400" />
      </button>
    </div>
  );
};
