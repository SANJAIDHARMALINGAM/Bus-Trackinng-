package com.example.bustracking.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.navigation.AppBottomNavigationBar

data class NotificationItemData(
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val notifications = listOf(
        NotificationItemData(
            title = "Booking Confirmed",
            subtitle = "Your ticket has been booked successfully.",
            timeAgo = "2m ago",
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF0E9F6E),
            bgColor = Color(0xFFDEF7EC)
        ),
        NotificationItemData(
            title = "Trip Reminder",
            subtitle = "Your trip to Mysuru starts at 07:30 AM",
            timeAgo = "1h ago",
            icon = Icons.Default.Notifications,
            iconColor = Color(0xFF2563EB),
            bgColor = Color(0xFFDBEAFE)
        ),
        NotificationItemData(
            title = "Offer",
            subtitle = "Flat 10% off on your next booking!",
            timeAgo = "1d ago",
            icon = Icons.Default.LocalOffer,
            iconColor = Color(0xFFD97706),
            bgColor = Color(0xFFFEF3C7)
        ),
        NotificationItemData(
            title = "Service Update",
            subtitle = "Maintenance scheduled on 25 Aug",
            timeAgo = "2d ago",
            icon = Icons.Default.Info,
            iconColor = Color(0xFF3B82F6),
            bgColor = Color(0xFFE0E7FF)
        ),
        NotificationItemData(
            title = "Payment Successful",
            subtitle = "Your payment of ₹500 is successful.",
            timeAgo = "2d ago",
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF0E9F6E),
            bgColor = Color(0xFFDEF7EC)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = null,
                onNavigate = onNavigateBottom
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(notifications) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = item.bgColor,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.iconColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = item.timeAgo,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.subtitle,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
            }
        }
    }
}
