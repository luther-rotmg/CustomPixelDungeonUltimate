# smoke-boot

Headless Android smoke-boot: boots a debug APK in an Android emulator and
confirms the app process is alive. This is **not** a full playthrough check —
it does not click through menus, load a save, or verify any gameplay. It only
confirms the process survives 10 seconds after `am start`, which is enough to
catch the class of failure this tool exists for: native crash on launch,
missing/corrupt assets, broken manifest/activity wiring, ANR on startup.

## What "PASS" means

1. The emulator (`-avd <name>`) reaches `sys.boot_completed=1` within the
   timeout (default 120s).
2. `adb install -r <apk>` exits 0.
3. `adb shell am start -n <package>/<activity>` is issued.
4. 10 seconds later, `adb shell pidof <package>` returns a non-empty pid.

If all four hold: exit 0, `PASS: app booted and running (pid <n>)`.
Any failure at any step: exit 1, with a `FAIL: <reason>` line explaining
which step failed. The emulator is killed (`adb emu kill`) on both the pass
and fail paths so it never leaks a running instance.

## Usage

```
# pwsh
pwsh -NoProfile -File services/tools/smoke-boot/smoke-boot.ps1
pwsh -NoProfile -File services/tools/smoke-boot/smoke-boot.ps1 -Apk path\to\app.apk -Avd my-avd -TimeoutSeconds 180

# bash
./services/tools/smoke-boot/smoke-boot.sh
./services/tools/smoke-boot/smoke-boot.sh path/to/app.apk my-avd 180
```

Both scripts default to:
- APK: `android/build/outputs/apk/debug/android-debug.apk`
- AVD name: `cpdu-smoke`
- Timeout: 120 seconds

## Prerequisites

- Android SDK with `ANDROID_HOME` set (checked at `User` then `Machine` scope
  on Windows for the pwsh script; a plain env var for the bash script).
- SDK packages installed: `platform-tools`, `emulator`, and at least one
  system image with a matching AVD (see "Creating the AVD" below).
- `build-tools` containing `aapt2` (used for auto-detecting the package id
  and launchable activity out of the APK — see "Why auto-detection" below).
- Hardware acceleration for the emulator (WHPX on Windows, HAXM on older
  Windows/macOS Intel, KVM on Linux). Without it the emulator either refuses
  to boot or is too slow to hit the default 120s timeout.

### Creating the AVD

```
sdkmanager "system-images;android-33;google_apis;x86_64"
avdmanager create avd --name cpdu-smoke --package "system-images;android-33;google_apis;x86_64" --device pixel
```

`sdkmanager`/`avdmanager` under `cmdline-tools/latest/bin` require a JDK 17+
runtime — on this machine the default `java` on PATH was JDK 8, which fails
with `UnsupportedClassVersionError`. Fix by setting `JAVA_HOME` to a JDK 17+
install (e.g. `C:\Program Files\Microsoft\jdk-17.x-hotspot`) before invoking
`sdkmanager`/`avdmanager`. The smoke-boot scripts themselves don't need Java —
only initial AVD/system-image setup does.

## Why auto-detection (package/activity)

Debug builds in this project apply `applicationIdSuffix ".indev"`
(`android/build.gradle`), so the installed package id is
`com.qsr.customspd.indev`, not the release id `com.qsr.customspd`. The
launcher activity's fully-qualified class name is
`com.qsr.customspd.android.AndroidLauncher`. Both scripts read these directly
out of the target APK via `aapt2 dump badging` so the tool keeps working
whether it's pointed at a debug, release, or future differently-suffixed
build, instead of relying on a component string that only matches one
variant. Override with `-Package`/`-Activity` (pwsh) or the `PACKAGE`/
`ACTIVITY` env vars (bash) if `aapt2` isn't available or you need to force a
specific component.

## Local test-run result (this machine, 2026-07-21)

Both scripts were run end-to-end against a real debug APK
(`android/build/outputs/apk/debug/android-debug.apk`) on the `cpdu-smoke` AVD
(Pixel device, API 33, google_apis x86_64, WHPX acceleration):

- `smoke-boot.ps1`: **PASS** — boot, install, launch, and pidof check all
  succeeded (pid 3367 observed alive 10s post-launch). Emulator killed
  cleanly on exit.
- `smoke-boot.sh`: **PASS** — same sequence, cold boot completed in ~35s
  (`Boot completed in 35130 ms`), pid 2328 observed alive. Emulator killed
  cleanly on exit.

Environment notes from getting to a working state on this machine:
- The SDK initially had no `emulator` package and no system images installed
  (only `build-tools`, `cmdline-tools`, `platform-tools`, `platforms`) — both
  had to be installed via `sdkmanager` before any AVD could be created.
- `sdkmanager`/`avdmanager` failed with `UnsupportedClassVersionError` until
  `JAVA_HOME` was pointed at the machine's already-installed JDK 17 (it was
  present but not what `java` on PATH resolved to).
- No HAXM/Hyper-V blockers were hit — WHPX acceleration was already usable
  (`emulator -accel-check` reported `WHPX(10.0.26200) is installed and
  usable`).

## Sub-G CI usage (future)

Not wired to CI yet (tracked under Sub-G). The intended integration is: build
the debug APK, then invoke `smoke-boot.sh` (Linux CI runner) or
`smoke-boot.ps1` (Windows CI runner) with the built APK path, treating a
non-zero exit as a failed acceptance gate. CI runners will need the same
prerequisites documented above (SDK + emulator + system image + AVD +
hardware acceleration exposed to the runner), which is a separate concern
from these scripts.
