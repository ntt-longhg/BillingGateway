import * as React from 'react';
import { cn } from '@/lib/utils';

const TooltipProvider = ({ children }: { children: React.ReactNode }) => <>{children}</>;
const Tooltip = ({ children }: { children: React.ReactNode }) => <>{children}</>;
const TooltipTrigger = React.forwardRef<HTMLButtonElement, React.ButtonHTMLAttributes<HTMLButtonElement>>(
  ({ ...props }, ref) => <button ref={ref} {...props} />
);
TooltipTrigger.displayName = 'TooltipTrigger';

const TooltipContent = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn(
        'z-50 overflow-hidden rounded-md bg-slate-900 px-3 py-1.5 text-xs text-slate-500',
        className
      )}
      {...props}
    />
  )
);
TooltipContent.displayName = 'TooltipContent';

export { Tooltip, TooltipTrigger, TooltipContent, TooltipProvider };
