package com.example.infostation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.infostation.api.BackendResult
import com.example.infostation.models.Weather
import com.example.infostation.repo.Repository
import com.example.infostation.utils.combine
import com.example.infostation.view.prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

enum class ValueType {
    TIME, DATE, TEMP
}

const val ONE_SECOND: Long = 1000
const val FIFTEEN_MINUTES: Long = 900000
const val MIDNIGHT: String = "00:00"
const val CELSIUS: String = "metric"
const val FAHRENHEIT: String = "imperial"

@HiltViewModel
class DisplayViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _time = MutableLiveData<Weather>()
    val time: LiveData<Weather> = _time

    private val _date = MutableLiveData<Weather>()
    val date: LiveData<Weather> = _date

    private val _temp = MutableLiveData<Weather>()
    val temp: LiveData<Weather> = _temp

    init {
        getCurrentTime()
        getCurrentDate()
    }

    private fun getCurrentTime() {
        fixedRateTimer("time", false, 0L, ONE_SECOND) {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            updateTime(currentTime)
        }
    }

    private fun updateTime(currentTime: String) {
        _time.postValue(Weather(type = ValueType.TIME, time = currentTime))
        if (currentTime == MIDNIGHT) getCurrentDate()
    }

    private fun getCurrentDate() {
        getWeekNumber(LocalDate.now())
    }

    private fun getWeekNumber(date: LocalDate) {
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekNumber = date[weekFields.weekOfWeekBasedYear()]
        updateDate(date.year, date.month, date.dayOfMonth, date.dayOfWeek, weekNumber)
    }

    private fun updateDate(
        year: Int,
        month: Month,
        day: Int,
        dayOfWeek: DayOfWeek,
        weekNumber: Int
    ) {
        _date.postValue(
            Weather(
                type = ValueType.DATE,
                day = day.toString(),
                month = month.toString(),
                year = year.toString(),
                dayOfWeek = dayOfWeek.toString(),
                week = weekNumber
            )
        )
    }

    fun setupWeather(latitude: String, longitude: String, units: String) {
        repository.weather(latitude, longitude, units) { weather ->
            when (weather) {
                is BackendResult.Success -> {
                    Log.d("tag", "weather success ${weather.data}")
                    _temp.postValue(
                        Weather(
                            type = ValueType.TEMP,
                            temp = weather.data.main?.temp,
                            city = weather.data.name,
                            icon = weather.data.weather?.first()?.icon,
                            description = weather.data.weather?.first()?.description,
                            timeStamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                Date()
                            ),
                            windSpeed = weather.data.wind?.speed
                        )
                    )
                }
                is BackendResult.Error -> {
                    Log.d("tag", "weather fail ${weather.message}")
                    _temp.postValue(Weather())
                }
            }
        }
    }

    fun setupLiveDataLists(): LiveData<ArrayList<Weather?>> {
        val data = combine(time, date, temp) { time, date, temp ->
            arrayListOf(time, date, temp)
        }
        return data
    }

    fun updateUnit(latitude: String, longitude: String) {
        val unit = if (prefs.prefUnit == FAHRENHEIT) CELSIUS else FAHRENHEIT
        setupWeather(latitude, longitude, unit)
        prefs.prefUnit = unit
    }

}


