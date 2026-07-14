package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
private val OrangeAccent = Color(0xFFFFB74D)

data class Habit(
    val id: Int,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val targetCount: Int = 1,
    var completedCount: Int = 0,
    var isCompleted: Boolean = false
)

@Composable
fun HabitTrackerScreen() {
    var habits by remember {
        mutableStateOf(
            listOf(
                Habit(1, "Drink Water (8 glasses)", Icons.Default.WaterDrop, 8),
                Habit(2, "Exercise / Walk", Icons.Default.DirectionsWalk, 1),
                Habit(3, "Read Book", Icons.Default.MenuBook, 1),
                Habit(4, "Meditation", Icons.Default.SelfImprovement, 1),
                Habit(5, "No Social Media", Icons.Default.PhoneLocked, 1),
                Habit(6, "Sleep 8 Hours", Icons.Default.Bedtime, 1),
                Habit(7, "Healthy Meal", Icons.Default.Restaurant, 3),
                Habit(8, "Study / Learn", Icons.Default.School, 1),
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }

    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size
    val overallProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Habit Tracker",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Build healthy daily habits",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Today's Progress",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                "$completedCount / $totalCount habits",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    if (overallProgress == 1f) GreenAccent.copy(alpha = 0.2f)
                                    else PurpleAccent.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${(overallProgress * 100).toInt()}%",
                                color = if (overallProgress == 1f) GreenAccent else PurpleAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { overallProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (overallProgress == 1f) GreenAccent else PurpleAccent,
                        trackColor = Color(0xFF2A2A3E)
                    )
                    if (overallProgress == 1f) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "🎉 Amazing! All habits completed today!",
                            color = GreenAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Habits",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Text("+ Add", color = PurpleAccent)
                }
            }
        }

        items(habits) { habit ->
            HabitItem(
                habit = habit,
                onToggle = {
                    habits = habits.map { h ->
                        if (h.id == habit.id) {
                            if (h.targetCount == 1) {
                                h.copy(isCompleted = !h.isCompleted)
                            } else {
                                val newCount = if (h.completedCount < h.targetCount)
                                    h.completedCount + 1 else 0
                                h.copy(
                                    completedCount = newCount,
                                    isCompleted = newCount >= h.targetCount
                                )
                            }
                        } else h
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Add Habit Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = CardBg,
            title = {
                Text("Add New Habit", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    label = { Text("Habit Name", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleAccent,
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newHabitName.isNotBlank()) {
                        val newId = (habits.maxOfOrNull { it.id } ?: 0) + 1
                        habits = habits + Habit(
                            newId, newHabitName, Icons.Default.CheckCircle
                        )
                        newHabitName = ""
                        showAddDialog = false
                    }
                }) {
                    Text("Add", color = PurpleAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newHabitName = ""
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun HabitItem(habit: Habit, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompleted) Color(0xFF0D2818) else CardBg
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (habit.isCompleted) GreenAccent.copy(alpha = 0.2f)
                        else PurpleAccent.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    habit.icon,
                    contentDescription = null,
                    tint = if (habit.isCompleted) GreenAccent else PurpleAccent,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Name + progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.name,
                    color = if (habit.isCompleted) GreenAccent else TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                if (habit.targetCount > 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${habit.completedCount} / ${habit.targetCount}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    LinearProgressIndicator(
                        progress = {
                            habit.completedCount.toFloat() / habit.targetCount.toFloat()
                        },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = if (habit.isCompleted) GreenAccent else PurpleAccent,
                        trackColor = Color(0xFF2A2A3E)
                    )
                }
            }

            // Toggle button
            IconButton(onClick = onToggle) {
                Icon(
                    if (habit.isCompleted) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (habit.isCompleted) GreenAccent else TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}