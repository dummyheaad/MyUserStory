package com.example.myuserstory.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myuserstory.data.repository.StoryRepository
import com.example.myuserstory.provider.Provider
import com.example.myuserstory.ui.detailuserstory.DetailUserStoryViewModel
import com.example.myuserstory.ui.main.MainViewModel
import com.example.myuserstory.ui.signin.SignInViewModel
import com.example.myuserstory.ui.signup.SignUpViewModel

class ViewModelFactory(private val repo: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(repo) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repo) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repo) as T
            }
            modelClass.isAssignableFrom(DetailUserStoryViewModel::class.java) -> {
                DetailUserStoryViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Provider.provideRepo(context))
            }.also {instance = it}
    }
}