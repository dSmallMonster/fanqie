# Quick Start Guide

## Get Started in 5 Minutes

### Prerequisites
- Android Studio (latest version)
- Android device or emulator (Android 7.0+)

### Steps

#### 1. Open Project
```bash
# Extract the archive (if needed)
tar -xzf WhiteNoiseApp.tar.gz
cd WhiteNoiseApp
```

Open Android Studio → **Open** → Select `WhiteNoiseApp` folder

#### 2. Wait for Gradle Sync
Android Studio will automatically sync dependencies. Wait for completion (check bottom status bar).

#### 3. Connect Device
- **Physical Device**: Enable USB Debugging and connect via USB
- **Emulator**: Launch from Device Manager (Tools → Device Manager)

#### 4. Run
Click the green **Run** button (▶) or press `Shift + F10`

#### 5. Test
- Tap the play button ▶ - audio should start
- Swipe left/right to change noise types
- Tap pause ⏸ - audio should stop smoothly

## Usage

### Controls
- **Play/Pause**: Tap the large circular button
- **Change Noise Type**: Swipe left or right
- **Exit**: Use back button or recent apps

### Noise Types
- **White**: Equal energy across all frequencies (like static)
- **Pink**: Equal energy per octave (balanced, natural)
- **Brown**: Deep, rumbling tone (like thunder)

## Troubleshooting

### Build Fails
- Ensure internet connection for Gradle sync
- Update Android SDK to API 34
- Invalidate caches: File → Invalidate Caches / Restart

### App Crashes
- Check device is Android 7.0 or higher
- View Logcat in Android Studio for error details
- Clear app data and reinstall

### No Sound
- Check device volume
- Ensure device is not in silent mode
- Try different noise type (swipe)

## Build APK

To create an installable APK:

```bash
# Command line
./gradlew assembleDebug

# Or in Android Studio
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

## Documentation

- **README.md**: Full project overview
- **BUILD_INSTRUCTIONS.md**: Detailed build guide
- **ARCHITECTURE.md**: Technical architecture
- **PROJECT_SUMMARY.md**: Quick reference

## Support

For detailed instructions, see **BUILD_INSTRUCTIONS.md**

## Features Checklist

After installation, verify:
- ✅ Full-screen immersive mode
- ✅ Smooth audio playback
- ✅ No clicks or pops
- ✅ Swipe navigation works
- ✅ Breathing animation when playing

---

**Enjoy your white noise experience!** 🎵
