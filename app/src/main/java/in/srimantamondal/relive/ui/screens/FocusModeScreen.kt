package `in`.srimantamondal.relive.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)

enum class FocusMode { FOCUS, SHORT_BREAK, LONG_BREAK }

@Composable
fun FocusModeScreen() {
    var selectedMode by remember { mutableStateOf(FocusMode.FOCUS) }
    var isRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var sessionsCompleted by remember { mutableStateOf(0) }

    LaunchedEffect(isRunning, selectedMode) {
        if (isRunning) {
            while (timeLeft > 0 && isRunning) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                isRunning = false
                if (selectedMode == FocusMode.FOCUS) sessionsCompleted++
            }
        }
    }

    LaunchedEffect(selectedMode) {
        isRunning = false
        timeLeft = when (selectedMode) {
            FocusMode.FOCUS -> 25 * 60
            FocusMode.SHORT_BREAK -> 5 * 60
            FocusMode.LONG_BREAK -> 15 * 60
        }
    }

    val totalTime = when (selectedMode) {
        FocusMode.FOCUS -> 25 * 60f
        FocusMode.SHORT_BREAK -> 5 * 60f
        FocusMode.LONG_BREAK -> 15 * 60f
    }
    val progress = timeLeft / totalTime

    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = EaseInOut),
            RepeatMode.Reverse
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Focus Mode",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FocusMode.values().forEach { mode ->
                    val label = when (mode) {
                        FocusMode.FOCUS -> "Focus"
                        FocusMode.SHORT_BREAK -> "Short Break"
                        FocusMode.LONG_BREAK -> "Long Break"
                    }
                    val isSelected = selectedMode == mode
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PurpleAccent else CardBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { selectedMode = mode }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier.size(240.dp).scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(240.dp).clip(CircleShape).background(CardBg)
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(240.dp),
                    color = when (selectedMode) {
                        FocusMode.FOCUS -> PurpleAccent
                        FocusMode.SHORT_BREAK -> GreenAccent
                        FocusMode.LONG_BREAK -> OrangeAccent
                    },
                    strokeWidth = 8.dp,
                    trackColor = Color(0xFF2A2A3E)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(timeLeft),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        when (selectedMode) {
                            FocusMode.FOCUS -> "Stay focused!"
                            FocusMode.SHORT_BREAK -> "Take a break"
                            FocusMode.LONG_BREAK -> "Rest well"
                        },
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$sessionsCompleted",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                        Text("Sessions", color = TextSecondary, fontSize = 12.sp)
                    }
                    Divider(
                        modifier = Modifier.height(50.dp).width(1.dp),
                        color = Color(0xFF2A2A3E)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${sessionsCompleted * 25}m",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenAccent
                        )
                        Text("Focus Time", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        timeLeft = when (selectedMode) {
                            FocusMode.FOCUS -> 25 * 60
                            FocusMode.SHORT_BREAK -> 5 * 60
                            FocusMode.LONG_BREAK -> 15 * 60
                        }
                    },
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(CardBg)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondary)
                }

                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedMode) {
                            FocusMode.FOCUS -> PurpleAccent
                            FocusMode.SHORT_BREAK -> GreenAccent
                            FocusMode.LONG_BREAK -> OrangeAccent
                        }
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = { isRunning = false; timeLeft = 0 },
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(CardBg)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = TextSecondary)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1040)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        when (selectedMode) {
                            FocusMode.FOCUS -> "Put your phone face down and focus on one task."
                            FocusMode.SHORT_BREAK -> "Stand up, stretch, rest your eyes."
                            FocusMode.LONG_BREAK -> "Take a walk, hydrate, come back refreshed!"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}