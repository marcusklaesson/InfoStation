package com.example.infostation.ui.display

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.infostation.api.BackendResult
import com.example.infostation.models.CustomModel
import com.example.infostation.models.Weather
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

    private val _displayValues = MutableLiveData<ArrayList<CustomModel>>()
    val displayValues: LiveData<ArrayList<CustomModel>> = _displayValues


    init {
        getWeather()
    }


    private fun setupData(data: Weather) {
        val date = LocalDate.now()
        val year = date.year
        val month = date.month
        val day = date.dayOfMonth
        var currentTime: String? = ""

        fixedRateTimer("timer", false, 0L, 1000) {
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            val model = arrayListOf(
                CustomModel(
                    type = ValueType.TIME,
                    time = currentTime,
                ),
                CustomModel(
                    type = ValueType.DATE,
                    day = day.toString(),
                    month = month.toString(),
                    year = year.toString()
                ),
                CustomModel(
                    type = ValueType.TEMP,
                    temp = data.main.temp.toInt(),
                    city = data.name,
                    icon = data.weather.first().icon
                )
            )

            _displayValues.postValue(model)
        }
    }

    private fun getWeather() {
        repository.weather { weather ->
            when (weather) {
                is BackendResult.Success -> {
                    Log.d("tag", "weather success ${weather.data}")
                    setupData(weather.data)
                }
                is BackendResult.Error -> {
                    Log.d("tag", "weather fail ${weather.message}")
                }
            }
        }
    }

}



