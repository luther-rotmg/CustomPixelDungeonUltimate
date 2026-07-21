# CustomPixelDungeonUltimate

**CustomPixelDungeonUltimate** (CPDU) is a public GPLv3 fork of [QuasiStellar's Custom Pixel Dungeon](https://github.com/QuasiStellar/custom-pixel-dungeon), itself a fork of [Evan Debenham's Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon).

CPDU takes Custom Pixel Dungeon current with upstream Shattered Pixel Dungeon and extends its modding framework with a broader Java-hook API. Shipped gameplay-mode addons include **God Mode**, **Hard Mode**, and **Bonfire Mode**.

## Status

Under active development. v0.1 release pending — see [`docs/superpowers/specs/`](docs/superpowers/specs/) for the roadmap.

## Attribution

Under the terms of the GNU General Public License v3, CustomPixelDungeonUltimate carries forward the full attribution chain:

- Base game: **Shattered Pixel Dungeon**, © Evan Debenham. Sourced from https://github.com/00-Evan/shattered-pixel-dungeon
- Modding framework: **Custom Pixel Dungeon**, © QuasiStellar. Sourced from https://github.com/QuasiStellar/custom-pixel-dungeon
- Predecessor: **Pixel Dungeon**, © Watabou

See [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md) for the full attribution chain and library notices.

## License

GPLv3. See [`LICENSE.txt`](LICENSE.txt).

## Platform support

| Platform | Status |
|---|---|
| Android | Supported — see build instructions below |
| Desktop (Windows / macOS / Linux) | Supported — see build instructions below |
| iOS | **Unmaintained.** The `:ios` Gradle module is retained in `settings.gradle` for future reactivation but is not built, tested, or shipped. |

## Building

### Prerequisites

- JDK 17+
- Android SDK (for the Android build)

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

## Modding

The modding framework is currently the one Custom Pixel Dungeon ships. The extended Java-hook API is under active development — reference documentation will land at `docs/modding-api-v1.md` when Sub-C ships.

## Contributing

CustomPixelDungeonUltimate does not currently accept unsolicited pull requests. Issue reports and feature discussion are welcome.
