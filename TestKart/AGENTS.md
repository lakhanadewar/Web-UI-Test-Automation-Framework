<!-- LOVABLE:BEGIN -->
> [!IMPORTANT]
> This project is connected to [Lovable](https://lovable.dev). Avoid rewriting
> published git history — force pushing, or rebasing/amending/squashing commits
> that are already pushed — as it rewrites history on Lovable's side and the
> user will likely lose their project history.
>
> Commits you push to the connected branch sync back to Lovable and show up in
> the editor, so keep the branch in a working state.
<!-- LOVABLE:END -->

- All app data lives in browser localStorage via `src/lib/store.tsx` (no backend) — app is an offline practice target for UI test automation.
- Business rules (coupons, shipping, quantity limits, validation, mock cards) live as pure functions in `src/lib/rules.ts`, tested in `src/test/rules.test.ts`.
- Every interactive element and key text carries a stable `data-testid`; never rename existing ids — external test suites depend on them.
