package com.tavarus.composemizzle.ocean

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tavarus.composemizzle.ui.theme.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Ocean(
    oceanViewModel: OceanViewModel,
    wheelViewModel: WheelViewModel,
) {

    val viewState by oceanViewModel.uiState.collectAsState()
//    LaunchedEffect(key1 = Unit, block = {
//        while (true) {
//            Log.d("KOG", "wow")
//            //here is where we want to do our update that recomposes the thingy
//            //So we might actually want this a bit outside of this composable yeah?
//            //Here we call a function that:
//            // Updates the composable with the data that's currently in the datamodel.
//            // So like, this should just be outside of the composable I guess?
//            // That or it could be like right here encapsulating the `Box` call?
//            // How would that work?
//            // Cuz we need a subscription no?
//            // Wait we just subscribe to the data right in here
//            // Hmm no because datamodel should always be 100% correct.
//            // We'd need like intermediary values in here that read from datamodel then only emit on this call
//            // But how do we emit the values from just in a coroutine?
//            // Like we can get the values at the right time here but how do we then send them to the ocean
//            // Without calling a static function
//            // And the function would ahve to be outside of the composable.
//
//
//            // We hold the viewmodel
//            // The viewmodel gives props to the lil rats
//            // Then the method in here calls the viewmodel, updates it, then that updates the rats??
//        }
//    })
    Box(
        modifier = Modifier
            .background(Color.OceanBackground)
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Boat(modifier = Modifier.align(Alignment.Center), viewState.boatAngle)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {

            }
            Wheel(
                modifier = Modifier
                    .weight(1f)
                    .padding(32.dp),
                onCenterUpdated = { x, y -> wheelViewModel.updateCenter(x, y) },
                onDragStart = { offset, rotation -> wheelViewModel.onDragStart(offset, rotation) },
                onDrag = { offset, rotation, running ->
                    wheelViewModel.onDrag(
                        offset,
                        rotation,
                        running
                    )
                },
                onDragAnimation = {rotation -> wheelViewModel.onDragAnimation(rotation) }
            )
        }
    }
}
