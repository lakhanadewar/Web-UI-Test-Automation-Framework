import { createFileRoute, Link } from "@tanstack/react-router";
import { useState } from "react";
import { useStore, money } from "@/lib/store";
import { btnCls, inputCls } from "@/components/RequireAuth";
import { maxQty } from "@/lib/rules";

export const Route = createFileRoute("/product/$id")({
  head: () => ({
    meta: [
      { title: "Product details — TestKart" },
      { name: "description", content: "Product details on TestKart." },
      { property: "og:title", content: "Product details — TestKart" },
      { property: "og:description", content: "Product details on TestKart." },
      { property: "og:type", content: "product" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: ProductPage,
});

function ProductPage() {
  const { id } = Route.useParams();
  const { data, addToCart, ready } = useStore();
  const p = data.products.find((x) => x.id === id);
  const [qty, setQty] = useState(1);
  const [msg, setMsg] = useState<{ text: string; ok: boolean } | null>(null);
  if (!p) return <p data-testid="product-not-found">Product not found. <Link to="/">Back</Link></p>;
  const out = p.stock <= 0;
  return (
    <div className="grid gap-6 md:grid-cols-2" data-testid="product-detail">
      <img src={p.image} alt={p.name} className="w-full rounded-lg" />
      <div className="space-y-3">
        <Link to="/" data-testid="back-to-products" className="text-sm text-muted-foreground">← Back to products</Link>
        <h1 data-testid="product-detail-name" className="text-2xl font-bold">{p.name}</h1>
        <p data-testid="product-detail-category" className="text-muted-foreground">{p.category}</p>
        <p data-testid="product-detail-price" className="text-xl font-semibold">{money(p.price)}</p>
        <p data-testid="product-detail-description">{p.description}</p>
        <p data-testid="product-detail-stock" className={out ? "text-destructive" : ""}>{out ? "Out of stock" : `In stock: ${p.stock}`}</p>
        {!out && (
          <div className="w-32">
            <label htmlFor="detail-qty" className="mb-1 block text-sm font-medium">Quantity</label>
            <input id="detail-qty" data-testid="product-detail-qty" type="number" min={1} max={maxQty(p.stock)} className={inputCls} value={qty} onChange={(e) => setQty(Number(e.target.value))} />
          </div>
        )}
        <button
          data-testid="product-detail-add-to-cart"
          className={btnCls}
          disabled={!ready || out}
          onClick={() => {
            if (!Number.isInteger(qty) || qty < 1) return setMsg({ text: "Quantity must be at least 1", ok: false });
            if (qty > maxQty(p.stock)) return setMsg({ text: `Maximum quantity is ${maxQty(p.stock)}`, ok: false });
            const err = addToCart(p.id, qty);
            setMsg({ text: err ?? "Added to cart", ok: !err });
          }}
        >
          {out ? "Out of stock" : "Add to cart"}
        </button>
        {msg && <p data-testid="product-detail-message" className={msg.ok ? "text-primary" : "text-destructive"}>{msg.text}</p>}
      </div>
    </div>
  );
}
