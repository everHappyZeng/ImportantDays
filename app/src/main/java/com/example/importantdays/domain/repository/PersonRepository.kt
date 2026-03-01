package com.example.importantdays.domain.repository

import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.PersonWithNextDay
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getAllPersons(): Flow<List<Person>>
    fun getPersonsWithNextDay(): Flow<List<PersonWithNextDay>>
    suspend fun getPersonById(id: Long): Person?
    suspend fun insertPerson(person: Person): Long
    suspend fun updatePerson(person: Person)
    suspend fun deletePersonById(id: Long)
    fun getDaysByPersonId(personId: Long): Flow<List<ImportantDay>>
}
