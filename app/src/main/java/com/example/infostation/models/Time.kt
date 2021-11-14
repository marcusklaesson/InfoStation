package com.example.infostation.models

import com.example.infostation.ui.display.ValueType

data class Time(
    val type: ValueType,
    val time: String? = ""
)
