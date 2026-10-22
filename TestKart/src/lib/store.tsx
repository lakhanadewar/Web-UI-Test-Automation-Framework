import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from "react";
import { SEED_PRODUCTS, SEED_USERS, type Product, type User } from "./seed";
import {
  SESSION_REMEMBER_MS,
  SESSION_SHORT_MS,
  clampQty,
  computeTotals,
  type AddressInput,
} from "./rules";

export type Address = AddressInput & { id: string };
export type CartItem = { productId: string; qty: number };
export type Order = {
  id: string;
  userId: string;
  createdAt: string;
  items: Array<{ productId: string; name: string; price: number; qty: number }>;
  address: AddressInput;
  coupon: string | null;
  subtotal: number;
  discount: number;
  shipping: number;
  total: number;
};
type Session = { userId: string; expiresAt: number };

type Data = {
  products: Product[];
  users: User[];
  carts: Record<string, CartItem[]>;
  coupons: Record<string, string | null>;
  addresses: Record<string, Address[]>;
  selectedAddress: Record<string, string | null>;
  orders: Order[];
};

const KEY = "testkart:data";
const SKEY = "testkart:session";

function seed(): Data {
  return {
    products: SEED_PRODUCTS,
    users: SEED_USERS,
    carts: {},
    coupons: {},
    addresses: {},
    selectedAddress: {},
    orders: [],
  };
}

function load(): Data {
  try {
    const s = localStorage.getItem(KEY);
    if (s) return { ...seed(), ...JSON.parse(s) };
  } catch {}
  return seed();
}

function loadSession(): Session | null {
  try {
    const s = localStorage.getItem(SKEY) ?? sessionStorage.getItem(SKEY);
    if (!s) return null;
    const v = JSON.parse(s) as Session;
    if (v.expiresAt < Date.now()) {
      localStorage.removeItem(SKEY);
      sessionStorage.removeItem(SKEY);
      return null;
    }
    return v;
  } catch {
    return null;
  }
}

const uid = (p: string) => `${p}-${Date.now().toString(36).toUpperCase()}${Math.floor(Math.random() * 1000)}`;

function useStoreImpl() {
  const [ready, setReady] = useState(false);
  const [data, setData] = useState<Data>(seed);
  const [session, setSession] = useState<Session | null>(null);

  useEffect(() => {
    setData(load());
    setSession(loadSession());
    setReady(true);
  }, []);

  const update = useCallback((fn: (d: Data) => Data) => {
    setData((prev) => {
      const next = fn(prev);
      localStorage.setItem(KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const user = session ? (data.users.find((u) => u.id === session.userId) ?? null) : null;
  const uidKey = user?.id ?? "guest";
  const cart = data.carts[uidKey] ?? [];
  const coupon = data.coupons[uidKey] ?? null;
  const addresses = user ? (data.addresses[user.id] ?? []) : [];
  const selectedAddressId = user ? (data.selectedAddress[user.id] ?? null) : null;

  const cartLines = cart
    .map((c) => {
      const p = data.products.find((x) => x.id === c.productId);
      return p ? { product: p, qty: c.qty } : null;
    })
    .filter((x): x is { product: Product; qty: number } => !!x);
  const subtotal = Math.round(cartLines.reduce((s, l) => s + l.product.price * l.qty, 0) * 100) / 100;
  const totals = computeTotals(subtotal, coupon);
  const cartCount = cartLines.reduce((s, l) => s + l.qty, 0);

  const setCart = (items: CartItem[]) => update((d) => ({ ...d, carts: { ...d.carts, [uidKey]: items } }));

  return {
    ready,
    data,
    user,
    cartLines,
    cartCount,
    totals,
    coupon,
    addresses,
    selectedAddressId,

    login(email: string, password: string, remember: boolean): string | null {
      const u = data.users.find((x) => x.email.toLowerCase() === email.trim().toLowerCase());
      if (!u || u.password !== password) return "Invalid email or password";
      if (u.locked) return "Your account is locked";
      const s: Session = { userId: u.id, expiresAt: Date.now() + (remember ? SESSION_REMEMBER_MS : SESSION_SHORT_MS) };
      localStorage.removeItem(SKEY);
      sessionStorage.removeItem(SKEY);
      (remember ? localStorage : sessionStorage).setItem(SKEY, JSON.stringify(s));
      // merge guest cart
      const guest = data.carts["guest"] ?? [];
      if (guest.length) {
        update((d) => {
          const mine = [...(d.carts[u.id] ?? [])];
          for (const g of guest) {
            const p = d.products.find((x) => x.id === g.productId);
            const ex = mine.find((m) => m.productId === g.productId);
            if (ex) ex.qty = clampQty(ex.qty + g.qty, p?.stock ?? 1);
            else mine.push(g);
          }
          return { ...d, carts: { ...d.carts, [u.id]: mine, guest: [] } };
        });
      }
      setSession(s);
      return null;
    },
    logout() {
      localStorage.removeItem(SKEY);
      sessionStorage.removeItem(SKEY);
      setSession(null);
    },
    signup(name: string, email: string, password: string): string | null {
      if (data.users.some((u) => u.email.toLowerCase() === email.trim().toLowerCase()))
        return "An account with this email already exists";
      update((d) => ({
        ...d,
        users: [...d.users, { id: uid("U"), name: name.trim(), email: email.trim(), password }],
      }));
      return null;
    },
    addToCart(productId: string, qty = 1): string | null {
      const p = data.products.find((x) => x.id === productId);
      if (!p || p.stock <= 0) return "Out of stock";
      const ex = cart.find((c) => c.productId === productId);
      const next = clampQty((ex?.qty ?? 0) + qty, p.stock);
      if (ex && ex.qty === next) return `Maximum quantity reached (${next})`;
      setCart(ex ? cart.map((c) => (c.productId === productId ? { ...c, qty: next } : c)) : [...cart, { productId, qty: next }]);
      return null;
    },
    setQty(productId: string, qty: number) {
      const p = data.products.find((x) => x.id === productId);
      if (!p) return;
      setCart(cart.map((c) => (c.productId === productId ? { ...c, qty: clampQty(qty, p.stock) } : c)));
    },
    removeFromCart(productId: string) {
      setCart(cart.filter((c) => c.productId !== productId));
    },
    setCoupon(code: string | null) {
      update((d) => ({ ...d, coupons: { ...d.coupons, [uidKey]: code } }));
    },
    saveAddress(a: AddressInput, id?: string): string {
      if (!user) return "";
      const newId = id ?? uid("A");
      update((d) => {
        const list = d.addresses[user.id] ?? [];
        const nextList = id ? list.map((x) => (x.id === id ? { ...a, id } : x)) : [...list, { ...a, id: newId }];
        return {
          ...d,
          addresses: { ...d.addresses, [user.id]: nextList },
          selectedAddress: { ...d.selectedAddress, [user.id]: d.selectedAddress[user.id] ?? newId },
        };
      });
      return newId;
    },
    selectAddress(id: string) {
      if (!user) return;
      update((d) => ({ ...d, selectedAddress: { ...d.selectedAddress, [user.id]: id } }));
    },
    placeOrder(): Order | null {
      if (!user) return null;
      const addr = addresses.find((a) => a.id === selectedAddressId);
      if (!addr || !cartLines.length) return null;
      const { id: _id, ...address } = addr;
      const order: Order = {
        id: uid("ORD"),
        userId: user.id,
        createdAt: new Date().toISOString(),
        items: cartLines.map((l) => ({ productId: l.product.id, name: l.product.name, price: l.product.price, qty: l.qty })),
        address,
        coupon,
        ...totals,
      };
      update((d) => ({
        ...d,
        orders: [order, ...d.orders],
        products: d.products.map((p) => {
          const l = cartLines.find((x) => x.product.id === p.id);
          return l ? { ...p, stock: p.stock - l.qty } : p;
        }),
        carts: { ...d.carts, [user.id]: [] },
        coupons: { ...d.coupons, [user.id]: null },
      }));
      return order;
    },
    resetDemo() {
      localStorage.removeItem(KEY);
      localStorage.removeItem(SKEY);
      sessionStorage.removeItem(SKEY);
      setData(seed());
      setSession(null);
    },
  };
}

type Store = ReturnType<typeof useStoreImpl>;
const Ctx = createContext<Store | null>(null);

export function StoreProvider({ children }: { children: ReactNode }) {
  const s = useStoreImpl();
  return <Ctx.Provider value={s}>{children}</Ctx.Provider>;
}

export function useStore() {
  const s = useContext(Ctx);
  if (!s) throw new Error("useStore outside provider");
  return s;
}

export const money = (n: number) => `$${n.toFixed(2)}`;
