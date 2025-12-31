package com.whitenoise.app

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

/**
 * Real-time audio synthesis engine for generating white, pink, and brown noise
 * Uses AudioTrack for low-latency playback with seamless looping
 */
class NoiseGenerator {
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val BUFFER_SIZE_MULTIPLIER = 4
        private const val FADE_DURATION_MS = 500
    }
    
    private var audioTrack: AudioTrack? = null
    private var generatorJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    
    // Pink noise state variables
    private var pinkB0 = 0.0
    private var pinkB1 = 0.0
    private var pinkB2 = 0.0
    private var pinkB3 = 0.0
    private var pinkB4 = 0.0
    private var pinkB5 = 0.0
    private var pinkB6 = 0.0
    
    // Brown noise state variable
    private var brownLastValue = 0.0
    
    @Volatile
    private var isPlaying = false
    
    @Volatile
    private var currentNoiseType = NoiseType.WHITE
    
    /**
     * Start generating and playing noise
     */
    fun start(noiseType: NoiseType) {
        if (isPlaying) {
            stop()
        }
        
        currentNoiseType = noiseType
        resetNoiseState()
        
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        val bufferSize = minBufferSize * BUFFER_SIZE_MULTIPLIER
        
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
        
        isPlaying = true
        audioTrack?.play()
        
        generatorJob = scope.launch {
            generateAudioStream(bufferSize / 2) // Divide by 2 because we're using 16-bit samples
        }
    }
    
    /**
     * Stop playing noise with fade-out
     */
    fun stop() {
        isPlaying = false
        generatorJob?.cancel()
        generatorJob = null
        
        scope.launch {
            delay(FADE_DURATION_MS.toLong() + 100) // Wait for fade-out to complete
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        }
    }
    
    /**
     * Check if currently playing
     */
    fun isPlaying(): Boolean = isPlaying
    
    /**
     * Clean up resources
     */
    fun release() {
        stop()
    }
    
    /**
     * Reset noise generation state variables
     */
    private fun resetNoiseState() {
        pinkB0 = 0.0
        pinkB1 = 0.0
        pinkB2 = 0.0
        pinkB3 = 0.0
        pinkB4 = 0.0
        pinkB5 = 0.0
        pinkB6 = 0.0
        brownLastValue = 0.0
    }
    
    /**
     * Generate continuous audio stream with fade-in/fade-out
     */
    private suspend fun generateAudioStream(bufferSizeInSamples: Int) {
        val buffer = ShortArray(bufferSizeInSamples)
        val fadeSamples = (SAMPLE_RATE * FADE_DURATION_MS) / 1000
        var sampleCount = 0
        
        while (isActive && isPlaying) {
            // Generate noise samples
            for (i in buffer.indices) {
                val sample = when (currentNoiseType) {
                    NoiseType.WHITE -> generateWhiteNoise()
                    NoiseType.PINK -> generatePinkNoise()
                    NoiseType.BROWN -> generateBrownNoise()
                }
                
                // Apply fade envelope
                val fadeFactor = when {
                    sampleCount < fadeSamples -> {
                        // Fade-in
                        sampleCount.toFloat() / fadeSamples
                    }
                    !isPlaying && i < fadeSamples -> {
                        // Fade-out
                        1.0f - (i.toFloat() / fadeSamples)
                    }
                    else -> 1.0f
                }
                
                buffer[i] = (sample * fadeFactor * Short.MAX_VALUE).toInt().toShort()
                sampleCount++
            }
            
            // Write to AudioTrack
            audioTrack?.write(buffer, 0, buffer.size)
            
            // Check if we should stop (for fade-out)
            if (!isPlaying) {
                break
            }
        }
    }
    
    /**
     * Generate white noise sample
     * White noise has equal energy across all frequencies
     */
    private fun generateWhiteNoise(): Double {
        return Random.nextDouble(-1.0, 1.0)
    }
    
    /**
     * Generate pink noise sample using Paul Kellet's algorithm
     * Pink noise has equal energy per octave (1/f spectrum)
     */
    private fun generatePinkNoise(): Double {
        val white = Random.nextDouble(-1.0, 1.0)
        
        pinkB0 = 0.99886 * pinkB0 + white * 0.0555179
        pinkB1 = 0.99332 * pinkB1 + white * 0.0750759
        pinkB2 = 0.96900 * pinkB2 + white * 0.1538520
        pinkB3 = 0.86650 * pinkB3 + white * 0.3104856
        pinkB4 = 0.55000 * pinkB4 + white * 0.5329522
        pinkB5 = -0.7616 * pinkB5 - white * 0.0168980
        
        val pink = pinkB0 + pinkB1 + pinkB2 + pinkB3 + pinkB4 + pinkB5 + pinkB6 + white * 0.5362
        pinkB6 = white * 0.115926
        
        return pink * 0.11 // Scale down to prevent clipping
    }
    
    /**
     * Generate brown noise sample (Brownian noise/red noise)
     * Brown noise has 1/f² spectrum, deeper and more rumbling than pink noise
     */
    private fun generateBrownNoise(): Double {
        val white = Random.nextDouble(-1.0, 1.0)
        brownLastValue = (brownLastValue + white * 0.02).coerceIn(-1.0, 1.0)
        return brownLastValue * 3.5 // Amplify as brown noise is naturally quieter
    }
}
