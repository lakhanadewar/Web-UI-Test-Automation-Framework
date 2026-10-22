import { useState } from "react";
import { validateAddress, type AddressInput } from "@/lib/rules";
import { Field, btnCls, btnOutlineCls, inputCls } from "./RequireAuth";

const EMPTY: AddressInput = { fullName: "", phone: "", line: "", city: "", postcode: "", country: "" };
const FIELDS: Array<[keyof AddressInput, string]> = [
  ["fullName", "Full name"],
  ["phone", "Phone (10 digits)"],
  ["line", "Address line"],
  ["city", "City"],
  ["postcode", "Postcode"],
  ["country", "Country"],
];

export function AddressForm({
  initial,
  onSave,
  onCancel,
}: {
  initial?: AddressInput;
  onSave: (a: AddressInput) => void;
  onCancel?: (() => void) | undefined;
}) {
  const [a, setA] = useState<AddressInput>(initial ?? EMPTY);
  const [errs, setErrs] = useState<Partial<Record<keyof AddressInput, string>>>({});
  return (
    <form
      noValidate
      data-testid="address-form"
      className="space-y-3 rounded-lg border border-border bg-card p-4"
      onSubmit={(e) => {
        e.preventDefault();
        const er = validateAddress(a);
        setErrs(er);
        if (Object.keys(er).length) return;
        onSave({ ...a, phone: a.phone.trim() });
      }}
    >
      <div className="grid gap-3 sm:grid-cols-2">
        {FIELDS.map(([k, label]) => (
          <Field key={k} id={`address-${k}`} label={label} error={errs[k]}>
            <input
              id={`address-${k}`}
              data-testid={`address-${k}`}
              className={inputCls}
              inputMode={k === "phone" ? "numeric" : undefined}
              value={a[k]}
              onChange={(e) => setA({ ...a, [k]: e.target.value })}
            />
          </Field>
        ))}
      </div>
      <div className="flex gap-2">
        <button data-testid="address-save" type="submit" className={btnCls}>Save address</button>
        {onCancel && <button data-testid="address-cancel" type="button" className={btnOutlineCls} onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  );
}
