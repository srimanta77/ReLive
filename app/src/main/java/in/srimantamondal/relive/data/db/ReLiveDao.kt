package `in`.srimantamondal.relive.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import `in`.srimantamondal.relive.data.model.ActivityRecord

@Dao
interface ReLiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ActivityRecord): Long

    @Delete
    suspend fun delete(record: ActivityRecord)

    @Update
    suspend fun update(record: ActivityRecord)

    @Query("SELECT * FROM activity_record ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityRecord>>

    @Query("DELETE FROM activity_record")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM activity_record")
    suspend fun getCount(): Int
}
