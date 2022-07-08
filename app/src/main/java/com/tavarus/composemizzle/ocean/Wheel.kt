package com.tavarus.composemizzle.ocean

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import com.tavarus.composemizzle.R
import kotlin.math.atan2
import kotlin.math.pow


/*
 * TODO: The user can rotate past the max visible/physical, and when they try to
    rotate back, they have to undo whatever they went past the max before they
    see any changes
 * TODO: Animate the wheel rotating back to the 0 resting point when the user lets go
 */

@Composable
fun Wheel(modifier: Modifier = Modifier) {
    // Some of this state should be moved out to a viewmodel, especially as we start to use the
    // visible rotation as a control for the steering of the ship.
    var rotation by remember { mutableStateOf(0f) }
    var rotationOffset by remember { mutableStateOf(0f) }
    var initialRotation by remember { mutableStateOf(0f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var centerY = 0f
    var centerX = 0f

    Card(
        modifier = modifier
            .onGloballyPositioned {
                centerY = it.size.height / 2.0f
                centerX = it.size.width / 2.0f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        offsetX = offset.x
                        offsetY = offset.y
                        val currentRotationAngle = (rotation + 180).mod(360f) - 180
                        initialRotation = touchAngle(offsetX, centerX, offsetY, centerY) - currentRotationAngle
                    }
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    var newAngle = touchAngle(offsetX, centerX, offsetY, centerY) - initialRotation

                    if (newAngle / 180 >= 1) {
                        newAngle -= 360
                    } else if (newAngle / 180 <= -1) {
                        newAngle += 360
                    }
                    // Check for a sign change
                    if ((rotation - rotationOffset * 360) * newAngle < 0) {
                        // If there is a sign change, check the difference between before and after
                        val crossoverDiff = (rotation - (newAngle + rotationOffset * 360)).toInt()
                        // We only care about the change between 180 & -180, which will always be > 180,
                        // so divide by 180 to get the rotation offset change
                        rotationOffset += (crossoverDiff / 180)
                    }
                    rotation = (newAngle + (rotationOffset * 360))
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
                .rotate(calculateVisibleRotation(rotation))
        )
    }
}

private fun touchAngle(touchX: Float, centerX: Float, touchY: Float, centerY: Float): Float {
    val dX = touchX - centerX
    val dY = centerY - touchY
    return Math.toDegrees(0 - atan2(dY.toDouble(), dX.toDouble())).toFloat()
}

private fun calculateVisibleRotation(rotation: Float): Float {
    val visibleRotation = if (rotation > 0) {
        if (rotation / WHEEL_DECEL_CUTOFF < DECAY_TIME) {
            rotation - (rotation.pow(2) / (2 * WHEEL_DECEL_CUTOFF))
        } else {
            rotation + ((DECAY_TIME).pow(2) * WHEEL_DECEL_CUTOFF / 2) - (DECAY_TIME * rotation)
        }
    } else {
        if (rotation / WHEEL_DECEL_CUTOFF > DECAY_TIME) {
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
val WHEEL_DECEL_CUTOFF = ((2 * MAX_VISIBLE_ROTATION) - (2 * MAX_PHYSICAL_ROTATION) + (2 * DECAY_TIME * MAX_PHYSICAL_ROTATION))/ DECAY_TIME.pow(2)
