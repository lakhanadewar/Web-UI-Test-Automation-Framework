import { createFileRoute, Link } from "@tanstack/react-router";
import { useStore, money } from "@/lib/store";
import { RequireAuth } from "@/components/RequireAuth";

export const Route = createFileRoute("/orders/")({
  head: () => ({
    meta: [
      { title: "Order history — TestKart" },
      { name: "description", content: "Your past TestKart orders." },
      { property: "og:title", content: "Order history — TestKart" },
      { property: "og:description", content: "Your past TestKart orders." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: () => (
    <RequireAuth>
      <Orders />
    </RequireAuth>
  ),
});

function Orders() {
  const { data, user } = useStore();
  const orders = data.orders.filter((o) => o.userId === user?.id);
  return (
    <div className="space-y-4">
      <h1 data-testid="orders-title" className="text-2xl font-bold">Order history</h1>
      {orders.length === 0 ? (
        <p data-testid="orders-empty">You have no orders yet</p>
      ) : (
        <ul className="divide-y divide-border rounded-lg border border-border bg-card" data-testid="orders-list">
          {orders.map((o) => (
            <li key={o.id} data-testid={`order-row-${o.id}`} className="flex flex-wrap items-center justify-between gap-2 p-3 text-sm">
              <Link to="/orders/$id" params={{ id: o.id }} data-testid={`order-link-${o.id}`} className="font-medium text-primary underline">{o.id}</Link>
              <span>{new Date(o.createdAt).toLocaleString()}</span>
              <span>{o.items.reduce((s, i) => s + i.qty, 0)} items</span>
              <span data-testid={`order-total-${o.id}`} className="font-semibold">{money(o.total)}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
