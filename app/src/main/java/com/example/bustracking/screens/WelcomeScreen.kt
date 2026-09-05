package com.example.bustracking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bustracking.R

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    onGuestAccessClick: () -> Unit
) {
    // Fill the entire device screen seamlessly edge-to-edge.
    // The cropped image has 0 margin borders, filling top, bottom, and both sides completely.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B232E)),
        contentAlignment = Alignment.Center
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Image(
            painter = painterResource(id = R.drawable.main_container),
            contentDescription = "Karnataka Bus Welcome",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Interactive touch zones over the baked-in buttons (390x855 aspect ratio)
        // Buttons sit near ~82% of vertical height
        val buttonWidth = screenWidth * 0.42f
        val buttonHeight = 52.dp
        val buttonYOffset = screenHeight * 0.835f

        // "Get Started" Click Target
        Box(
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .offset(
                    x = -(screenWidth * 0.24f),
                    y = buttonYOffset - (screenHeight / 2)
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onGetStartedClick
                )
        )

        // "Guest Access" Click Target
        Box(
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .offset(
                    x = (screenWidth * 0.24f),
                    y = buttonYOffset - (screenHeight / 2)
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onGuestAccessClick
                )
        )
    }
}
