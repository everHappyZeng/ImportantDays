package com.example.importantdays.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.data.model.DayType
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.domain.repository.PersonRepository
import com.example.importantdays.domain.usecase.SaveImportantDayUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AddEditViewModel(
    private val dayId: Long,
    private val repository: ImportantDayRepository,
    private val personRepository: PersonRepository,
    private val saveImportantDayUseCase: SaveImportantDayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        loadPersons()
        if (dayId != 0L) {
            loadDay()
        }
    }

    private fun loadPersons() {
        viewModelScope.launch {
            personRepository.getAllPersons().collect { persons ->
                _uiState.value = _uiState.value.copy(persons = persons)
            }
        }
    }

    private fun loadDay() {
        viewModelScope.launch {
            repository.getDayById(dayId)?.let { day ->
                _uiState.value = AddEditUiState(
                    title = day.title,
                    description = day.description,
                    date = day.date,
                    dayType = day.dayType,
                    reminderEnabled = day.reminderEnabled,
                    reminderDaysBefore = day.reminderDaysBefore,
                    timeEnabled = day.timeEnabled,
                    time = day.time ?: LocalTime.of(12, 0, 0),
                    personId = day.personId,
                    persons = _uiState.value.persons,
                    isEditMode = true
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onDayTypeChange(dayType: DayType) {
        _uiState.value = _uiState.value.copy(dayType = dayType)
    }

    fun onReminderEnabledChange(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(reminderEnabled = enabled)
    }

    fun onReminderDaysBeforeChange(days: Int) {
        _uiState.value = _uiState.value.copy(reminderDaysBefore = days)
    }

    fun onTimeEnabledChange(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(timeEnabled = enabled)
    }

    fun onTimeChange(time: LocalTime) {
        _uiState.value = _uiState.value.copy(time = time)
    }

    fun onPersonChange(personId: Long?) {
        _uiState.value = _uiState.value.copy(personId = personId)
    }

    fun saveDay(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Title cannot be empty")
            return
        }

        viewModelScope.launch {
            val day = ImportantDay(
                id = dayId,
                title = state.title,
                description = state.description,
                date = state.date,
                dayType = state.dayType,
                personId = state.personId,
                reminderEnabled = state.reminderEnabled,
                reminderDaysBefore = state.reminderDaysBefore,
                timeEnabled = state.timeEnabled,
                time = if (state.timeEnabled) state.time else null
            )
            saveImportantDayUseCase(day)
            onSuccess()
        }
    }
}

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val dayType: DayType = DayType.ONE_TIME,
    val personId: Long? = null,
    val persons: List<Person> = emptyList(),
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 1,
    val timeEnabled: Boolean = false,
    val time: LocalTime = LocalTime.of(12, 0, 0),
    val isEditMode: Boolean = false,
    val errorMessage: String? = null
)
