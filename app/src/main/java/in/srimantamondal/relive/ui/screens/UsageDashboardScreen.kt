package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.srimantamondal.relive.data.usage.AppUsageInfo
import `in`.srimantamondal.relive.data.usage.AppUsageManager
import `in`.srimantamondal.relive.ui.UsageStatsHelper

// Brand Colors
private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)

@Composable
fun UsageDashboardScreen() {
    val context = LocalContext.current
    val usageManager = remember { AppUsageManager(context) }

    var usageList by remember { mutableStateOf<List<AppUsageInfo>>(emptyList()) }
    var totalScreenTime by remember { mutableStateOf(0L) }
    var hasPermission by remember { mutableStateOf(false) }

    // Load usage data
    LaunchedEffect(Unit) {
        hasPermission = UsageStatsHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            usageList = usageManager.getTodayUsageStats()
            totalScreenTime = usageManager.getTotalScreenTimeToday()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Today's Screen Time",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasPermission) {
            // No permission card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Usage Access Required",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Grant Usage Access permission to see your screen time data.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { UsageStatsHelper.openUsageAccessSettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                    ) {
                        Text("Grant Permission", color = Color.White)
                    }
                }
            }
        } else {
            // Total Screen Time Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Total Screen Time",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            usageManager.formatDuration(totalScreenTime),
                            color = GreenAccent,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App list header
            Text(
                "App Usage",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (usageList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No usage data available yet.",
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(usageList.take(15)) { appInfo ->
                        AppUsageItem(
                            appInfo = appInfo,
                            totalTime = totalScreenTime,
                            formattedTime = usageManager.formatDuration(
                                appInfo.totalTimeInForeground
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppUsageItem(
    appInfo: AppUsageInfo,
    totalTime: Long,
    formattedTime: String
) {
    val percentage = if (totalTime > 0) {
        (appInfo.totalTimeInForeground.toFloat() / totalTime.toFloat())
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        appInfo.appName,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    formattedTime,
                    color = GreenAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PurpleAccent,
                trackColor = Color(0xFF2A2A3E)
            )
        }
    }
}