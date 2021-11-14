package com.example.infostation.api

import com.example.infostation.models.Weather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    var service: ApiRequests

    init {
        service = createService()
    }

    private fun createService(): ApiRequests {
        val retrofit = Retrofit.Builder()
            .baseUrl(API.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiRequests::class.java)
    }
}

typealias BackendCallback<T> = (BackendResult<T>) -> Unit

sealed class BackendResult<T> {
    class Success<T>(val data: Weather) : BackendResult<T>()
    class Error<T>(val message: String) : BackendResult<T>()

}