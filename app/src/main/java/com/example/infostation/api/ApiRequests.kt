package com.example.infostation.api

import com.example.infostation.models.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {
    @GET("data/2.5/weather")
    fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") celsius: String,
        @Query("lang") lang: String,
        @Query("APPID") api_key: String
    ): Call<Weather>
}