package com.example.infostation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.infostation.R
import com.example.infostation.api.API
import com.example.infostation.models.Weather
import com.example.infostation.view.prefs
import com.example.infostation.viewmodel.CELSIUS
import com.example.infostation.viewmodel.ValueType
import kotlinx.android.synthetic.main.item_date.view.*
import kotlinx.android.synthetic.main.item_time.view.*
import kotlinx.android.synthetic.main.item_weather.view.*
import kotlin.math.roundToInt

enum class MonthType {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
}

enum class DayType {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

interface OnTempClickListener {
    fun onItemClicked()
}

class DisplayAdapter(
    private var displayData: ArrayList<Weather?>,
    private var clickListener: OnTempClickListener
) : RecyclerView.Adapter<DisplayAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private object ListItemConstants {
        const val LAYOUT_TIME = 0
        const val LAYOUT_DATE = 1
        const val LAYOUT_WEATHER = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewId = when (viewType) {
            ListItemConstants.LAYOUT_WEATHER -> R.layout.item_weather
            ListItemConstants.LAYOUT_DATE -> R.layout.item_date
            else -> R.layout.item_time
        }
        val view = LayoutInflater.from(parent.context).inflate(viewId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayData[position]?.type) {
            ValueType.TIME -> ListItemConstants.LAYOUT_TIME
            ValueType.DATE -> ListItemConstants.LAYOUT_DATE
            ValueType.TEMP -> ListItemConstants.LAYOUT_WEATHER
            else -> ListItemConstants.LAYOUT_TIME
        }
    }


    override fun getItemCount(): Int {
        return displayData.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val display = displayData[position]

        val dayId = when (display?.dayOfWeek) {
            DayType.MONDAY.name -> R.string.monday
            DayType.TUESDAY.name -> R.string.tuesday
            DayType.WEDNESDAY.name -> R.string.wednesday
            DayType.THURSDAY.name -> R.string.thursday
            DayType.FRIDAY.name -> R.string.friday
            DayType.SATURDAY.name -> R.string.saturday
            DayType.SUNDAY.name -> R.string.sunday
            else -> null
        }
        val day = dayId?.let { holder.itemView.context.getString(it) }

        val monthId = when (display?.month) {
            MonthType.JANUARY.name -> R.string.january
            MonthType.FEBRUARY.name -> R.string.february
            MonthType.MARCH.name -> R.string.march
            MonthType.APRIL.name -> R.string.april
            MonthType.MAY.name -> R.string.may
            MonthType.JUNE.name -> R.string.june
            MonthType.JULY.name -> R.string.july
            MonthType.AUGUST.name -> R.string.august
            MonthType.SEPTEMBER.name -> R.string.september
            MonthType.OCTOBER.name -> R.string.october
            MonthType.NOVEMBER.name -> R.string.november
            MonthType.DECEMBER.name -> R.string.december
            else -> null
        }
        val month = monthId?.let { holder.itemView.context.getString(it) }

        when (display?.type) {
            ValueType.TIME -> {
                holder.itemView.apply {
                    time.text = display.time
                    time.textSize = 50f
                }
            }
            ValueType.DATE -> {
                holder.itemView.apply {
                    date.text = "${display.day} $month ${display.year}"
                    week_and_day.text =
                        context.getString(R.string.week) + display.week.toString() + " " + day
                }
            }
            ValueType.TEMP -> {
                holder.itemView.apply {
                    val unit = if (prefs.prefUnit == CELSIUS) {
                        "${display.temp?.roundToInt()}${context.getString(R.string.celsius)}"

                    } else {
                        "${display.temp}${context.getString(R.string.fahrenheit)}"
                    }
                    weather.text = "$unit ${display.city}"

                    val windSpeed = if (prefs.prefUnit == CELSIUS) "m/s" else "mph"
                    wind.text = "${display.windSpeed.toString()} $windSpeed"

                    icon.visibility = View.VISIBLE
                    description.text = display.description
                    weather.setOnClickListener {
                        clickListener.onItemClicked()
                    }
                }
                Glide.with(holder.itemView.context)
                    .load(API.ICON_URL + (display.icon) + API.ICON_FORMAT)
                    .into(holder.itemView.icon)
            }
        }
    }
}