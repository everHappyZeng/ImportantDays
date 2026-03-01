package com.example.importantdays.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.importantdays.data.model.ImportantDayEntity

@Database(
    entities = [ImportantDayEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ImportantDaysDatabase : RoomDatabase() {
    abstract fun importantDayDao(): ImportantDayDao

    companion object {
        @Volatile
        private var INSTANCE: ImportantDaysDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE important_days ADD COLUMN timeEnabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE important_days ADD COLUMN time TEXT")
            }
        }

        fun getDatabase(context: Context): ImportantDaysDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImportantDaysDatabase::class.java,
                    "important_days_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
