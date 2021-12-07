package com.example.infostation.models

import com.example.infostation.viewmodel.ValueType

data class Weather(
    val time: String? = "",
    val type: ValueType,
    val day: String? = "",
    val month: String? = "",
    val year: String? = "",
    val temp: Double? = 0.0,
    val city: String? = "",
    val icon: String? = "",
    val timeStamp: String? = "",
    val week: Int? = null,
    val dayOfWeek: String? = "",
    val description: String? = "",
    val main: Main? = null,
    val name: String? = "",
    val weather: List<WeatherX>? = listOf(),
    val wind: Wind? = null,
    val windSpeed: Double? = 0.0
)
