package com.example.bustracking.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.navigation.BrandGreen

enum class AppSnackbarType {
    ERROR, SUCCESS, WARNING, INFO
}

/**
 * Universal premium popup notification host for the entire app.
 * Replaces standard Android Toasts and default Material Snackbars with a
 * polished floating card with themed icon badge, smooth dismissal, and crisp typography.
 */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) { data ->
        AppCustomSnackbar(data = data)
    }
}

@Composable
fun AppCustomSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    val message = data.visuals.message
    val lower = message.lowercase()

    val type = when {
        lower.contains("needed") || lower.contains("error") || lower.contains("failed") ||
        lower.contains("not match") || lower.contains("invalid") || lower.contains("cannot") ||
        lower.contains("required") || lower.contains("short") ||
        lower.contains("ಅಗತ್ಯವಿದೆ") || lower.contains("ತಪ್ಪಾದ") || lower.contains("ಆಗಿರಬಾರದು") -> AppSnackbarType.ERROR

        lower.contains("success") || lower.contains("confirmed") || lower.contains("saved") ||
        lower.contains("added") || lower.contains("changed") || lower.contains("updated") ||
        lower.contains("reset") || lower.contains("ಯಶಸ್ವಿಯಾಗಿ") -> AppSnackbarType.SUCCESS

        lower.contains("sos") || lower.contains("warning") || lower.contains("emergency") ||
        lower.contains("delayed") || lower.contains("caution") -> AppSnackbarType.WARNING

        else -> AppSnackbarType.INFO
    }

    val (accentColor, icon, badgeBg, borderColor) = when (type) {
        AppSnackbarType.ERROR -> Quadruple(
            Color(0xFFF87171),
            Icons.Default.ErrorOutline,
            Color(0x33EF4444),
            Color(0x55EF4444)
        )
        AppSnackbarType.SUCCESS -> Quadruple(
            Color(0xFF34D399),
            Icons.Default.CheckCircle,
            Color(0x3310B981),
            Color(0x5510B981)
        )
        AppSnackbarType.WARNING -> Quadruple(
            Color(0xFFFBBF24),
            Icons.Default.WarningAmber,
            Color(0x33F59E0B),
            Color(0x55F59E0B)
        )
        AppSnackbarType.INFO -> Quadruple(
            Color(0xFF38BDF8),
            Icons.Default.Info,
            Color(0x330284C7),
            Color(0x550284C7)
        )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A), // Premium sleek Slate 900
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Themed circular icon badge
            Surface(
                shape = CircleShape,
                color = badgeBg,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Message Text
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp,
                modifier = Modifier.weight(1f)
            )

            // Optional Interactive Action
            data.visuals.actionLabel?.let { actionLabel ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = actionLabel,
                    color = BrandGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { data.performAction() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            // Quick Dismiss Button
            IconButton(
                onClick = { data.dismiss() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
