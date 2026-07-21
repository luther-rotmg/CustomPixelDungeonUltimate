# CustomPixelDungeonUltimate — design spec

**Date:** 2026-07-21
**Author:** LO (via brainstorming session)
**Status:** approved, pre-implementation
**Fork base:** [QuasiStellar/custom-pixel-dungeon](https://github.com/QuasiStellar/custom-pixel-dungeon) @ `c97fb83` (2025-08-15)
**Sync target:** [00-Evan/shattered-pixel-dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) @ `v3.3.8` (2026-03-19)
**Target repo:** `luther-rotmg/CustomPixelDungeonUltimate` (public, GPLv3)
**Local location:** `C:\Users\minec\Documents\Projects\CustomPixelDungeonUltimate\`

---

## 1. Project overview

CustomPixelDungeonUltimate (CPDU) is a public GPLv3 fork of QuasiStellar's Custom Pixel Dungeon (CPD), itself a fork of Evan Debenham's Shattered Pixel Dungeon (SPD). CPD provides a data-driven modding framework layered over SPD; CPDU takes that framework current with upstream SPD and extends it with a broader Java-hook API, then ships three flagship gameplay-mode mods that consume the extended API.

The v0.1 milestone is a single tagged release containing: upstream-synced base game + extended modding platform + three shipped gameplay-mode addons (God Mode, Hard Mode, Bonfire Mode — the last is trademark-safe rename of what we've been calling "Dark Souls Mode" internally).

## 2. Locked design decisions

Six scope-shaping decisions were locked during the brainstorming session that authored this spec. They are re-listed here so downstream sub-project specs can reference them unambiguously.

| # | Decision | Rationale |
|---|---|---|
| 1 | Extend CPD's modding framework with Java hooks (a broad extension surface, not stay-within-JSON-only) | Enables ambitious mods like Bonfire Mode that cannot be expressed in pure data |
| 2 | Public GitHub fork under `luther-rotmg`, GPLv3-compliant | User-visible marketplace/DLC framing requires distribution + attribution + docs |
| 3 | Monorepo: all addons live under `marketplace/<addon>/` including any Java code they ship | Matches CPD's shipped `marketplace/` pattern; simplest Gradle composite build; single source of truth |
| 4 | Broad modding platform API from day one (all 8 hook categories) | Aligned with the "super awesome" ambition — third-party modders should be able to build meaningful mods without touching the fork itself |
| 5 | iOS on paper only (Gradle target retained, never tested or shipped) | Matches CPD's current posture; avoids doubling the Sub-B merge surface + Sub-C API constraints |
| 6 | v0.1 release gate = Sub-A + Sub-B + Sub-C + all three flagship mods shipped together | The DLC-store framing needs a launch identity; a bare-sync release without content wouldn't land |

## 3. Sub-project decomposition

Six sub-projects for v0.1; five more deferred to v0.2+. Each sub-project gets its own frontier-authored spec → plan → implementation cycle.

```
Sub-A (Fork infra)
     │
     ▼
Sub-B (Upstream sync — "Monolith Task 1")
     │
     ▼
Sub-C (Broad modding platform API)
     │
     ├────────┬────────┐
     ▼        ▼        ▼
   Sub-D    Sub-E    Sub-F           (parallelizable once C's API is stable)
   God      Hard     Bonfire
   Mode     Mode     Mode
     │        │        │
     └────────┼────────┘
              ▼
     v0.1 tagged release
```

Critical path is `A → B → C → F`. Sub-D and Sub-E can be built in parallel with Sub-F once Sub-C's first hook category lands and gets validated.

### Deferred sub-projects (v0.2+)

| Deferred | Purpose |
|---|---|
| Sub-G | `.github/workflows/` CI — build verification, artifact upload on tag |
| Sub-H | Marketplace UI polish — featured DLC categorization, filters, search |
| Sub-I | Mod signing + integrity + attribution surface for community-contributed mods |
| Sub-J | iOS reactivation — Gradle target reactivation, RoboVM/multi-OS-engine validation, App Store distribution decisions |
| Sub-K | Co-op multiplayer — nickname + room-code model, no player-hosted servers (implies a small hosted matchmaking/relay backend). Warranting its own brainstorming pass when v0.2 stabilizes. |

## 4. Sub-A — Fork infrastructure

### 4.1 Scope

Create the fork on GitHub, clone locally, wire up remotes, verify the base game builds on Android and Desktop, and import this design document as the first spec.

### 4.2 Deliverables

- `gh repo fork QuasiStellar/custom-pixel-dungeon --clone=false --fork-name=CustomPixelDungeonUltimate` under `luther-rotmg`, public
- Local clone at `C:\Users\minec\Documents\Projects\CustomPixelDungeonUltimate\`
- Remotes:
  - `origin` → `luther-rotmg/CustomPixelDungeonUltimate`
  - `upstream-cpd` → `QuasiStellar/custom-pixel-dungeon` (CPD modding-framework updates)
  - `upstream-spd` → `00-Evan/shattered-pixel-dungeon` (base game updates)
- Fork base pinned to CPD commit `c97fb83` (2025-08-15) via an explicit tag `cpd-sync-base-2025-08-15`
- `README.md` rewritten with:
  - Project intro (fork-of-fork chain)
  - GPLv3 attribution to Evan + QSR
  - Install instructions for Android + Desktop
  - "iOS: known unmaintained" disclosure
  - Link to `docs/superpowers/specs/2026-07-21-custompixeldungeonultimate-design.md`
- `LICENSE.txt` — GPLv3, retained from CPD
- `THIRD_PARTY_NOTICES.md` — attribution chain (Evan → QSR → CPDU), libGDX + RoboVM notices
- `docs/superpowers/specs/2026-07-21-custompixeldungeonultimate-design.md` — this document, imported from scratchpad
- Gradle sanity build verified:
  - `./gradlew android:assembleDebug` succeeds
  - `./gradlew desktop:dist` succeeds
  - iOS deliberately NOT built (matches Sub-A scope decision)

### 4.3 Acceptance

- Public GitHub repo exists, populated, discoverable
- Local clone builds Android + Desktop cleanly
- Design doc committed
- All three remotes configured and fetched
- No push to `upstream-cpd` or `upstream-spd` (read-only)

## 5. Sub-B — Upstream sync ("Monolith Task 1")

### 5.1 Scope

Bring the fork's `core/` current with Evan `v3.3.8`. Approximately 7 months of upstream drift (2025-08-15 → 2026-03-19) to integrate.

### 5.2 Strategy — merge-in-slices

Straight `git merge upstream-spd/master` would produce one gigantic un-auditable merge commit. Baseline-rewrite would burn the v0.1 timeline before it starts. The chosen strategy is merge-in-slices:

1. **Diff-triage phase (frontier, inline, small).** Compute `git log evan..cpd` and `git log cpd..evan` between fork-point and tips. Categorize Evan's commits into three buckets:
   - Additive — new items/mobs/levels/patches — likely clean merges
   - Modifications to code CPD extended for modding — conflict-prone, hand-review each
   - Structural refactors — highest risk, may force adjustments to CPD hook design in Sub-C
2. **Anchor selection.** Pick 3–5 major-version anchors from Evan's history between 2025-08 and 2026-03-19. Each anchor becomes a rollback point.
3. **Per-anchor merge loop.** For each anchor:
   - `git merge <anchor>` onto working branch
   - Resolve conflicts
   - `./gradlew android:assembleDebug && ./gradlew desktop:dist` must both pass
   - Run modmark regression (see 5.3) — every CPD-shipped mod loads
   - Commit with anchor tag in message
4. **Escalation.** If any anchor's conflicts prove intractable at a hook site, defer that anchor and open a Sub-C hook-design ticket. Do not force-merge broken hook code.

### 5.3 Modmark regression

CPD ships 30+ mods in `marketplace/` (1,553 files). These are our integration test suite for the modding hooks — if they all still load and apply overrides post-sync, the framework didn't break. Automated:

- Headless boot iterates every `marketplace/<mod>/` directory
- Parses `mod_info.json` — assertion: no parse errors
- Executes registration — assertion: no unregistered classes, no missing overrides, no mod-load exceptions
- Reports pass/fail per mod

Any regression is a Sub-B blocker.

### 5.4 Acceptance

- Fork's `core/` runs Evan `v3.3.8` code semantics
- All 30+ CPD-shipped marketplace mods load cleanly
- Android APK + Desktop JAR build without warnings introduced by the merge
- Each anchor commit is documented; final merge history is auditable
- One integration playthrough: canonical seeded warrior run to depth 25 completes without hangs/crashes

### 5.5 Explicit non-goals for Sub-B

- Save-format compatibility with vanilla SPD saves (players start fresh saves)
- Re-adding Evan's `docs/` folder (CPD removed it; we accept staying-removed; Sub-A already writes our own README)
- Chasing CPD's HEAD if CPD gets new commits during our sync — we pin to `c97fb83` and address CPD's newer commits in a v0.2+ pass

## 6. Sub-C — Broad modding platform API

### 6.1 Scope

Extend CPD's `mod_info.json`-driven loader so mods can ship Java code, register new game object classes, and subscribe to game events. Preserve backward compatibility with CPD's shipped JSON-only mods.

### 6.2 Extended manifest schema

Additive on top of CPD's existing `mod_info.json`:

```json
{
  "name": "Bonfire Mode",
  "version": 1,
  "description": "Souls, bonfires, estus. You die, you retrieve, you learn.",
  "author": "luther-rotmg",
  "license": "GPLv3",
  "gameplay_mod": true,
  "min_cpd_version": 11,

  "requires_api_version": ">=1.0",
  "java_package": "com.luther.cpdu.mods.bonfire",
  "items":       ["items.EstusFlask", "items.HumanityShard", "items.Bonfire"],
  "mobs":        ["mobs.HollowKnight"],
  "heroes":      [],
  "game_modes":  ["BonfireMode"],
  "hooks": {
    "on_hero_death":       "systems.BonfireSystem::onHeroDeath",
    "on_level_transition": "systems.BonfireSystem::onLevelTransition",
    "on_kill":             "systems.SoulsSystem::onKill"
  },
  "ui_extensions": ["ui.SoulsCounter", "ui.BonfireOverlay"],
  "custom_stats":  ["souls_earned", "bonfires_lit", "hollow_deaths"]
}
```

Mods without `requires_api_version` are legacy JSON-only mods and route through CPD's existing loader with no behavior change.

### 6.3 Eight hook categories

| # | Category | Java surface | Manifest field | Content location |
|---|---|---|---|---|
| 1 | Item registration | Extend `Item` (or `MeleeWeapon`/`Armor`/`Potion`/`Wand`/`Ring`/`Artifact`) | `items: []` | `<mod>/src/main/java/.../items/` |
| 2 | Mob registration | Extend `Mob` with AI, stats, drops | `mobs: []` | `<mod>/src/main/java/.../mobs/` |
| 3 | Hero registration | Extend `HeroClass` — adds to new-game hero-select | `heroes: []` | `<mod>/src/main/java/.../heroes/` |
| 4 | Level/room extension | Extend `Level`, `Room`, or `LevelTransitionScene` | `levels: []` (added later) | `<mod>/src/main/java/.../levels/` |
| 5 | UI extension | Add HUD widgets, main-menu entries, config panels | `ui_extensions: []` | `<mod>/src/main/java/.../ui/` |
| 6 | Event bus | Subscribe to game events by static-method reference | `hooks: {}` | anywhere in mod's Java package |
| 7 | Game mode extension | Register a new selectable difficulty/mode with tuning params | `game_modes: []` | `<mod>/src/main/java/.../modes/` |
| 8 | Mod resources & stats | Auto-load sprites/, sounds/, messages.properties + register custom stats persisted per-run and lifetime | `custom_stats: []` + folder convention | `<mod>/sprites/`, `<mod>/sounds/` |

### 6.4 Event bus — initial event catalog

Published centrally by `ModEventBus`. Mods declare subscriptions in `hooks: {"event_name": "ClassName::staticMethod"}`. Handler signature is `static void handle(TypedEventPayload)`.

- **Lifecycle:** `on_new_game`, `on_load_game`, `on_save_game`, `on_game_over`, `on_hero_ascended`
- **Hero:** `on_hero_death`, `on_hero_hit`, `on_hero_heal`, `on_hero_level_up`, `on_hero_starve`
- **Combat:** `on_kill`, `on_mob_spawn`, `on_mob_death`, `on_hit_landed`, `on_hit_dodged`
- **Movement:** `on_level_transition`, `on_room_enter`, `on_tile_step`
- **Items:** `on_pickup`, `on_use`, `on_equip`, `on_unequip`, `on_identify`, `on_curse_break`
- **Turn:** `on_turn_start`, `on_turn_end` — high fire rate, use sparingly

### 6.5 Java code loading — no runtime classloader

Compile-time inclusion via Gradle composite build:

- `settings.gradle` adds one line per marketplace mod: `include ':marketplace:god-mode'`, etc.
- Each mod is a Gradle sub-project with standard `src/main/java/` + `src/main/resources/` layout
- Core game module has `compile` dependency on every marketplace mod → their classes are on the classpath at boot
- Android APK is single-DEX with base + all mods; no `DexClassLoader` gymnastics
- No runtime binary loading, no plugin ABI, no classloader security surface

### 6.6 Backward compatibility

- Legacy JSON-only mods (no `requires_api_version`) route through CPD's existing loader unchanged
- New extended-API mods route through a new `ModRegistry` that reads the manifest, registers Java classes into typed registries, wires event subscriptions, and gates behind the game mode / feature flag if the mod is opt-in
- Both paths coexist; no mod-authoring workflow is broken

### 6.7 Documentation

- `docs/modding-api-v1.md` — full hook catalog, code example per category, event payload types, registration lifecycle
- Reference mod: `marketplace/_reference-hello-world/` — a working mod that exercises every hook category, serving as the modder's copy-paste starting point

### 6.8 Acceptance

- `ModRegistry`, `ModEventBus`, and all 8 registration paths implemented
- `docs/modding-api-v1.md` published and reviewed
- Reference mod loads, all 8 hook categories fire at least once in a headless run
- Automated integration tests: every shipped mod's Java classes exist, every hook target is a valid static method, every declared item/mob/hero class registers into the appropriate registry
- All 30+ legacy CPD marketplace mods continue to load (regression from Sub-B)

## 7. Content addons — Sub-D, Sub-E, Sub-F sketches

Each addon gets its own brainstorming pass + spec before implementation. Sketches are provided here to confirm Sub-C's hook categories cover them and to size v0.1 timeline expectations.

### 7.1 Sub-D — God Mode

**Concept:** cheat/power-fantasy toggle. Hero starts with god-tier loadout, damage capped near zero, autocrits on landing hit.

**Consumes hooks:** item registration (~6 items), hero starter-kit overrides (all base heroes), game-mode extension, event bus (`on_hero_hit` → cap damage, `on_hit_landed` → autocrit).

**Content:**
- Godslayer Sword (+30 dmg, autocurse-immune)
- Aegis Shield (100% block)
- Ring of Divinity (+9 all stats)
- Elixir of Immortality (regen full HP/turn)
- Boots of Teleportation
- Cloak of Truesight
- Hero starter-kit JSONs for each base hero
- Game-mode registration: "God Mode"

**Effort: LOW.** This is Sub-C's first API consumer — validates hook categories 1, 3, 6, 7 end-to-end. Vibe: "not balanced, don't care, have fun."

### 7.2 Sub-E — Hard Mode

**Concept:** vanilla mechanics, tuned up. No new systems, no new items beyond one mob variant class. For players who beat vanilla and want challenge without weird gimmicks.

**Consumes hooks:** game-mode extension, mob-stat overrides via `on_mob_spawn`, level extension for loot/merchant rates, event bus `on_pickup` filter.

**Content:**
- Mob HP × 1.5, damage × 1.3, accuracy × 1.15, XP × 0.9
- Food frequency ↓ 25%
- Healing potions ↓ 30%
- Merchants stock 60% of vanilla
- Champion mob variant — 10% chance per floor, ×2 stats + special affix (Path-of-Exile flavor)
- No new item classes

**Effort: MEDIUM.** Tuning is small; getting the numbers *right* takes playtesting iteration. Champion affix system is a modest new class. Vibe: "same game, respect the danger."

### 7.3 Sub-F — Bonfire Mode (public name) / Dark Souls Mode (internal)

**Concept:** flagship differentiator. Grafts Dark Souls' bonfire / souls / estus system onto SPD's roguelike loop.

**Consumes hooks:** game-mode extension, item registration (Bonfire, Estus Flask, Humanity Shard), mob registration (Hollow Knight elite), event bus (`on_kill`, `on_hero_death`, `on_level_transition`), level extension (guaranteed bonfire per floor), UI extensions (souls counter, bonfire overlay), custom stats (souls earned, bonfires lit, hollow deaths).

**Content:**
- **Bonfire system.** One guaranteed per floor. "Rest at bonfire" → full HP + refill estus + save checkpoint + level-up screen.
- **Souls currency.** Every mob drops souls on kill. Spent at bonfires to level up (player chooses stat).
- **Death & retrieval.** Hero dies → respawns at last-lit bonfire with 0 souls. Soul pile dropped at death location on that floor. One retrieval attempt.
- **Estus Flask.** Replaces vanilla potions of healing. 3 charges per bonfire rest. Upgradeable via found items.
- **Humanity Shard.** Rare currency. Solo v0.1 use: kindle a bonfire permanently (+1 estus charge at that bonfire forever). Multiplayer-invasion semantics deferred to Sub-K.

**Non-goals for v0.1:** covenants, PvP invasions, multiplayer bloodstains, upgradeable estus (max charges only), covenant items.

**MVP fallback (per R3):** if scope creeps past a reasonable ceiling, ship v0.1 with bonfires + souls + basic estus. Defer humanity + estus upgrades + Hollow Knight elite to v0.1.1.

**Effort: HIGH.** Dominates the v0.1 timeline. Realistic estimate: longer than Sub-B or Sub-C. Vibe: "if you die, you learn."

### 7.4 Public naming — trademark safety

The internal name "Dark Souls Mode" is used throughout this spec for clarity. The **public display name is Bonfire Mode** to avoid trademark exposure against FromSoftware / Bandai Namco's "Dark Souls" mark. Sub-F's implementation spec must reflect this; all in-game strings, marketplace `mod_info.json` display name, README references, and screenshot captions use "Bonfire Mode."

## 8. Testing strategy

### 8.1 Regression suites

- **Base-game regression.** Canonical seeded warrior playthrough to depth 25 must complete without hangs / crashes / softlocks. Run after every sub-project merge.
- **Mod-framework regression (Sub-B onward).** All 30+ CPD-shipped marketplace mods must load, register, and apply overrides. Automated headless boot.
- **Extended-API mod tests (Sub-C onward).** Every hook category has at least one test that registers a mod using it and asserts the hook fires. Reference mod `marketplace/_reference-hello-world/` exercises every category.
- **Save/load format regression (Sub-F).** Bonfire Mode adds new save fields. Tests:
  - Save-with-mod → load-with-mod (round-trip)
  - Save-without-mod → load-without-mod (base saves stay compatible)
  - Load-vanilla-save-in-mod-mode → intentional rejection with user-friendly error
  - Load-mod-save-in-vanilla-mode → intentional rejection

### 8.2 Playtest gates

Sub-D, Sub-E, Sub-F cannot ship without a manual playthrough at each mode. Playtest reports live in `docs/playtest/YYYY-MM-DD-<mode>.md`. Not automatable; wall-clock cost accepted.

### 8.3 CI

Deferred to Sub-G (v0.2+). v0.1 ships on manual `./gradlew` builds. Local pre-commit gate is the test suite plus a warrior playthrough on the mode that changed.

## 9. Risk register

| # | Risk | Severity | Mitigation |
|---|---|---|---|
| R1 | Sub-B merge conflicts prove worse than expected | High | Merge-in-slices strategy; each anchor is a rollback checkpoint. Diff-triage phase runs first; escalate to baseline-rewrite if delta is catastrophic |
| R2 | Sub-C API needs redesign mid-implementation because Sub-D reveals a hook gap | Medium | Build Sub-D immediately after Sub-C's first hook category lands — Sub-D is the API's first real consumer. Redesign hook before touching Sub-E/F if needed |
| R3 | Sub-F Bonfire Mode scope creep past reasonable ceiling | High | MVP fallback: bonfires + souls + basic estus. Cut lines: humanity mechanics, upgradeable estus, elite Hollow Knight. Ship v0.1 with MVP; polish in v0.1.1 |
| R4 | Trademark exposure — "Dark Souls" is FromSoftware/Bandai-Namco mark | Medium | Public display name is "Bonfire Mode." Internal spec / dev conversations use "Dark Souls Mode" for clarity; all shipping surfaces use the safe name |
| R5 | iOS silently rots | Low (accepted) | Chose iOS on paper only. Documented as known-unmaintained. Sub-J is the deferred reactivation path |
| R6 | GPLv3 compliance oversight | Medium | Sub-A includes explicit attribution audit: README, LICENSE, THIRD_PARTY_NOTICES.md. Every addon inherits GPLv3 unless explicitly re-licensed; no incompatible-license dependencies without review gate |
| R7 | Save-format incompatibility with vanilla | Medium | Segregated save slots per game mode. Bonfire Mode save not loadable in vanilla, and vice versa. Documented at new-game screen |
| R8 | Humanity Shard designed as multiplayer bait; if Sub-K never ships, becomes dead-weight | Low | v0.1 solo use: kindle bonfire permanently (+1 estus charge at that bonfire). Multiplayer semantics layer on later without invalidating solo function |
| R9 | Playtesting time is un-scoped | Medium | Accept as wall-clock cost. Playtest is checklist-driven; invite community once fork is public |
| R10 | CPD pushes new commits during Sub-B, drifting our sync target | Low | Pin merge base to CPD commit `c97fb83` explicitly. Address CPD's newer commits in a v0.2+ pass |

## 10. Documentation deliverables (part of shipping v0.1)

- `README.md` — project intro, install per platform, GPLv3 attribution to Evan + QSR
- `LICENSE.txt` — GPLv3 (retained from CPD)
- `THIRD_PARTY_NOTICES.md` — attribution chain (Evan → QSR → CPDU) + libGDX / RoboVM notices
- `docs/modding-api-v1.md` — extended-API reference (Sub-C output)
- `docs/game-modes.md` — user-facing description of God / Hard / Bonfire modes
- `docs/playtest/YYYY-MM-DD-<mode>.md` — playtest reports per mode
- `docs/superpowers/specs/2026-07-21-custompixeldungeonultimate-design.md` — this document
- `docs/superpowers/specs/<per-sub-project spec>.md` — one per sub-project (frontier authors each before that sub starts)

## 11. Sub-K (multiplayer) — parked notes

Not part of v0.1. Parked here so the design constraints don't get lost.

- **Model:** nickname + room-code lobby (no accounts). No player-hosted servers.
- **Backend:** small hosted matchmaking/relay service. Candidate stacks: Nakama, PlayFab Realtime, bespoke Node/Go relay. Decision belongs to Sub-K's own brainstorming.
- **Multiplayer-friendly features baked into v0.1:**
  - SPD's deterministic seeded level generator — both players share seed, only actions sync (turn-based tolerant of latency)
  - Humanity Shard's solo function is designed to layer multiplayer semantics on later without breaking solo use
- **Deferred entirely to Sub-K:** matchmaking backend, session lifecycle, action synchronization, desync recovery, PvP invasions, cooperative-only Bonfire Mode variant, chat, spectator mode.
