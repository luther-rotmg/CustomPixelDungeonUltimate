# Changelog

All notable changes to Lutherverse (repo: `CustomPixelDungeonUltimate`) are documented here.

Format loosely follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/); the project uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html) where release tags are involved (see also the `cpd-sync-base-*` and future `spd-sync-*` tag families for merge-checkpoint anchors).

**Every substantive commit updates this file and [`PROJECT-STATUS.md`](PROJECT-STATUS.md) in the same commit.** Collaborators and alpha-testers read these first; commit messages are secondary.

---

## [Unreleased]

Work-in-progress on `main` since the fork base (`cpd-sync-base-2025-08-15`, CPD commit `c97fb83`).

### Added — Sub-A (Fork infrastructure)

- Public GitHub fork of QuasiStellar/custom-pixel-dungeon at [luther-rotmg/CustomPixelDungeonUltimate](https://github.com/luther-rotmg/CustomPixelDungeonUltimate) created 2026-07-21
- Three git remotes wired: `origin` (this fork), `upstream-cpd` (QSR at `margarita` @ c97fb83), `upstream-spd` (Evan at `master`, v3.3.8 @ 7b8b845a)
- Annotated tag `cpd-sync-base-2025-08-15` pinning the fork base at c97fb83 for future merge-in-slices reference
- `README.md` rewritten as the public Lutherverse hype doc: placeholder title card, badge row, vision sections (cosmic-horror + FFX + KH2 + DkS + Bloodborne inspiration, keyblades + Keybearer class, coop modes, save zones, towns, sphere grid, 200-floor descent), roadmap table, alpha-tester CTA, cameo list (O'aka, Patches, Sora, Doll, plus light-touch nods), attribution chain
- `THIRD_PARTY_NOTICES.md` covering the GPLv3 attribution chain (Watabou → Evan → QSR → CPDU), libGDX Apache 2.0, RoboVM/MobiVM GPLv2-with-Classpath-Exception, SPD-classes
- `docs/superpowers/specs/2026-07-21-custompixeldungeonultimate-design.md` — original Sub-A design spec (six locked scope decisions + six-sub-project decomposition)
- `docs/superpowers/plans/2026-07-21-cpdu-sub-a-fork-infrastructure.md` — Sub-A implementation plan (6 tasks)
- `docs/assets/lutherverse-title.svg` — placeholder title card (cosmic-horror-adjacent purple/gold, uzumaki spirals — replaceable with real hero art)
- This `CHANGELOG.md` and [`PROJECT-STATUS.md`](PROJECT-STATUS.md) as living project-state documents

### Changed

- Default branch renamed `margarita` (inherited from CPD's cocktail-codename scheme) → `main` on both local and origin, per LO decision during Sub-A execution. Remote-tracking branches for upstream-cpd stay on `margarita` (that's QSR's naming, we don't own it); upstream-spd stays on `master` (Evan's naming). Sub-B will `git merge upstream-cpd/margarita` and `upstream-spd/master` into our `main`.

### Deferred (not this sub-project)

- Any change to game code under `core/`, `android/`, `desktop/`, `ios/`, `services/`, `SPD-classes/`, or `marketplace/` — Sub-A is docs-only. Game code changes begin in Sub-B (upstream sync to SPD v3.3.8).
- CI setup (GitHub Actions, etc.) — deferred to Sub-G.
- iOS build — retained in `settings.gradle` for future reactivation, currently unmaintained on paper (see README platform support table).

### Known caveats

- The annotated tag `cpd-sync-base-2025-08-15` was pushed to origin during Sub-A Task 2 (implementer overreach — the plan brief said "no push" at Task 2, push was for Task 6). Effect: Task 6's tag-push step becomes a no-op. Documented and accepted; no corrective delete on origin (would double the GitHub webhook events for no user-visible difference).

---

## Historical (pre-fork)

Everything before `c97fb832` is CPD (QuasiStellar) history and, further back, SPD (Evan) history — both maintained upstream. Their changelogs live in their respective repos:

- [Custom Pixel Dungeon changelog](https://github.com/QuasiStellar/custom-pixel-dungeon/blob/margarita/CHANGES.md) *(if present)*
- [Shattered Pixel Dungeon changelog](https://github.com/00-Evan/shattered-pixel-dungeon/tree/master/core/src/main/assets/messages/journal) *(in-game changelog assets)*
