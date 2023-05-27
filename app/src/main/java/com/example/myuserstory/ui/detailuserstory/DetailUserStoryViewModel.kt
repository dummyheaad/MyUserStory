package com.example.myuserstory.ui.detailuserstory


import androidx.lifecycle.ViewModel
import com.example.myuserstory.data.repository.StoryRepository

class DetailUserStoryViewModel(private val repo: StoryRepository) : ViewModel() {

    val repository: StoryRepository
        get() = repo

    fun displayDetailUserStory(token: String, id: String) = repo.displayDetailUserStory(token, id)
}