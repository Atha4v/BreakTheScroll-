package com.breakthescroll.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.breakthescroll.app.ui.screens.ActivitiesScreen
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.SurfaceNeutral

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreakTheScrollTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SurfaceNeutral,
                ) {
                    ActivitiesScreen(
                        onAccepted = { challenge ->
                            Toast
                                .makeText(
                                    this,
                                    "Accepted: ${challenge.title}",
                                    Toast.LENGTH_SHORT,
                                )
                                .show()
                        },
                        onPassed = { challenge ->
                            Toast
                                .makeText(
                                    this,
                                    "Passed: ${challenge.title}",
                                    Toast.LENGTH_SHORT,
                                )
                                .show()
                        },
                    )
                }
            }
        }
    }
}
