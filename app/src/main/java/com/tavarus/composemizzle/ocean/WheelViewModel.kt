package com.tavarus.composemizzle.ocean

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sign


class WheelViewModel() {
    // Angle of the player's initial tap
    var initialRotation = 0f
    // How far past the max the rotation is
    var rotationOverflow = 0f
    // How many full rotations we've gone past
    var rotationOffset = 0
    var offsetX = 0f
    var offsetY = 0f
    var centerY = 0f
    var centerX = 0f

    fun updateCenter(cenX: Float, cenY: Float) {
        centerX = cenX
        centerY = cenY
    }

    fun onDragStart(offset: Offset, currentRotation: Float) {
        offsetX = offset.x
        offsetY = offset.y
        val currentRotationAngle = (currentRotation + 180).mod(360f) - 180
        initialRotation =
            touchAngle(offsetX, centerX, offsetY, centerY) - currentRotationAngle
    }

    fun onDrag(offset: Offset, currentRotation: Float, rotationRunning: Boolean): Float {
        // How many full rotations we've gone past
        if (rotationRunning) {
            rotationOffset = (currentRotation/360).toInt()
        }

        offsetX += offset.x
        offsetY += offset.y
        var newAngle = touchAngle(offsetX, centerX, offsetY, centerY) - initialRotation
        // Logic for if the user is at the max
        if (abs(currentRotation) == MAX_PHYSICAL_ROTATION) {
            // For the swap between -179 to 179
            if (abs(newAngle - rotationOverflow) > 180) {
                if (newAngle.sign == currentRotation.sign) {
                    // If the user is going opposite how they were (ie they're starting to unwind)
                    initialRotation =
                        (initialRotation + rotationOverflow + 180).mod(360f) - 180
                    newAngle -= rotationOverflow
                    rotationOverflow = 0f
                } else {
                    // Otherwise we just make sure the angle is past the max rotation
                    // so that it will get coerced down
                    rotationOverflow = newAngle
                    newAngle += currentRotation
                }
            } else {
                // Same as above, just different logic for determining whether they're
                // starting to unwind
                if ((rotationOverflow - newAngle).sign == currentRotation.sign) {
                    initialRotation =
                        (initialRotation + rotationOverflow + 180).mod(360f) - 180
                    newAngle -= rotationOverflow
                    rotationOverflow = 0f
                } else {
                    rotationOverflow = newAngle
                    newAngle += currentRotation
                }
            }
        } else {
            if (newAngle / 180 >= 1) {
                newAngle -= 360
            } else if (newAngle / 180 <= -1) {
                newAngle += 360
            }
            // When we're capped at max, the rotation offset doesn't keep going up, so this flips at some points
            // Check for a sign change
            if ((currentRotation - rotationOffset * 360) * newAngle <= 0) {
                val crossoverDiff =
                    (currentRotation - (newAngle + rotationOffset * 360)).toInt()
                // We only care about the change between 180 & -180, which will always be > 180,
                // so divide by 180 to get the rotation offset change
                rotationOffset += (crossoverDiff / 180)
            }
        }
        //TODO: Use this to also update the data model
        return (newAngle + (rotationOffset * 360)).coerceIn(
            -MAX_PHYSICAL_ROTATION,
            MAX_PHYSICAL_ROTATION
        )
    }
}

private fun touchAngle(touchX: Float, centerX: Float, touchY: Float, centerY: Float): Float {
    val dX = touchX - centerX
    val dY = centerY - touchY
    return Math.toDegrees(0 - atan2(dY.toDouble(), dX.toDouble())).toFloat()
}
