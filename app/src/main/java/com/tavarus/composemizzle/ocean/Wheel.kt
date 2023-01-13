package com.tavarus.composemizzle.ocean

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import com.tavarus.composemizzle.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sign

@Composable
fun Wheel(
    modifier: Modifier = Modifier,
    onCenterUpdated: (Float, Float) -> Unit,
    onDragStart: (Offset, Float) -> Unit,
    onDrag: (Offset, Float, Boolean) -> Float,
    onDragAnimation: (Float) -> Unit,
) {
    // Rotation
    val rotation by remember { mutableStateOf(Animatable(0f)) }

    Card(
        modifier = modifier
            .onGloballyPositioned {
                val centerY = it.size.height / 2.0f
                val centerX = it.size.width / 2.0f
                onCenterUpdated(centerX, centerY)
            }
            .pointerInput(Unit) {
                coroutineScope {
                    detectDragGestures(
                        onDragStart = { offset ->
                            launch { rotation.stop() }
                            onDragStart(offset, rotation.value)
                        },
                        onDragEnd = {
                            launch {
                                rotation.animateTo(0f, spring(0.25f)) {
                                    onDragAnimation(this.value)
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        change.consumeAllChanges()
                        launch {
                            rotation.snapTo(onDrag(dragAmount, rotation.value, rotation.isRunning))
                        }
                    }
                }

            },
        shape = CircleShape,
        backgroundColor = Color.Transparent
    ) {
        Image(
            painterResource(R.drawable.ship_wheel),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .rotate(calculateVisibleRotation(rotation.value))
        )
    }
}

// All of this is a view-specific and stateless, so it stays in here instead of the viewModel
private fun calculateVisibleRotation(rotation: Float): Float {
    val visibleRotation = if (rotation > 0) {
        if (rotation / WHEEL_DECEL_CUTOFF < DECAY_TIME) {
            rotation - (rotation.pow(2) / (2 * WHEEL_DECEL_CUTOFF))
        } else {
            rotation + ((DECAY_TIME).pow(2) * WHEEL_DECEL_CUTOFF / 2) - (DECAY_TIME * rotation)  
        }
    } else {
        if (rotation / WHEEL_DECEL_CUTOFF > -DECAY_TIME) {
            rotation + (rotation.pow(2) / (2 * WHEEL_DECEL_CUTOFF))
        } else {
            rotation - ((DECAY_TIME).pow(2) * WHEEL_DECEL_CUTOFF / 2) - (DECAY_TIME * rotation)
        }
    }

    return visibleRotation.coerceIn(-MAX_VISIBLE_ROTATION, MAX_VISIBLE_ROTATION)
}

// MAX_VIS = MAX_PHYS + (DECAY^2 * CUTOFF)/2 - MAX_PHYS * DECAY
const val MAX_VISIBLE_ROTATION = 720f
const val MAX_PHYSICAL_ROTATION = 1440f

// At what percentage of the way to a full stop do we want to stop decelerating
const val DECAY_TIME = 0.7f

// A constant value based off of the above equation
val WHEEL_DECEL_CUTOFF =
    ((2 * MAX_VISIBLE_ROTATION) - (2 * MAX_PHYSICAL_ROTATION) + (2 * DECAY_TIME * MAX_PHYSICAL_ROTATION)) / DECAY_TIME.pow(
        2
    )
