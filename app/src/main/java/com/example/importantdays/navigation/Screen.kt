package com.example.importantdays.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object Persons : Screen("persons")
    object Records : Screen("records")
    object AddEdit : Screen("add_edit/{dayId}") {
        fun createRoute(dayId: Long = 0L) = "add_edit/$dayId"
    }
    object Detail : Screen("detail/{dayId}") {
        fun createRoute(dayId: Long) = "detail/$dayId"
    }
    object AddEditRecord : Screen("add_edit_record/{recordId}/{personId}/{importantDayId}") {
        fun createRoute(recordId: Long = 0, personId: Long = 0, importantDayId: Long = 0) =
            "add_edit_record/$recordId/$personId/$importantDayId"
    }
    object PersonDetail : Screen("person_detail/{personId}") {
        fun createRoute(personId: Long) = "person_detail/$personId"
    }
}
