# Sub-B Pre-Brainstorm Research — Executive Summary

**Generated:** 2026-07-21 by CPDU Sub-B research workflow (7 agents, 3 phases, 422k tokens)
**Full structured data:** the workflow output JSON was retained in the frontier scratchpad, not the repo (too transient to version). The narrative below is authoritative.
**Status:** Ready to inform Sub-B brainstorm — DO NOT execute until LO's 8 brainstorm-questions are answered

---

## The one-line finding

**CPD is on SPD v2.1.0 (frozen since late 2023). Sub-B is a ~2.5-year, 1,417-commit upstream absorption across five SPD minor versions — one of the largest merge projects this stack has taken on.**

## Base-version determination

- **CPD's SPD base = v2.1.0** (high confidence)
- Method: numstat-diff sweep over 8-13 core files across 20+ SPD tags — v2.1.0 shows a clean minimum diff to CPD's HEAD
- Cross-confirmed by CPD's own tag scheme: `v2.0.2-0.6` → `v2.1.0-0.7/0.8/0.9/1.0` → then plain "Mod updates" commits with no SPD-version bump
- CPD divergence character: heavy local mods (Kotlin modding DSL, marketplace, custom services, rewrites of Hero/Char/Talent/Armor/Weapon/HeroClass/Dungeon/Level/Blacksmith) — NOT a rolling merge

## SPD's headline additions since v2.1.0 (roughly)

| Range | Commits | Headlines |
|---|---|---|
| v2.5.x (2024-06 → 2024-10) | 368 | Journal/bestiary/landmarks UI; 4 new trinkets; cursed-wand rework; stone rebalance |
| v3.0.x (2024-10 → 2025-03) | 392 | **Cleric class + spell system + Priest/Paladin subclasses**; 4 ultimate armor abilities; global enemy-AI overhaul |
| v3.1.x (2025-03 → 2025-06) | 195 | Warrior seal rework; gnoll exiles + hermit crabs; region decoration; colorblind palette; iOS 120Hz |
| v3.2.x (2025-06 → 2025-09) | 227 | **Thrown-weapon "sets" rewrite**; mobile edge-to-edge + dynamic island + cutouts; libGDX 1.13; Java 11 baseline |
| v3.3.x (2025-10 → 2026-03) | 235 | **Vault imp-quest area** + grid level builder + laser sentries + investigating AI; **libGDX 1.14 + JDK 17 + R8 full-mode + gradle 9** |

## Conflict surface (1,419 shared files, 25 hotspots)

**Critical hotspots** (both CPD and SPD both rewrote):
- `SPD-classes/.../Bundle.java` — SAVE FORMAT ROOT, same path both sides (43+43 LOC contested) — **any silent merge conflict here corrupts every user's save**
- `.../scenes/InterlevelScene.java` (729+96) — save/load + level transitions
- `.../scenes/GameScene.java` (543+437) — SPD's v3.3.x tilemap pipeline rebuild vs CPD's custom-tile system
- `.../actors/hero/Hero.java` (797+136), `.../actors/Char.java` (481+10), `.../actors/mobs/Mob.java` (443+154) — actor identity + save + AI
- `.../android/AndroidPlatformSupport.java` (254+300) — near-total rewrites on both sides
- `.../Dungeon.java` (532+19), `.../levels/CityLevel.java` (181+147), `.../actors/hero/Talent.java` (661+27), `.../ui/StatusPane.java` (180+202)

**Cold zones** (near-frictionless takeover): `SPD-classes/watabou/glwrap/`, `noosa/particles/`, `noosa/tweeners/`, `noosa/audio/`, `utils/` (except Bundle), root gradle wrappers, `marketplace/**` (pure CPD), `docs/` + `ios/` (SPD-only, safe wholesale import), CPD `custom/` mod-system code.

---

## Merge strategy — INITIAL synthesis (8 slices, 148 tasks)

This was refuted by all 3 verify lenses. Preserved here for reference but see the REVISED numbers below.

| # | Name | SPD range | Severity (initial) | Tasks (initial) |
|---|---|---|---|---|
| 0 | Foundation & bridge scaffolding | v2.1.0 baseline | low | 10 |
| 1 | v2.1.0 → v2.5.4 catchup | v2.1.0..v2.5.4 | medium | 16 |
| 2 | v2.5.x feature layer (Journal, trinkets, wands) | v2.5.0..v2.5.4 features | medium | 14 |
| 3 | v3.0.x — Cleric + spells + armor ultimates + AI | v2.5.4..v3.0.2 | critical | 30 |
| 4 | v3.1.x — Warrior seal + new mobs + colorblind | v3.0.2..v3.1.4 | medium | 16 |
| 5 | v3.2.x — Thrown weapons + edge-to-edge + engine | v3.1.4..v3.2.5 | critical | 24 |
| 6 | v3.3.x — Vault + grid builder + libGDX 1.14 + JDK17 | v3.2.5..v3.3.8 | critical | 26 |
| 7 | Marketplace + modding-DSL reconciliation | post-v3.3.8 | medium | 12 |

## Adversarial verify — 3/3 REFUTED (this is ultracode doing its job)

### Slice-boundary lens (refuted)
- Slices 3, 5, 6 are each too big to review atomically → split into 3a/3b/3c, 5a/5b/5c, 6a/6b/6c
- Slice 1/2 boundary overlaps (both cover v2.5) — either combine into one v2.1..v2.5.4 slice or rewrite honestly as engine-vs-feature cherry-pick with budgeted per-commit classification cost
- No slice defines explicit acceptance criteria — every slice needs a mandatory acceptance block (build cmd, save-load smoke, marketplace-pack boot smoke, API-diff report)
- Slice 0's Bundle bridge is premature-abstracted — ship SCAFFOLDING in Slice 0, ship each version's actual translator IN the slice that drops that version's format
- Slice 7 as deferred marketplace-fix creates a release-freeze window of 100+ tasks the strategy under-acknowledges — inline patches for CPD-shipped default packs into whichever slice breaks them

### Conflict-forecast lens (refuted)
- Slice 1 at "medium/180 files" too optimistic — Hero/Char/Actor tuning against CPD-rewritten files = semantic conflicts surface weeks later as balance regressions → bump to **high**
- Slice 2 hides a critical enum-ordinal-drift vector in cursed-wand rework — insert-in-middle breaks every save that serialized ordinals → bump to **high** with mandatory `.ordinal()` vs `.name()` grep audit
- Slice 4 with Talent.java hotspot re-lit deserves **high**, not medium
- Bundle bridge stress happens in Slices 3/5/6 (Cleric bundle keys / thrown-weapon migration / Vault storage + pre-v2.5.4 drop), NOT in Slice 1 as claimed
- Level generators unforecast in Slices 1, 3 — SPD engine tweaks land where CPD's custom-tile renderer is spliced in
- Boss AI files (Tengu, Yog-Dzewa, DwarfKing, Goo, DM-300) never explicitly forecast — Slice 3's AI-overhaul folds bosses into "40 mob files" but per-boss AI conflicts are individually harder
- Slice 7 at 12 tasks under-scales with API-drift cascade

### Task-count lens (refuted — 50% under-estimate)
- Slice 0 realistic: **18-22** (bridge needs 3 per-drop translators × 3-5 tasks each = 10-15 just for the bridge, before rename tool + CI + API-diff + smoke-boot)
- Slice 3 realistic: **50-60** (20+ per-file beads + AI propagation to ~40 mobs + enum audit + first live bridge exercise + spell-system runtime tests × 4 ultimates × 2 subclasses + DSL exposure decisions)
- Slice 5 realistic: **32-38** (thrown-weapon per-subclass migration + marketplace thrown-weapon pack patches + edge-to-edge per-scene + 5-hero class-balance playtest + save-compat drop #2)
- Slice 6 realistic: **38-46** (R8 obfuscation-rule iteration loop 3-5 rounds + grid-builder architecture decision + investigating-AI per-CPD-mob retrofit + Vault runtime E2E)
- Slice 7 realistic: **25-30** (per-pack audit + patch + boot smoke × ~6+ shipped packs + DSL extensions + regression matrix + R9 backport budget)
- Systematic omissions: CHANGELOG+PROJECT-STATUS update per slice (8-15 tasks missing), manual playtest smoke per gameplay slice (10-20 missing), strings-diff-and-merge per slice (5 missing), iOS parity if shipping (+15)

**REVISED TOTAL: 220-290 tasks (initial 148 was ~50% under)**

## Risk register (10 risks, ranked)

| # | Risk | Lik | Impact | Key mitigation |
|---|---|---|---|---|
| R1 | Save-format corruption (Bundle.java, Belongings, Badges, Mob, Tengu, SPDSettings) | H | CRIT | Slice 0 Bundle-bridge scaffolding + per-slice save-roundtrip regression bead |
| R2 | Marketplace mod breakage (packs reference SPD internals by name/ordinal) | H | H | API-diff script from Slice 0 + Mod Compatibility banner + per-pack audit beads |
| R3 | Kotlin modding DSL API-surface drift (v2.1.0-designed, no repr for Cleric/Vault/investigating/thrown-sets) | H | M | Slice 7 DSL extension pass + versioned DSL compat matrix |
| R4 | Custom-tile system architectural collision (CPD renderer vs SPD v3.3 tilemap rebuild) | H | H | LO-level decision UPFRONT — cannot be worker-bead delegated |
| R5 | gradle9 + JDK17 + libGDX1.14 + R8-full-mode stack (each hostile to libGDX individually) | M | H | Slice 6 ships SPD's exact proguard/R8 rules verbatim + headless Android smoke-boot in CI |
| R6 | iOS/RoboVM drift (CPD may not have kept iOS green; adopting SPD v3.2 dynamic-island + v3.1 metalangle) | M | M | LO decides iOS ship-yes/ship-no UPFRONT; if no, build-flag gate all iOS-touching hunks |
| R7 | Translation drift (5 versions of string-key renames) | H | L | Per-slice strings-diff report + two-file merge (SPD base + CPD overlay) |
| R8 | HeroClass enum-ordinal serialization drift (adding Cleric changes enum layout, breaks ordinal-serialized saves) | M | CRIT | Slice 3 begins with mandatory `.ordinal()` vs `.name()` grep audit, refuse merge until name-based |
| R9 | Cross-slice regression discovery forces re-opening earlier slices | M | M | Explicit 20% Slice 7 overhead + Rework Loopback control-flow edge in the plan |
| R10 | Save-compat cliff shocks CPD users (v2.1.0-era saves get unbounded compat-drop when merge ships) | H | H | Bundle bridge upcasts in-memory + 'save conversion' UI on first launch + 2-week release-notes lead |

---

## 8 brainstorm questions for LO — must answer these before Sub-B begins

1. **Slice count discipline**: 8 slices (recommended) or collapse 1+2 and 4+5 into 6 fatter PRs?
2. **Cleric class ship-status**: first-class CPD hero, marketplace-optional pack, or excluded? **This is the single biggest LO call in the project.** Recommendation: first-class.
3. **Custom-tile system**: keep CPD's custom-tile layer atop SPD v3.3.x's rebuilt tilemap pipeline (preserves CPD identity, adds indirection), OR migrate CPD to SPD's pipeline and delete the layer (cleaner, marketplace tile packs break)?
4. **Save-compat policy**: hard-drop pre-CPD-v2.1.0-1.0 (matches SPD's cliff cadence) OR ship Slice 0 bridge preserving saves back to CPD's oldest tag (~10 task overhead)? Recommendation: bridge.
5. **Marketplace mod policy during merge**: gate merge on all packs booting green (safest, delays release), Mod Compatibility banner (honest, faster), or ship silently and fix post-hoc (burns trust)? Recommendation: banner.
6. **iOS support scope**: is CPD still shipping iOS? If yes, +~15 tasks across Slices 5+6. If no, everything iOS-touching goes behind a build flag.
7. **Modding-DSL freeze**: freeze DSL API during Slices 1-6 (mod authors get stable target, no new features) OR evolve slice-by-slice (mod authors chase moving target)?
8. **Vault-area home**: default quest progression OR marketplace-optional content pack?

Recommend LO answers these BEFORE opening the Sub-B brainstorm, so the brainstorm can focus on execution shape rather than architecture calls.

---

## What Sub-B's actual implementation plan should incorporate

- Use the REVISED 220-290 task total, not the initial 148
- Adopt the sub-slice splits (3a/3b/3c, 5a/5b/5c, 6a/6b/6c) — each ~10-14 tasks and individually PR-reviewable
- Mandatory per-slice acceptance block: build passes, headless Android smoke-boot for engine-touching slices, save-roundtrip regression for save-touching slices, marketplace-pack boot smoke, API-diff report zero unexpected removals
- Slice 0 ships bridge SCAFFOLDING only; per-version translators ship IN the slice that drops that version
- Inline marketplace-pack patches for CPD-shipped default packs into whichever slice breaks them (lifts release-freeze window)
- CHANGELOG.md + PROJECT-STATUS.md update per commit as first-class task (per LO's changelog-cadence memory rule)
- After Slice 1 lands, re-estimate Slices 2-7 with actual conflict-resolution velocity data — merge plans should self-correct at first empirical data point rather than riding the initial guess
