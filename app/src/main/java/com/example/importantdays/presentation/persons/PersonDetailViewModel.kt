package com.example.importantdays.presentation.persons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.repository.ActivityRecordRepository
import com.example.importantdays.domain.repository.PersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    private val personRepository: PersonRepository,
    private val activityRecordRepository: ActivityRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonDetailUiState>(PersonDetailUiState.Loading)
    val uiState: StateFlow<PersonDetailUiState> = _uiState.asStateFlow()

    private var currentPersonId: Long = 0

    fun loadPerson(personId: Long) {
        currentPersonId = personId
        viewModelScope.launch {
            // 加载联系人信息
            val person = personRepository.getPersonById(personId)
            if (person == null) {
                _uiState.value = PersonDetailUiState.Error("未找到该联系人")
                return@launch
            }

            // 加载该联系人的重要日子
            personRepository.getDaysByPersonId(personId).collect { days ->
                _uiState.value = PersonDetailUiState.Success(
                    person = person,
                    importantDays = days
                )
            }
        }

        // 加载该联系人的活动记录
        viewModelScope.launch {
            activityRecordRepository.getActivityRecordsByPerson(personId).collect { records ->
                val currentState = _uiState.value
                if (currentState is PersonDetailUiState.Success) {
                    _uiState.value = currentState.copy(activityRecords = records)
                }
            }
        }
    }

    fun updatePerson(person: Person) {
        viewModelScope.launch {
            personRepository.updatePerson(person)
        }
    }

    fun updateHobbies(hobbies: List<String>) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is PersonDetailUiState.Success) {
                val updatedPerson = currentState.person.copy(hobbies = hobbies)
                personRepository.updatePerson(updatedPerson)
            }
        }
    }

    fun deletePerson(onDone: () -> Unit) {
        viewModelScope.launch {
            personRepository.deletePersonById(currentPersonId)
            onDone()
        }
    }

    fun deleteActivityRecord(recordId: Long) {
        viewModelScope.launch {
            activityRecordRepository.deleteActivityRecord(recordId)
        }
    }
}

sealed class PersonDetailUiState {
    object Loading : PersonDetailUiState()
    data class Error(val message: String) : PersonDetailUiState()
    data class Success(
        val person: Person,
        val importantDays: List<ImportantDay> = listOf(),
        val activityRecords: List<ActivityRecord> = listOf()
    ) : PersonDetailUiState()
}
