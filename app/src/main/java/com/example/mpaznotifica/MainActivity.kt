package com.example.mpaznotifica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mpaznotifica.ui.navigation.AppNavigation
import com.example.mpaznotifica.ui.theme.MpazNotificaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MpazNotificaTheme {
                AppNavigation()
            }
        }
    }
}
