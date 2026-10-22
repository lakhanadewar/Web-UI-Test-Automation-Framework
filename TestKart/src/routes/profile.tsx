import { createFileRoute } from "@tanstack/react-router";
import { useStore } from "@/lib/store";
import { RequireAuth } from "@/components/RequireAuth";
import { AddressBook } from "@/components/AddressBook";

export const Route = createFileRoute("/profile")({
  head: () => ({
    meta: [
      { title: "Profile — TestKart" },
      { name: "description", content: "Your TestKart profile and addresses." },
      { property: "og:title", content: "Profile — TestKart" },
      { property: "og:description", content: "Your TestKart profile and addresses." },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary" },
    ],
  }),
  component: () => (
    <RequireAuth>
      <Profile />
    </RequireAuth>
  ),
});

function Profile() {
  const { user } = useStore();
  return (
    <div className="space-y-4">
      <h1 data-testid="profile-title" className="text-2xl font-bold">Profile</h1>
      <p>Name: <span data-testid="profile-name">{user?.name}</span></p>
      <p>Email: <span data-testid="profile-email">{user?.email}</span></p>
      <h2 className="text-lg font-semibold">Addresses</h2>
      <AddressBook />
    </div>
  );
}
