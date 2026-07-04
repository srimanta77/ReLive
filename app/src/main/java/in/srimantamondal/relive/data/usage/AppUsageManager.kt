package `in`.srimantamondal.relive.data.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTimeUsed: Long
)

class AppUsageManager(private val context: Context) {

    fun getTodayUsageStats(): List<AppUsageInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as UsageStatsManager

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
                    val appInfo = packageManager.getApplicationInfo(
                        stats.packageName,
                        android.content.pm.PackageManager.GET_META_DATA
                    )
                    packageManager.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    // Package name se readable naam banao
                    // e.g. com.instagram.android -> Instagram
                    stats.packageName
                        .split(".")
                        .lastOrNull()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: stats.packageName
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

    fun getTotalScreenTimeToday(): Long {
        return getTodayUsageStats().sumOf { it.totalTimeInForeground }
    }

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