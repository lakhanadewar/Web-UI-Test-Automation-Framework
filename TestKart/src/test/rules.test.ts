import { describe, expect, it } from "vitest";
import {
  chargeCard,
  checkCoupon,
  clampQty,
  computeTotals,
  validateAddress,
  validatePassword,
  validatePaymentFields,
} from "@/lib/rules";

describe("TestKart rules", () => {
  it("password needs 8 chars, a number and a special char", () => {
    expect(validatePassword("Abc@123")).not.toBeNull();
    expect(validatePassword("Abcdefgh@")).not.toBeNull();
    expect(validatePassword("Abcdefg1")).not.toBeNull();
    expect(validatePassword("Test@1234")).toBeNull();
  });
  it("coupons", () => {
    expect(checkCoupon("EXPIRED20")).toEqual({ ok: false, error: "Coupon expired" });
    expect(checkCoupon("NOPE")).toEqual({ ok: false, error: "Invalid coupon" });
    expect(computeTotals(150, "SAVE10").discount).toBe(15);
    expect(computeTotals(199, "FLAT50").discount).toBe(0);
    expect(computeTotals(200, "FLAT50").discount).toBe(50);
  });
  it("shipping free over 100, else 5", () => {
    expect(computeTotals(100, null).shipping).toBe(5);
    expect(computeTotals(100.01, null).shipping).toBe(0);
  });
  it("quantity between 1 and min(10, stock)", () => {
    expect(clampQty(0, 20)).toBe(1);
    expect(clampQty(15, 20)).toBe(10);
    expect(clampQty(5, 3)).toBe(3);
  });
  it("cards", () => {
    expect(chargeCard("4242 4242 4242 4242")).toBe("success");
    expect(chargeCard("4000 0000 0000 0002")).toBe("declined");
    expect(chargeCard("1111 1111 1111 1111")).toBe("invalid");
  });
  it("expiry not in past, cvv 3 digits", () => {
    const now = new Date(2026, 9, 22);
    expect(validatePaymentFields({ card: "4242", expiry: "09/26", cvv: "123" }, now).expiry).toBe("Card has expired");
    expect(validatePaymentFields({ card: "4242", expiry: "10/26", cvv: "12" }, now)).toEqual({ cvv: "CVV must be 3 digits" });
  });
  it("phone must be 10 digits", () => {
    const base = { fullName: "A", line: "x", city: "c", postcode: "12345", country: "UK" };
    expect(validateAddress({ ...base, phone: "123456789" }).phone).toBeDefined();
    expect(validateAddress({ ...base, phone: "1234567890" })).toEqual({});
  });
});
