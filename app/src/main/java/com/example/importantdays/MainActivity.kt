package com.example.importantdays

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.importantdays.navigation.NavGraph
import com.example.importantdays.navigation.Screen
import com.example.importantdays.ui.theme.ImportantDaysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImportantDaysTheme {
                ImportantDaysApp()
            }
        }
    }
}

@Composable
fun ImportantDaysApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Favorites.route,
        Screen.Profile.route
    )

    if (showBottomBar) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) {
            NavGraph(
                navController = navController,
                startDestination = Screen.Home.route
            )
        }
    } else {
        NavGraph(
            navController = navController,
            startDestination = Screen.Home.route
        )
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    HOME("Home", Icons.Default.Home, Screen.Home.route),
    FAVORITES("Favorites", Icons.Default.Favorite, Screen.Favorites.route),
    RECORDS("Records", Icons.Default.List, Screen.Records.route),
    PROFILE("Profile", Icons.Default.AccountBox, Screen.Profile.route),
}
