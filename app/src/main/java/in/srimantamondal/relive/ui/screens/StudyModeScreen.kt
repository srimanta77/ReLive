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

@Composable
fun StudyModeScreen() {
    var isStudying by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableStateOf(0) }
    var studyGoalMinutes by remember { mutableStateOf(60) }
    var subject by remember { mutableStateOf("") }
    var completedSessions by remember { mutableStateOf(0) }
    var totalStudyTime by remember { mutableStateOf(0) }

    LaunchedEffect(isStudying) {
        if (isStudying) {
            while (isStudying) {
                delay(1000)
                timeElapsed++
                if (timeElapsed >= studyGoalMinutes * 60) {
                    isStudying = false
                    completedSessions++
                    totalStudyTime += studyGoalMinutes
                    timeElapsed = 0
                }
            }
        }
    }

    val progress = if (studyGoalMinutes > 0) {
        (timeElapsed.toFloat() / (studyGoalMinutes * 60f)).coerceIn(0f, 1f)
    } else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Study Mode",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Track your study sessions",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        item {
            // Subject input
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject / Topic", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isStudying,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = Color(0xFF2A2A3E),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                leadingIcon = {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null,
                        tint = PurpleAccent
                    )
                }
            )
        }

        item {
            // Goal selector
            Text(
                "Study Goal",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 45, 60, 90).forEach { mins ->
                    val isSelected = studyGoalMinutes == mins
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PurpleAccent else CardBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            if (!isStudying) {
                                studyGoalMinutes = mins
                                timeElapsed = 0
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${mins}m",
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            // Timer circle
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                )
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(200.dp),
                    color = GreenAccent,
                    strokeWidth = 8.dp,
                    trackColor = Color(0xFF2A2A3E)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(timeElapsed),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        "/ ${studyGoalMinutes}m",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    if (subject.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            subject,
                            fontSize = 11.sp,
                            color = PurpleAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        item {
            // Stats row
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
                        Text(
                            "$completedSessions",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                        Text("Sessions", color = TextSecondary, fontSize = 11.sp)
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
                        Text(
                            "${totalStudyTime}m",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenAccent
                        )
                        Text("Total Time", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            // Control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isStudying = false
                        timeElapsed = 0
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Button(
                    onClick = { isStudying = !isStudying },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStudying) OrangeAccent else GreenAccent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        if (isStudying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isStudying) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (isStudying) {
                            isStudying = false
                            completedSessions++
                            totalStudyTime += timeElapsed / 60
                            timeElapsed = 0
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = GreenAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        item {
            // Tip card
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
                        if (isStudying) "Great! Stay focused. Avoid distractions."
                        else "Set your subject, choose a goal, and start studying!",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}