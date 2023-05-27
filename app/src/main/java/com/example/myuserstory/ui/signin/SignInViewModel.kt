package com.example.myuserstory.ui.signin

import androidx.lifecycle.*
import com.example.myuserstory.data.repository.StoryRepository
import kotlinx.coroutines.launch

class SignInViewModel(private val repo: StoryRepository) : ViewModel() {

    val repository: StoryRepository
        get() = repo

    fun signin(email: String, password: String) = repo.signIn(email, password)

    fun getUser() = repo.getUser()

    fun saveUser(name: String, userId: String, userToken: String) {
        viewModelScope.launch {
            repo.saveUser(name, userId, userToken)
        }
    }

    fun signout() {
        viewModelScope.launch {
            repo.signOut()
        }
    }

}