package com.example.myuserstory.provider

import android.content.Context
import com.example.myuserstory.data.database.local.StoryDatabase
import com.example.myuserstory.data.datastore.UserPreferenceDatastore
import com.example.myuserstory.data.repository.StoryRepository
import com.example.myuserstory.data.source.RemoteDataSource
import com.example.myuserstory.network.ApiConfig

object Provider {
    fun provideRepo(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferenceDatastore = UserPreferenceDatastore.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, userPreferenceDatastore, remoteDataSource, storyDatabase)
    }
}