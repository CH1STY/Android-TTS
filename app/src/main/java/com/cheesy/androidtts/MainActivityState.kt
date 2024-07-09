package com.cheesy.androidtts

import kotlinx.coroutines.flow.MutableStateFlow

data class MainActivityState(
    var dropDownExpanded : Boolean = false,
    var voiceStyle : String = "UK English Female",
    var textValue : String = "",
    var visibilityFrag: Float = 0f
)
