// Pure business rules for TestKart. No browser APIs here so they are unit-testable.

export const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validatePassword(pw: string): string | null {
  if (!pw) return "Password is required";
  if (pw.length < 8) return "Password must be at least 8 characters";
  if (!/\d/.test(pw)) return "Password must contain at least 1 number";
  if (!/[^A-Za-z0-9]/.test(pw)) return "Password must contain at least 1 special character";
  return null;
}

export type CouponResult =
  | { ok: true; code: string }
  | { ok: false; error: string };

export function checkCoupon(raw: string): CouponResult {
  const code = raw.trim().toUpperCase();
  if (code === "SAVE10" || code === "FLAT50") return { ok: true, code };
  if (code === "EXPIRED20") return { ok: false, error: "Coupon expired" };
  return { ok: false, error: "Invalid coupon" };
}

export function computeDiscount(code: string | null, subtotal: number): number {
  if (code === "SAVE10") return round2(subtotal * 0.1);
  if (code === "FLAT50") return subtotal >= 200 ? 50 : 0;
  return 0;
}

export function computeShipping(subtotal: number): number {
  if (subtotal === 0) return 0;
  return subtotal > 100 ? 0 : 5;
}

export function computeTotals(subtotal: number, coupon: string | null) {
  const discount = computeDiscount(coupon, subtotal);
  const shipping = computeShipping(subtotal);
  return { subtotal, discount, shipping, total: round2(subtotal - discount + shipping) };
}

export function maxQty(stock: number): number {
  return Math.min(10, stock);
}

export function clampQty(q: number, stock: number): number {
  if (!Number.isFinite(q)) return 1;
  return Math.max(1, Math.min(maxQty(stock), Math.floor(q)));
}

export type PaymentInput = { card: string; expiry: string; cvv: string };
export type PaymentErrors = Partial<Record<"card" | "expiry" | "cvv" | "payment", string>>;

export function validatePaymentFields(p: PaymentInput, now = new Date()): PaymentErrors {
  const e: PaymentErrors = {};
  const digits = p.card.replace(/\s/g, "");
  if (!digits) e.card = "Card number is required";
  const m = /^(\d{2})\/(\d{2})$/.exec(p.expiry.trim());
  if (!p.expiry.trim()) e.expiry = "Expiry is required";
  else if (!m || +m[1]! < 1 || +m[1]! > 12) e.expiry = "Expiry must be MM/YY";
  else {
    const y = 2000 + +m[2]!;
    const mo = +m[1]!;
    const cy = now.getFullYear();
    const cm = now.getMonth() + 1;
    if (y < cy || (y === cy && mo < cm)) e.expiry = "Card has expired";
  }
  if (!p.cvv) e.cvv = "CVV is required";
  else if (!/^\d{3}$/.test(p.cvv)) e.cvv = "CVV must be 3 digits";
  return e;
}

/** Result of charging the mock gateway once fields are valid. */
export function chargeCard(card: string): "success" | "declined" | "invalid" {
  const d = card.replace(/\s/g, "");
  if (d === "4242424242424242") return "success";
  if (d === "4000000000000002") return "declined";
  return "invalid";
}

export type AddressInput = {
  fullName: string;
  phone: string;
  line: string;
  city: string;
  postcode: string;
  country: string;
};

export function validateAddress(a: AddressInput): Partial<Record<keyof AddressInput, string>> {
  const e: Partial<Record<keyof AddressInput, string>> = {};
  if (!a.fullName.trim()) e.fullName = "Full name is required";
  if (!a.phone.trim()) e.phone = "Phone is required";
  else if (!/^\d{10}$/.test(a.phone.trim())) e.phone = "Phone must be exactly 10 digits";
  if (!a.line.trim()) e.line = "Address line is required";
  if (!a.city.trim()) e.city = "City is required";
  if (!a.postcode.trim()) e.postcode = "Postcode is required";
  else if (!/^[A-Za-z0-9 -]{3,10}$/.test(a.postcode.trim())) e.postcode = "Postcode is invalid";
  if (!a.country.trim()) e.country = "Country is required";
  return e;
}

export const SESSION_SHORT_MS = 24 * 60 * 60 * 1000;
export const SESSION_REMEMBER_MS = 7 * 24 * 60 * 60 * 1000;

function round2(n: number) {
  return Math.round(n * 100) / 100;
}
