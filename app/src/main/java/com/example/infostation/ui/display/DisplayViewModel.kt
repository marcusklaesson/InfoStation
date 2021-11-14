package com.example.infostation.ui.display

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.infostation.api.BackendResult
import com.example.infostation.models.CustomModel
import com.example.infostation.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

enum class MonthType {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
}

enum class ValueType {
    TIME, DATE, TEMP
}

@HiltViewModel
class DisplayViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _time = MutableLiveData<CustomModel>()
    val time: LiveData<CustomModel> = _time

    private val _date = MutableLiveData<CustomModel>()
    val date: LiveData<CustomModel> = _date

    private val _temp = MutableLiveData<CustomModel>()
    val temp: LiveData<CustomModel> = _temp

    init {
        setupTime()
        setupDate()
        setupWeather()
    }

    private fun setupTime() {
        fixedRateTimer("time", false, 0L, 1000) {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _time.postValue(CustomModel(type = ValueType.TIME, time = currentTime))

            if (currentTime == "23:59") setupDate()
        }
    }

    private fun setupDate() {
        val date = LocalDate.now()
        val year = date.year
        val month = date.month
        val day = date.dayOfMonth

        _date.postValue(
            CustomModel(
                type = ValueType.DATE,
                day = day.toString(),
                month = month.toString(),
                year = year.toString()
            )
        )
    }

    private fun setupWeather() {
        fixedRateTimer("weather", false, 0L, 3600000) {
            repository.weather { weather ->
                when (weather) {
                    is BackendResult.Success -> {
                        Log.d("tag", "weather success ${weather.data}")
                        _temp.postValue(
                            CustomModel(
                                type = ValueType.TEMP,
                                temp = weather.data.main.temp.toInt().toString(),
                                city = weather.data.name,
                                icon = weather.data.weather.first().icon
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
}


