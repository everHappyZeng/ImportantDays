package com.example.importantdays.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.presentation.components.EmptyContent
import com.example.importantdays.presentation.components.ImportantDayCard
import com.example.importantdays.presentation.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = FavoritesViewModel(
        getFavoriteDaysUseCase = app.container.getFavoriteDaysUseCase,
        toggleFavoriteUseCase = app.container.toggleFavoriteUseCase
    )

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收藏") }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            is FavoritesUiState.Empty -> {
                EmptyContent(
                    message = "暂无收藏的日子。\n将日子标记为收藏吧！",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is FavoritesUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.days, key = { it.id }) { day ->
                        ImportantDayCard(
                            day = day,
                            onCardClick = { onNavigateToDetail(day.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(day) }
                        )
                    }
                }
            }
        }
    }
}
