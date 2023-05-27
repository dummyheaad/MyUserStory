package com.example.myuserstory

import com.example.myuserstory.data.remote.response.*

object DataDummy {

    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_test-pic.png",
                "Mae $i",
                "Ini Adalah Unit testing $i",
                "2022-01-08T06:34:18.598Z",
                -10.212,
                -10.212
            )
            items.add(quote)
        }
        return items
    }

    fun generateDummyStoryResponse(): ListStoryResponse {
        return ListStoryResponse(
            generateDummyListStoryItem(),
            false,
            "Stories fetched successfully"
        )
    }

    fun generateDummyAddStoryResponse(): AddNewStoryResponse {
        return AddNewStoryResponse(
            false,
            "success",
        )
    }


    fun generateDummyResponseSignInSuccess(): SignInResponse {
        val loginResult = SignInResult(
            "akane27",
            "user-TGuD2v4bJ7PAinJj",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVRHdUQydjRiSjdQQWluSmoiLCJpYXQiOjE2ODQ2MjAyODR9.i20a_LLtS0o05q3YWHImef3c9U3tHi-2tJYNEGv1tkE"
        )
        return SignInResponse(
            loginResult,
            error = false,
            message = "200"
        )
    }

    fun generateDummyResponseSignUp(): SignUpResponse {
        return SignUpResponse(
            error = false,
            message = "success"
        )
    }
}