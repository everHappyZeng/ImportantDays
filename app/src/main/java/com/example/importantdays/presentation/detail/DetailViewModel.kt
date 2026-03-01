package com.example.importantdays.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.usecase.DeleteImportantDayUseCase
import com.example.importantdays.domain.usecase.ToggleFavoriteUseCase
import com.example.importantdays.domain.repository.ImportantDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val dayId: Long,
    private val repository: ImportantDayRepository,
    private val deleteImportantDayUseCase: DeleteImportantDayUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDay()
    }

    private fun loadDay() {
        viewModelScope.launch {
            val day = repository.getDayById(dayId)
            _uiState.value = if (day != null) {
                DetailUiState.Success(day)
            } else {
                DetailUiState.Error("Day not found")
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            viewModelScope.launch {
                toggleFavoriteUseCase(currentState.day.id, !currentState.day.isFavorite)
                loadDay()
            }
        }
    }

    fun deleteDay(onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteImportantDayUseCase(dayId)
            onSuccess()
        }
    }
}

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val day: ImportantDay) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
