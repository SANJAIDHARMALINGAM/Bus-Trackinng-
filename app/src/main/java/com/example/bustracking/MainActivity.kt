package com.example.bustracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.bustracking.data.AppLanguageManager
import com.example.bustracking.navigation.AppNavigation
import com.example.bustracking.ui.theme.BusTrackingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize persistent language preferences
        AppLanguageManager.initialize(this)

        // Enable edge-to-edge full screen so the layout extends edge-to-edge with no awkward white cutoffs
        enableEdgeToEdge()

        setContent {
            BusTrackingTheme {
                AppNavigation()
            }
        }
    }
}