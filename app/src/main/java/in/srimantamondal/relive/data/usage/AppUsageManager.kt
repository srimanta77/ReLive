package `in`.srimantamondal.relive.data.usage

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long, // milliseconds
    val lastTimeUsed: Long
)

class AppUsageManager(private val context: Context) {

    /**
     * Aaj ka usage data fetch karo
     */
    fun getTodayUsageStats(): List<AppUsageInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as UsageStatsManager

        // Aaj ka start time (midnight se)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        if (usageStatsList.isNullOrEmpty()) return emptyList()

        val packageManager = context.packageManager

        return usageStatsList
            .filter { it.totalTimeInForeground > 0 }
            .map { stats ->
                val appName = try {
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(stats.packageName, 0)
                    ).toString()
                } catch (e: Exception) {
                    stats.packageName
                }
                AppUsageInfo(
                    packageName = stats.packageName,
                    appName = appName,
                    totalTimeInForeground = stats.totalTimeInForeground,
                    lastTimeUsed = stats.lastTimeUsed
                )
            }
            .sortedByDescending { it.totalTimeInForeground }
    }

    /**
     * Total screen time aaj ka — milliseconds mein
     */
    fun getTotalScreenTimeToday(): Long {
        return getTodayUsageStats().sumOf { it.totalTimeInForeground }
    }

    /**
     * Milliseconds ko readable format mein convert karo
     * e.g. 3661000 -> "1h 1m"
     */
    fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return when {
            hours > 0 -> "${hours}h ${remainingMinutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}