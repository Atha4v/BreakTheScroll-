package com.breakthescroll.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.breakthescroll.app.ui.navigation.BreakTheScrollApp
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.DeepSlate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreakTheScrollTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSlate,
                ) {
                    BreakTheScrollApp(
                        onAcceptedToast = { title ->
                            Toast.makeText(this, "Accepted: $title", Toast.LENGTH_SHORT).show()
                        },
                        onPassedToast = { title ->
                            Toast.makeText(this, "Passed: $title", Toast.LENGTH_SHORT).show()
                        },
                    )
                }
            }
        }
    }
}
