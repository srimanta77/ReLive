package `in`.srimantamondal.relive.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single tracked activity record.
 * Fields: id (auto-generated), title, notes, timestamp (epoch ms)
 */
@Entity(tableName = "activity_record")
data class ActivityRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val notes: String? = null,
    val timestamp: Long
)
