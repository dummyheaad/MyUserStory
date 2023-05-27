package com.example.myuserstory.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myuserstory.data.datastore.UserPreferenceDatastore
import com.example.myuserstory.data.remote.response.ListStoryItem
import com.example.myuserstory.network.ApiService
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val apiServicePaging: ApiService,
    private val dataStoreRepository: UserPreferenceDatastore
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus((1))
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val token = dataStoreRepository.getUser().first()
            val userToken = "Bearer ${token.token}"
            val queryParam = HashMap<String, Int>()
            queryParam[PAGE] = position
            queryParam[SIZE] = params.loadSize
            queryParam[LOCATION] = 0

            val responseData = apiServicePaging.getListStory(userToken, queryParam)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) { return LoadResult.Error(e) }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
        private const val PAGE = "page"
        private const val SIZE = "size"
        private const val LOCATION = "location"
    }
}