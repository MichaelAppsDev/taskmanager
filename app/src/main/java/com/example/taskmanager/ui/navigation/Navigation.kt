package com.example.taskmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmanager.ui.screens.*
import com.example.taskmanager.ui.viewmodel.TaskViewModel

@Composable
fun Navigation(
    viewModel: TaskViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(viewModel = viewModel)
        }

        composable("calendar") {
            CalendarScreen(viewModel = viewModel)
        }

        composable("collections") {
            CollectionsScreen(
                viewModel = viewModel,
                onCollectionClick = { collectionId ->
                    if (collectionId == "archive") {
                        navController.navigate("archive")
                    } else {
                        navController.navigate("collection/$collectionId")
                    }
                },
                userId = "default" // TODO: Get actual user ID
            )
        }

        composable("archive") {
            ArchiveCollectionScreen(viewModel = viewModel)
        }

        composable("collection/{collectionId}") { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId") ?: return@composable
            CollectionDetailScreen(
                viewModel = viewModel,
                collectionId = collectionId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 