package com.tavarus.composemizzle

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tavarus.composemizzle.ocean.Ocean

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "ocean"
    ) {
        composable("ocean") {
            Ocean()
        }
        composable("details") {
            //TODO
        }
    }
}
