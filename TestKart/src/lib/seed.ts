export type Product = {
  id: string;
  name: string;
  price: number;
  category: "Electronics" | "Clothing" | "Home";
  stock: number;
  image: string;
  description: string;
};

export type User = { id: string; name: string; email: string; password: string; locked?: boolean };

function img(label: string, hue: number) {
  const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='400' height='300'><rect width='100%' height='100%' fill='hsl(${hue},45%,85%)'/><text x='50%' y='50%' font-family='sans-serif' font-size='28' fill='hsl(${hue},40%,25%)' text-anchor='middle' dominant-baseline='middle'>${label}</text></svg>`;
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

const raw: Array<[string, string, number, Product["category"], number, number]> = [
  ["p1", "Wireless Headphones", 79.99, "Electronics", 15, 200],
  ["p2", "Smart Watch", 149.0, "Electronics", 8, 210],
  ["p3", "Bluetooth Speaker", 45.5, "Electronics", 0, 220],
  ["p4", "USB-C Charger", 19.99, "Electronics", 30, 230],
  ["p5", "Cotton T-Shirt", 15.0, "Clothing", 50, 20],
  ["p6", "Denim Jacket", 89.0, "Clothing", 5, 30],
  ["p7", "Running Shoes", 120.0, "Clothing", 12, 40],
  ["p8", "Wool Scarf", 25.0, "Clothing", 0, 10],
  ["p9", "Ceramic Mug", 12.5, "Home", 40, 120],
  ["p10", "Desk Lamp", 39.99, "Home", 3, 130],
  ["p11", "Throw Pillow", 22.0, "Home", 20, 140],
  ["p12", "Coffee Maker", 210.0, "Home", 6, 150],
];

export const SEED_PRODUCTS: Product[] = raw.map(([id, name, price, category, stock, hue]) => ({
  id,
  name,
  price,
  category,
  stock,
  image: img(name, hue),
  description: `${name} — a reliable ${category.toLowerCase()} item for everyday use.`,
}));

export const SEED_USERS: User[] = [
  { id: "u1", name: "Standard User", email: "standard@testkart.com", password: "Test@1234" },
  { id: "u2", name: "Locked User", email: "locked@testkart.com", password: "Test@1234", locked: true },
];

export const CATEGORIES = ["Electronics", "Clothing", "Home"] as const;
