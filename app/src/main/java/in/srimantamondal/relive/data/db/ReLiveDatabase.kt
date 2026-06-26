package `in`.srimantamondal.relive.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import `in`.srimantamondal.relive.data.model.ActivityRecord

@Database(entities = [ActivityRecord::class], version = 1, exportSchema = false)
abstract class ReLiveDatabase : RoomDatabase() {
    abstract fun reliveDao(): ReLiveDao

    companion object {
        @Volatile
        private var INSTANCE: ReLiveDatabase? = null

        fun getInstance(context: Context): ReLiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    ReLiveDatabase::class.java,
                    "relive_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
