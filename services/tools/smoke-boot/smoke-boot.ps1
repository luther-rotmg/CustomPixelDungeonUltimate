#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Headless Android smoke-boot: boots a debug APK in an emulator and confirms
    the app process is alive. Not a full playthrough check -- see README.md.

.DESCRIPTION
    Exit 0  -> APK installed, app process alive 10s after launch (PASS).
    Exit 1  -> any failure (emulator didn't boot, install failed, process died).

    Package/Activity are auto-detected from the APK via aapt2 (build-tools)
    unless explicitly passed with -Package/-Activity. Auto-detection exists
    because debug builds apply an applicationIdSuffix (this project uses
    ".indev"), so the installed package id and launchable activity are not
    fixed strings -- they must be read out of the APK being tested.
#>
param(
    [string]$Apk = "android/build/outputs/apk/debug/android-debug.apk",
    [string]$Avd = "cpdu-smoke",
    [int]$TimeoutSeconds = 120,
    [string]$Package = "",
    [string]$Activity = ""
)

$env:ANDROID_HOME = [Environment]::GetEnvironmentVariable('ANDROID_HOME', 'User')
if (-not $env:ANDROID_HOME) {
    $env:ANDROID_HOME = [Environment]::GetEnvironmentVariable('ANDROID_HOME', 'Machine')
}
if (-not $env:ANDROID_HOME) {
    Write-Host "FAIL: ANDROID_HOME is not set (User or Machine scope)"
    exit 1
}

$emulator = Join-Path $env:ANDROID_HOME "emulator\emulator.exe"
$adb = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"

if (-not (Test-Path $emulator)) {
    Write-Host "FAIL: emulator.exe not found at $emulator (install the 'emulator' SDK package)"
    exit 1
}
if (-not (Test-Path $adb)) {
    Write-Host "FAIL: adb.exe not found at $adb (install the 'platform-tools' SDK package)"
    exit 1
}
if (-not (Test-Path $Apk)) {
    Write-Host "FAIL: APK not found at $Apk"
    exit 1
}

# Auto-detect package id / launchable activity from the APK via aapt2, unless
# the caller passed them explicitly. Debug builds may carry an
# applicationIdSuffix, so a hardcoded component string would silently break
# the moment the suffix (or the launcher activity) changes.
if ((-not $Package) -or (-not $Activity)) {
    $aapt2 = Get-ChildItem -Path (Join-Path $env:ANDROID_HOME "build-tools") -Filter "aapt2.exe" -Recurse -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending | Select-Object -First 1
    if ($aapt2) {
        $badging = & $aapt2.FullName dump badging $Apk 2>$null
        if (-not $Package) {
            $pkgLine = $badging | Select-String "^package: name='([^']+)'"
            if ($pkgLine) { $Package = $pkgLine.Matches[0].Groups[1].Value }
        }
        if (-not $Activity) {
            $actLine = $badging | Select-String "^launchable-activity: name='([^']+)'"
            if ($actLine) { $Activity = $actLine.Matches[0].Groups[1].Value }
        }
    }
}
if ((-not $Package) -or (-not $Activity)) {
    Write-Host "FAIL: could not determine package/activity from $Apk (pass -Package/-Activity explicitly, or install the 'build-tools' aapt2 binary)"
    exit 1
}
$component = "$Package/$Activity"

Write-Host "starting emulator ($Avd)..."
$null = Start-Process -FilePath $emulator -ArgumentList "-avd", $Avd, "-no-window", "-no-audio", "-no-snapshot" -PassThru

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
$booted = $false
while ((Get-Date) -lt $deadline) {
    $state = & $adb shell getprop sys.boot_completed 2>$null
    if ($state -match "1") { $booted = $true; break }
    Start-Sleep -Seconds 3
}

if (-not $booted) {
    & $adb emu kill 2>$null
    Write-Host "FAIL: emulator did not boot within $TimeoutSeconds seconds"
    exit 1
}

Write-Host "installing APK ($Apk)..."
& $adb install -r $Apk
$installExit = $LASTEXITCODE
if ($installExit -ne 0) {
    & $adb emu kill 2>$null
    Write-Host "FAIL: install exit $installExit"
    exit 1
}

Write-Host "launching app ($component)..."
& $adb shell am start -n $component
Start-Sleep -Seconds 10

Write-Host "checking process alive..."
$running = & $adb shell pidof $Package
if ([string]::IsNullOrWhiteSpace($running)) {
    & $adb emu kill 2>$null
    Write-Host "FAIL: app process not alive 10s after launch"
    exit 1
}

Write-Host "PASS: app booted and running (pid $running)"
& $adb emu kill 2>$null
exit 0
