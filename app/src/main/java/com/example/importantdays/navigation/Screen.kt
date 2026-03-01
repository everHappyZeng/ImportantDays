package com.example.importantdays.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object AddEdit : Screen("add_edit/{dayId}") {
        fun createRoute(dayId: Long = 0L) = "add_edit/$dayId"
    }
    object Detail : Screen("detail/{dayId}") {
        fun createRoute(dayId: Long) = "detail/$dayId"
    }
}
