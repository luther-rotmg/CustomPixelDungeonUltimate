<p align="center">
  <img src="docs/assets/lutherverse-title.svg" alt="Lutherverse — a cosmic-horror roguelike descent" width="820">
</p>

<h1 align="center">Lutherverse</h1>

<p align="center">
  <em>a 200-floor cosmic-horror roguelike descent, built on top of Shattered Pixel Dungeon</em>
</p>

<p align="center">
  <a href="LICENSE.txt"><img alt="License: GPL v3" src="https://img.shields.io/badge/license-GPL--3.0-blueviolet.svg"></a>
  <img alt="Status: pre-alpha" src="https://img.shields.io/badge/status-pre--alpha-orange.svg">
  <img alt="Platforms: Android · Desktop" src="https://img.shields.io/badge/platforms-android%20%C2%B7%20desktop-lightgrey.svg">
  <img alt="Base: SPD v3.3.8" src="https://img.shields.io/badge/base-SPD%20v3.3.8-informational.svg">
  <img alt="Java: 17+" src="https://img.shields.io/badge/java-17%2B-red.svg">
  <a href="https://github.com/luther-rotmg/CustomPixelDungeonUltimate/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/luther-rotmg/CustomPixelDungeonUltimate?style=social"></a>
  <a href="https://github.com/luther-rotmg/CustomPixelDungeonUltimate/watchers"><img alt="GitHub watchers" src="https://img.shields.io/github/watchers/luther-rotmg/CustomPixelDungeonUltimate?style=social"></a>
</p>

---

## What is this

**Lutherverse** *(repo: `CustomPixelDungeonUltimate`)* is a wildly ambitious fork-of-a-fork of [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon) built on top of [Custom Pixel Dungeon](https://github.com/QuasiStellar/custom-pixel-dungeon)'s marketplace-mod framework. The goal is a github cult classic — the roguelike that indie-game weirdos and cosmic-horror nerds pass around to each other with a knowing nod.

Shattered Pixel Dungeon is 25-ish floors of tight, replayable dungeon crawling. Lutherverse takes that engine and stretches it into a **200-floor descent** with wildly different feeling worlds — hyper-utopian futures with a twist, calm plains that go quiet-wrong, the Zanarkand you were never supposed to see — plus a story spine, cameos of characters you'll recognize, keyblades as their own weapon type, save zones, towns, sphere-grid progression, and coop with a friend on their phone.

Junji Ito × HP Lovecraft for aesthetic. Bloodborne × FFX × KH2 for inspiration. Custom Pixel Dungeon for the modding framework. Written in Java on libGDX. GPLv3 all the way down.

---

## The vision

<details open>
  <summary><strong>Core gameplay</strong></summary>

- **200-floor dungeon** with unique changes and content the whole way up — 8× SPD's default depth
- **Sphere-grid skill system** (FFX-inspired) — node-based, path-unlocked progression that replaces or wraps SPD's talent tree
- **Toggleable turn-based combat mode** — play the classic real-time roguelike combat, or flip to FFX-style turn-based action-queue combat per run
- **Save zones (KH2-style)** — checkpoint/rest rooms, with per-run toggles for healing on/off and whether they appear at all (hardcore mode)
- **Towns every 10 floors** — buying, selling, trading, crafting, resting, quests. Over ~30-floor spans, ONE city can repeat with different quest steps, inventories, and events based on your earlier choices
- **Keyblade weapon type** with a **dual-wield reward system** and a **Keybearer class** built around it. Meta-keyblades crafted from **orichalcum** as top-tier variants
</details>

<details>
  <summary><strong>Aesthetic direction</strong></summary>

- **Cosmic horror**: Junji Ito (Uzumaki spirals, Tomie unsettling designs) + HP Lovecraft (existential dread, deep-ones, mythos creatures)
- **Bloodborne**: chalice-dungeon-style procedural mini-dungeons, fountain-style shop, gothic ambient horror
- **Final Fantasy X**: Zanarkand, Calm Lands, Sin as recurring apocalyptic threat, Aeons as summonable creatures, sphere grid
- **Kingdom Hearts 2**: world-hopping between tonally distinct settings, keyblades, save-zone save/load flow
- **Cameos**: Sora (KH), Doll (Bloodborne), Patches (FromSoft), Tidus + O'aka (FFX), plus light-touch nods to GTA · Warframe · Naruto · DBZ · Binding of Isaac
- Every stretch of floors feels distinct — the dungeon changes on you the deeper you go
</details>

<details>
  <summary><strong>Multiplayer</strong></summary>

Two coop modes, serving very different sessions:

- **Real-time coop** — nickname + create-a-room simplicity, no self-hosted servers. Lobby toggles which "cheat" addons are allowed (God-tier gear = cheat, Hard-mode / Bonfire-mode = fair).
- **Turn-based Nestalgia-style coop** — party of 4 units split across players (1p=4, 2p=2 each, 4p=1). Async play with a friend on their phone; long-term quest oriented. Pairs with the turn-based combat mode above.
</details>

<details>
  <summary><strong>Community goodies</strong></summary>

- **Leaderboards** for the intrepid
- **Labeled seed sharing** — share a run as `seed COSMICNIGHTMARE-1`, not `seed 12345`
- **Story spine + cutscenes/dialogues** all the way up 200 floors
- Deep modding-platform API so the community can add their own biomes, mobs, weapons, and story branches
</details>

---

## Roadmap

Every substantive commit updates this section — treat it as ground truth for where the project is right now.

**Sub-projects (v0.1 track):**

| Sub | Name | Status | Notes |
|---|---|---|---|
| A | Fork infrastructure | ✅ done | This repo. Attribution + docs + branch rename `margarita`→`main`. |
| B | Upstream sync — CPD → SPD v3.3.8 | 🟡 planning | Bring the game engine current with Evan's latest. Merge-in-slices strategy. |
| C | Broad modding-platform API | ⏳ next | Java-hook API on top of CPD's JSON-manifest framework: cutscenes, dialogue, dual-wield, story flags, biome swapping, NPC insertion. |
| D | God Mode addon | ⏳ | Top-tier starter gear. Flagged as "cheat" in coop lobby. |
| E | Hard Mode addon | ⏳ | Balance tuning. Coop-fair. |
| F | Bonfire Mode addon | ⏳ | Souls-ish permadeath modifiers. Coop-fair. (Public name; internal working title: Dark Souls Mode.) |

**Post-v0.1 waves** (these need their own brainstorm passes — this list is the north star, not commitments):

- Sphere Grid progression system
- Keyblade weapon type · Keybearer class · dual-wield reward system
- Turn-based combat toggle
- Save zones · towns · quest system · narrative-state module
- 200-floor generator + biome variety framework · cosmic-horror biome pack
- Character cameo framework + first cameos
- Real-time coop (Sub-K)
- Nestalgia-style turn-based coop
- Leaderboards + labeled seed sharing
- Story spine + cutscene/dialogue engine

---

## Alpha testers, watchers, star-clickers — welcome

Nothing playable-new yet — the fork is at its base commit, awaiting Sub-B. But if any of the above sounds like the kind of thing you'd want to watch unfold: **★ Star this repo** to bookmark it, and **👁 Watch → Custom → Releases** if you'd like a ping when the first alpha ships. Follow-along energy makes cult classics.

Issues welcome (bugs, feature ideas, "you should absolutely cameo X"). PRs currently not accepted — the modding API is not stable and every hook is subject to change. That will loosen up when Sub-C ships.

---

## Attribution

Under GPL-3.0, Lutherverse carries forward the full attribution chain:

- **Base game** — *Shattered Pixel Dungeon*, © Evan Debenham · [00-Evan/shattered-pixel-dungeon](https://github.com/00-Evan/shattered-pixel-dungeon)
- **Modding framework** — *Custom Pixel Dungeon*, © QuasiStellar · [QuasiStellar/custom-pixel-dungeon](https://github.com/QuasiStellar/custom-pixel-dungeon)
- **Predecessor** — *Pixel Dungeon*, © Watabou

See [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md) for the full attribution chain and library notices (libGDX Apache 2.0, RoboVM/MobiVM GPLv2-with-Classpath-Exception, SPD-classes).

Not affiliated with, endorsed by, or connected to any of the games or franchises Lutherverse takes creative inspiration from. Character cameos are non-commercial fan-project references.

---

## License

**GPL-3.0.** See [`LICENSE.txt`](LICENSE.txt).

If you use or modify Lutherverse's code, your derivative work must also be GPL-3.0. That's the terms Evan set for Shattered Pixel Dungeon and we carry them forward faithfully.

---

## Platform support

| Platform | Status |
|---|---|
| **Android** | Supported — see build instructions below |
| **Desktop** (Windows / macOS / Linux) | Supported — see build instructions below |
| **iOS** | **Unmaintained.** The `:ios` Gradle module is retained in `settings.gradle` for future reactivation but is not built, tested, or shipped. |

---

## Building

### Prerequisites

- JDK 17+
- Android SDK (for the Android build) — compile-SDK 33, build-tools 33.0.2

### Android APK

```
./gradlew android:assembleDebug
```

Debug APK will be produced under `android/build/outputs/apk/debug/`.

### Desktop JAR

```
./gradlew desktop:dist
```

Runnable JAR will be produced under `desktop/build/libs/`.

---

## Modding

The modding framework is currently the one Custom Pixel Dungeon ships — JSON manifest per mod, resource overrides, hero JSON merge semantics. See CPD's mod documentation until Sub-C's broader Java-hook API lands. When it does, reference documentation will live at [`docs/modding-api-v1.md`](docs/modding-api-v1.md).

---

## Development docs

- **Changelog:** [`CHANGELOG.md`](CHANGELOG.md) — every substantive commit
- **Project status:** [`PROJECT-STATUS.md`](PROJECT-STATUS.md) — near-term state, blockers, roadmap
- Design specs: [`docs/superpowers/specs/`](docs/superpowers/specs/)
- Implementation plans: [`docs/superpowers/plans/`](docs/superpowers/plans/)
- Assets: [`docs/assets/`](docs/assets/)

Living design docs. Living roadmap. Living project.

<p align="center">
  <sub>Lutherverse — a fan project by <a href="https://github.com/luther-rotmg">luther-rotmg</a> · built with love on top of Watabou → Evan → QSR</sub>
</p>
