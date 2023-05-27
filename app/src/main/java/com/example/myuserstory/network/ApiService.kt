package com.example.myuserstory.network

import com.example.myuserstory.data.*
import com.example.myuserstory.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("v1/stories")
    suspend fun getListStory(
        @Header("Authorization") bearer: String?,
        @QueryMap queries: Map<String, Int>
    ): ListStoryResponse

    @GET("v1/stories/{id}")
    fun getDetailStory(
        @Header("Authorization") bearer: String?,
        @Path("id") id: String
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("/v1/stories")
    fun addNewStory(
        @Header("Authorization") bearer: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<AddNewStoryResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun signUp(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SignUpResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun signIn(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<SignInResponse>

    @GET("/v1/stories?location=1")
    fun getUsersLocation(
        @Header("Authorization") bearer: String?
    ): Call<ListStoryResponse>
}