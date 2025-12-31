package com.whitenoise.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages audio playback state and controls the NoiseGenerator
 * Provides a clean interface for the UI layer
 */
class AudioManager {
    
    private val noiseGenerator = NoiseGenerator()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentNoiseType = MutableStateFlow(NoiseType.WHITE)
    val currentNoiseType: StateFlow<NoiseType> = _currentNoiseType.asStateFlow()
    
    /**
     * Toggle play/pause state
     */
    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }
    
    /**
     * Start playing current noise type
     */
    fun play() {
        if (!_isPlaying.value) {
            noiseGenerator.start(_currentNoiseType.value)
            _isPlaying.value = true
        }
    }
    
    /**
     * Pause playback
     */
    fun pause() {
        if (_isPlaying.value) {
            noiseGenerator.stop()
            _isPlaying.value = false
        }
    }
    
    /**
     * Switch to a different noise type
     * If currently playing, restart with new noise type
     */
    fun setNoiseType(noiseType: NoiseType) {
        if (_currentNoiseType.value != noiseType) {
            val wasPlaying = _isPlaying.value
            
            if (wasPlaying) {
                noiseGenerator.stop()
            }
            
            _currentNoiseType.value = noiseType
            
            if (wasPlaying) {
                noiseGenerator.start(noiseType)
            }
        }
    }
    
    /**
     * Release all resources
     */
    fun release() {
        noiseGenerator.release()
        _isPlaying.value = false
    }
}
