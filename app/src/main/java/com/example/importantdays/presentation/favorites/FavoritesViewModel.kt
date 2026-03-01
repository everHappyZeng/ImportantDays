package com.example.importantdays.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.domain.usecase.GetFavoriteDaysUseCase
import com.example.importantdays.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteDaysUseCase: GetFavoriteDaysUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            getFavoriteDaysUseCase().collect { days ->
                _uiState.value = if (days.isEmpty()) {
                    FavoritesUiState.Empty
                } else {
                    FavoritesUiState.Success(days)
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

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    object Empty : FavoritesUiState()
    data class Success(val days: List<ImportantDay>) : FavoritesUiState()
}
