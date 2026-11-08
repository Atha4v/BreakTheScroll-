package com.breakthescroll.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
