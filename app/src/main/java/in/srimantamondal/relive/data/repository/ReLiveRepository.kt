package `in`.srimantamondal.relive.data.repository

import `in`.srimantamondal.relive.data.db.ReLiveDao
import `in`.srimantamondal.relive.data.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

class ReLiveRepository(private val dao: ReLiveDao) {

    suspend fun insertRecord(record: ActivityRecord): Long =
        dao.insert(record)

    fun getAllActivities(): Flow<List<ActivityRecord>> =
        dao.getAllActivities()

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun getCount(): Int = dao.getCount()
}
