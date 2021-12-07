package com.example.infostation.repository

import android.util.Log
import com.example.infostation.api.API
import com.example.infostation.api.ApiClient
import com.example.infostation.api.BackendCallback
import com.example.infostation.api.BackendResult
import com.example.infostation.models.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

interface WeatherServiceListener {
    fun weather(latitude: String, longitude: String, callback: BackendCallback<Weather>)
}

class WeatherService @Inject constructor() : WeatherServiceListener {
    override fun weather(latitude: String, longitude: String, callback: BackendCallback<Weather>) {
        ApiClient.service.getWeather(latitude, longitude, "metric", Locale.getDefault().language, API.KEY)
            .enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            callback(BackendResult.Success(it))
                        }
                    }
                }

                override fun onFailure(call: Call<Weather>, t: Throwable) {
                    callback(BackendResult.Error(t.message ?: ""))
                    Log.d("tag", t.message.toString())
                }
            })
    }

}