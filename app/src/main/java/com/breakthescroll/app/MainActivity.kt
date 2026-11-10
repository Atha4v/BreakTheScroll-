package com.breakthescroll.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.breakthescroll.app.ui.navigation.BreakTheScrollApp
import com.breakthescroll.app.ui.theme.BreakTheScrollTheme
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import com.breakthescroll.app.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        val app = application as BreakTheScrollApplication
        AppViewModel.Factory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContent {
                val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()
                BreakTheScrollTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = LocalBtsPalette.current.background,
                    ) {
                        BreakTheScrollApp(
                            viewModel = viewModel,
                            onAcceptedToast = { title ->
                                Toast.makeText(this, "Completed: $title 🔥", Toast.LENGTH_SHORT).show()
                            },
                            onPassedToast = { title ->
                                Toast.makeText(this, "Passed: $title", Toast.LENGTH_SHORT).show()
                            },
                        )
                    }
                }
            }
        } catch (error: Throwable) {
            Log.e("BreakTheScroll", "Fatal UI init", error)
            setContent {
                BreakTheScrollTheme(darkTheme = true) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "BreakTheScroll hit a startup issue.\nClear app data and reopen.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}
