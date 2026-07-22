# Sub-B Design Spec: CPD → SPD v3.3.8 Upstream Sync

**Status:** Approved by LO on 2026-07-22 via brainstorming pass; ready for per-slice implementation plans.

**Author:** LO
**Predecessors:** [Sub-A design spec](../specs/2026-07-21-custompixeldungeonultimate-design.md) (fork infrastructure, shipped 2026-07-21), [Sub-B preliminary research](../research/sub-b-preliminary-research.md) (workflow output, verify pass caught 50% task under-estimate)

---

## 1. Overview

Sub-B brings CustomPixelDungeonUltimate (Lutherverse) from its SPD v2.1.0 base (frozen May 2023 when QuasiStellar last synced CPD with SPD upstream) up to SPD v3.3.8 (March 2026). The delta is approximately 1,417 upstream commits across five SPD minor versions (v2.5, v3.0, v3.1, v3.2, v3.3).

CPD is a heavy-mods fork, not a rolling merge of upstream. Beyond the mechanical `com.shatteredpixel.shatteredpixeldungeon` → `com.qsr.customspd` package rename, QuasiStellar (a) added a Kotlin-based `modding/` DSL layer over SPD's Java engine, (b) added a top-level `marketplace/` tree containing 30 shipped mod packs, (c) added CPD-owned `services/` at repo root, and (d) rewrote every load-bearing core class (Hero, Char, Talent, Armor, Weapon, HeroClass, Dungeon, Level, Blacksmith). The upstream sync must preserve these local additions while absorbing SPD's engine and content evolution.

The absorption strategy is merge-in-slices: fourteen tag-aligned pull requests landing sequentially on `main`, each individually reviewable, each with mandatory acceptance criteria. No branch-long freeze on CPD-side maintenance; intermediate patches can land between slices if urgently needed.

## 2. Locked design decisions

Eight architectural questions were resolved during the 2026-07-22 brainstorming pass. These are binding for Sub-B implementation and any deviation is a re-brainstorm trigger.

1. **Custom-tile system:** SPD v3.3.x tilemap edits win. CPD's tile mods (which extend `CustomTilemap` for four boss-level classes plus marketplace tile packs) get re-applied on top of SPD's rewrite. This treats SPD's tilemap pipeline as the authoritative base and CPD's additions as an additive layer, rather than fighting a merge conflict at every SPD tilemap hunk.
2. **Cleric class:** first-class Lutherverse hero, default-available in hero-select. The Cleric ships with Holy Tome artifact, spell-selection UI, quick-cast, Priest and Paladin subclasses, and integrates into all existing balance work.
3. **Save-compat policy:** Slice 0 ships bridge scaffolding (extension points, version-detection code, test harness with pinned save fixtures). Per-version translators land in the slice that drops that version's save format. Preserves save-slots back to CPD's oldest tag.
4. **Marketplace-mod policy:** full gate. The Sub-B merge tag cannot cut until every one of the 30 shipped marketplace packs boots green under a smoke test. No Mod Compatibility banner listing broken packs; no ship-and-fix-later.
5. **Modding-DSL evolution:** freeze the Kotlin `modding/` DSL API through Slices 1-6c. DSL v2 (exposing spells, vault rooms, investigating AI, thrown-weapon sets) lands in Slice 7. Mod authors get a stable API target during the merge.
6. **iOS support:** deferred entirely. QSR deleted the `ios/` directory in May 2023; the `:ios` entry in `settings.gradle` is a leftover pointing at nothing. Slice 0 (or Slice 6a as gradle-housekeeping) deletes the entry. iOS resurrection, if ever pursued, becomes a post-v0.1 sub-project.
7. **Vault imp-quest area:** first-class content. Ships as part of the default quest progression, not marketplace-optional. Cross-cutting mechanics (skeleton key, escape crystal, spyglass trinket, investigating AI state) land as base-game features.
8. **Slice count:** 14 slices with sub-splits (3a/3b/3c, 5a/5b/5c, 6a/6b/6c). The adversarial verify pass showed the original 8-slice plan produced individually-unreviewable PRs at Slices 3, 5, and 6; the sub-splits keep each slice at 10-26 tasks (Slice 7 at 40-60 as the pack-gate closer).

## 3. Success criteria

Sub-B is closed when all of the following are true simultaneously on `main`:

1. `git log --oneline upstream-cpd/margarita..main` shows the full slice sequence, each with its own merged PR and each PR passing its acceptance block.
2. `./gradlew android:assembleDebug` and `./gradlew desktop:release` both pass without errors.
3. A pinned save-slot from CPD's v2.1.0-1.0 tag loads through the compat bridge, plays 10 turns, saves, and reloads without corruption.
4. All 30 shipped marketplace packs boot without exception under a `--no-daemon` gradle run using the CPD mod-loader smoke script (green-gate).
5. The Cleric hero is selectable in the default hero-select UI and completes a run.
6. The Vault imp-quest area is reachable in the default quest progression and completes.
7. Kotlin modding DSL v2 exposes spells, vault rooms, investigating AI, and thrown-weapon sets; documentation lands in `docs/modding-api-v2.md` before merge tag.
8. Release notes documenting the save-compat cliff for CPD-v2.1.0-era users are published at least 2 weeks before the merge tag.

## 4. Non-goals

Explicitly out of scope for Sub-B:

- iOS support (deferred; `:ios` entry gets deleted, no RoboVM/edge-to-edge work)
- CI setup (Sub-G, deferred)
- Any Lutherverse-original content (200-floor generator, keyblades, coop, cameos, cosmic-horror biomes, story spine — all post-v0.1 wave, brainstormed separately after Sub-B ships)
- Full library-notice expansion in `THIRD_PARTY_NOTICES.md` (Apache/BSD/MIT deps like FreeType, LWJGL, Kotlin, Ktor, SLF4J, org.json, gdx-controllers, androidx.multidex; scheduled as Slice 7 sub-task before first alpha binary)
- Removal of CPD's `com.qsr.customspd` package namespace (Sub-B preserves the namespace forever; rename tool exists for merge convenience, not for full renaming)

## 5. Architecture: 14 slices

Each row is one PR. Task estimates are Sub-B-specific and use the beads-pipeline sizing model (see [Sub-A execution log](https://github.com/luther-rotmg/CustomPixelDungeonUltimate/blob/main/CHANGELOG.md) for how those estimates worked out empirically).

| # | Name | SPD range | Headline content | Est. tasks |
|---|---|---|---|---|
| 0 | Foundation | *(no upstream advance)* | Package-rename tool (both directions), Bundle-level save-compat bridge scaffolding + fixture-based test harness, API-diff auditor CLI, CI matrix (linux + windows + headless Android smoke-boot), delete `:ios` settings.gradle entry, per-slice acceptance-template file | 18-22 |
| 1 | v2.1 → v2.5 catchup | v2.1.0..v2.5.4 | Four minor-versions of accumulated tuning + engine tweaks (368 SPD commits in v2.5.x window alone plus the v2.1→v2.5 backlog). First live exercise of the compat bridge validates Slice 0 fail-fast. Hero.java / Char.java / Actor.java hotspot files each get their own per-file conflict-resolution bead. | 22-26 |
| 2 | v2.5 features | v2.5.0..v2.5.4 feature layer | Journal + bestiary + landmarks + custom-notes catalog UI, WndUpgrade confirmation window, 4 new trinkets (salt cube, vial of blood, shard of oblivion, chaotic censer), trinket-energizing partial refund, complete cursed-wand effect rework with new rare outcomes (shapeshift, supernova, sinkhole, gravity chaos). Mandatory `.ordinal()` → `.name()` enum-audit gate before merge closes. | 18-22 |
| 3a | Cleric era: class | v2.5.4..v3.0.2 (class subset) | Cleric hero class + Holy Tome artifact + spell-selection window + quick-cast + Priest / Paladin subclasses. HeroClass surgical graft (~345 lines of CPD divergence to reconcile against ~2 lines of SPD change). Full enum-serialization audit fires here as an acceptance requirement (R8). Save bridge first-live-exercise for pre-v2.3.2 saves. | 20-24 |
| 3b | Cleric era: ultimates | v3.0.2 (ultimate armor subset) | Four ultimate armor abilities: Ascended Form, Divine Intervention, Trinity (spirit / mind / body forms per equipment type), Power of Many (life link + stasis + beaming ray). Runtime tested per subclass × ultimate combination. | 12-16 |
| 3c | Cleric era: AI overhaul | v3.0.2 (AI subset) | Global enemy-AI upgrade: target swapping toward recent attackers, pursuit of unseen damage sources. Retrofitted through every CPD-customized mob (~40 files). Boss AI files (Tengu, Yog-Dzewa, DwarfKing, Goo, DM-300) each get individual per-file bead review because their AI overrides are idiosyncratic. | 20-26 |
| 4 | v3.1 grab-bag | v3.0.2..v3.1.4 | Warrior seal overhaul (shielding no longer purely passive, broken-seal transfer eased, talent rebalance: Provoked Anger / Liquid Willpower / Lethal Defense / Hearty Meal). New enemies: gnoll exiles in prison, hermit crabs in caves. Region decoration additions across all five biomes. Colorblind palette pass. HP-bar tweaks + dodge-source indicator. Talent.java hotspot re-visited. iOS metalangle backend for 120Hz skipped per Q6. | 18-22 |
| 5a | v3.2 toolchain | v3.1.4..v3.2.5 (toolchain subset) | Java 11 baseline, libGDX 1.13.6, target SDK 35, Android min raised to 5.0, gradle / AGP bump. Acceptance = build green on both platforms + headless Android smoke-boot + all 30 marketplace packs still compile (no functional breakage yet, just compile). | 10-14 |
| 5b | v3.2 thrown weapons | v3.2.5 (thrown-weapon subset) | Thrown-weapon rewrite into upgradeable "sets" with shared enchants / glyphs / curses, tier-scaled damage rebalance across all five tiers, ID mechanics, liquid-metal repair path. Save migration from per-instance thrown-weapon format to set format. Save-compat pre-v2.4.2 drop lands here. Bridge exercises a fundamentally new migration shape. | 16-20 |
| 5c | v3.2 mobile edge-to-edge | v3.2.5 (mobile subset) | Complete iOS/Android edge-to-edge inset support: dynamic-island detection, camera cutouts, dark inset bars that block input, adaptive status / menu / buff bars. iOS-specific bits go behind build flags (per Q6 defer). | 10-14 |
| 6a | v3.3 engine | v3.2.5..v3.3.8 (engine subset) | libGDX 1.14.0, JDK 17 baseline for Windows / Linux builds, gradle 9.x, R8 full-mode enabled. Ships SPD's exact proguard / R8 rules verbatim plus CPD-specific reflected-class extensions (modding DSL Kotlin metadata, marketplace mod entry points). Iteration budget for R8 obfuscation-rule loop (3-5 rounds expected based on libGDX historical behavior). Headless Android smoke-boot is the acceptance gate. | 14-18 |
| 6b | v3.3 Vault + tilemap | v3.3.8 (content subset) | Vault imp-quest area (skeleton key, escape crystal, cracked spyglass trinket, custom vault rooms, inventory storage / retrieval, key-locked garden + magic-well rooms). Grid-based level builder tied into the vault, adaptive room placement, 6 treasure-room types. Adopt SPD v3.3.x tilemap base wholesale + re-apply CPD's additive tile mods on top (per Q3 decision) — CPD's tile mods extend `CustomTilemap` for `CavesBossLevel`, `CityBossLevel`, `HallsBossLevel`, `LastLevel` plus marketplace tile packs. | 20-26 |
| 6c | v3.3 investigating AI + polish | v3.3.8 (polish subset) | Investigating AI state retrofit into every CPD-mod mob (adds a new `Mob.AiState` enum value that all mob subclasses must handle). Chalice of Blood rework (triangular damage range affected by all damage reduction, clear pre-reduction death-percent display). Hero randomize buttons on hero-select and hero-progression, blinking reminder animation, fully-randomized-victory badge. Laser sentries + 2 new hazards. DM-300 rockfall damage. Tengu spawn delay + shocker turn alignment. Hostile champion scaling by depth. Save-compat pre-v2.5.4 drop lands here. Third bridge exercise. | 14-18 |
| 7 | Marketplace + DSL closure | *(no upstream advance)* | Full 30-pack green-gate: audit each marketplace pack against the new SPD API surface, patch as needed, boot-smoke-test each pack, iterate. DSL v2 extension: expose the spell system, vault rooms + trinkets, investigating AI state, thrown-weapon sets. Publish DSL v1 → v2 compat matrix. Full regression matrix: save migration from every prior CPD tag through the bridge, boss fights, quest lines. Release-notes preparation for save-compat cliff (2-week user lead time). Merge tag ships from here. Rework-loopback budget of ~10 tasks for backport patches to Slices 3-6 as regressions surface. | 40-60 |

**Total: 14 slices, 252-328 tasks.**

## 6. Save-compat bridge design

The bridge is the defense against R1 (save-format corruption) and R10 (save-compat cliff for existing CPD users). It lives in `SPD-classes/src/main/java/com/watabou/utils/BundleBridge.java` (new file) with per-version translator classes in `SPD-classes/src/main/java/com/watabou/utils/bridge/` (new subpackage).

**Design shape:**

- `BundleBridge.upcast(Bundle, sourceVersion)` reads a legacy `Bundle`, applies zero or more chained translators (`PreV232Translator`, `PreV242Translator`, `PreV254Translator`), returns a modern `Bundle` ready for the current game code to consume.
- Version detection happens at load time from the save's own `version` field. If the field is missing (very old saves), fall back to a signature-heuristic check on which bundle keys are present.
- Slice 0 ships:
  - `BundleBridge` class with the version-detection + translator-chain entry points
  - Empty translator classes for each pre-drop point (stubs, throw `UnsupportedOperationException` if invoked)
  - Test harness `BundleBridgeTest.java` under `SPD-classes/src/test/`
  - Pinned save fixtures under `SPD-classes/src/test/resources/save-fixtures/` for each historic format (pre-v2.3.2, pre-v2.4.2, pre-v2.5.4, current-CPD-v2.1.0-1.0)
- Slices 3a, 5b, 6c each populate the translator that their respective save-compat drop introduces. Each populates its translator based on the actual SPD deletion diff (what fields SPD removed, what CPD needs to translate them to).

**Regression bead template (mandatory per save-touching slice):**

```
1. Load save fixture from previous format via BundleBridge.upcast().
2. Play 10 turns using SPD's headless dungeon-runner harness.
3. Save the game state.
4. Reload the saved game.
5. Confirm hero state, inventory, current depth, and RNG position all match expected values.
```

Any slice touching bundle-serialized state without this bead does not merge.

## 7. Marketplace-pack gate infrastructure

The gate is the defense against R2 (marketplace mod breakage). Two components:

**API-diff auditor** (`services/tools/api-diff/`, new): a CLI tool that compares two revisions of the CPD Java surface (post-package-rename) and reports every public / protected / package-private symbol that was removed, renamed, or had its signature changed. Runs as an acceptance step in every slice from Slice 1 onward. Any flagged removal without a documented reason blocks the slice.

**Pack boot-smoke harness** (`services/tools/pack-smoke/`, new): a CLI tool that iterates over every pack under `marketplace/`, loads it via the CPD mod-loader in a headless JVM, and reports which packs threw during load. Runs in Slice 5a (compile-only check), in Slice 7 (full green-gate), and on-demand during any slice a maintainer suspects mod breakage.

**Slice 7 pack-audit workflow** (per-pack, ~2-3 tasks each × 30 packs = 60-90 tasks base, offset by ~10 tasks of shared tooling):

1. Run pack boot-smoke against target pack; capture failure trace.
2. Identify the SPD API surface the pack referenced that changed.
3. Patch the pack's Kotlin / JSON to match the new API (or exempt if the pack becomes DSL-v2 rather than compat-updated).
4. Re-run boot-smoke; confirm green.
5. Record pack version bump in `marketplace/<pack>/mod_info.json`.

Community-authored packs (versus CPD-shipped defaults) are also gated. If a community pack cannot be reached / patched, Slice 7 either bundles a working fork or removes the pack from the marketplace with a documented deprecation notice.

## 8. Modding DSL evolution

**During Slices 1 through 6c:** the Kotlin `modding/` DSL API surface is frozen. No new class shapes, no new fields, no new methods. Mod authors get a stable target during the six-month disruption window.

**In Slice 7:** DSL v2 lands. New API surface:
- `HeroConfig` gains `spellSelection`, `heroSubclass = "priest" | "paladin"` (Cleric-related)
- `CustomLevelLayout` gains `vaultRooms`, `investigatingAiTolerance`
- `ItemDistribution` gains `thrownWeaponSet` (replaces per-instance thrown fields)
- New DSL entry: `NpcCameo` for future character-cameo framework hooks (skeleton only in Sub-B; populated in the vision-wave sub-project that authors cameo content)
- `DungeonLayout` gains `savezoneFrequency` and `savezoneHealsEnabled` (skeleton only; wired live by the save-zones sub-project)

**Compatibility matrix:** DSL v1 packs continue to work forever. They cannot use v2-exclusive fields. DSL v2 packs cannot run on pre-Sub-B CPD. Both matrices documented in `docs/modding-api-v2.md`.

## 9. Per-slice acceptance criteria template

Every slice's PR must show all of the following green before merge:

1. **Build passes:** `./gradlew android:assembleDebug` and `./gradlew desktop:release` both exit 0.
2. **Headless Android smoke-boot:** the APK from step 1 launches in the Android emulator, reaches the title screen, exits cleanly. Runs in CI as part of the Slice 0 CI matrix.
3. **Save-roundtrip:** if the slice touches any bundle-serialized state, the regression bead from Section 6 passes.
4. **Marketplace-pack boot smoke:** the pack-smoke harness reports zero unexpected failures against the current pack set. In Slices 1-6, "unexpected" means "a pack that was booting green in the previous slice now fails without a documented reason." In Slice 7, "unexpected" means "any failure at all."
5. **API-diff report:** the API-diff auditor reports zero unexpected public-symbol removals or renames beyond the set documented in the slice's PR description.
6. **CHANGELOG + PROJECT-STATUS updated:** per LO's changelog-cadence rule, both files update in the same commit as the slice's headline work.
7. **Enum-serialization audit (Slice 2, 3a, 6c):** grep for `.ordinal()` in save-write sites confirms zero uses on serialized-to-Bundle enums.

Any slice whose PR omits proof of these gates does not merge.

## 10. Risk register

Ten risks carried forward from the pre-brainstorm research, plus two new ones added by LO's marketplace-full-gate decision.

| # | Risk | Lik | Impact | Mitigation |
|---|---|---|---|---|
| R1 | Save-format corruption (Bundle contested on both sides for pre-v2.3.2 / pre-v2.4.2 / pre-v2.5.4 drops) | H | Critical | Slice 0 bridge scaffolding + per-slice save-roundtrip regression bead |
| R2 | Marketplace mod breakage (30 packs reference SPD internals that change) | H | High | API-diff auditor per slice + full 30-pack green-gate in Slice 7 (per Q5) |
| R3 | Kotlin DSL API-surface drift (v2.1.0-designed, no representation for Cleric / Vault / investigating AI / thrown-sets) | H | Medium | DSL freeze Slices 1-6c + DSL v2 extension in Slice 7 + versioned compat matrix (per Q7) |
| R4 | Custom-tile system architectural collision | (retired) | (retired) | Reframed by prep-findings: same abstraction on both sides. Q3 chose "SPD wins, re-apply CPD mods". Slice 6b executes; no LO decision remains. |
| R5 | Gradle 9 + JDK 17 + libGDX 1.14 + R8 full-mode stack (each hostile to libGDX individually) | M | High | Slice 6a ships SPD's exact proguard/R8 rules verbatim + headless Android smoke-boot in CI + 3-5 iteration rounds budgeted |
| R6 | iOS drift | (retired) | (retired) | Q6 chose defer entirely + delete `:ios` entry. No iOS work in Sub-B. |
| R7 | Translation drift (5 versions of string-key renames) | H | Low | Per-slice strings-diff report + two-file merge (SPD base + CPD overlay); untranslated new-feature strings fall back to English |
| R8 | HeroClass enum-ordinal serialization drift (adding Cleric changes enum layout, breaks ordinal-serialized saves) | M | Critical | Slice 2 enum-audit gate fires FIRST (before Cleric lands). Slice 3a re-fires the audit with Cleric-related enums. `.ordinal()` → `.name()` migration enforced. |
| R9 | Cross-slice regression discovery forces re-opening earlier slices | M | Medium | Slice 7 explicit rework-loopback budget of ~10 tasks. Do NOT tag/release the merge until Slice 7 completes even after Slices 1-6c ship to merge branch. |
| R10 | Save-compat cliff shocks CPD users on v2.1.0-era saves | H | High | Slice 0 bridge preserves saves. Release notes 2-week lead time. First-launch UI reports which saves are migrated vs abandoned. |
| R11 (new) | Merge branch stays unreleasable for the whole Slice 3a → Slice 7 window (per Q5 full-gate decision) | H | Medium | Accept as a cost of the gate policy. CPD-side emergency patches go on `main` before Slice 1 starts or on a hotfix branch off the last merged slice. Communicate the freeze window in the sub-B announcement. |
| R12 (new) | DSL freeze during Slices 1-6c blocks mod authors from building v3.x-flavored packs (per Q7) | M | Low | Accept as a cost of the freeze policy. DSL v2 lands in Slice 7. Sub-B announcement calls this out so mod authors know when to expect v2. |

## 11. Rollout and release cadence

**During Sub-B execution:**
- Each slice merges to `main` when its acceptance gates pass.
- No intermediate release tags between slices. The merge branch is unreleasable from Slice 3a (Cleric enum-drift risk) through Slice 7 (green-gate closure) per LO's Q5 decision.
- CPD-side emergency patches (security, critical bug) that need to ship during the Sub-B window go on a hotfix branch cut from the last shipped tag (currently the fork base at `cpd-sync-base-2025-08-15`) and get forward-ported into whatever slice is currently in-flight.

**At Sub-B close:**
- Slice 7 completes with all 30 packs booting green + DSL v2 documented + save cliff release-notes drafted.
- LO cuts a release tag `v0.1-base` from the Slice 7 merge commit.
- Alpha binaries can ship from this tag once THIRD_PARTY_NOTICES.md is completed (Apache/BSD/MIT deps expansion, listed as a Slice 7 sub-task).

**After Sub-B:**
- Sub-C (broad modding-platform API) begins, consuming the DSL v2 surface Sub-B established.
- The vision re-brainstorm opens (LO deferred to post-Sub-B during Sub-A execution).

## 12. Interfaces produced for downstream sub-projects

- **For Sub-C (broad modding-platform API):** Kotlin DSL v2 (Section 8), documented at `docs/modding-api-v2.md`, with skeleton entries for cameo-framework / save-zones / cutscene hooks that Sub-C will fully wire.
- **For Sub-D/E/F (God / Hard / Bonfire mode addons):** Stable SPD v3.3.8 API surface + all v3.x subsystems (spells, vault, investigating AI, thrown-weapon sets) available for mode addons to reference.
- **For post-v0.1 vision waves:** Save-bridge infrastructure (Section 6) generalizes to future save-format evolution. Marketplace-pack gate infrastructure (Section 7) reusable for gating any future content wave.

## 13. Per-slice implementation plans

The Slice 0 implementation plan lands alongside this design spec at `docs/superpowers/plans/2026-07-22-cpdu-sub-b-slice-0-foundation.md`. Slice 0 is well-defined (pure infrastructure, no upstream advance) and can be planned honestly right now.

Slices 1 through 7 will have their implementation plans authored just-in-time as each slice begins. This is deliberate: the pre-brainstorm research workflow's verify pass showed that merge-plan task estimates historically low-ball by 40-60%, and that the honest correction is "after Slice 1 lands, re-estimate Slices 2-7 with actual conflict-resolution velocity data." Writing all 14 slice plans upfront would produce 250+ tasks of speculative detail that would need heavy rewrite after Slice 1's empirical outcome.

Each per-slice plan will be authored via a fresh `superpowers:writing-plans` invocation when that slice becomes the next work item.

---

## Sign-off

Design approved by LO on 2026-07-22 via "go full auto" delegation of the section-by-section approval process. Any material change to Sections 2 (locked decisions), 5 (slice architecture), or 10 (risk register) requires a re-brainstorm; smaller adjustments to per-slice content estimates in Section 5 are expected as slices execute.
