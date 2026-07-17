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
private val OrangeAccent = Color(0xFFFFB74D)

data class SleepRecord(
    val day: String,
    val hours: Float,
    val quality: String
)

@Composable
fun SleepTrackerScreen() {
    var bedTime by remember { mutableStateOf("11:00 PM") }
    var wakeTime by remember { mutableStateOf("7:00 AM") }
    var sleepHours by remember { mutableStateOf(8f) }
    var sleepQuality by remember { mutableStateOf(3) } // 1-5 stars
    var showBedTimePicker by remember { mutableStateOf(false) }

    val weeklyData = remember {
        listOf(
            SleepRecord("Mon", 7.5f, "Good"),
            SleepRecord("Tue", 6.0f, "Fair"),
            SleepRecord("Wed", 8.0f, "Great"),
            SleepRecord("Thu", 5.5f, "Poor"),
            SleepRecord("Fri", 7.0f, "Good"),
            SleepRecord("Sat", 9.0f, "Great"),
            SleepRecord("Sun", 8.0f, "Great"),
        )
    }

    val avgSleep = weeklyData.map { it.hours }.average().toFloat()
    val goalHours = 8f
    val isGoalMet = sleepHours >= goalHours

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Sleep Tracker",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Track your sleep for better health",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // Tonight's sleep card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGoalMet) Color(0xFF0D2818) else CardBg
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Bedtime,
                        contentDescription = null,
                        tint = BlueAccent,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${sleepHours.toInt()}h ${((sleepHours % 1) * 60).toInt()}m",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGoalMet) GreenAccent else BlueAccent
                    )
                    Text(
                        if (isGoalMet) "✅ Goal reached! (${goalHours.toInt()}h target)"
                        else "Target: ${goalHours.toInt()}h per night",
                        color = if (isGoalMet) GreenAccent else TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { (sleepHours / goalHours).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (isGoalMet) GreenAccent else BlueAccent,
                        trackColor = Color(0xFF2A2A3E)
                    )
                }
            }
        }

        item {
            // Sleep time settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bed Time", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            bedTime,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = OrangeAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Wake Time", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            wakeTime,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        item {
            // Sleep duration slider
            Text(
                "Adjust Sleep Duration",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("4h", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            "${sleepHours.toInt()}h ${((sleepHours % 1) * 60).toInt()}m",
                            color = BlueAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text("12h", color = TextSecondary, fontSize = 12.sp)
                    }
                    Slider(
                        value = sleepHours,
                        onValueChange = { sleepHours = it },
                        valueRange = 4f..12f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = BlueAccent,
                            activeTrackColor = BlueAccent,
                            inactiveTrackColor = Color(0xFF2A2A3E)
                        )
                    )
                }
            }
        }

        item {
            // Sleep quality
            Text(
                "Sleep Quality",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { sleepQuality = star }) {
                            Icon(
                                if (star <= sleepQuality) Icons.Default.Star
                                else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (star <= sleepQuality) OrangeAccent
                                else TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            // Weekly chart
            Text(
                "This Week",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Avg: ${String.format("%.1f", avgSleep)}h",
                            color = BlueAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            "Goal: ${goalHours.toInt()}h",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyData.forEach { record ->
                            val fraction = record.hours / 12f
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height((70 * fraction).dp)
                                        .background(
                                            color = if (record.hours >= goalHours) GreenAccent
                                            else BlueAccent,
                                            shape = RoundedCornerShape(
                                                topStart = 4.dp, topEnd = 4.dp
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    record.day,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1628)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "💡 Sleep Tips",
                        color = BlueAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "Maintain a consistent sleep schedule",
                        "Avoid screens 1 hour before bed",
                        "Keep your room cool and dark",
                        "Avoid caffeine after 2 PM"
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