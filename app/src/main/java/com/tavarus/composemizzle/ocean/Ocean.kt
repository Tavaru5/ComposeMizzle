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
