package com.example.taskmanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.ui.screens.ActivityScreen
import com.example.taskmanager.ui.screens.CalendarScreen
import com.example.taskmanager.ui.screens.CollectionsScreen
import com.example.taskmanager.ui.screens.HomeScreen
import com.example.taskmanager.ui.screens.ProfileScreen
import com.example.taskmanager.ui.viewmodel.AuthViewModel
import com.example.taskmanager.ui.viewmodel.TaskViewModel
import com.example.taskmanager.ui.viewmodel.AuthState

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Collections : Screen("collections", Icons.Default.List, "Collections")
    object Calendar : Screen("calendar", Icons.Default.DateRange, "Calendar")
    object Activity : Screen("activity", Icons.Default.Notifications, "Activity")
    object Profile : Screen("profile", Icons.Default.Person, "Profile")
}

@Composable
fun AppNavigation(
    viewModel: TaskViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == Screen.Home.route,
                            onClick = { navController.navigate(Screen.Home.route) },
                            icon = { Icon(Screen.Home.icon, contentDescription = Screen.Home.label) },
                            label = { Text(Screen.Home.label) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.Collections.route,
                            onClick = { navController.navigate(Screen.Collections.route) },
                            icon = { Icon(Screen.Collections.icon, contentDescription = Screen.Collections.label) },
                            label = { Text(Screen.Collections.label) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.Calendar.route,
                            onClick = { navController.navigate(Screen.Calendar.route) },
                            icon = { Icon(Screen.Calendar.icon, contentDescription = Screen.Calendar.label) },
                            label = { Text(Screen.Calendar.label) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.Activity.route,
                            onClick = { navController.navigate(Screen.Activity.route) },
                            icon = { Icon(Screen.Activity.icon, contentDescription = Screen.Activity.label) },
                            label = { Text(Screen.Activity.label) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == Screen.Profile.route,
                            onClick = { navController.navigate(Screen.Profile.route) },
                            icon = { Icon(Screen.Profile.icon, contentDescription = Screen.Profile.label) },
                            label = { Text(Screen.Profile.label) }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(viewModel = viewModel)
                    }
                    composable(Screen.Collections.route) {
                        val currentUser = when (authState) {
                            is AuthState.Authenticated -> (authState as AuthState.Authenticated).user
                            else -> null
                        }
                        CollectionsScreen(
                            viewModel = viewModel,
                            onCollectionClick = { _ -> 
                                // TODO: Handle collection click navigation
                            },
                            userId = currentUser?.uid ?: ""
                        )
                    }
                    composable(Screen.Calendar.route) {
                        CalendarScreen(viewModel = viewModel)
                    }
                    composable(Screen.Activity.route) {
                        ActivityScreen(viewModel = viewModel)
                    }
                    composable(Screen.Profile.route) {
                        ProfileScreen(viewModel = viewModel, authViewModel = authViewModel)
                    }
                }
            }
        }
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            // If not authenticated, show nothing (LoginScreen will be shown by MainActivity)
            Box(modifier = Modifier.fillMaxSize())
        }
    }
} 