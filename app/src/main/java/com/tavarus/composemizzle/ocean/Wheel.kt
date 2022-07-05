package com.tavarus.composemizzle.ocean

import android.util.Log
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


@Composable
fun Wheel(modifier: Modifier = Modifier) {
    var rotation by remember { mutableStateOf(0f) }
    var rotationOffset by remember {
        mutableStateOf(0f)
    }
    var initialRotation by remember {
        mutableStateOf(0f)
    }
    var offsetX by remember {
        mutableStateOf(0f)
    }
    var offsetY by remember {
        mutableStateOf(0f)
    }
    var centerY = 0f
    var centerX = 0f


    var visibleRotation: Float = if (rotation > 0) {
        if (rotation / WHEEL_DECEL_CUTOFF < 0.7) {
            rotation - (rotation.pow(2) / (2 * WHEEL_DECEL_CUTOFF))
        } else {
            (0.7f * WHEEL_DECEL_CUTOFF) - ((0.7f * WHEEL_DECEL_CUTOFF).pow(2) / (2 * WHEEL_DECEL_CUTOFF)) + .3f * (rotation - (0.7f * WHEEL_DECEL_CUTOFF))
        }
    } else {
        if (rotation / WHEEL_DECEL_CUTOFF > -0.7) {
            Log.d("KOG", "rotation: $rotation")
            rotation + (rotation.pow(2) / (2 * WHEEL_DECEL_CUTOFF))
        } else {
            0f - (0.7f * WHEEL_DECEL_CUTOFF) + ((0.7f * WHEEL_DECEL_CUTOFF).pow(2) / (2 * WHEEL_DECEL_CUTOFF)) + .3f * (rotation + (0.7f * WHEEL_DECEL_CUTOFF))
        }
    }
    if (visibleRotation > MAX_VISIBLE_ROTATION) {
        visibleRotation = MAX_VISIBLE_ROTATION
    } else if (visibleRotation < -MAX_VISIBLE_ROTATION) {
        visibleRotation = -MAX_VISIBLE_ROTATION
    }
    Log.d("KOG", "visible: $visibleRotation")

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
                        initialRotation = touchAngle(offsetX, centerX, offsetY, centerY)
                    }
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    val newAngle = touchAngle(offsetX, centerX, offsetY, centerY) - initialRotation

                    // Check for a sign change
                    if ((rotation - rotationOffset * 360) * newAngle < 0) {
                        // If there is a sign change, check the difference between before and after
                        val crossoverDiff = (rotation - (newAngle + rotationOffset * 360)).toInt()
                        // We only care about the change between 180 & -180, which will always be > 180,
                        // so divide by 180 to get the rotation offset change
                        rotationOffset += (crossoverDiff / 180)
                    }
                    rotation = newAngle + (rotationOffset * 360)
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
                .rotate(visibleRotation)
        )
    }
}

private fun touchAngle(touchX: Float, centerX: Float, touchY: Float, centerY: Float): Float {
    val dX = touchX - centerX
    val dY = centerY - touchY
    return Math.toDegrees(0 - atan2(dY.toDouble(), dX.toDouble())).toFloat()
}

const val WHEEL_DECEL_CUTOFF = 1080f
const val MAX_VISIBLE_ROTATION = 720f
