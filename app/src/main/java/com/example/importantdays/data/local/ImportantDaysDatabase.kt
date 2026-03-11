package com.example.importantdays.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.importantdays.data.model.ActivityRecordEntity
import com.example.importantdays.data.model.ImportantDayEntity
import com.example.importantdays.data.model.PersonEntity

@Database(
    entities = [ImportantDayEntity::class, PersonEntity::class, ActivityRecordEntity::class],
    version = 4,
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

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE important_days ADD COLUMN timeEnabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE important_days ADD COLUMN time TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `persons` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `avatar` TEXT,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `important_days_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `dayType` TEXT NOT NULL,
                        `isFavorite` INTEGER NOT NULL,
                        `reminderEnabled` INTEGER NOT NULL,
                        `reminderDaysBefore` INTEGER NOT NULL,
                        `notificationChannels` TEXT NOT NULL,
                        `timeEnabled` INTEGER NOT NULL,
                        `time` TEXT,
                        `personId` INTEGER,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`personId`) REFERENCES `persons`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO `important_days_new` (
                        `id`, `title`, `description`, `date`, `dayType`,
                        `isFavorite`, `reminderEnabled`, `reminderDaysBefore`,
                        `notificationChannels`, `timeEnabled`, `time`,
                        `personId`, `createdAt`, `updatedAt`
                    )
                    SELECT
                        `id`, `title`, `description`, `date`, `dayType`,
                        `isFavorite`, `reminderEnabled`, `reminderDaysBefore`,
                        `notificationChannels`, `timeEnabled`, `time`,
                        NULL, `createdAt`, `updatedAt`
                    FROM `important_days`
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE `important_days`")
                db.execSQL("ALTER TABLE `important_days_new` RENAME TO `important_days`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_important_days_personId` ON `important_days` (`personId`)")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add hobbies column to persons table
                db.execSQL("ALTER TABLE persons ADD COLUMN hobbies TEXT NOT NULL DEFAULT '[]'")

                // Create activity_records table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `activity_records` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `personId` INTEGER NOT NULL,
                        `importantDayId` INTEGER,
                        `activityType` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        FOREIGN KEY(`personId`) REFERENCES `persons`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`importantDayId`) REFERENCES `important_days`(`id`) ON DELETE SET NULL
                    )
                    """.trimIndent()
                )

                db.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_records_personId` ON `activity_records` (`personId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_records_importantDayId` ON `activity_records` (`importantDayId`)")
            }
        }

        fun getDatabase(context: Context): ImportantDaysDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImportantDaysDatabase::class.java,
                    "important_days_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
