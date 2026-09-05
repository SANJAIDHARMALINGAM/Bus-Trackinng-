package com.example.bustracking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.R
import com.example.bustracking.data.AppStrings
import com.example.bustracking.data.UserProfileManager
import com.example.bustracking.navigation.BrandGreen

@Composable
fun SideMenuDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val strings = AppStrings.current
    val userName = UserProfileManager.fullName.value
    val avatarResId = UserProfileManager.avatarResId.value

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            // User Header with Gmail Profile Avatar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFE2E8F0), CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Gmail Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.clickable { onNavigate("profile") }) {
                    Text(
                        text = "${strings.hiGreetingPrefix}$userName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = strings.viewProfile,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Menu List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp)
            ) {

                SideMenuItem(
                    icon = Icons.Default.ReportProblem,
                    label = strings.complaints,
                    isSelected = false,
                    onClick = { onNavigate("help") }
                )
                SideMenuItem(
                    icon = Icons.Default.Person,
                    label = strings.navAccount,
                    isSelected = currentRoute == "profile",
                    onClick = { onNavigate("profile") }
                )
                SideMenuItem(
                    icon = Icons.Default.HelpOutline,
                    label = strings.helpSupport,
                    isSelected = currentRoute == "help",
                    onClick = { onNavigate("help") }
                )
                SideMenuItem(
                    icon = Icons.Default.Info,
                    label = strings.aboutUs,
                    isSelected = false,
                    onClick = { onNavigate("help") }
                )
            }

            // Bottom Sign Out Action
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSignOut() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = strings.signOut,
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = strings.signOut,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
fun SideMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFE7F3EC) else Color.Transparent
    val contentColor = if (isSelected) BrandGreen else Color(0xFF334155)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
