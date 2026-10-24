package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AlbakalApp
import com.example.ui.AlbakalViewModel
import com.example.ui.theme.AlbakalTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AlbakalTheme {
        val viewModel: AlbakalViewModel = viewModel()
        AlbakalApp(viewModel = viewModel)
      }
    }
  }
}
