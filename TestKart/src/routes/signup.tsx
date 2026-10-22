import { createFileRoute, Link } from "@tanstack/react-router";
import { useState, type ChangeEvent } from "react";
import { useStore } from "@/lib/store";
import { EMAIL_RE, validatePassword } from "@/lib/rules";
import { Field, btnCls, inputCls } from "@/components/RequireAuth";

export const Route = createFileRoute("/signup")({
  head: () => ({
    meta: [
      { title: "Sign up — TestKart" },
      { name: "description", content: "Create a TestKart account." },
      { property: "og:title", content: "Sign up — TestKart" },
      { property: "og:description", content: "Create a TestKart account." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: Signup,
});

function Signup() {
  const { signup } = useStore();
  const [f, setF] = useState({ name: "", email: "", password: "", confirm: "" });
  const [errs, setErrs] = useState<Partial<Record<"name" | "email" | "password" | "confirm", string>>>({});
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);
  const set = (k: keyof typeof f) => (e: ChangeEvent<HTMLInputElement>) => setF({ ...f, [k]: e.target.value });

  if (done)
    return (
      <div className="mx-auto max-w-sm space-y-3">
        <p data-testid="signup-success" className="text-primary">Account created successfully. You can now log in.</p>
        <Link to="/login" search={{ redirect: undefined }} data-testid="signup-login-link" className={btnCls}>Go to login</Link>
      </div>
    );

  return (
    <form
      noValidate
      data-testid="signup-form"
      className="mx-auto max-w-sm space-y-4"
      onSubmit={(e) => {
        e.preventDefault();
        const er: Partial<Record<"name" | "email" | "password" | "confirm", string>> = {};
        if (!f.name.trim()) er.name = "Name is required";
        if (!f.email.trim()) er.email = "Email is required";
        else if (!EMAIL_RE.test(f.email.trim())) er.email = "Enter a valid email";
        const pe = validatePassword(f.password);
        if (pe) er.password = pe;
        if (!f.confirm) er.confirm = "Confirm password is required";
        else if (f.confirm !== f.password) er.confirm = "Passwords do not match";
        setErrs(er);
        setError(null);
        if (Object.keys(er).length) return;
        const err = signup(f.name, f.email, f.password);
        if (err) return setError(err);
        setDone(true);
      }}
    >
      <h1 data-testid="signup-title" className="text-2xl font-bold">Sign up</h1>
      {error && <p data-testid="signup-error" role="alert" className="rounded-md bg-destructive/10 p-2 text-sm text-destructive">{error}</p>}
      <Field id="signup-name" label="Name" error={errs.name}>
        <input id="signup-name" data-testid="signup-name" className={inputCls} value={f.name} onChange={set("name")} />
      </Field>
      <Field id="signup-email" label="Email" error={errs.email}>
        <input id="signup-email" data-testid="signup-email" type="email" className={inputCls} value={f.email} onChange={set("email")} />
      </Field>
      <Field id="signup-password" label="Password" error={errs.password}>
        <input id="signup-password" data-testid="signup-password" type="password" className={inputCls} value={f.password} onChange={set("password")} />
      </Field>
      <Field id="signup-confirm" label="Confirm password" error={errs.confirm}>
        <input id="signup-confirm" data-testid="signup-confirm" type="password" className={inputCls} value={f.confirm} onChange={set("confirm")} />
      </Field>
      <button data-testid="signup-submit" type="submit" className={`${btnCls} w-full`}>Create account</button>
    </form>
  );
}
