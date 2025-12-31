# Technical Architecture Documentation

## Overview

The White Noise application follows modern Android development best practices with a clean architecture pattern, separating concerns between UI, business logic, and audio processing layers.

## Architecture Pattern

### MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────────────────────┐
│                         View Layer                       │
│                    (Jetpack Compose)                     │
│                      MainActivity.kt                     │
└────────────────────────┬────────────────────────────────┘
                         │ observes StateFlow
                         ↓
┌─────────────────────────────────────────────────────────┐
│                     ViewModel Layer                      │
│                    MainViewModel.kt                      │
│              (State Management & UI Logic)               │
└────────────────────────┬────────────────────────────────┘
                         │ delegates to
                         ↓
┌─────────────────────────────────────────────────────────┐
│                    Business Logic Layer                  │
│                     AudioManager.kt                      │
│              (Playback Control & State)                  │
└────────────────────────┬────────────────────────────────┘
                         │ controls
                         ↓
┌─────────────────────────────────────────────────────────┐
│                   Audio Processing Layer                 │
│                    NoiseGenerator.kt                     │
│           (Real-time Synthesis & AudioTrack)             │
└─────────────────────────────────────────────────────────┘
```

## Component Details

### 1. View Layer (MainActivity.kt)

**Responsibilities**:
- Render UI using Jetpack Compose
- Handle user interactions (tap, swipe)
- Observe ViewModel state changes
- Display immersive full-screen experience

**Key Components**:
- `WhiteNoiseApp`: Main composable function
- `NoiseTypeIndicator`: Displays current noise type and description
- `PlayPauseButton`: Interactive button with breathing animation
- `SwipeHint`: User guidance for gesture navigation

**UI State Management**:
```kotlin
val isPlaying by viewModel.isPlaying.collectAsState()
val currentNoiseType by viewModel.currentNoiseType.collectAsState()
```

**Gesture Detection**:
- Uses `detectHorizontalDragGestures` for swipe navigation
- Threshold: 100 pixels for noise type switching
- Circular navigation: White → Pink → Brown → White

### 2. ViewModel Layer (MainViewModel.kt)

**Responsibilities**:
- Manage UI state
- Expose StateFlow for UI observation
- Delegate audio operations to AudioManager
- Handle lifecycle events

**State Flows**:
- `isPlaying: StateFlow<Boolean>` - Playback state
- `currentNoiseType: StateFlow<NoiseType>` - Active noise type

**Lifecycle Management**:
```kotlin
override fun onCleared() {
    audioManager.release() // Clean up resources
}
```

**State Sharing Strategy**:
- `SharingStarted.WhileSubscribed(5000)` - Keep state active while UI is visible
- 5-second timeout after last subscriber

### 3. Business Logic Layer (AudioManager.kt)

**Responsibilities**:
- Control NoiseGenerator lifecycle
- Manage playback state
- Handle noise type switching
- Provide reactive state updates

**State Management**:
```kotlin
private val _isPlaying = MutableStateFlow(false)
val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
```

**Key Operations**:
- `togglePlayPause()`: Toggle between play and pause states
- `play()`: Start audio generation
- `pause()`: Stop audio with fade-out
- `setNoiseType()`: Switch noise type (restarts if playing)

**Thread Safety**:
- Uses Kotlin Flow for thread-safe state updates
- All operations are main-thread safe

### 4. Audio Processing Layer (NoiseGenerator.kt)

**Responsibilities**:
- Real-time audio synthesis
- AudioTrack management
- Fade-in/fade-out envelope application
- Seamless looping

**Audio Specifications**:
```kotlin
SAMPLE_RATE = 44100 Hz
ENCODING = PCM 16-bit
CHANNEL = Mono
BUFFER_SIZE = minBufferSize × 4
```

**Threading Model**:
- Uses Kotlin Coroutines (Dispatchers.Default)
- Audio generation runs on background thread
- Non-blocking operations

**Noise Generation Algorithms**:

#### White Noise
```kotlin
fun generateWhiteNoise(): Double {
    return Random.nextDouble(-1.0, 1.0)
}
```
- Pure random values
- Equal energy across all frequencies
- Flat frequency spectrum

#### Pink Noise (Paul Kellet's Algorithm)
```kotlin
fun generatePinkNoise(): Double {
    // Cascaded IIR filters
    pinkB0 = 0.99886 * pinkB0 + white * 0.0555179
    pinkB1 = 0.99332 * pinkB1 + white * 0.0750759
    // ... (6 filter stages)
    return pink * 0.11 // Scale to prevent clipping
}
```
- 1/f frequency spectrum
- Equal energy per octave
- Natural, balanced sound

#### Brown Noise (Brownian Motion)
```kotlin
fun generateBrownNoise(): Double {
    brownLastValue = (brownLastValue + white * 0.02).coerceIn(-1.0, 1.0)
    return brownLastValue * 3.5
}
```
- 1/f² frequency spectrum
- Random walk with clamping
- Deep, rumbling tone

**Fade Envelope**:
```kotlin
val fadeFactor = when {
    sampleCount < fadeSamples -> sampleCount.toFloat() / fadeSamples  // Fade-in
    !isPlaying && i < fadeSamples -> 1.0f - (i.toFloat() / fadeSamples)  // Fade-out
    else -> 1.0f
}
```
- Linear fade over 500ms
- Prevents audio clicks and pops
- Applied per-sample during generation

## Data Flow

### Play Audio Flow
```
User taps Play Button
    ↓
MainActivity detects tap
    ↓
viewModel.togglePlayPause()
    ↓
audioManager.play()
    ↓
noiseGenerator.start(noiseType)
    ↓
AudioTrack initialized
    ↓
Coroutine launched on Dispatchers.Default
    ↓
generateAudioStream() loop begins
    ↓
Continuous audio buffer generation
    ↓
AudioTrack.write(buffer)
    ↓
Audio output to device
```

### Swipe to Change Noise Type Flow
```
User swipes horizontally
    ↓
detectHorizontalDragGestures detects drag
    ↓
onDragEnd calculates new noise type
    ↓
viewModel.setNoiseType(newType)
    ↓
audioManager.setNoiseType(newType)
    ↓
If playing: stop current generator
    ↓
Update state: _currentNoiseType.value = newType
    ↓
If was playing: start new generator
    ↓
UI updates via StateFlow observation
```

## Threading Model

### Thread Distribution

| Component | Thread | Purpose |
|-----------|--------|---------|
| UI (Compose) | Main Thread | Render UI, handle user input |
| ViewModel | Main Thread | State management, coordination |
| AudioManager | Main Thread | Control operations |
| NoiseGenerator | Dispatchers.Default | Audio synthesis, buffer generation |
| AudioTrack | Native Audio Thread | Low-latency audio output |

### Synchronization

- **StateFlow**: Thread-safe state updates
- **Volatile variables**: For `isPlaying` flag
- **Coroutine cancellation**: Clean shutdown of audio generation

## Memory Management

### Resource Lifecycle

1. **Creation**: AudioTrack created when play starts
2. **Usage**: Continuous buffer generation and playback
3. **Cleanup**: AudioTrack stopped and released on pause/destroy

### Preventing Memory Leaks

```kotlin
// In MainViewModel
override fun onCleared() {
    audioManager.release() // Stops generator, releases AudioTrack
}

// In NoiseGenerator
fun release() {
    isPlaying = false
    generatorJob?.cancel()
    audioTrack?.stop()
    audioTrack?.release()
}
```

### Buffer Management

- Fixed-size buffer allocation
- Reused across audio generation loop
- No dynamic allocation during playback

## Performance Optimizations

### Audio Performance

1. **Buffer Size**: 4× minimum buffer size for smooth playback
2. **Streaming Mode**: AudioTrack.MODE_STREAM for continuous audio
3. **Background Thread**: Audio generation on Dispatchers.Default
4. **Efficient Algorithms**: Optimized noise generation algorithms

### UI Performance

1. **Compose Recomposition**: Minimal recomposition with StateFlow
2. **Animation**: Hardware-accelerated Compose animations
3. **Gesture Detection**: Efficient pointer input handling
4. **Immersive Mode**: Reduced system UI overhead

### Battery Optimization

- **Efficient Algorithms**: Minimal CPU usage for noise generation
- **No Network**: Zero network activity
- **No Background Services**: App only active when in foreground

## Security & Privacy

### Permissions

**AndroidManifest.xml**:
```xml
<!-- NO INTERNET PERMISSION -->
<!-- NO LOCATION PERMISSION -->
<!-- NO STORAGE PERMISSION -->
```

### Data Collection

- **Zero data collection**: No analytics, no tracking
- **No external communication**: 100% offline operation
- **No user data**: No personal information stored or accessed

## Testing Considerations

### Unit Testing Targets

1. **NoiseGenerator**: Test noise generation algorithms
2. **AudioManager**: Test state management logic
3. **MainViewModel**: Test UI state transformations

### Integration Testing

1. **Audio Playback**: Verify seamless looping
2. **Fade Transitions**: Verify no audio artifacts
3. **Noise Type Switching**: Verify smooth transitions

### UI Testing

1. **Gesture Detection**: Test swipe navigation
2. **Button Interaction**: Test play/pause functionality
3. **State Updates**: Test UI reflects correct state

## Future Enhancement Possibilities

### Audio Features
- Volume control
- Timer/sleep mode
- Mix multiple noise types
- Custom noise profiles

### UI Features
- Color themes
- Visualizer/waveform display
- Favorites/presets
- Widget support

### Technical Improvements
- AudioTrack to AAudio migration (lower latency)
- Kotlin Multiplatform support
- Automated testing suite
- Performance profiling

## Dependencies

### Core Dependencies
- `androidx.core:core-ktx` - Kotlin extensions
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle management
- `androidx.activity:activity-compose` - Compose integration

### Compose Dependencies
- `androidx.compose:compose-bom` - Compose Bill of Materials
- `androidx.compose.ui:ui` - Core Compose UI
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel integration

### Build Configuration
- Kotlin 1.9.20
- Compose Compiler 1.5.4
- Android Gradle Plugin 8.2.0
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)

## Conclusion

The White Noise application demonstrates a clean, modern Android architecture with:
- Clear separation of concerns
- Reactive state management
- Efficient audio processing
- Smooth user experience
- Privacy-first design

The architecture is scalable, maintainable, and follows Android best practices for production-quality applications.
