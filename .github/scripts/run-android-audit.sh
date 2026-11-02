#!/usr/bin/env bash
set -uo pipefail

# Android 16 emulator can leave the keyguard or home window on top after boot.
adb shell svc power stayon true || true
adb shell input keyevent 224 || true
adb shell wm dismiss-keyguard || true
adb shell input keyevent 82 || true
adb shell input swipe 300 800 300 250 350 || true
adb shell settings put system screen_off_timeout 1800000 || true
sleep 3

set +e
./gradlew :app:connectedDebugAndroidTest --stacktrace --no-daemon
result=$?
set -e

mkdir -p app/build/reports/androidTests
{
  echo "=== System window focus ==="
  adb shell dumpsys window windows 2>&1 | grep -E 'mCurrentFocus|mFocusedApp|mInputMethodTarget|mObscuringWindow' || true
  echo "=== Resumed activities ==="
  adb shell dumpsys activity activities 2>&1 | grep -E 'mResumedActivity|topResumedActivity|mFocusedApp' || true
  echo "=== Keyguard ==="
  adb shell dumpsys window policy 2>&1 | grep -E 'keyguard|Keyguard' | head -12 || true
} > app/build/reports/androidTests/window-focus.txt
adb exec-out screencap -p > app/build/reports/androidTests/emulator-screen.png || true
cat app/build/reports/androidTests/window-focus.txt
exit "$result"
