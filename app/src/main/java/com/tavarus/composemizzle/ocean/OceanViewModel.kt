package com.tavarus.composemizzle.ocean

import androidx.lifecycle.ViewModel

class OceanViewModel: ViewModel() {
    data class ViewState(
        val wheelRotation: Double
    )
}
