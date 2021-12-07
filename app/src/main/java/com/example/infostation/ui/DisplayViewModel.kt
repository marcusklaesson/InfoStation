package com.example.infostation.ui.display

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.infostation.api.BackendResult
import com.example.infostation.models.Weather
import com.example.infostation.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt

enum class ValueType {
    TIME, DATE, TEMP
}

const val ONE_SECOND: Long = 1000
const val FIFTEEN_MINUTES: Long = 900000
const val MIDNIGHT: String = "00:00"

@HiltViewModel
class DisplayViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _time = MutableLiveData<Weather>()
    val time: LiveData<Weather> = _time

    private val _date = MutableLiveData<Weather>()
    val date: LiveData<Weather> = _date

    private val _temp = MutableLiveData<Weather>()
    val temp: LiveData<Weather> = _temp

    init {
        setupTime()
        setupDate()
    }

    private fun setupTime() {
        fixedRateTimer("time", false, 0L, ONE_SECOND) {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _time.postValue(Weather(type = ValueType.TIME, time = currentTime))
            if (currentTime == MIDNIGHT) setupDate()
        }
    }

    private fun setupDate() {
        val date = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val weekNumber = date[weekFields.weekOfWeekBasedYear()]
        val year = date.year
        val month = date.month
        val day = date.dayOfMonth

        _date.postValue(
            Weather(
                type = ValueType.DATE,
                day = day.toString(),
                month = month.toString(),
                year = year.toString(),
                dayOfWeek = date.dayOfWeek.toString(),
                week = weekNumber
            )
        )
    }

    fun setupWeather(latitude: String, longitude: String) {
        repository.weather(latitude, longitude) { weather ->
            when (weather) {
                is BackendResult.Success -> {
                    Log.d("tag", "weather success ${weather.data}")
                    _temp.postValue(
                        Weather(
                            type = ValueType.TEMP,
                            temp = weather.data.main?.temp?.roundToInt().toString(),
                            city = weather.data.name,
                            icon = weather.data.weather?.first()?.icon,
                            description = weather.data.weather?.first()?.description,
                            timeStamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                Date()
                            )
                        )
                    )
                }
                is BackendResult.Error -> {
                    Log.d("tag", "weather fail ${weather.message}")
                }
            }
        }
    }

}


