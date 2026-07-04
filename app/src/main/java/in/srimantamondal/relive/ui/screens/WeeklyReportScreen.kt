package `in`.srimantamondal.relive.ui.screens

import android.app.usage.UsageStatsManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.srimantamondal.relive.data.usage.AppUsageManager
import `in`.srimantamondal.relive.ui.UsageStatsHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)

data class DayStats(
    val dayName: String,
    val totalTime: Long
)

@Composable
fun WeeklyReportScreen() {
    val context = LocalContext.current
    val usageStatsManager = remember {
        context.getSystemService(android.content.Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    val usageManager = remember { AppUsageManager(context) }

    var weeklyData by remember { mutableStateOf<List<DayStats>>(emptyList()) }
    var totalWeekTime by remember { mutableStateOf(0L) }
    var avgDailyTime by remember { mutableStateOf(0L) }
    var hasPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasPermission = UsageStatsHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val result = mutableListOf<DayStats>()

            // Last 7 days ka data
            for (i in 6 downTo 0) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val startTime = cal.timeInMillis

                val endCal = cal.clone() as Calendar
                endCal.set(Calendar.HOUR_OF_DAY, 23)
                endCal.set(Calendar.MINUTE, 59)
                endCal.set(Calendar.SECOND, 59)
                val endTime = endCal.timeInMillis

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    startTime,
                    endTime
                )

                val dayTotal = stats?.sumOf { it.totalTimeInForeground } ?: 0L
                val dayName = dayFormat.format(Date(startTime))
                result.add(DayStats(dayName, dayTotal))
            }

            weeklyData = result
            totalWeekTime = result.sumOf { it.totalTime }
            avgDailyTime = if (result.isNotEmpty()) totalWeekTime / result.size else 0L
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Weekly Report",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Last 7 days",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!hasPermission) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Usage Access Required",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { UsageStatsHelper.openUsageAccessSettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Text("Grant Permission", color = Color.White)
                        }
                    }
                }
            }
        } else {
            item {
                // Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = PurpleAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                usageManager.formatDuration(totalWeekTime),
                                color = GreenAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Total Week",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = OrangeAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                usageManager.formatDuration(avgDailyTime),
                                color = OrangeAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Daily Avg",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Daily Breakdown",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Bar Chart
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val maxTime = weeklyData.maxOfOrNull { it.totalTime } ?: 1L

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weeklyData.forEach { day ->
                                val fraction = if (maxTime > 0) {
                                    (day.totalTime.toFloat() / maxTime.toFloat())
                                } else 0f

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .height((100 * fraction).dp)
                                            .background(
                                                color = if (fraction > 0.7f) OrangeAccent
                                                else PurpleAccent,
                                                shape = RoundedCornerShape(
                                                    topStart = 4.dp,
                                                    topEnd = 4.dp
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        day.dayName,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFF2A2A3E))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Daily list
                        weeklyData.forEach { day ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    day.dayName,
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(40.dp)
                                )
                                LinearProgressIndicator(
                                    progress = {
                                        if (maxTime > 0) day.totalTime.toFloat() / maxTime else 0f
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .padding(horizontal = 8.dp),
                                    color = PurpleAccent,
                                    trackColor = Color(0xFF2A2A3E)
                                )
                                Text(
                                    usageManager.formatDuration(day.totalTime),
                                    color = GreenAccent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}