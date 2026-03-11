package com.example.importantdays.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.importantdays.data.model.PersonEntity
import com.example.importantdays.data.model.PersonWithNextDaySummary
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Query(
        """
        SELECT
            p.id AS person_id,
            p.name AS person_name,
            p.avatar AS person_avatar,
            p.notes AS person_notes,
            p.hobbies AS person_hobbies,
            p.createdAt AS person_createdAt,
            d.id AS nextDayId,
            d.title AS nextDayTitle,
            d.date AS nextDayDate
        FROM persons p
        LEFT JOIN important_days d ON d.id = (
            SELECT id
            FROM important_days
            WHERE personId = p.id
            ORDER BY date ASC
            LIMIT 1
        )
        ORDER BY p.name ASC
        """
    )
    fun getPersonsWithNextDay(): Flow<List<PersonWithNextDaySummary>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPersonById(id: Long): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Query("DELETE FROM persons WHERE id = :id")
    suspend fun deletePersonById(id: Long)
}
