import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { useStore, money } from "@/lib/store";
import { chargeCard, validatePaymentFields, type PaymentErrors } from "@/lib/rules";
import { RequireAuth, Field, btnCls, btnOutlineCls, inputCls } from "@/components/RequireAuth";
import { AddressBook } from "@/components/AddressBook";
import { CouponBox, Totals } from "@/components/CartBits";

export const Route = createFileRoute("/checkout")({
  head: () => ({
    meta: [
      { title: "Checkout — TestKart" },
      { name: "description", content: "Complete your TestKart order." },
      { property: "og:title", content: "Checkout — TestKart" },
      { property: "og:description", content: "Complete your TestKart order." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: () => (
    <RequireAuth>
      <Checkout />
    </RequireAuth>
  ),
});

const STEPS = ["Address", "Payment", "Review"];

function Checkout() {
  const { cartLines, addresses, selectedAddressId, placeOrder } = useStore();
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [stepErr, setStepErr] = useState<string | null>(null);
  const [pay, setPay] = useState({ card: "", expiry: "", cvv: "" });
  const [pErr, setPErr] = useState<PaymentErrors>({});

  if (!cartLines.length)
    return (
      <div className="space-y-3">
        <p data-testid="checkout-empty">Your cart is empty</p>
        <Link to="/" className={btnCls}>Continue shopping</Link>
      </div>
    );

  const addr = addresses.find((a) => a.id === selectedAddressId);

  return (
    <div className="space-y-6">
      <h1 data-testid="checkout-title" className="text-2xl font-bold">Checkout</h1>
      <ol className="flex gap-2" data-testid="checkout-progress">
        {STEPS.map((s, i) => (
          <li
            key={s}
            data-testid={`checkout-step-${s.toLowerCase()}`}
            data-active={i === step}
            data-complete={i < step}
            aria-current={i === step ? "step" : undefined}
            className={`flex-1 rounded-md border px-3 py-2 text-center text-sm ${i === step ? "border-primary bg-primary text-primary-foreground" : i < step ? "border-primary text-primary" : "border-border text-muted-foreground"}`}
          >
            {i + 1}. {s}
          </li>
        ))}
      </ol>
      {stepErr && <p data-testid="checkout-error" role="alert" className="text-sm text-destructive">{stepErr}</p>}
      <div className="grid gap-6 md:grid-cols-[2fr_1fr]">
        <div>
          {step === 0 && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold">Delivery address</h2>
              <AddressBook />
              <button
                data-testid="checkout-address-continue"
                className={btnCls}
                onClick={() => {
                  if (!addr) return setStepErr("Please select a delivery address");
                  setStepErr(null);
                  setStep(1);
                }}
              >
                Continue to payment
              </button>
            </div>
          )}
          {step === 1 && (
            <form
              noValidate
              data-testid="payment-form"
              className="space-y-3"
              onSubmit={(e) => {
                e.preventDefault();
                const er = validatePaymentFields(pay);
                if (!Object.keys(er).length) {
                  const r = chargeCard(pay.card);
                  if (r === "declined") er.payment = "Payment declined";
                  if (r === "invalid") er.payment = "Invalid card";
                }
                setPErr(er);
                if (Object.keys(er).length) return;
                setStep(2);
              }}
            >
              <h2 className="text-lg font-semibold">Payment</h2>
              {pErr.payment && <p data-testid="payment-error" role="alert" className="rounded-md bg-destructive/10 p-2 text-sm text-destructive">{pErr.payment}</p>}
              <Field id="payment-card" label="Card number" error={pErr.card}>
                <input id="payment-card" data-testid="payment-card" inputMode="numeric" placeholder="4242 4242 4242 4242" className={inputCls} value={pay.card} onChange={(e) => setPay({ ...pay, card: e.target.value })} />
              </Field>
              <div className="grid grid-cols-2 gap-3">
                <Field id="payment-expiry" label="Expiry (MM/YY)" error={pErr.expiry}>
                  <input id="payment-expiry" data-testid="payment-expiry" placeholder="MM/YY" className={inputCls} value={pay.expiry} onChange={(e) => setPay({ ...pay, expiry: e.target.value })} />
                </Field>
                <Field id="payment-cvv" label="CVV" error={pErr.cvv}>
                  <input id="payment-cvv" data-testid="payment-cvv" type="password" inputMode="numeric" maxLength={4} className={inputCls} value={pay.cvv} onChange={(e) => setPay({ ...pay, cvv: e.target.value })} />
                </Field>
              </div>
              <div className="flex gap-2">
                <button type="button" data-testid="payment-back" className={btnOutlineCls} onClick={() => setStep(0)}>Back</button>
                <button type="submit" data-testid="payment-continue" className={btnCls}>Continue to review</button>
              </div>
            </form>
          )}
          {step === 2 && addr && (
            <div className="space-y-4" data-testid="review-step">
              <h2 className="text-lg font-semibold">Review your order</h2>
              <div className="rounded-lg border border-border bg-card p-3 text-sm" data-testid="review-address">
                <strong>{addr.fullName}</strong> · {addr.phone}<br />
                {addr.line}, {addr.city}, {addr.postcode}, {addr.country}
              </div>
              <p className="text-sm" data-testid="review-card">Card ending {pay.card.replace(/\s/g, "").slice(-4)}</p>
              <ul className="divide-y divide-border rounded-lg border border-border bg-card text-sm" data-testid="review-items">
                {cartLines.map((l) => (
                  <li key={l.product.id} data-testid={`review-item-${l.product.id}`} className="flex justify-between p-2">
                    <span>{l.product.name} × {l.qty}</span>
                    <span>{money(l.product.price * l.qty)}</span>
                  </li>
                ))}
              </ul>
              <div className="flex gap-2">
                <button data-testid="review-back" className={btnOutlineCls} onClick={() => setStep(1)}>Back</button>
                <button
                  data-testid="place-order"
                  className={btnCls}
                  onClick={() => {
                    const o = placeOrder();
                    if (!o) return setStepErr("Could not place order");
                    navigate({ to: "/orders/$id", params: { id: o.id } });
                  }}
                >
                  Place order
                </button>
              </div>
            </div>
          )}
        </div>
        <aside className="space-y-3">
          <Totals />
          {step < 2 && <CouponBox />}
        </aside>
      </div>
    </div>
  );
}
