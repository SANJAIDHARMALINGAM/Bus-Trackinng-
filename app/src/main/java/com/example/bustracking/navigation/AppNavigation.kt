package com.example.bustracking.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bustracking.data.AppPreferences
import com.example.bustracking.screens.AiAssistantScreen
import com.example.bustracking.screens.BusDetailsScreen
import com.example.bustracking.screens.FavoritesScreen
import com.example.bustracking.screens.HelpSupportScreen
import com.example.bustracking.screens.HomeScreen
import com.example.bustracking.screens.LiveTrackingScreen
import com.example.bustracking.screens.LoginScreen
import com.example.bustracking.screens.NotificationsScreen
import com.example.bustracking.screens.ProfileScreen
import com.example.bustracking.screens.SearchResultsScreen
import com.example.bustracking.screens.WelcomeScreen

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()

    // Determines start destination: Welcome on first launch only, then Home or Login
    val startDestination = remember {
        AppPreferences.getStartDestination(context)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // IMAGE 1: WELCOME SCREEN (Only shown on the first app launch)
        composable("welcome") {
            WelcomeScreen(
                onGetStartedClick = {
                    AppPreferences.setHasSeenWelcome(context, true)
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onGuestAccessClick = {
                    AppPreferences.setHasSeenWelcome(context, true)
                    AppPreferences.setLoggedIn(context, true)
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // IMAGE 2: LOGIN SCREEN
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    AppPreferences.setHasSeenWelcome(context, true)
                    AppPreferences.setLoggedIn(context, true)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // IMAGE 4: HOME SCREEN (With Side Menu Drawer & Bottom Navigation)
        composable("home") {
            HomeScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onSignOut = {
                    AppPreferences.setLoggedIn(context, false)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // SEARCH RESULTS SCREEN (With Bottom Navigation)
        composable(
            route = "search_results?from={from}&to={to}&date={date}",
            arguments = listOf(
                navArgument("from") { type = NavType.StringType; defaultValue = "" },
                navArgument("to") { type = NavType.StringType; defaultValue = "" },
                navArgument("date") { type = NavType.StringType; defaultValue = "Today" }
            )
        ) { backStackEntry ->
            val rawFrom = backStackEntry.arguments?.getString("from") ?: ""
            val rawTo = backStackEntry.arguments?.getString("to") ?: ""
            val rawDate = backStackEntry.arguments?.getString("date") ?: "Today"

            val fromCity = try { java.net.URLDecoder.decode(rawFrom, "UTF-8") } catch (e: Exception) { rawFrom }
            val toCity = try { java.net.URLDecoder.decode(rawTo, "UTF-8") } catch (e: Exception) { rawTo }
            val travelDate = try { java.net.URLDecoder.decode(rawDate, "UTF-8") } catch (e: Exception) { rawDate }

            SearchResultsScreen(
                fromCity = fromCity,
                toCity = toCity,
                travelDate = travelDate,
                onBackClick = { navController.popBackStack() },
                onBusSelect = { busId ->
                    navController.navigate("bus_details/$busId")
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // BUS DETAILS SCREEN (With Bottom Navigation)
        composable(
            route = "bus_details/{busId}",
            arguments = listOf(navArgument("busId") { type = NavType.StringType; defaultValue = "101" })
        ) { backStackEntry ->
            val busId = backStackEntry.arguments?.getString("busId") ?: "101"
            BusDetailsScreen(
                busId = busId,
                onBackClick = { navController.popBackStack() },
                onTrackBusClick = { id ->
                    navController.navigate("live_tracking/$id")
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // LIVE TRACKING SCREEN (With Bottom Navigation)
        composable(
            route = "live_tracking/{busId}",
            arguments = listOf(navArgument("busId") { type = NavType.StringType; defaultValue = "101" })
        ) { backStackEntry ->
            val busId = backStackEntry.arguments?.getString("busId") ?: "101"
            LiveTrackingScreen(
                busId = busId,
                onBackClick = { navController.popBackStack() },
                onViewFullRouteClick = {
                    navController.navigate("bus_details/$busId")
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // FAVOURITES SCREEN (With Bottom Navigation)
        composable("favorites") {
            FavoritesScreen(
                onBackClick = { navController.popBackStack() },
                onRouteClick = { from, to ->
                    val encFrom = java.net.URLEncoder.encode(from, "UTF-8")
                    val encTo = java.net.URLEncoder.encode(to, "UTF-8")
                    navController.navigate("search_results?from=$encFrom&to=$encTo&date=Today")
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // AI ASSISTANT SCREEN (With Bottom Navigation)
        composable("ai") {
            AiAssistantScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToSearch = { from, to ->
                    val encFrom = java.net.URLEncoder.encode(from, "UTF-8")
                    val encTo = java.net.URLEncoder.encode(to, "UTF-8")
                    navController.navigate("search_results?from=$encFrom&to=$encTo&date=Today")
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HELP & SUPPORT SCREEN (With Bottom Navigation)
        composable("help") {
            HelpSupportScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // MY PROFILE / ACCOUNT SCREEN (With Bottom Navigation)
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutClick = {
                    AppPreferences.setLoggedIn(context, false)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToSearch = { from, to ->
                    val encFrom = java.net.URLEncoder.encode(from, "UTF-8")
                    val encTo = java.net.URLEncoder.encode(to, "UTF-8")
                    val encDate = java.net.URLEncoder.encode("Today", "UTF-8")
                    navController.navigate("search_results?from=$encFrom&to=$encTo&date=$encDate")
                },
                onNavigateToLiveTracking = { busId ->
                    navController.navigate("live_tracking/$busId")
                }
            )
        }

        // NOTIFICATIONS SCREEN (With Bottom Navigation)
        composable("notifications") {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateBottom = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}