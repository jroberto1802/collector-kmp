package com.softcom.collector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.softcom.collector.platform.AndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContext.initialize(applicationContext)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { App() }
    }
}
