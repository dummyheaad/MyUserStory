package com.example.myuserstory.data.source

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.myuserstory.data.remote.response.*
import java.io.File

interface AppDataSource {
    fun getUser(): LiveData<SignInResult>
    fun signIn(email: String, password: String): LiveData<SignInResponse>
    fun signUp(name: String, email: String, password: String): LiveData<SignUpResponse>
    fun addNewStory(token: String, imageFile: File, desc: String, lat: String, lon: String): LiveData<AddNewStoryResponse>
    fun displayDetailUserStory(token: String, id: String): LiveData<DetailStoryResponse>
    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>>
    fun getUserLocations(token: String): LiveData<ListStoryResponse>
}