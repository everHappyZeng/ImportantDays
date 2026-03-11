package com.example.importantdays.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.importantdays.ImportantDaysApplication
import com.example.importantdays.domain.model.ActivityRecord
import com.example.importantdays.presentation.addedit.AddEditScreen
import com.example.importantdays.presentation.detail.DetailScreen
import com.example.importantdays.presentation.favorites.FavoritesScreen
import com.example.importantdays.presentation.home.HomeScreen
import com.example.importantdays.presentation.persons.PersonsScreen
import com.example.importantdays.presentation.persons.PersonsUiState
import com.example.importantdays.presentation.persons.PersonsViewModel
import com.example.importantdays.presentation.profile.ProfileScreen
import com.example.importantdays.presentation.records.ActivityRecordsScreen
import com.example.importantdays.presentation.records.AddEditRecordScreen
import com.example.importantdays.presentation.records.ActivityRecordsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as ImportantDaysApplication
    val personsViewModel = PersonsViewModel(app.container.personRepository)
    val recordsViewModel = ActivityRecordsViewModel(
        app.container.activityRecordRepository,
        app.container.personRepository
    )

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDetail = { dayId ->
                    navController.navigate(Screen.Detail.createRoute(dayId))
                },
                onNavigateToAddEdit = { dayId ->
                    navController.navigate(Screen.AddEdit.createRoute(dayId))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateToDetail = { dayId ->
                    navController.navigate(Screen.Detail.createRoute(dayId))
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToPersons = {
                    navController.navigate(Screen.Persons.route)
                },
                onNavigateToRecords = {
                    navController.navigate(Screen.Records.route)
                }
            )
        }

        composable(Screen.Persons.route) {
            PersonsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Records.route) {
            val personsState by personsViewModel.uiState.collectAsState()
            val persons = when (val state: PersonsUiState = personsState) {
                is PersonsUiState.Success -> state.persons.map { it.person }
                else -> emptyList()
            }
            ActivityRecordsScreen(
                viewModel = recordsViewModel,
                persons = persons,
                onAddRecord = {
                    navController.navigate(Screen.AddEditRecord.createRoute())
                },
                onRecordClick = { record: ActivityRecord ->
                    navController.navigate(Screen.AddEditRecord.createRoute(recordId = record.id))
                },
                onDeleteRecord = { recordId: Long ->
                    recordsViewModel.deleteRecord(recordId)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("dayId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getLong("dayId") ?: 0L
            DetailScreen(
                dayId = dayId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id: Long ->
                    navController.navigate(Screen.AddEdit.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                navArgument("dayId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val dayId = backStackEntry.arguments?.getLong("dayId") ?: 0L
            AddEditScreen(
                dayId = dayId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddEditRecord.route,
            arguments = listOf(
                navArgument("recordId") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("personId") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("importantDayId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
            val importantDayId = backStackEntry.arguments?.getLong("importantDayId") ?: 0L

            val personsState by personsViewModel.uiState.collectAsState()
            val persons = when (val state: PersonsUiState = personsState) {
                is PersonsUiState.Success -> state.persons.map { it.person }
                else -> emptyList()
            }

            AddEditRecordScreen(
                viewModel = recordsViewModel,
                persons = persons,
                editRecord = null,
                preselectedPersonId = if (personId > 0) personId else null,
                preselectedImportantDayId = if (importantDayId > 0) importantDayId else null,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
    }
}
