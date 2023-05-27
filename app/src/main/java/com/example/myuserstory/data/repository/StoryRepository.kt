package com.example.myuserstory.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.myuserstory.data.database.local.StoryDatabase
import com.example.myuserstory.data.datastore.UserPreferenceDatastore
import com.example.myuserstory.data.remote.response.*
import com.example.myuserstory.data.source.AppDataSource
import com.example.myuserstory.data.source.RemoteDataSource
import com.example.myuserstory.data.source.StoryPagingSource
import com.example.myuserstory.network.ApiService
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
    private val pref: UserPreferenceDatastore,
    private val remoteDataSource: RemoteDataSource,
    private val storyDatabase: StoryDatabase
) : AppDataSource {

    val remoteData: RemoteDataSource
        get() = remoteDataSource

    override fun getUser(): LiveData<SignInResult> {
        return pref.getUser().asLiveData()
    }

    suspend fun saveUser(userName: String, userId: String, userToken: String) {
        pref.saveUser(userName, userId, userToken)
    }

    suspend fun signOut() {
        pref.signout()
    }

    override fun signIn(email: String, password: String): LiveData<SignInResponse> {
        val response = MutableLiveData<SignInResponse>()
        val callback = object : RemoteDataSource.SignInCallback {
            override fun onSignIn(signInResponse: SignInResponse) {
                response.postValue(signInResponse)
            }
        }

        remoteDataSource.signIn(callback, email, password)
        return response
    }

    override fun signUp(name: String, email: String, password: String): LiveData<SignUpResponse> {
        val response = MutableLiveData<SignUpResponse>()
        val callback = object : RemoteDataSource.SignUpCallback {
            override fun onSignUp(signUpResponse: SignUpResponse) {
                response.postValue(signUpResponse)
            }
        }

        remoteDataSource.signUp(callback, name, email, password)
        return response
    }

    override fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(
                    apiServicePaging = apiService,
                    dataStoreRepository = pref
                )
            }
        ).liveData
    }

    override fun addNewStory(
        token: String,
        imageFile: File,
        desc: String,
        lat: String,
        lon: String
    ): LiveData<AddNewStoryResponse> {
        val response = MutableLiveData<AddNewStoryResponse>()
        val callback = object : RemoteDataSource.AddNewStoryCallback {
            override fun onAddNewStory(addNewStoryResponse: AddNewStoryResponse) {
                response.postValue(addNewStoryResponse)
            }
        }

        remoteDataSource.addNewStory(callback, token, imageFile, desc, lat, lon)
        return response
    }

    override fun displayDetailUserStory(token: String, id: String): LiveData<DetailStoryResponse> {
        val response = MutableLiveData<DetailStoryResponse>()
        val callback = object : RemoteDataSource.GetDetailUserStoryCallback {
            override fun onDetailUserStoryLoad(detailStoryResponse: DetailStoryResponse) {
                response.postValue(detailStoryResponse)
            }
        }

        remoteDataSource.displayDetailUserStory(callback, token, id)
        return response
    }

    override fun getUserLocations(token: String): LiveData<ListStoryResponse> {
        val response = MutableLiveData<ListStoryResponse>()
        val callback = object : RemoteDataSource.GetUserLocationsCallback {
            override fun onUsersLocationLoad(storyResponse: ListStoryResponse) {
                response.postValue(storyResponse)
            }
        }

        remoteDataSource.getUserLocations(callback, token)
        return response
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            pref: UserPreferenceDatastore,
            remoteDataSource: RemoteDataSource,
            storyDatabase: StoryDatabase
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, pref, remoteDataSource, storyDatabase)
        }.also { instance = it }
    }
}