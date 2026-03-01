package com.example.importantdays.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.importantdays.presentation.addedit.AddEditScreen
import com.example.importantdays.presentation.detail.DetailScreen
import com.example.importantdays.presentation.favorites.FavoritesScreen
import com.example.importantdays.presentation.home.HomeScreen
import com.example.importantdays.presentation.persons.PersonsScreen
import com.example.importantdays.presentation.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
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
                }
            )
        }

        composable(Screen.Persons.route) {
            PersonsScreen(
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
                onNavigateToEdit = { id ->
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
    }
}
