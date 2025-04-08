package com.example.taskmanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.TaskDatabase
import com.example.taskmanager.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val database = TaskDatabase.getDatabase(application)
    private val userDao = database.userDao()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        auth.currentUser?.let { user ->
            viewModelScope.launch {
                saveUserToLocal(user)
                _authState.value = AuthState.Authenticated(user)
            }
        } ?: run {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    saveUserToLocal(user)
                    _authState.value = AuthState.Authenticated(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    user.updateProfile(profileUpdates).await()
                    saveUserToLocal(user)
                    _authState.value = AuthState.Authenticated(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                userDao.deleteUser(user.uid)
            }
            _authState.value = AuthState.Unauthenticated
        }
    }

    private suspend fun saveUserToLocal(user: FirebaseUser) {
        val localUser = User(
            uid = user.uid,
            email = user.email ?: "",
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString()
        )
        userDao.insertUser(localUser)
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
} 