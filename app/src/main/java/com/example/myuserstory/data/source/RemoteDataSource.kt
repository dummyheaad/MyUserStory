package com.example.myuserstory.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myuserstory.data.remote.response.*
import com.example.myuserstory.network.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSource {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    var responseCode = ""

    fun signIn(callback: SignInCallback, email: String, password: String) {
        _isLoading.value = true
        callback.onSignIn(
            SignInResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().signIn(email, password)
        client.enqueue(object : Callback<SignInResponse> {
            override fun onResponse(call: Call<SignInResponse>, response: Response<SignInResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSignIn(it) }
                }
                else {
                    when (response.code()) {
                        200 -> responseCode = "200"
                        400 -> responseCode = "400"
                        401 -> responseCode = "401"
                        else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                    }
                    callback.onSignIn(
                        SignInResponse(
                            SignInResult("", "", ""),
                            true,
                            responseCode
                        )
                    )
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                _isLoading.value = true
                callback.onSignIn(
                    SignInResponse(
                        null,
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun signUp(callback: SignUpCallback, name: String, email: String, password: String) {
        _isLoading.value = true
        val signUpInfo = SignUpResponse(
            true,
            ""
        )
        callback.onSignUp(
            signUpInfo
        )

        val client = ApiConfig.getApiService().signUp(name, email, password)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSignUp(it) }
                    responseCode = "201"
                    callback.onSignUp(
                        SignUpResponse(
                            true,
                            responseCode
                        )
                    )
                }
                else {
                    responseCode = "400"
                    callback.onSignUp(
                        SignUpResponse(
                            true,
                            responseCode
                        )
                    )
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _isLoading.value = true
                callback.onSignUp(
                    SignUpResponse(
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun addNewStory(callback: AddNewStoryCallback, token: String, imageFile: File, desc: String, lat: String? = null, lon: String? = null) {
        _isLoading.value = true
        callback.onAddNewStory(
            addNewStoryResponse = AddNewStoryResponse(
                true,
                ""
            )
        )

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().addNewStory(bearer = "Bearer $token", imageMultipart, description, latitude!!, longitude!!)

        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onAddNewStory(responseBody)
                    }
                    else {
                        callback.onAddNewStory(
                            addNewStoryResponse = AddNewStoryResponse(
                                true,
                                "Upload file failed"
                            )
                        )
                    }
                }
                else {
                    callback.onAddNewStory(
                        addNewStoryResponse = AddNewStoryResponse(
                            true,
                            "Upload file failed"
                        )
                    )
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = true
                callback.onAddNewStory(
                    addNewStoryResponse = AddNewStoryResponse(
                        true,
                        "Upload file failed"
                    )
                )
            }
        })
    }

    fun displayDetailUserStory(callback: GetDetailUserStoryCallback,token: String, id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory(bearer = "Bearer $token", id)
        client.enqueue(object: Callback<DetailStoryResponse> {
            override fun onResponse(call: Call<DetailStoryResponse>, response: Response<DetailStoryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onDetailUserStoryLoad(it) }
                }
                else {
                    val detailUserResponse = DetailStoryResponse(
                        true,
                        "Failed to load detail profile",
                        null
                    )
                    callback.onDetailUserStoryLoad(detailUserResponse)
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = true
                val detailUserResponse = DetailStoryResponse(
                    true,
                    t.message.toString(),
                    null
                )
                callback.onDetailUserStoryLoad(detailUserResponse)
            }
        })
    }

    fun getUserLocations(callback: GetUserLocationsCallback, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUsersLocation(bearer = "Bearer $token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onUsersLocationLoad(it) }
                }
                else {
                    val listStoryResponse = ListStoryResponse(
                        emptyList(),
                        true,
                        "Load failed!"
                    )
                    callback.onUsersLocationLoad(listStoryResponse)
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                _isLoading.value = true
                val listStoryResponse = ListStoryResponse(
                    emptyList(),
                    true,
                    t.message.toString()
                )
                callback.onUsersLocationLoad(listStoryResponse)
            }
        })
    }

    interface SignInCallback {
        fun onSignIn(signInResponse: SignInResponse)
    }

    interface SignUpCallback {
        fun onSignUp(signUpResponse: SignUpResponse)
    }

    interface AddNewStoryCallback {
        fun onAddNewStory(addNewStoryResponse: AddNewStoryResponse)
    }

    interface GetUserLocationsCallback {
        fun onUsersLocationLoad(storyResponse: ListStoryResponse)
    }

    interface GetDetailUserStoryCallback {
        fun onDetailUserStoryLoad(detailStoryResponse: DetailStoryResponse)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }
}