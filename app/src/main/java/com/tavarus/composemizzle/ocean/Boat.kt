package com.tavarus.composemizzle.ocean

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tavarus.composemizzle.R

@Composable
fun Boat(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.boat),
        contentDescription = "",
        modifier = modifier.size(80.dp),
    )
}
