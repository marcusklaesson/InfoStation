package com.example.infostation.models

import com.example.infostation.ui.display.ValueType

data class CustomModel(
    val time: String? = "",
    val type: ValueType,
    val day: String? = "",
    val month: String? = "",
    val year: String? = "",
    val temp: String = "",
    val city: String? = "",
    val icon: String? = ""
)
