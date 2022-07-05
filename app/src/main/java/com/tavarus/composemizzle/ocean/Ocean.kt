package com.tavarus.composemizzle.ocean

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tavarus.composemizzle.ui.theme.Color

@Composable
fun Ocean(

) {
    Box(modifier = Modifier
        .background(Color.OceanBackground)
        .fillMaxHeight()
        .fillMaxWidth()
        .padding(bottom = 16.dp)
    ) {
        Row(modifier = Modifier
            .align(Alignment.BottomEnd)
            .fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {

            }
            Wheel(modifier = Modifier
                .weight(2f)
                .padding(32.dp))
        }
    }
}
