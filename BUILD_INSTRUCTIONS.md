# Build Instructions for White Noise App

## Quick Start Guide

This document provides step-by-step instructions for building and running the White Noise Android application.

## Prerequisites

### Required Software

1. **Android Studio** (Recommended)
   - Version: Hedgehog (2023.1.1) or later
   - Download: https://developer.android.com/studio

2. **Java Development Kit (JDK)**
   - Version: JDK 17
   - Usually bundled with Android Studio

3. **Android SDK**
   - API Level 34 (Android 14)
   - Install via Android Studio SDK Manager

### Optional
- **Physical Android Device** (Android 7.0+) or **Android Emulator**
- **ADB (Android Debug Bridge)** - Usually installed with Android Studio

## Method 1: Build with Android Studio (Recommended)

### Step 1: Open Project
1. Launch Android Studio
2. Select **"Open"** from the welcome screen
3. Navigate to the `WhiteNoiseApp` folder
4. Click **"OK"**

### Step 2: Sync Gradle
1. Android Studio will automatically detect the Gradle project
2. Wait for Gradle sync to complete (check bottom status bar)
3. If prompted, accept any SDK or Gradle plugin updates

### Step 3: Configure Device
**Option A: Use Physical Device**
1. Enable Developer Options on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Go to Settings → Developer Options
   - Enable "USB Debugging"
3. Connect device via USB
4. Accept USB debugging prompt on device

**Option B: Use Emulator**
1. Open AVD Manager (Tools → Device Manager)
2. Create a new Virtual Device
3. Select a device definition (e.g., Pixel 6)
4. Select a system image (API 34 recommended)
5. Click "Finish" and launch the emulator

### Step 4: Build and Run
1. Select your device from the device dropdown (top toolbar)
2. Click the **"Run"** button (green play icon) or press `Shift + F10`
3. Wait for the build to complete
4. The app will automatically install and launch on your device

### Step 5: Build APK (Optional)
To create a standalone APK file:

1. Go to **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for the build to complete
3. Click **"locate"** in the notification to find the APK
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

## Method 2: Build from Command Line

### Step 1: Setup Gradle Wrapper (if not present)
```bash
cd WhiteNoiseApp
gradle wrapper --gradle-version 8.2
```

### Step 2: Build Debug APK
```bash
# On Linux/Mac
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

### Step 3: Install APK
```bash
# Find connected devices
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Step 4: Launch App
```bash
adb shell am start -n com.whitenoise.app/.MainActivity
```

## Method 3: Build Release APK (Unsigned)

### Using Android Studio
1. Go to **Build → Generate Signed Bundle / APK**
2. Select **APK**
3. For testing, you can skip signing and build unsigned
4. Select **release** build variant
5. Click **Finish**

### Using Command Line
```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

**Note**: For production release, you need to sign the APK with a keystore.

## Troubleshooting

### Issue: Gradle Sync Failed
**Solution**: 
- Check internet connection
- Update Gradle plugin: File → Project Structure → Project
- Invalidate caches: File → Invalidate Caches / Restart

### Issue: SDK Not Found
**Solution**:
- Open SDK Manager: Tools → SDK Manager
- Install Android SDK Platform 34
- Install Android SDK Build-Tools 34.0.0

### Issue: Device Not Detected
**Solution**:
- Check USB cable connection
- Enable USB Debugging on device
- Try different USB port
- Run `adb devices` to verify connection

### Issue: Build Error - "Kotlin not configured"
**Solution**:
- Ensure Kotlin plugin is installed in Android Studio
- Check `build.gradle.kts` for correct Kotlin version
- Sync Gradle again

### Issue: App Crashes on Launch
**Solution**:
- Check Logcat in Android Studio for error messages
- Ensure device is running Android 7.0 (API 24) or higher
- Clear app data: Settings → Apps → White Noise → Clear Data

## Verification

After successful installation, verify the app:

1. ✅ App launches without crashes
2. ✅ Full-screen immersive mode (no status/nav bars)
3. ✅ Tap play button - audio starts playing
4. ✅ Swipe left/right - noise type changes
5. ✅ Tap pause - audio stops smoothly (no clicks)
6. ✅ No internet permission warnings

## Project Configuration Files

Key files for build configuration:

- `build.gradle.kts` (root) - Project-level build config
- `app/build.gradle.kts` - App module build config
- `settings.gradle.kts` - Project settings
- `gradle.properties` - Gradle properties
- `app/src/main/AndroidManifest.xml` - App manifest

## Build Variants

The project supports two build variants:

1. **Debug**: For development and testing
   - Includes debugging symbols
   - Not optimized
   - Faster build time

2. **Release**: For production
   - Optimized with ProGuard/R8
   - Smaller APK size
   - Requires signing for distribution

## Additional Resources

- **Android Developer Documentation**: https://developer.android.com/docs
- **Kotlin Documentation**: https://kotlinlang.org/docs/home.html
- **Jetpack Compose**: https://developer.android.com/jetpack/compose

## Support

For issues or questions:
1. Check Logcat output in Android Studio
2. Review error messages in Build Output
3. Verify all prerequisites are installed
4. Ensure device meets minimum requirements (Android 7.0+)

## Next Steps

After successful build:
- Test all three noise types (White, Pink, Brown)
- Verify smooth audio transitions
- Test swipe gestures
- Check battery usage during extended playback
- Test on different Android versions and devices
