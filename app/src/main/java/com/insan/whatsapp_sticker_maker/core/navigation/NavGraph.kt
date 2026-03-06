package com.insan.whatsapp_sticker_maker.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.insan.whatsapp_sticker_maker.features.sticker_pack.presentation.ui.HomeScreen

@Composable
fun NavGraph() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = Destination.Home) {
    composable<Destination.Home> { HomeScreen() }
  }
}
