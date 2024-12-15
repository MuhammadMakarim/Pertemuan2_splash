package com.example.pertemuan2_splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pertemuan2_splash.ui.theme.Pertemuan2_splashTheme
import com.example.pertemuan2_splash.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pertemuan2_splashTheme {
                NavGraph()
            }
        }
    }
}