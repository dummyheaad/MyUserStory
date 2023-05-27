package com.example.myuserstory.ui.signup

import androidx.lifecycle.ViewModel
import com.example.myuserstory.data.repository.StoryRepository

class SignUpViewModel(private val repo: StoryRepository) : ViewModel() {

    val repository: StoryRepository
        get() = repo

    fun signup(name: String, email: String, password: String) = repo.signUp(name, email, password)
}