package com.example.infostation.repo

import com.example.infostation.api.BackendResult
import com.example.infostation.models.Weather
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val service: WeatherService) {
    fun weather(latitude: String,longitude:String,units:String, callback: (BackendResult<Weather>) -> Unit) {
        service.weather(latitude,longitude,units) { result ->
            when (result) {
                is BackendResult.Success -> callback(BackendResult.Success(result.data))
                is BackendResult.Error -> callback(BackendResult.Error(result.message))
            }
        }
    }
}