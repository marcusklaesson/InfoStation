package com.example.infostation.ui.display

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.infostation.R
import com.example.infostation.api.API
import com.example.infostation.models.CustomModel
import kotlinx.android.synthetic.main.item_list.view.*

class DisplayAdapter(
    private var displayData: ArrayList<CustomModel>
) : RecyclerView.Adapter<DisplayAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return displayData.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val display = displayData[position]

        val monthId = when (display.month) {
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

        when (display.type) {
            ValueType.TIME -> {
                holder.itemView.apply {
                    value.text = display.time
                    value.textSize = 50f
                }
            }
            ValueType.DATE -> {
                holder.itemView.value.text = "${display.day} $month ${display.year}"
            }
            ValueType.TEMP -> {
                holder.itemView.value.text = "${display.temp}°C ${display.city}"
                holder.itemView.icon.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(API.ICON_URL + display.icon + API.ICON_FORMAT)
                    .into(holder.itemView.icon)
            }
        }
    }

}