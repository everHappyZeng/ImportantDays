package com.example.importantdays.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.importantdays.data.model.ActivityRecordEntity
import com.example.importantdays.data.model.ImportantDayEntity
import com.example.importantdays.data.model.PersonEntity

@Database(
    entities = [ImportantDayEntity::class, PersonEntity::class, ActivityRecordEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ImportantDaysDatabase : RoomDatabase() {
    abstract fun importantDayDao(): ImportantDayDao
    abstract fun personDao(): PersonDao
    abstract fun activityRecordDao(): ActivityRecordDao

    companion object {
        @Volatile
        private var INSTANCE: ImportantDaysDatabase? = null

        fun getDatabase(context: Context): ImportantDaysDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImportantDaysDatabase::class.java,
                    "important_days_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
