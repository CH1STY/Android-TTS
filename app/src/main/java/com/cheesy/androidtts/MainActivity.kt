package com.cheesy.androidtts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import com.cheesy.androidtts.Constants.PRIMARY_URL
import com.cheesy.androidtts.Constants.voiceNames
import com.cheesy.androidtts.ui.theme.AndroidTTSTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    lateinit var localWebView: WebView

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        setContent {
            AndroidTTSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    BaseComposable(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        state = state
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BaseComposable(
        modifier: Modifier,
        viewModel: MainActivityViewModel,
        state: MainActivityState
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LocalWebView(modifier = Modifier.fillMaxSize(state.visibilityFrag), url = PRIMARY_URL)
            TextField(value = state.textValue, onValueChange = {
                viewModel.updateText(it, webView = localWebView)
            })
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val mMaxHeight = maxHeight
                val mMaxWidth = maxWidth
                Button(modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(90f)
                    .align(Center),onClick = { viewModel.toggleDropDown() }) {
                    Text(text = "${state.voiceStyle} ↓", fontWeight = FontWeight.Black)
                }
                DropdownMenu(
                    expanded = state.dropDownExpanded,
                    onDismissRequest = { viewModel.toggleDropDown() }) {
                    Box(
                        modifier = Modifier
                            .width(mMaxWidth)
                            .height(250.dp), // Fill both width and height
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(), // Only fill width here
                        ) {
                            items(voiceNames) { item ->
                                DropdownMenuItem(  text = { Text(text = item) },
                                    onClick = {
                                        viewModel.setVoiceStyle(item)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun LocalWebView(modifier: Modifier, url: String) {
        AndroidView(modifier = modifier, factory = {
            WebView(it).apply {
                //Set to Class Variable
                localWebView = this
                //Setup the View
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                //Enable JS
                settings.javaScriptEnabled = true
                //Set WebChromeClient Necessary Function
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        //Listen to Console Messages
                        Log.i(TAG, "onConsoleMessage: ${consoleMessage?.message()}")
                        return super.onConsoleMessage(consoleMessage)
                    }
                    override fun onPermissionRequest(request: PermissionRequest?) {
                        super.onPermissionRequest(request)
                    }
                }
            }
        }, update = {
            it.loadUrl(url)
        })

    }

}