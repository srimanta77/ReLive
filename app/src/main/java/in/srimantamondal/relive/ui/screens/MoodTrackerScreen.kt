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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)
private val RedAccent = Color(0xFFFF5252)
private val BlueAccent = Color(0xFF40C4FF)

data class Mood(
    val emoji: String,
    val label: String,
    val color: Color,
    val score: Int
)

data class MoodEntry(
    val day: String,
    val mood: Mood
)

@Composable
fun MoodTrackerScreen() {
    val moods = listOf(
        Mood("😢", "Terrible", RedAccent, 1),
        Mood("😕", "Bad", OrangeAccent, 2),
        Mood("😐", "Okay", BlueAccent, 3),
        Mood("😊", "Good", GreenAccent, 4),
        Mood("🤩", "Amazing", PurpleAccent, 5)
    )

    var selectedMood by remember { mutableStateOf<Mood?>(null) }
    var moodNote by remember { mutableStateOf("") }
    var moodSaved by remember { mutableStateOf(false) }

    val weeklyMoods = remember {
        listOf(
            MoodEntry("Mon", moods[3]),
            MoodEntry("Tue", moods[2]),
            MoodEntry("Wed", moods[4]),
            MoodEntry("Thu", moods[1]),
            MoodEntry("Fri", moods[3]),
            MoodEntry("Sat", moods[4]),
            MoodEntry("Sun", moods[2]),
        )
    }

    val avgMoodScore = weeklyMoods.map { it.mood.score }.average()
    val avgMoodLabel = when {
        avgMoodScore >= 4.5 -> "Amazing week!"
        avgMoodScore >= 3.5 -> "Good week!"
        avgMoodScore >= 2.5 -> "Okay week"
        else -> "Tough week"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Mood Tracker",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "How are you feeling today?",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // Mood selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (selectedMood != null) selectedMood!!.emoji
                        else "🙂",
                        fontSize = 56.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        selectedMood?.label ?: "Select your mood",
                        color = selectedMood?.color ?: TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        moods.forEach { mood ->
                            val isSelected = selectedMood == mood
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) mood.color.copy(alpha = 0.25f)
                                            else Color(0xFF2A2A3E)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(
                                        onClick = {
                                            selectedMood = mood
                                            moodSaved = false
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(mood.emoji, fontSize = 24.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    mood.label,
                                    color = if (isSelected) mood.color else TextSecondary,
                                    fontSize = 9.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Note input
            OutlinedTextField(
                value = moodNote,
                onValueChange = { moodNote = it },
                label = { Text("Add a note (optional)", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = Color(0xFF2A2A3E),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                placeholder = {
                    Text(
                        "What's on your mind?",
                        color = TextSecondary.copy(alpha = 0.5f)
                    )
                }
            )
        }

        item {
            // Save button
            Button(
                onClick = {
                    if (selectedMood != null) {
                        moodSaved = true
                        moodNote = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedMood?.color ?: PurpleAccent
                ),
                shape = RoundedCornerShape(10.dp),
                enabled = selectedMood != null
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (moodSaved) "Mood Saved! ✓" else "Save Today's Mood",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            // Weekly overview
            Text(
                "This Week",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Weekly Mood",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            avgMoodLabel,
                            color = GreenAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weeklyMoods.forEach { entry ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(entry.mood.emoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    entry.day,
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
            // Mood stats
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
                        Text("😊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            weeklyMoods.count { it.mood.score >= 4 }.toString(),
                            color = GreenAccent,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Good Days", color = TextSecondary, fontSize = 11.sp)
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
                        Text("📊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            String.format("%.1f", avgMoodScore),
                            color = PurpleAccent,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Avg Score", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}