package com.example.importantdays.presentation.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.data.model.ActivityType
import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.repository.ActivityRecordRepository
import com.example.importantdays.domain.repository.PersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class ActivityRecordsViewModel(
    private val activityRecordRepository: ActivityRecordRepository,
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordsUiState>(RecordsUiState.Loading)
    val uiState: StateFlow<RecordsUiState> = _uiState.asStateFlow()

    private val _persons = MutableStateFlow<List<Person>>(emptyList())
    val persons: StateFlow<List<Person>> = _persons.asStateFlow()

    init {
        loadRecords()
        loadPersons()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            activityRecordRepository.getAllRecords().collect { records ->
                _uiState.value = if (records.isEmpty()) {
                    RecordsUiState.Empty
                } else {
                    RecordsUiState.Success(records)
                }
            }
        }
    }

    private fun loadPersons() {
        viewModelScope.launch {
            personRepository.getAllPersons().collect { persons ->
                _persons.value = persons
            }
        }
    }

    fun getRecordsByPerson(personId: Long, onResult: (List<ActivityRecord>) -> Unit) {
        viewModelScope.launch {
            activityRecordRepository.getRecordsByPerson(personId).collect { records ->
                onResult(records)
            }
        }
    }

    fun getRecordsByImportantDay(importantDayId: Long, onResult: (List<ActivityRecord>) -> Unit) {
        viewModelScope.launch {
            activityRecordRepository.getRecordsByImportantDay(importantDayId).collect { records ->
                onResult(records)
            }
        }
    }

    fun saveRecord(
        id: Long = 0,
        personId: Long,
        importantDayId: Long?,
        activityType: ActivityType,
        title: String,
        description: String,
        date: LocalDate,
        notes: String,
        onDone: () -> Unit
    ) {
        if (title.isBlank() || personId == 0L) {
            return
        }
        viewModelScope.launch {
            val record = ActivityRecord(
                id = id,
                personId = personId,
                importantDayId = importantDayId,
                activityType = activityType,
                title = title.trim(),
                description = description.trim(),
                date = date,
                notes = notes.trim()
            )
            if (id == 0L) {
                activityRecordRepository.insertRecord(record)
            } else {
                activityRecordRepository.updateRecord(record)
            }
            onDone()
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            activityRecordRepository.deleteRecordById(id)
        }
    }
}

sealed class RecordsUiState {
    object Loading : RecordsUiState()
    object Empty : RecordsUiState()
    data class Success(val records: List<ActivityRecord>) : RecordsUiState()
}
