package `in`.srimantamondal.relive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
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
import `in`.srimantamondal.relive.data.usage.ScreenTimeLimitManager
import `in`.srimantamondal.relive.ui.UsageStatsHelper
import kotlinx.coroutines.launch

private val NavyBg = Color(0xFF0B132B)
private val CardBg = Color(0xFF1C2541)
private val PurpleAccent = Color(0xFF7C4DFF)
private val TextPrimary = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB0BEC5)
private val GreenAccent = Color(0xFF69F0AE)
private val OrangeAccent = Color(0xFFFFB74D)
private val RedAccent = Color(0xFFFF5252)

@Composable
fun ScreenTimeLimitScreen() {
    val context = LocalContext.current
    val limitManager = remember { ScreenTimeLimitManager(context) }
    val usageManager = remember { AppUsageManager(context) }
    val scope = rememberCoroutineScope()

    val dailyLimit by limitManager.dailyLimit.collectAsState(initial = ScreenTimeLimitManager.NO_LIMIT)
    var currentUsage by remember { mutableStateOf(0L) }
    var hasPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasPermission = UsageStatsHelper.hasUsageStatsPermission(context)
        if (hasPermission) {
            currentUsage = usageManager.getTotalScreenTimeToday()
        }
    }

    val usagePercentage = limitManager.getUsagePercentage(currentUsage, dailyLimit)
    val isExceeded = limitManager.isLimitExceeded(currentUsage, dailyLimit)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            "Screen Time Limit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            "Set daily screen time goals",
            fontSize = 13.sp,
            color = TextSecondary
        )

        // Current Usage Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isExceeded) Color(0xFF2D1B1B) else CardBg
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        if (isExceeded) Icons.Default.Warning else Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isExceeded) RedAccent else PurpleAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            "Today's Usage",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            usageManager.formatDuration(currentUsage),
                            color = if (isExceeded) RedAccent else GreenAccent,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (dailyLimit != ScreenTimeLimitManager.NO_LIMIT) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Limit: ${limitManager.formatLimit(dailyLimit)}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            "${(usagePercentage * 100).toInt()}% used",
                            color = if (isExceeded) RedAccent else OrangeAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { usagePercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (isExceeded) RedAccent else PurpleAccent,
                        trackColor = Color(0xFF2A2A3E)
                    )

                    if (isExceeded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "⚠️ Daily limit exceeded!",
                            color = RedAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Set Limit Section
        Text(
            "Set Daily Limit",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        val limits = listOf(
            ScreenTimeLimitManager.NO_LIMIT to "No Limit",
            ScreenTimeLimitManager.LIMIT_1H to "1 Hour",
            ScreenTimeLimitManager.LIMIT_2H to "2 Hours",
            ScreenTimeLimitManager.LIMIT_3H to "3 Hours",
            ScreenTimeLimitManager.LIMIT_4H to "4 Hours"
        )

        limits.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (limitValue, labelText) ->
                    val isSelected = dailyLimit == limitValue
                    Card(
                        modifier = Modifier
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PurpleAccent else CardBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            scope.launch {
                                limitManager.setDailyLimit(limitValue)
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                labelText,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                // Agar row mein sirf 1 item hai toh empty space
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Status message
        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Grant Usage Access to track screen time",
                        color = TextSecondary,
                        fontSize = 13.sp
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
    }
}