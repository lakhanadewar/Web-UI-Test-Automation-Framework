import { Link, useNavigate } from "@tanstack/react-router";
import { useStore } from "@/lib/store";
import { btnOutlineCls } from "./RequireAuth";

export function SiteHeader() {
  const { user, cartCount, logout, ready } = useStore();
  const navigate = useNavigate();
  return (
    <header className="border-b border-border bg-card">
      <nav className="mx-auto flex max-w-5xl flex-wrap items-center gap-4 px-4 py-3 text-sm">
        <Link to="/" data-testid="nav-home" className="text-lg font-bold text-primary">
          TestKart
        </Link>
        <Link to="/" data-testid="nav-products">Products</Link>
        {user && <Link to="/orders" data-testid="nav-orders">Orders</Link>}
        {user && <Link to="/profile" data-testid="nav-profile">Profile</Link>}
        <div className="ml-auto flex items-center gap-4">
          <Link to="/cart" data-testid="nav-cart">
            Cart (<span data-testid="cart-count">{cartCount}</span>)
          </Link>
          {ready && user ? (
            <>
              <span data-testid="nav-user-name">Hi, {user.name}</span>
              <button
                data-testid="logout-button"
                className={btnOutlineCls}
                onClick={() => {
                  logout();
                  navigate({ to: "/login", search: { redirect: undefined } });
                }}
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" search={{ redirect: undefined }} data-testid="nav-login">Login</Link>
              <Link to="/signup" data-testid="nav-signup">Sign up</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}

export function SiteFooter() {
  const { resetDemo } = useStore();
  const navigate = useNavigate();
  return (
    <footer className="border-t border-border bg-card">
      <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-4 text-sm text-muted-foreground">
        <span>TestKart — demo shop for test automation</span>
        <button
          data-testid="reset-demo-data"
          className={btnOutlineCls}
          onClick={() => {
            resetDemo();
            navigate({ to: "/" });
          }}
        >
          Reset demo data
        </button>
      </div>
    </footer>
  );
}
