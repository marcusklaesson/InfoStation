package com.example.infostation.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.infostation.viewmodel.CELSIUS

class Prefs(context: Context) {
    private var UNIT = "unit"

    private val preferences: SharedPreferences =
        context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)

    var prefUnit: String?
        get() = preferences.getString(UNIT, CELSIUS)
        set(value) = preferences.edit().putString(UNIT, value).apply()


}