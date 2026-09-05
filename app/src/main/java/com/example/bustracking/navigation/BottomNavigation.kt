package com.example.bustracking.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.tooling.preview.Preview
import com.example.bustracking.data.AppStrings

sealed class BottomNavItem(
    val route: String,
    val defaultTitle: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Favourites : BottomNavItem("favorites", "Favourite", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object AI : BottomNavItem("ai", "AI", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Help : BottomNavItem("help", "Help", Icons.AutoMirrored.Filled.Help, Icons.AutoMirrored.Outlined.HelpOutline)
    object Account : BottomNavItem("profile", "Account", Icons.Filled.Person, Icons.Outlined.PersonOutline)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Favourites,
    BottomNavItem.AI,
    BottomNavItem.Help,
    BottomNavItem.Account
)

val BrandGreen = Color(0xFF136B3B)
val NavUnselectedGray = Color(0xFF9E9E9E)

@Composable
fun AppBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val strings = AppStrings.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFE5E7EB))
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route || (item.route == "home" && currentRoute == null)
                val localizedTitle = when (item.route) {
                    "home" -> strings.navHome
                    "favorites" -> strings.navFavourite
                    "ai" -> strings.navAi
                    "help" -> strings.navHelp
                    "profile" -> strings.navAccount
                    else -> item.defaultTitle
                }

                NavigationBarItem(
                    selected = selected,
                    alwaysShowLabel = true,
                    onClick = {
                        if (currentRoute != item.route) {
                            onNavigate(item.route)
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier.size(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item == BottomNavItem.AI) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(
                                            color = if (selected) BrandGreen else Color(0xFFDEF7EC),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = localizedTitle,
                                        tint = if (selected) Color.White else BrandGreen,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = localizedTitle,
                                    tint = if (selected) BrandGreen else NavUnselectedGray,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = localizedTitle,
                            fontSize = 11.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) BrandGreen else NavUnselectedGray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreen,
                        unselectedIconColor = NavUnselectedGray,
                        unselectedTextColor = NavUnselectedGray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationBarPreview() {
    AppBottomNavigationBar(
        currentRoute = "home",
        onNavigate = {}
    )
}

