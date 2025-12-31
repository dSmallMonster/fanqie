package com.whitenoise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge and hide system bars for immersive experience
        enableEdgeToEdge()
        hideSystemBars()
        
        setContent {
            WhiteNoiseApp()
        }
    }
    
    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
fun WhiteNoiseApp(viewModel: MainViewModel = viewModel()) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentNoiseType by viewModel.currentNoiseType.collectAsState()
    
    // Swipe detection state
    var dragOffset by remember { mutableStateOf(0f) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E27),
                        Color(0xFF1A1F3A)
                    )
                )
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(dragOffset) > 100) {
                            val newType = when (currentNoiseType) {
                                NoiseType.WHITE -> if (dragOffset > 0) NoiseType.BROWN else NoiseType.PINK
                                NoiseType.PINK -> if (dragOffset > 0) NoiseType.WHITE else NoiseType.BROWN
                                NoiseType.BROWN -> if (dragOffset > 0) NoiseType.PINK else NoiseType.WHITE
                            }
                            viewModel.setNoiseType(newType)
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Noise type indicator
            NoiseTypeIndicator(
                currentNoiseType = currentNoiseType,
                modifier = Modifier.padding(top = 48.dp)
            )
            
            // Center: Play/Pause button with breathing animation
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = { viewModel.togglePlayPause() }
            )
            
            // Bottom: Swipe hint
            SwipeHint(modifier = Modifier.padding(bottom = 48.dp))
        }
    }
}

@Composable
fun NoiseTypeIndicator(
    currentNoiseType: NoiseType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (currentNoiseType) {
                NoiseType.WHITE -> "White Noise"
                NoiseType.PINK -> "Pink Noise"
                NoiseType.BROWN -> "Brown Noise"
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when (currentNoiseType) {
                NoiseType.WHITE -> "Equal energy across all frequencies"
                NoiseType.PINK -> "Equal energy per octave"
                NoiseType.BROWN -> "Deep, rumbling tone"
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    // Breathing animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val buttonScale = if (isPlaying) scale else 1f
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(160.dp)
            .scale(buttonScale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPlaying) Color(0xFF4CAF50) else Color(0xFF2196F3)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Text(
            text = if (isPlaying) "⏸" else "▶",
            fontSize = 48.sp,
            color = Color.White
        )
    }
}

@Composable
fun SwipeHint(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "← Swipe to change noise type →",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}
