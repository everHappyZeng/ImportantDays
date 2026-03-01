package com.example.importantdays.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.domain.model.ImportantDay
import com.example.importantdays.presentation.components.EmptyContent
import com.example.importantdays.presentation.components.ImportantDayCard
import com.example.importantdays.presentation.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val viewModel = HomeViewModel(
        getUpcomingDaysUseCase = app.container.getUpcomingDaysUseCase,
        toggleFavoriteUseCase = app.container.toggleFavoriteUseCase
    )
    val persons by app.container.personRepository.getAllPersons().collectAsState(initial = emptyList())
    val personNameMap = remember(persons) { persons.associate { it.id to it.name } }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("即将到来的日子") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(0L) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加新日子")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(paddingValues))
            }
            is HomeUiState.Empty -> {
                EmptyContent(
                    message = "暂无即将到来的重要日子。\n点击 + 添加一个！",
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val groupedDays = linkedMapOf<String, MutableList<ImportantDay>>()
                    state.days.forEach { day ->
                        val ownerLabel = day.personId?.let(personNameMap::get) ?: "未归属"
                        groupedDays.getOrPut(ownerLabel) { mutableListOf() }.add(day)
                    }

                    item {
                        HomeSummaryCard(
                            totalCount = state.days.size,
                            ownedCount = state.days.count { it.personId != null }
                        )
                    }
                    groupedDays.forEach { (ownerLabel, days) ->
                        item(key = "group_$ownerLabel") {
                            GroupHeader(
                                ownerLabel = ownerLabel,
                                count = days.size
                            )
                        }
                        items(days, key = { it.id }) { day ->
                            ImportantDayCard(
                                day = day,
                                ownerName = day.personId?.let(personNameMap::get),
                                onCardClick = { onNavigateToDetail(day.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(day) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSummaryCard(
    totalCount: Int,
    ownedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("即将到来", style = MaterialTheme.typography.labelLarge)
                Text("$totalCount 条", style = MaterialTheme.typography.headlineSmall)
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text("已关联人员", style = MaterialTheme.typography.labelLarge)
                Text("$ownedCount 条", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
private fun GroupHeader(
    ownerLabel: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = ownerLabel,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "$count 条",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
