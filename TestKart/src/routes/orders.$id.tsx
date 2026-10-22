import { createFileRoute, Link } from "@tanstack/react-router";
import { useStore, money } from "@/lib/store";
import { RequireAuth, btnCls } from "@/components/RequireAuth";

export const Route = createFileRoute("/orders/$id")({
  head: () => ({
    meta: [
      { title: "Order confirmation — TestKart" },
      { name: "description", content: "Your TestKart order details." },
      { property: "og:title", content: "Order confirmation — TestKart" },
      { property: "og:description", content: "Your TestKart order details." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: () => (
    <RequireAuth>
      <OrderDetail />
    </RequireAuth>
  ),
});

function OrderDetail() {
  const { id } = Route.useParams();
  const { data, user } = useStore();
  const o = data.orders.find((x) => x.id === id && x.userId === user?.id);
  if (!o) return <p data-testid="order-not-found">Order not found</p>;
  return (
    <div className="mx-auto max-w-xl space-y-4" data-testid="order-confirmation">
      <h1 data-testid="order-confirmation-title" className="text-2xl font-bold">Thank you! Your order is confirmed.</h1>
      <p>Order ID: <strong data-testid="order-id">{o.id}</strong></p>
      <ul className="divide-y divide-border rounded-lg border border-border bg-card text-sm">
        {o.items.map((i) => (
          <li key={i.productId} data-testid={`order-item-${i.productId}`} className="flex justify-between p-2">
            <span>{i.name} × {i.qty}</span>
            <span>{money(i.price * i.qty)}</span>
          </li>
        ))}
      </ul>
      <dl className="space-y-1 rounded-lg border border-border bg-card p-3 text-sm">
        <div className="flex justify-between"><dt>Subtotal</dt><dd data-testid="order-subtotal">{money(o.subtotal)}</dd></div>
        <div className="flex justify-between"><dt>Discount{o.coupon ? ` (${o.coupon})` : ""}</dt><dd data-testid="order-discount">-{money(o.discount)}</dd></div>
        <div className="flex justify-between"><dt>Shipping</dt><dd data-testid="order-shipping">{o.shipping === 0 ? "Free" : money(o.shipping)}</dd></div>
        <div className="flex justify-between font-semibold"><dt>Total</dt><dd data-testid="order-total">{money(o.total)}</dd></div>
      </dl>
      <p className="text-sm" data-testid="order-address">
        Delivering to {o.address.fullName}, {o.address.line}, {o.address.city}, {o.address.postcode}, {o.address.country}
      </p>
      <div className="flex gap-2">
        <Link to="/orders" data-testid="view-orders" className={btnCls}>View all orders</Link>
        <Link to="/" data-testid="continue-shopping" className="self-center text-sm underline">Continue shopping</Link>
      </div>
    </div>
  );
}
