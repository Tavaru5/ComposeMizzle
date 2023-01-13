package com.tavarus.composemizzle.ocean

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OceanViewModel(
    val oceanScope: CoroutineScope,
    val wheelViewModel: WheelViewModel,
    ) {
    data class ViewState(
        val boatAngle: Float = 0f
    )

    private val _uiState = MutableStateFlow(ViewState())
    val uiState: StateFlow<ViewState> get() = _uiState

    init {
        oceanScope.launch {
            while (true) {
                val newAngle = _uiState.value.boatAngle + wheelViewModel.actualRotation/1440f
                //this boatangle is resetting or something
//                if (wheelViewModel.actualRotation != 0f) {
//                    Log.d("KOG", "wheel " + wheelViewModel.actualRotation/1440f)
//                    Log.d("KOG", "newangle: $newAngle")
//                }
                _uiState.value =  ViewState(boatAngle = newAngle)
                delay(20)
            }
        }
    }

}
