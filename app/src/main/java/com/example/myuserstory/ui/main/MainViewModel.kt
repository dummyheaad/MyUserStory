package com.example.myuserstory.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myuserstory.data.repository.StoryRepository
import java.io.File

class MainViewModel(private val repo: StoryRepository) : ViewModel() {

    val repository: StoryRepository
        get() = repo

    val coordLat = MutableLiveData(0.0)
    val coordLon = MutableLiveData(0.0)

    fun getAllStory(token: String) = repo.getAllStory(token)
    fun addNewStory(token: String, imageFile: File, desc: String, lat: String?, lon: String?) = repo.addNewStory(token, imageFile, desc, lat!!, lon!!)
    fun getUserLocations(token: String) = repo.getUserLocations(token)
}