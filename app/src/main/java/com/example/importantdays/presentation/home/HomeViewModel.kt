package com.example.importantdays.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.usecase.GetUpcomingDaysUseCase
import com.example.importantdays.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUpcomingDaysUseCase: GetUpcomingDaysUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDays()
    }

    private fun loadDays() {
        viewModelScope.launch {
            getUpcomingDaysUseCase().collect { days ->
                _uiState.value = if (days.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(days)
                }
            }
        }
    }

    fun toggleFavorite(day: ImportantDay) {
        viewModelScope.launch {
            toggleFavoriteUseCase(day.id, !day.isFavorite)
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val days: List<ImportantDay>) : HomeUiState()
}
