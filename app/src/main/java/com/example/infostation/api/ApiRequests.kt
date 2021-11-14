package com.example.infostation.api

import com.example.infostation.models.Weather
import retrofit2.Call
import retrofit2.http.GET

interface ApiRequests {
    @GET(API.WEATHER + API.KEY)
    fun getWeather(): Call<Weather>
}