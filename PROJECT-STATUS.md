# Project Status

*Snapshot of where Lutherverse (repo: `CustomPixelDungeonUltimate`) is right now. Updated in the same commit as any substantive work — treat as ground truth for near-term state.*

**Last update:** 2026-07-21
**Current tip:** `main` @ `6041725c8` (or later if this file was updated in a subsequent commit — check `git log -1 main`)

---

## Where we are

**Sub-A (Fork infrastructure)** is essentially complete. All doc content shipped in three commits (`ad6be78d4`, `6e5d6447b`, `6041725c8`). Environment prereqs (JDK 17, Android SDK 33, build-tools 33.0.2, platform-tools) installed. Gradle Android + Desktop build verification is in progress in a background subagent; once green, the branch pushes to origin and a final whole-branch review closes Sub-A.

**Sub-B (Upstream sync to SPD v3.3.8)** is in the planning stage. Pre-brainstorm research is complete — see [Sub-B research summary](../../../AppData/Local/Temp/claude/c--Users-minec-Documents-Projects-VSCode/be0d765b-b8c0-43a5-b81e-d3c5a546d773/scratchpad/sub-b-research/SUMMARY.md) if you have local access, or ask the frontier to load it from memory. Headline: CPD is on SPD v2.1.0 (frozen since late 2023), so Sub-B absorbs ~1,417 upstream commits across five SPD minor versions. Adversarial verify revised the task estimate from 148 to **220-290**. 8 architectural brainstorm questions await LO answers before Sub-B implementation begins.

---

## Roadmap

| Sub | Name | Status | Blockers / Notes |
|---|---|---|---|
| A | Fork infrastructure | 🟢 nearly done | Task 5 gradle build running; Task 6 push after |
| B | Upstream sync (CPD → SPD v3.3.8) | 🟡 research done, brainstorm pending | 8 LO decisions needed; revised task estimate 220-290 across 8 slices |
| C | Broad modding-platform API | ⚪ not started | Blocked on Sub-B ship |
| D | God Mode addon | ⚪ | Blocked on Sub-C |
| E | Hard Mode addon | ⚪ | Blocked on Sub-C |
| F | Bonfire Mode addon | ⚪ | Blocked on Sub-C |
| G | CI / GitHub Actions | ⚪ | Deferred |
| J | iOS reactivation | ⚪ | On paper only; deferred indefinitely |
| K | Real-time coop | ⚪ | Post-v0.1 |

**Post-v0.1 north-star waves** (the "Lutherverse ultimate vision" — needs a re-brainstorm after Sub-B ships): sphere grid progression · keyblades + Keybearer class + dual-wield reward system · turn-based combat toggle · save zones + towns + quest system + narrative state · 200-floor generator + biome variety · cosmic-horror biome pack · character-cameo framework · Nestalgia-style turn-based coop · leaderboards + labeled seeds · story spine + cutscene/dialogue engine.

---

## Recent activity

**2026-07-21 (Sub-A execution session):**
- Sub-A brainstorm + design spec + implementation plan
- Task 1: fork + local clone + 3 remotes + margarita→main branch rename
- Task 2: pin fork base with annotated tag `cpd-sync-base-2025-08-15` (implementer prematurely pushed the tag — non-blocking)
- Task 3: attribution docs commit (`ad6be78d4`)
- Task 4: design + Sub-A plan import (`6e5d6447b`)
- Ad-hoc: Lutherverse rebrand commit (`6041725c8`)
- Env: installed JDK 17 (Microsoft OpenJDK 17.0.19.10) + Android SDK 33
- Sub-B pre-brainstorm research workflow: 7 agents, 3 phases, adversarial verify caught 50% task-estimate under
- CHANGELOG.md + PROJECT-STATUS.md (this file) added for LO's changelog-cadence discipline

**Sub-A Task 5 (build verification) status:** blocked on pre-existing upstream dependency rot. Both Android (`./gradlew android:assembleDebug`) and Desktop (`./gradlew desktop:dist`) fail at fork-base commit `c97fb83` because CPD's `build.gradle` references `gdxControllersVersion = '2.2.4-SNAPSHOT'` which no longer resolves from any Maven repository (SNAPSHOT deps expire). Since Sub-A never touched any gradle file, this failure is inherited from upstream, not caused by us. Awaiting LO decision on: (a) pin to a real release version inline (~1-line build.gradle patch, deviates from Sub-A "no gradle changes" scope), (b) accept partial Sub-A + defer full build verification to Sub-B where SPD engine bumps will naturally resolve it, or (c) vendor a local jar. See [CHANGELOG](CHANGELOG.md#deferred-verifications-task-5-partially-blocked).

**Ad-hoc additions this session:**
- Ad-hoc rebrand commit (Lutherverse README + placeholder title card SVG) — `6041725c8`
- CHANGELOG.md + PROJECT-STATUS.md added per changelog-cadence rule — `f37dfb1b2`
- CONTRIBUTING.md + `.github/ISSUE_TEMPLATE/` (bug / feature / cameo templates + config) — this commit

---

## Awaiting LO input

- **Sub-B brainstorm** — 8 architectural questions pre-loaded (Cleric ship-status, custom-tile decision, save-compat policy, marketplace-mod gating, iOS scope, DSL freeze policy, Vault-area home, slice-count discipline). Opens after Sub-A ships.
- **Ultimate vision re-brainstorm** — LO explicitly deferred to "after Sub-B ships". Vision wave 1+2 already captured in frontier memory.

---

## Contribution status

- ★ Star / 👁 Watch → Releases welcome — see [README's alpha-tester section](README.md#alpha-testers-watchers-star-clickers--welcome)
- Issues welcome (bugs, feature ideas, cameo requests)
- **PRs currently not accepted** — modding API is not stable, every hook is subject to change; this loosens up when Sub-C ships
