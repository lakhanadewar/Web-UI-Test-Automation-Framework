import { createFileRoute, Link } from "@tanstack/react-router";
import { useStore, money } from "@/lib/store";
import { maxQty } from "@/lib/rules";
import { btnCls, btnOutlineCls, inputCls } from "@/components/RequireAuth";
import { CouponBox, Totals } from "@/components/CartBits";

export const Route = createFileRoute("/cart")({
  head: () => ({
    meta: [
      { title: "Cart — TestKart" },
      { name: "description", content: "Your TestKart shopping cart." },
      { property: "og:title", content: "Cart — TestKart" },
      { property: "og:description", content: "Your TestKart shopping cart." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: Cart,
});

function Cart() {
  const { cartLines, setQty, removeFromCart } = useStore();
  if (!cartLines.length)
    return (
      <div className="space-y-3">
        <h1 className="text-2xl font-bold">Cart</h1>
        <p data-testid="cart-empty">Your cart is empty</p>
        <Link to="/" data-testid="continue-shopping" className={btnCls}>Continue shopping</Link>
      </div>
    );
  return (
    <div className="space-y-4">
      <h1 data-testid="cart-title" className="text-2xl font-bold">Cart</h1>
      <ul className="divide-y divide-border rounded-lg border border-border bg-card" data-testid="cart-items">
        {cartLines.map(({ product: p, qty }) => (
          <li key={p.id} data-testid={`cart-item-${p.id}`} className="flex flex-wrap items-center gap-3 p-3">
            <img src={p.image} alt={p.name} className="h-14 w-16 rounded object-cover" />
            <div className="flex-1">
              <p data-testid={`cart-item-name-${p.id}`} className="font-medium">{p.name}</p>
              <p className="text-sm text-muted-foreground">{money(p.price)} each</p>
            </div>
            <button data-testid={`cart-decrease-${p.id}`} aria-label="Decrease quantity" className={btnOutlineCls} disabled={qty <= 1} onClick={() => setQty(p.id, qty - 1)}>−</button>
            <label htmlFor={`qty-${p.id}`} className="sr-only">Quantity</label>
            <input id={`qty-${p.id}`} data-testid={`cart-qty-${p.id}`} type="number" min={1} max={maxQty(p.stock)} className={`${inputCls} w-16`} value={qty} onChange={(e) => setQty(p.id, Number(e.target.value))} />
            <button data-testid={`cart-increase-${p.id}`} aria-label="Increase quantity" className={btnOutlineCls} disabled={qty >= maxQty(p.stock)} onClick={() => setQty(p.id, qty + 1)}>+</button>
            <span data-testid={`cart-line-total-${p.id}`} className="w-20 text-right font-medium">{money(p.price * qty)}</span>
            <button data-testid={`cart-remove-${p.id}`} className={btnOutlineCls} onClick={() => removeFromCart(p.id)}>Remove</button>
            {qty >= maxQty(p.stock) && (
              <p data-testid={`cart-max-${p.id}`} className="w-full text-sm text-muted-foreground">Maximum quantity reached ({maxQty(p.stock)})</p>
            )}
          </li>
        ))}
      </ul>
      <div className="grid gap-4 md:grid-cols-2">
        <CouponBox />
        <div className="space-y-3">
          <Totals />
          <Link to="/checkout" data-testid="checkout-button" className={`${btnCls} w-full`}>Proceed to checkout</Link>
        </div>
      </div>
    </div>
  );
}
