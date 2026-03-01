package com.example.importantdays.di

import android.content.Context
import com.example.importantdays.data.local.ImportantDaysDatabase
import com.example.importantdays.data.repository.ImportantDayRepositoryImpl
import com.example.importantdays.data.repository.PersonRepositoryImpl
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.domain.repository.PersonRepository
import com.example.importantdays.domain.usecase.*

class AppContainer(context: Context) {
    private val database = ImportantDaysDatabase.getDatabase(context)
    private val importantDayDao = database.importantDayDao()
    private val personDao = database.personDao()

    val repository: ImportantDayRepository = ImportantDayRepositoryImpl(importantDayDao)
    val personRepository: PersonRepository = PersonRepositoryImpl(personDao, importantDayDao)

    val getAllDaysUseCase = GetAllDaysUseCase(repository)
    val getUpcomingDaysUseCase = GetUpcomingDaysUseCase(repository)
    val getFavoriteDaysUseCase = GetFavoriteDaysUseCase(repository)
    val saveImportantDayUseCase = SaveImportantDayUseCase(repository, context)
    val deleteImportantDayUseCase = DeleteImportantDayUseCase(repository, context)
    val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
}
