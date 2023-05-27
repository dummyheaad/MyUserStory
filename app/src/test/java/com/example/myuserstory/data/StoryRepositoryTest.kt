import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.myuserstory.DataDummy
import com.example.myuserstory.MainDispatcherRule
import com.example.myuserstory.data.datastore.UserPreferenceDatastore
import com.example.myuserstory.data.database.local.StoryDatabase
import com.example.myuserstory.data.remote.response.ListStoryItem
import com.example.myuserstory.network.ApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import com.example.myuserstory.StoryPagingSource
import com.example.myuserstory.adapter.UserStoryAdapter
import com.example.myuserstory.data.repository.StoryRepository
import com.example.myuserstory.data.source.RemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.mockito.Mockito
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource
    @Mock
    private lateinit var storyDatabase: StoryDatabase
    @Mock
    private lateinit var apiService: ApiService
    @Mock
    private lateinit var userPreferenceDatastore: UserPreferenceDatastore

    @Mock
    private lateinit var storyRepository: StoryRepository

    private val dummyStory = DataDummy.generateDummyListStoryItem()

    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVRHdUQydjRiSjdQQWluSmoiLCJpYXQiOjE2ODQ2MjAyODR9.i20a_LLtS0o05q3YWHImef3c9U3tHi-2tJYNEGv1tkE"


    @Before
    fun setup(){
        storyRepository = StoryRepository(apiService, userPreferenceDatastore, remoteDataSource, storyDatabase)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Get All Story Success Should Return Data`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        val storyRepositoryMock = Mockito.mock(StoryRepository::class.java)
        Mockito.`when`(storyRepositoryMock.getAllStory(token)).thenReturn(expectedStory)

        storyRepositoryMock.getAllStory(token).observeForever {
            val differ = AsyncPagingDataDiffer(
                diffCallback = UserStoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainDispatcherRule.testDispatcher,
                workerDispatcher = mainDispatcherRule.testDispatcher
            )
            CoroutineScope(Dispatchers.IO).launch {
                differ.submitData(it)
            }
            advanceUntilIdle()
            verify(storyRepositoryMock).getAllStory(token)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(differ.snapshot().size, dummyStory.size)
            Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
        }

    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}