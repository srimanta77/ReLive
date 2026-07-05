package `in`.srimantamondal.relive.data.usage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.limitDataStore by preferencesDataStore(name = "screen_time_limits")

class ScreenTimeLimitManager(private val context: Context) {

    companion object {
        private val DAILY_LIMIT_KEY = longPreferencesKey("daily_limit_millis")

        // Default limits
        const val LIMIT_1H = 60 * 60 * 1000L
        const val LIMIT_2H = 2 * 60 * 60 * 1000L
        const val LIMIT_3H = 3 * 60 * 60 * 1000L
        const val LIMIT_4H = 4 * 60 * 60 * 1000L
        const val NO_LIMIT = 0L
    }

    // Daily limit flow
    val dailyLimit: Flow<Long> = context.limitDataStore.data
        .map { prefs -> prefs[DAILY_LIMIT_KEY] ?: NO_LIMIT }

    // Set daily limit
    suspend fun setDailyLimit(millis: Long) {
        context.limitDataStore.edit { prefs ->
            prefs[DAILY_LIMIT_KEY] = millis
        }
    }

    // Check karo limit cross hui ya nahi
    fun isLimitExceeded(currentUsageMillis: Long, limitMillis: Long): Boolean {
        if (limitMillis == NO_LIMIT) return false
        return currentUsageMillis >= limitMillis
    }

    // Percentage calculate karo
    fun getUsagePercentage(currentUsageMillis: Long, limitMillis: Long): Float {
        if (limitMillis == NO_LIMIT) return 0f
        return (currentUsageMillis.toFloat() / limitMillis.toFloat()).coerceIn(0f, 1f)
    }

    // Readable format
    fun formatLimit(millis: Long): String {
        return when (millis) {
            NO_LIMIT -> "No Limit"
            LIMIT_1H -> "1 Hour"
            LIMIT_2H -> "2 Hours"
            LIMIT_3H -> "3 Hours"
            LIMIT_4H -> "4 Hours"
            else -> {
                val hours = millis / (60 * 60 * 1000)
                val minutes = (millis % (60 * 60 * 1000)) / (60 * 1000)
                if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            }
        }
    }
}