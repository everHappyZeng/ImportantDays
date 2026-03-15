package com.example.importantdays.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.data.model.DayType
import com.example.importantdays.data.model.DateType
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.repository.ImportantDayRepository
import com.example.importantdays.domain.repository.PersonRepository
import com.example.importantdays.domain.usecase.SaveImportantDayUseCase
import com.example.importantdays.util.DateUtils
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
                    dateType = day.dateType,
                    lunarMonth = day.lunarMonth,
                    lunarDay = day.lunarDay,
                    isLunarLeapMonth = day.isLunarLeapMonth,
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
        // 如果选择农历重复，自动切换到农历模式
        val newDateType = if (dayType == DayType.LUNAR_YEARLY_REPEAT && _uiState.value.dateType != DateType.LUNAR) {
            DateType.LUNAR
        } else {
            _uiState.value.dateType
        }
        _uiState.value = _uiState.value.copy(dayType = dayType, dateType = newDateType)
    }

    fun onDateTypeChange(dateType: DateType) {
        _uiState.value = _uiState.value.copy(dateType = dateType)
    }

    fun onLunarMonthChange(month: Int) {
        _uiState.value = _uiState.value.copy(lunarMonth = month)
    }

    fun onLunarDayChange(day: Int) {
        _uiState.value = _uiState.value.copy(lunarDay = day)
    }

    fun onLunarLeapMonthChange(isLeap: Boolean) {
        _uiState.value = _uiState.value.copy(isLunarLeapMonth = isLeap)
    }

    /**
     * 根据农历日期计算对应的公历日期
     */
    fun calculateSolarFromLunar() {
        val state = _uiState.value
        if (state.dateType == DateType.LUNAR && state.lunarMonth != null && state.lunarDay != null) {
            val solarDate = DateUtils.solarFromLunar(
                LocalDate.now().year,
                state.lunarMonth,
                state.lunarDay,
                state.isLunarLeapMonth
            )
            if (solarDate != null) {
                _uiState.value = state.copy(date = solarDate)
            }
        }
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
            _uiState.value = state.copy(errorMessage = "标题不能为空")
            return
        }

        // 如果是农历日期，计算对应的公历日期
        var finalDate = state.date
        if (state.dateType == DateType.LUNAR && state.lunarMonth != null && state.lunarDay != null) {
            val solarDate = DateUtils.solarFromLunar(
                LocalDate.now().year,
                state.lunarMonth,
                state.lunarDay,
                state.isLunarLeapMonth
            )
            if (solarDate != null) {
                finalDate = solarDate
            }
        }

        viewModelScope.launch {
            val day = ImportantDay(
                id = dayId,
                title = state.title,
                description = state.description,
                date = finalDate,
                dayType = state.dayType,
                dateType = state.dateType,
                lunarMonth = if (state.dateType == DateType.LUNAR) state.lunarMonth else null,
                lunarDay = if (state.dateType == DateType.LUNAR) state.lunarDay else null,
                isLunarLeapMonth = state.isLunarLeapMonth,
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
    val dateType: DateType = DateType.SOLAR,
    val lunarMonth: Int? = null,
    val lunarDay: Int? = null,
    val isLunarLeapMonth: Boolean = false,
    val personId: Long? = null,
    val persons: List<Person> = emptyList(),
    val reminderEnabled: Boolean = false,
    val reminderDaysBefore: Int = 1,
    val timeEnabled: Boolean = false,
    val time: LocalTime = LocalTime.of(12, 0, 0),
    val isEditMode: Boolean = false,
    val errorMessage: String? = null
)
