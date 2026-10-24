package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.captain.CaptainsFleetScreen
import com.example.ui.customer.CustomerHomeScreen
import com.example.ui.merchant.MerchantHomeScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AlbakalApp(viewModel: AlbakalViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(Unit) {
    viewModel.toastMessage.collectLatest { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  BackHandler(enabled = currentScreen != "LOGIN") {
    when (currentScreen) {
      "REGISTER" -> viewModel.navigateTo("LOGIN")
      "CUSTOMER_HOME", "MERCHANT_HOME", "ADMIN_DASHBOARD", "CAPTAIN_HOME" -> {
        // Confirm logout or keep on screen
      }
      else -> viewModel.navigateTo("LOGIN")
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize()
  ) { padding ->
    Box(modifier = Modifier.fillMaxSize()) {
      when (currentScreen) {
        "LOGIN" -> LoginScreen(viewModel)
        "REGISTER" -> RegisterScreen(viewModel)
        "CUSTOMER_HOME" -> CustomerHomeScreen(viewModel)
        "MERCHANT_HOME" -> MerchantHomeScreen(viewModel)
        "ADMIN_DASHBOARD" -> AdminDashboardScreen(viewModel)
        "CAPTAIN_HOME" -> CaptainsFleetScreen(viewModel)
        else -> LoginScreen(viewModel)
      }
    }
  }
}
