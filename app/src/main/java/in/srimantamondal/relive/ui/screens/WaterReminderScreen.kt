package `in`.srimantamondal.relive.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val BlueAccent = Color(0xFF40C4FF)

@Composable
fun WaterReminderScreen() {
    var glassesConsumed by remember { mutableStateOf(0) }
    val dailyGoal = 8

    val progress = glassesConsumed.toFloat() / dailyGoal.toFloat()
    val isGoalReached = glassesConsumed >= dailyGoal

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Water Reminder",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Stay hydrated throughout the day",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // Main water card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGoalReached) Color(0xFF0D2818) else CardBg
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Water drop icon big
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BlueAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "$glassesConsumed",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGoalReached) GreenAccent else BlueAccent
                    )
                    Text(
                        "of $dailyGoal glasses",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = if (isGoalReached) GreenAccent else BlueAccent,
                        trackColor = Color(0xFF2A2A3E)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        if (isGoalReached) "🎉 Daily goal reached! Great job!"
                        else "${dailyGoal - glassesConsumed} more glasses to go!",
                        color = if (isGoalReached) GreenAccent else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isGoalReached) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        item {
            // Glass grid
            Text(
                "Track Your Glasses",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4x2 grid of glasses
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0..1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0..3) {
                            val glassIndex = row * 4 + col + 1
                            val isFilled = glassIndex <= glassesConsumed
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isFilled) BlueAccent.copy(alpha = 0.2f)
                                    else CardBg
                                ),
                                shape = RoundedCornerShape(10.dp),
                                onClick = {
                                    glassesConsumed = if (isFilled) glassIndex - 1
                                    else glassIndex
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = if (isFilled) BlueAccent else Color(0xFF2A2A3E),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Quick add/remove buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { if (glassesConsumed > 0) glassesConsumed-- },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CardBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove", color = TextSecondary)
                }

                Button(
                    onClick = {
                        if (glassesConsumed < dailyGoal) glassesConsumed++
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Glass", color = Color.White)
                }
            }
        }

        item {
            // Tips card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1628)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💡 Hydration Tips",
                        color = BlueAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Drink a glass of water when you wake up",
                        "Keep a water bottle at your desk",
                        "Drink before every meal",
                        "Set reminders every 2 hours"
                    ).forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = BlueAccent, fontSize = 12.sp)
                            Text(tip, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}