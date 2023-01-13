package com.tavarus.composemizzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tavarus.composemizzle.ocean.Ocean
import com.tavarus.composemizzle.ocean.OceanViewModel
import com.tavarus.composemizzle.ocean.WheelViewModel

@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "ocean"
    ) {
        composable("ocean") {
            // This is not being disposed properly/lifecycle safe etc
            // But it works for now with this being the only screen
            // And I've been stuck on it for a while so I'm moving past it
            // to work on more fun functionality
            // I *will* be back to it.
            val oceanScope = rememberCoroutineScope()
            val wheelViewModel = WheelViewModel()
            val oceanViewModel = OceanViewModel(oceanScope, wheelViewModel)
            Ocean(
                oceanViewModel,
                wheelViewModel,
            )
        }
        composable("details") {
            //TODO
        }
    }
}
