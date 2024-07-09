package com.cheesy.androidtts

import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.log

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()
    var callingJob : Job? = null
    fun toggleDropDown() {
        _state.value = _state.value.copy(
            dropDownExpanded = !(_state.value.dropDownExpanded)
        )
    }

    fun setVoiceStyle(item: String) {
        _state.value = _state.value.copy(
            voiceStyle = item ,
            dropDownExpanded = !(_state.value.dropDownExpanded)
        )
    }

    fun updateText(text:String,webView: WebView){
        _state.value = _state.value.copy(
            textValue = text
        )
        callingJob?.cancel()
        callingJob = viewModelScope.launch {
            delay(1000)
            if (text.isNotEmpty()){
                webView.loadUrl("javascript:playVoice(\"${text}\",\"${state.value.voiceStyle}\")")
            }
        }
    }
}