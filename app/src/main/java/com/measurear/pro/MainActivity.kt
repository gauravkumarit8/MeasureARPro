package com.measurear.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.measurear.pro.navigation.MeasureARProNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Android 15 (API 35) enforces edge-to-edge by default — call this
        // explicitly rather than relying on the default, so status/nav bar
        // insets are handled deliberately (Scaffold + NavigationBar consume
        // them automatically in Material3, but only once edge-to-edge is on).
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    MeasureARProNavHost()
                }
            }
        }
    }
}
