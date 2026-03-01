package com.example.importantdays.data.repository

import com.example.importantdays.data.local.ImportantDayDao
import com.example.importantdays.data.local.PersonDao
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.model.PersonWithNextDay
import com.example.importantdays.domain.repository.PersonRepository
import com.example.importantdays.util.toDomain
import com.example.importantdays.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PersonRepositoryImpl(
    private val personDao: PersonDao,
    private val importantDayDao: ImportantDayDao
) : PersonRepository {

    override fun getAllPersons(): Flow<List<Person>> {
        return personDao.getAllPersons().map { it.map { entity -> entity.toDomain() } }
    }

    override fun getPersonsWithNextDay(): Flow<List<PersonWithNextDay>> {
        return personDao.getPersonsWithNextDay().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getPersonById(id: Long): Person? {
        return personDao.getPersonById(id)?.toDomain()
    }

    override suspend fun insertPerson(person: Person): Long {
        return personDao.insertPerson(person.toEntity())
    }

    override suspend fun updatePerson(person: Person) {
        personDao.updatePerson(person.toEntity())
    }

    override suspend fun deletePersonById(id: Long) {
        personDao.deletePersonById(id)
    }

    override fun getDaysByPersonId(personId: Long): Flow<List<ImportantDay>> {
        return importantDayDao.getDaysByPersonId(personId).map { it.map { entity -> entity.toDomain() } }
    }
}
