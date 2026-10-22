import { useState } from "react";
import { useStore } from "@/lib/store";
import { AddressForm } from "./AddressForm";
import { btnOutlineCls } from "./RequireAuth";

export function AddressBook() {
  const { addresses, selectedAddressId, saveAddress, selectAddress } = useStore();
  const [editing, setEditing] = useState<string | "new" | null>(addresses.length ? null : "new");
  return (
    <div className="space-y-3" data-testid="address-book">
      {addresses.length === 0 && editing !== "new" && <p data-testid="no-addresses">No saved addresses</p>}
      <ul className="space-y-2">
        {addresses.map((a) =>
          editing === a.id ? (
            <li key={a.id}>
              <AddressForm initial={a} onSave={(v) => { saveAddress(v, a.id); setEditing(null); }} onCancel={() => setEditing(null)} />
            </li>
          ) : (
            <li key={a.id} data-testid={`address-item-${a.id}`} className="flex items-start gap-3 rounded-lg border border-border bg-card p-3">
              <input
                type="radio"
                name="address"
                id={`address-select-${a.id}`}
                data-testid={`address-select-${a.id}`}
                checked={selectedAddressId === a.id}
                onChange={() => selectAddress(a.id)}
                className="mt-1"
              />
              <label htmlFor={`address-select-${a.id}`} className="flex-1 text-sm">
                <strong>{a.fullName}</strong> · {a.phone}
                <br />
                {a.line}, {a.city}, {a.postcode}, {a.country}
              </label>
              <button data-testid={`address-edit-${a.id}`} className={btnOutlineCls} onClick={() => setEditing(a.id)}>Edit</button>
            </li>
          ),
        )}
      </ul>
      {editing === "new" ? (
        <AddressForm onSave={(v) => { const id = saveAddress(v); selectAddress(id); setEditing(null); }} onCancel={addresses.length ? () => setEditing(null) : undefined} />
      ) : (
        <button data-testid="address-add" className={btnOutlineCls} onClick={() => setEditing("new")}>+ Add new address</button>
      )}
    </div>
  );
}
