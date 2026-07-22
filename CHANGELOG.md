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
- `CONTRIBUTING.md` — how issues/stars/watches are welcomed; PR gate explanation; per-issue-type expectations
- `.github/ISSUE_TEMPLATE/` — three templates (🐛 bug report, 💡 feature request, 🎭 cameo suggestion) + `config.yml` disabling blank issues and providing quick links to PROJECT-STATUS / CHANGELOG / design specs

### Changed

- Default branch renamed `margarita` (inherited from CPD's cocktail-codename scheme) → `main` on both local and origin, per LO decision during Sub-A execution. Remote-tracking branches for upstream-cpd stay on `margarita` (that's QSR's naming, we don't own it); upstream-spd stays on `master` (Evan's naming). Sub-B will `git merge upstream-cpd/margarita` and `upstream-spd/master` into our `main`.

### Fixed — Sub-A build-baseline hotfix

Sub-A Task 5 (`./gradlew android:assembleDebug && ./gradlew desktop:release` must both pass) discovered that CPD's fork-base commit `c97fb83` did not build out-of-the-box because of four independent layers of upstream rot. Each layer was fixed one at a time. Final state: **both builds pass** — `desktop-2.1.0-1.0.jar` (45.9 MB), `android-debug.apk` (22.8 MB). Under LO's `Task 5 unblock` and `Android unblock` decisions:

- **`build.gradle`** — pinned `gdxControllersVersion` from `'2.2.4-SNAPSHOT'` → `'2.2.3'`. The SNAPSHOT dep had expired from Google Maven, Maven Central, and Sonatype OSS Snapshots. 2.2.3 is the latest real gdx-controllers release (April 2023; 2.2.4 was never cut, existed only as SNAPSHOT).
- **`android/build.gradle`** — added `multiDexEnabled true` to `defaultConfig` + `implementation "androidx.multidex:multidex:2.0.1"` to `dependencies`. Fixes DEX 64K method-reference limit exposed after the pin unblocked dependency resolution. Standard fix required for any moderately-sized libGDX + Kotlin + Android project with `minSdk < 21`.
- **`android/src/main/AndroidManifest.xml`** — set `android:name="androidx.multidex.MultiDexApplication"` on the `<application>` element (CPD had no custom Application class, so extending MultiDexApplication via the manifest is the minimal path — no new Java code required).
- **`gradle.properties`** — added `android.useAndroidX=true`. Required by AGP once any `androidx.*` dependency is on the classpath. `enableJetifier` NOT needed (repo has zero `com.android.support` refs).
- **`desktop:dist` task name corrections** — the Sub-A plan brief, imported design spec, imported Sub-A plan, and README all documented `./gradlew desktop:dist` as the Desktop build command. CPD's `desktop/build.gradle` has no `dist` task — the correct task is `desktop:release` (custom `Jar`-type task at `desktop/build.gradle:31`). Corrected in README, `docs/superpowers/specs/`, and `docs/superpowers/plans/`.

**Sub-B implication:** SPD v3.2 bumps `minSdk` to 21, at which point native Android multidex takes over and the `androidx.multidex` dep becomes unnecessary (revisit during that slice). SPD v3.2 / v3.3 also bump `libGDX` to `1.13.6-SNAPSHOT` and `1.14.0` respectively, which will resolve gdx-controllers to a more recent version — the 2.2.3 pin is interim.

### Deferred (not this sub-project)

- Any change to game code under `core/`, `android/`, `desktop/`, `ios/`, `services/`, `SPD-classes/`, or `marketplace/` — Sub-A is docs-only. Game code changes begin in Sub-B (upstream sync to SPD v3.3.8).
- CI setup (GitHub Actions, etc.) — deferred to Sub-G.
- iOS build — retained in `settings.gradle` for future reactivation, currently unmaintained on paper (see README platform support table).

### Known caveats

- The annotated tag `cpd-sync-base-2025-08-15` was pushed to origin during Sub-A Task 2 (implementer overreach — the plan brief said "no push" at Task 2, push was for Task 6). Effect: Task 6's tag-push step becomes a no-op. Documented and accepted; no corrective delete on origin (would double the GitHub webhook events for no user-visible difference).
- Sub-A's build-baseline hotfix (see "Fixed" above) modified `build.gradle`, `android/build.gradle`, `android/src/main/AndroidManifest.xml`, and `gradle.properties` — deviating from Sub-A's original "docs only, no gradle changes" scope. Deviation authorized by LO during execution under the `Task 5 unblock` (pin gdx-controllers) and `Android unblock` (enable multidex) decisions. Alternative was shipping Sub-A with an unbuildable baseline for Sub-B to inherit blindly. Total gradle+manifest diff: 9 lines across 4 files, all standard patterns from Android/libGDX best practice.

### Polish

- README ran through the humanizer skill to strip AI-writing tells: em-dashes across every section, promotional language ("wildly ambitious", "cult classic", "built with love"), the "Living design docs / Living roadmap / Living project" aphorism trio, the `**Bold header:** description` bullet pattern that dominated the vision sections, and the "Follow-along energy makes cult classics" closer. Structure preserved (badges, roadmap table, `<details>` sections, attribution chain, alpha-tester CTA); prose rewritten to sound like an indie dev with a real project. Zero em/en dashes remain per the humanizer's §14 hard constraint.

### Fixed — post-review cleanup

Final whole-branch review workflow (63 agents, 5 lenses, 3 adversarial verifiers per finding, 2.6M subagent tokens) returned a `block-until-fixed` verdict with one HIGH blocker + 4 MEDIUM doc-hygiene findings. This commit resolves the blocker plus the mediums plus a handful of low/nit polish items in a single pass.

Blocker:
- `README.md` roadmap table row F leaked `(Public name; internal working title: Dark Souls Mode.)` into the public shipping surface, defeating the exact trademark-safety split spec risk R4 was designed to prevent. Stripped the parenthetical; the row now reads with only the public "Bonfire Mode" name. Internal "Dark Souls Mode" reference retained in the design spec at `docs/superpowers/specs/…-design.md` per that spec's own line-303 policy ("used throughout this spec for clarity").

Mediums:
- `PROJECT-STATUS.md` had a stale `Current tip: main @ 6041725c8` (four commits behind), a broken alpha-tester anchor (`#alpha-testers-watchers-star-clickers--welcome` from before the humanizer rename), a dead scratchpad link at `AppData/Local/Temp/...` for the Sub-B research summary (unreachable on GitHub), a tense mismatch on build verification (line 12 said "in progress" while line 22 said "verified"), and a "this commit" self-reference in the recent-activity list that was stale by two commits. Rewrote all of them; the roadmap row for Sub-A now reads `✅ shipped`.
- `README.md` attribution paragraph promised "the full attribution chain and library notices" but the notices file only covered the GPL fork chain plus three libraries. Reworded to "the GPL fork chain plus the currently-audited library notices"; expanded the Apache/BSD/MIT dep audit (FreeType, LWJGL, Kotlin, Ktor, SLF4J, org.json, gdx-controllers, androidx.multidex) is scheduled for Sub-B before the first alpha binary ships.

Lows and nits bundled in:
- `README.md` iOS row said `The :ios Gradle module is retained in settings.gradle for future reactivation` which implied dormant iOS code exists. Verified: QSR deleted the `ios/` directory from CPD in May 2023. The `:ios` entry in `settings.gradle` is a leftover pointing at nothing. Reworded to match reality.
- `README.md` modding section rendered `docs/modding-api-v1.md` as a live link but the file does not exist yet. Changed to unlinked path with an explicit "nothing there yet" note.
- `THIRD_PARTY_NOTICES.md` RoboVM copyright line attributed `2013-2026 RoboVM AB` but RoboVM AB dissolved after Xamarin's 2015 acquisition; the 2016+ work is the MobiVM community fork. Split the attribution.
- `docs/assets/lutherverse-title.svg` lacked an SPDX-License-Identifier header. Added `GPL-3.0-or-later` + copyright.
- Sub-B preliminary research imported into `docs/superpowers/research/sub-b-preliminary-research.md` (was previously only in transient scratchpad and linked from PROJECT-STATUS via a dead `AppData/Temp/` path).

Backlog items (real but sub-alpha-triggered, Sub-A ships without them):
- Full library-notice expansion in `THIRD_PARTY_NOTICES.md` (see mediums above; scheduled for Sub-B pre-alpha).
- Cameo IP disclaimer expansion to explicitly reserve trademarks to their owners.
- Vision-section prose reads present-indicative for aspirational features; sub-alpha risk of a reader mistaking aspiration for shipped state. Defer to Sub-C polish pass.
- "Lutherverse" brand-collision check (USPTO / domain / handle) not run.
- Keyblade / Aeons FFX-terminology rename-vs-keep decision when those become in-game nouns.

### Build verification (both green as of this commit)

- `./gradlew android:assembleDebug` → `BUILD SUCCESSFUL in 38s` → `android/build/outputs/apk/debug/android-debug.apk` (22.8 MB)
- `./gradlew desktop:release` → `BUILD SUCCESSFUL in 29s` → `desktop/build/libs/desktop-2.1.0-1.0.jar` (45.9 MB)
- Environment: JDK 17 (Microsoft OpenJDK 17.0.19.10), Android SDK 33, build-tools 33.0.2, gradle wrapper 8.5

---

## Historical (pre-fork)

Everything before `c97fb832` is CPD (QuasiStellar) history and, further back, SPD (Evan) history — both maintained upstream. Their changelogs live in their respective repos:

- [Custom Pixel Dungeon changelog](https://github.com/QuasiStellar/custom-pixel-dungeon/blob/margarita/CHANGES.md) *(if present)*
- [Shattered Pixel Dungeon changelog](https://github.com/00-Evan/shattered-pixel-dungeon/tree/master/core/src/main/assets/messages/journal) *(in-game changelog assets)*
