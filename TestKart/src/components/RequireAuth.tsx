import { useNavigate, useLocation } from "@tanstack/react-router";
import { useEffect, useRef, type ReactNode } from "react";
import { useStore } from "@/lib/store";

export function RequireAuth({ children }: { children: ReactNode }) {
  const { ready, user } = useStore();
  const navigate = useNavigate();
  const loc = useLocation();
  const from = useRef(loc.href);
  useEffect(() => {
    if (ready && !user) navigate({ to: "/login", search: { redirect: from.current }, replace: true });
  }, [ready, user, navigate]);
  if (!ready || !user) return <p data-testid="auth-loading" className="p-8 text-muted-foreground">Loading…</p>;
  return <>{children}</>;
}

export function Field({
  id,
  label,
  error,
  children,
}: {
  id: string;
  label: string;
  error?: string | undefined;
  children: ReactNode;
}) {
  return (
    <div className="space-y-1">
      <label htmlFor={id} className="block text-sm font-medium">
        {label}
      </label>
      {children}
      {error && (
        <p data-testid={`${id}-error`} className="text-sm text-destructive" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}

export const inputCls =
  "w-full rounded-md border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring";
export const btnCls =
  "inline-flex items-center justify-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed";
export const btnOutlineCls =
  "inline-flex items-center justify-center rounded-md border border-input bg-background px-4 py-2 text-sm font-medium hover:bg-accent disabled:opacity-50";
