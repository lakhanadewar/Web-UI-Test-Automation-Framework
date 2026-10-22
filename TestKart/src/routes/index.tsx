import { createFileRoute, Link } from "@tanstack/react-router";
import { useMemo, useState } from "react";
import { useStore, money } from "@/lib/store";
import { CATEGORIES } from "@/lib/seed";
import { btnCls, inputCls } from "@/components/RequireAuth";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Products — TestKart" },
      { name: "description", content: "Browse, search and filter the TestKart product catalogue." },
      { property: "og:title", content: "Products — TestKart" },
      { property: "og:description", content: "Browse, search and filter the TestKart product catalogue." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: Catalogue,
});

function Catalogue() {
  const { data, addToCart, ready } = useStore();
  const [q, setQ] = useState("");
  const [cat, setCat] = useState("all");
  const [sort, setSort] = useState("none");
  const [msg, setMsg] = useState<{ id: string; text: string; ok: boolean } | null>(null);

  const list = useMemo(() => {
    let l = data.products.filter((p) => p.name.toLowerCase().includes(q.trim().toLowerCase()));
    if (cat !== "all") l = l.filter((p) => p.category === cat);
    if (sort === "asc") l = [...l].sort((a, b) => a.price - b.price);
    if (sort === "desc") l = [...l].sort((a, b) => b.price - a.price);
    return l;
  }, [data.products, q, cat, sort]);

  return (
    <div className="space-y-4">
      <h1 data-testid="catalogue-title" className="text-2xl font-bold">Products</h1>
      <div className="grid gap-3 sm:grid-cols-3">
        <div>
          <label htmlFor="search" className="mb-1 block text-sm font-medium">Search</label>
          <input id="search" data-testid="search-input" className={inputCls} value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search by name" />
        </div>
        <div>
          <label htmlFor="category" className="mb-1 block text-sm font-medium">Category</label>
          <select id="category" data-testid="category-filter" className={inputCls} value={cat} onChange={(e) => setCat(e.target.value)}>
            <option value="all">All</option>
            {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
        <div>
          <label htmlFor="sort" className="mb-1 block text-sm font-medium">Sort</label>
          <select id="sort" data-testid="sort-select" className={inputCls} value={sort} onChange={(e) => setSort(e.target.value)}>
            <option value="none">Default</option>
            <option value="asc">Price: low to high</option>
            <option value="desc">Price: high to low</option>
          </select>
        </div>
      </div>
      <p data-testid="result-count" className="text-sm text-muted-foreground">{list.length} products</p>
      {list.length === 0 && <p data-testid="no-results">No products found</p>}
      <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-3" data-testid="product-list">
        {list.map((p) => (
          <div key={p.id} data-testid={`product-card-${p.id}`} className="rounded-lg border border-border bg-card p-3">
            <img src={p.image} alt={p.name} className="mb-2 aspect-[4/3] w-full rounded object-cover" />
            <Link to="/product/$id" params={{ id: p.id }} data-testid={`product-link-${p.id}`} className="font-semibold hover:underline">
              <span data-testid={`product-name-${p.id}`}>{p.name}</span>
            </Link>
            <p className="text-sm text-muted-foreground">{p.category}</p>
            <p data-testid={`product-price-${p.id}`} className="font-medium">{money(p.price)}</p>
            <p data-testid={`product-stock-${p.id}`} className={p.stock > 0 ? "text-sm" : "text-sm text-destructive"}>
              {p.stock > 0 ? `In stock: ${p.stock}` : "Out of stock"}
            </p>
            <button
              data-testid={`add-to-cart-${p.id}`}
              className={`${btnCls} mt-2 w-full`}
              disabled={!ready || p.stock <= 0}
              onClick={() => {
                const err = addToCart(p.id);
                setMsg({ id: p.id, text: err ?? "Added to cart", ok: !err });
              }}
            >
              {p.stock > 0 ? "Add to cart" : "Out of stock"}
            </button>
            {msg?.id === p.id && (
              <p data-testid={`add-to-cart-message-${p.id}`} className={msg.ok ? "mt-1 text-sm text-primary" : "mt-1 text-sm text-destructive"}>
                {msg.text}
              </p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
