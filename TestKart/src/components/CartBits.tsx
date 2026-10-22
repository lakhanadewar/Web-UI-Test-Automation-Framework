import { useState } from "react";
import { useStore, money } from "@/lib/store";
import { checkCoupon } from "@/lib/rules";
import { btnCls, btnOutlineCls, inputCls } from "./RequireAuth";

export function CouponBox() {
  const { coupon, setCoupon } = useStore();
  const [code, setCode] = useState("");
  const [msg, setMsg] = useState<{ text: string; ok: boolean } | null>(null);
  return (
    <div className="space-y-2 rounded-lg border border-border bg-card p-3">
      {coupon ? (
        <div className="flex items-center justify-between">
          <p data-testid="coupon-applied">Coupon <strong>{coupon}</strong> applied</p>
          <button data-testid="coupon-remove" className={btnOutlineCls} onClick={() => { setCoupon(null); setMsg({ text: "Coupon removed", ok: true }); }}>
            Remove coupon
          </button>
        </div>
      ) : (
        <form
          noValidate
          className="flex items-end gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            if (!code.trim()) return setMsg({ text: "Enter a coupon code", ok: false });
            const r = checkCoupon(code);
            if (!r.ok) return setMsg({ text: r.error, ok: false });
            setCoupon(r.code);
            setCode("");
            setMsg({ text: "Coupon applied", ok: true });
          }}
        >
          <div className="flex-1">
            <label htmlFor="coupon-input" className="mb-1 block text-sm font-medium">Coupon code</label>
            <input id="coupon-input" data-testid="coupon-input" className={inputCls} value={code} onChange={(e) => setCode(e.target.value)} />
          </div>
          <button data-testid="coupon-apply" type="submit" className={btnCls}>Apply</button>
        </form>
      )}
      {msg && <p data-testid="coupon-message" role="status" className={msg.ok ? "text-sm text-primary" : "text-sm text-destructive"}>{msg.text}</p>}
    </div>
  );
}

export function Totals() {
  const { totals, coupon } = useStore();
  return (
    <dl className="space-y-1 rounded-lg border border-border bg-card p-3 text-sm" data-testid="order-totals">
      <Row label="Subtotal" id="subtotal" v={money(totals.subtotal)} />
      <Row label={`Discount${coupon ? ` (${coupon})` : ""}`} id="discount" v={`-${money(totals.discount)}`} />
      <Row label="Shipping" id="shipping" v={totals.shipping === 0 ? "Free" : money(totals.shipping)} />
      <div className="border-t border-border pt-1 font-semibold">
        <Row label="Total" id="total" v={money(totals.total)} />
      </div>
    </dl>
  );
}

function Row({ label, id, v }: { label: string; id: string; v: string }) {
  return (
    <div className="flex justify-between">
      <dt>{label}</dt>
      <dd data-testid={`summary-${id}`}>{v}</dd>
    </div>
  );
}
