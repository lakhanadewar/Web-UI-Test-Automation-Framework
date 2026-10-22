import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { useStore } from "@/lib/store";
import { EMAIL_RE } from "@/lib/rules";
import { Field, btnCls, inputCls } from "@/components/RequireAuth";

export const Route = createFileRoute("/login")({
  validateSearch: (s: Record<string, unknown>): { redirect?: string | undefined } => ({
    redirect: typeof s["redirect"] === "string" && s["redirect"].startsWith("/") ? s["redirect"] : undefined,
  }),
  head: () => ({
    meta: [
      { title: "Login — TestKart" },
      { name: "description", content: "Log in to your TestKart account." },
      { property: "og:title", content: "Login — TestKart" },
      { property: "og:description", content: "Log in to your TestKart account." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: Login,
});

function Login() {
  const { login } = useStore();
  const { redirect } = Route.useSearch();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [remember, setRemember] = useState(false);
  const [errs, setErrs] = useState<Partial<Record<"email" | "password", string>>>({});
  const [error, setError] = useState<string | null>(null);

  return (
    <form
      noValidate
      data-testid="login-form"
      className="mx-auto max-w-sm space-y-4"
      onSubmit={(e) => {
        e.preventDefault();
        const er: Partial<Record<"email" | "password", string>> = {};
        if (!email.trim()) er.email = "Email is required";
        else if (!EMAIL_RE.test(email.trim())) er.email = "Enter a valid email";
        if (!password) er.password = "Password is required";
        setErrs(er);
        setError(null);
        if (Object.keys(er).length) return;
        const err = login(email, password, remember);
        if (err) return setError(err);
        navigate({ to: redirect ?? "/" });
      }}
    >
      <h1 data-testid="login-title" className="text-2xl font-bold">Login</h1>
      {error && <p data-testid="login-error" role="alert" className="rounded-md bg-destructive/10 p-2 text-sm text-destructive">{error}</p>}
      <Field id="login-email" label="Email" error={errs.email}>
        <input id="login-email" data-testid="login-email" type="email" className={inputCls} value={email} onChange={(e) => setEmail(e.target.value)} />
      </Field>
      <Field id="login-password" label="Password" error={errs.password}>
        <input id="login-password" data-testid="login-password" type="password" className={inputCls} value={password} onChange={(e) => setPassword(e.target.value)} />
      </Field>
      <div className="flex items-center gap-2">
        <input id="login-remember" data-testid="login-remember" type="checkbox" checked={remember} onChange={(e) => setRemember(e.target.checked)} />
        <label htmlFor="login-remember" className="text-sm">Remember me (7 days)</label>
      </div>
      <button data-testid="login-submit" type="submit" className={`${btnCls} w-full`}>Login</button>
      <p className="text-sm">No account? <Link to="/signup" data-testid="login-signup-link" className="text-primary underline">Sign up</Link></p>
    </form>
  );
}
