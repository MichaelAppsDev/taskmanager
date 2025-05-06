package com.example.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanager.ui.navigation.AppNavigation
import com.example.taskmanager.ui.screens.LoginScreen
import com.example.taskmanager.ui.theme.TaskmanagerTheme
import com.example.taskmanager.ui.viewmodel.AuthState
import com.example.taskmanager.ui.viewmodel.AuthViewModel
import com.example.taskmanager.ui.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TaskViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskmanagerTheme {
                val authState by authViewModel.authState.collectAsState()
                
                when (authState) {
                    is AuthState.Authenticated -> {
                        AppNavigation(viewModel = viewModel, authViewModel = authViewModel)
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
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                authViewModel.refreshAuthState()
                            }
                        )
                    }
                }
            }
        }
    }
}

// MainScreen is no longer needed since MainActivity handles this functionality
// Removing this composable to avoid duplicate ViewModel instances