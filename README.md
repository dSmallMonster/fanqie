# White Noise Android Application

A minimalist Android white noise application built with Kotlin and Jetpack Compose, featuring real-time audio synthesis and an immersive user experience.

## Features

### Real-time Audio Synthesis
- **No external audio files**: All noise is mathematically generated in real-time using AudioTrack
- **Three noise types**:
  - **White Noise**: Equal energy across all frequencies
  - **Pink Noise**: Equal energy per octave (1/f spectrum)
  - **Brown Noise**: Deep, rumbling tone (1/f² spectrum)

### Audio Engineering
- **Seamless looping**: Continuous playback without gaps or pops
- **Smooth transitions**: 500ms fade-in/fade-out envelope to prevent clicks
- **Background processing**: All audio generation happens on background threads
- **Low latency**: Uses AudioTrack with optimized buffer sizes

### Minimalist UI/UX
- **Full-screen immersive mode**: Hides status and navigation bars
- **Dark theme**: Sleek gradient background (deep blue tones)
- **Horizontal swipe navigation**: Swipe left/right to switch between noise types
- **Breathing animation**: Pulsating play button when audio is active
- **Simple controls**: Single large play/pause button in the center

### Privacy & Offline
- **100% offline**: No network permissions in AndroidManifest.xml
- **Zero data collection**: No analytics, no tracking, no internet access

## Project Structure

```
WhiteNoiseApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/whitenoise/app/
│   │   │   ├── MainActivity.kt          # Main UI with Jetpack Compose
│   │   │   ├── MainViewModel.kt         # ViewModel for state management
│   │   │   ├── AudioManager.kt          # Audio playback controller
│   │   │   ├── NoiseGenerator.kt        # Real-time noise synthesis engine
│   │   │   └── NoiseType.kt             # Noise type enum
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml          # String resources
│   │   │   │   └── themes.xml           # App theme
│   │   │   └── mipmap-*/                # Launcher icons
│   │   └── AndroidManifest.xml          # App manifest (no internet permission)
│   ├── build.gradle.kts                 # App module build configuration
│   └── proguard-rules.pro               # ProGuard rules
├── build.gradle.kts                     # Root build configuration
├── settings.gradle.kts                  # Project settings
├── gradle.properties                    # Gradle properties
└── README.md                            # This file
```

## Technical Details

### NoiseGenerator Class

The `NoiseGenerator` class implements three different noise algorithms:

1. **White Noise**: Pure random values between -1.0 and 1.0
2. **Pink Noise**: Uses Paul Kellet's algorithm with cascaded filters
3. **Brown Noise**: Brownian motion with random walk and clamping

Key features:
- Uses `AudioTrack` in streaming mode for continuous playback
- Implements fade-in/fade-out envelopes to prevent audio artifacts
- Runs on coroutines (Dispatchers.Default) to keep UI responsive
- Maintains state variables for pink and brown noise generation

### Audio Specifications
- **Sample Rate**: 44,100 Hz
- **Encoding**: PCM 16-bit
- **Channel**: Mono
- **Buffer Size**: 4x minimum buffer size for smooth playback

### UI Architecture
- **MVVM Pattern**: ViewModel manages state, UI observes via StateFlow
- **Jetpack Compose**: Modern declarative UI framework
- **Immersive Mode**: Edge-to-edge with hidden system bars
- **Gesture Detection**: Horizontal drag gestures for noise type switching

## Building the Application

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API level 34
- Gradle 8.2+

### Build Steps

1. **Open the project in Android Studio**:
   ```bash
   cd WhiteNoiseApp
   # Open in Android Studio or use command line
   ```

2. **Sync Gradle**:
   - Android Studio will automatically sync Gradle dependencies
   - Or run: `./gradlew build` (if gradlew wrapper is configured)

3. **Build APK**:
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build
   ./gradlew assembleRelease
   ```

4. **Install on device**:
   ```bash
   # Via ADB
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Alternative: Manual Build with Gradle

If you don't have Android Studio, you can build from command line:

```bash
# Download Gradle wrapper (if not present)
gradle wrapper --gradle-version 8.2

# Build the project
./gradlew assembleDebug
```

## Usage

1. **Launch the app**: Tap the app icon
2. **Play/Pause**: Tap the large circular button in the center
3. **Switch noise types**: Swipe left or right
4. **Exit**: Use back button or recent apps to close

## Code Quality

- **Kotlin best practices**: Immutable state, coroutines, StateFlow
- **Thread safety**: All audio processing on background threads
- **Memory management**: Proper resource cleanup in onCleared()
- **No memory leaks**: AudioTrack properly released
- **Smooth animations**: Compose animations with proper lifecycle

## Requirements Met

✅ Real-time audio synthesis (no MP3/WAV files)  
✅ White, Pink, and Brown noise generation  
✅ Seamless looping without gaps  
✅ 500ms fade-in/fade-out transitions  
✅ No network permissions  
✅ Full-screen immersive mode  
✅ Dark-themed aesthetic  
✅ Horizontal swipe navigation  
✅ Breathing animation on play button  
✅ Background thread audio processing  
✅ Jetpack Compose UI  

## License

This project is provided as-is for educational and personal use.

## Notes

- The app requires Android 7.0 (API 24) or higher
- Tested on Android emulators and physical devices
- Audio quality is optimized for continuous playback
- No external dependencies beyond AndroidX and Compose
