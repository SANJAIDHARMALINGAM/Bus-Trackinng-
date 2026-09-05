package com.example.bustracking.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.data.AppStrings
import com.example.bustracking.navigation.BrandGreen
import kotlinx.coroutines.launch

private val TextDark = Color(0xFF1E293B)
private val TextSub = Color(0xFF64748B)
private val LightGreenCircle = Color(0xFF10B981)
private val DestRedPin = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val strings = AppStrings.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("Today, 24 Aug 2026") }
    var showCalendarDialog by remember { mutableStateOf(false) }

    var isFromFocused by remember { mutableStateOf(false) }
    var isToFocused by remember { mutableStateOf(false) }

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }

    val allCities = remember {
        listOf(
            "Bengaluru", "Mysuru", "Mangaluru", "Hubballi", "Dharwad",
            "Belagavi", "Shivamogga", "Davangere", "Ballari", "Vijayapura",
            "Kalaburagi", "Tumakuru", "Udupi", "Hassan", "Bidar",
            "Hosapete", "Gadag", "Raichur", "Kolar", "Mandya",
            "Chikkamagaluru", "Madikeri", "Chitradurga", "Sirsi", "Karwar",
            "Gokarna", "Dharmasthala", "Kukke Subramanya", "Murudeshwara", "Hampi",
            "Bangalore", "Mysore", "Mangalore", "Shimoga", "Hubli", "Belgaum", "Bellary", "Gulbarga"
        )
    }

    fun getFuzzySuggestions(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        // 1. Direct prefix / substring matches get priority (including exact match so user can select/confirm it)
        val exactMatches = allCities.filter { city ->
            val c = city.lowercase()
            c.contains(q)
        }.sortedBy { city ->
            val c = city.lowercase()
            when {
                c == q -> 0
                c.startsWith(q) -> 1
                else -> 2
            }
        }

        if (exactMatches.size >= 5) return exactMatches.take(5)

        // Levenshtein distance calculation
        fun levenshtein(s1: String, s2: String): Int {
            val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
            for (i in 0..s1.length) dp[i][0] = i
            for (j in 0..s2.length) dp[0][j] = j
            for (i in 1..s1.length) {
                for (j in 1..s2.length) {
                    val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,      // deletion
                        dp[i][j - 1] + 1,      // insertion
                        dp[i - 1][j - 1] + cost // substitution
                    )
                }
            }
            return dp[s1.length][s2.length]
        }

        val maxAllowedDistance = when {
            q.length <= 3 -> 1
            q.length <= 6 -> 2
            else -> 3
        }

        val fuzzyMatches = allCities.filter { city ->
            val c = city.lowercase()
            !exactMatches.contains(city) && c != q && run {
                val dist = levenshtein(q, c)
                val prefixDist = if (c.length >= q.length) levenshtein(q, c.substring(0, q.length)) else dist
                dist <= maxAllowedDistance || prefixDist <= 1
            }
        }.sortedBy { city ->
            levenshtein(q, city.lowercase())
        }

        return (exactMatches + fuzzyMatches).distinct().take(5)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            SideMenuDrawerContent(
                currentRoute = "home",
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    onNavigate(route)
                },
                onSignOut = {
                    scope.launch { drawerState.close() }
                    onSignOut()
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.navHome,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Drawer Menu",
                                tint = TextDark
                            )
                        }
                    },
                    actions = {},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                AppBottomNavigationBar(
                    currentRoute = "home",
                    onNavigate = onNavigate
                )
            },
            containerColor = Color(0xFFF8FAFC)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Trip Search Main Card (Matches Image 4)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        // From & To Inputs with Swap Button (Matching Screenshot Precisely)
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // From Input
                                val fromSuggestions = remember(fromLocation, isFromFocused) {
                                    if (isFromFocused && fromLocation.isNotBlank()) {
                                        getFuzzySuggestions(fromLocation)
                                    } else emptyList()
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { fromFocusRequester.requestFocus() }
                                        .padding(end = 56.dp, top = 4.dp, bottom = 4.dp)
                                ) {
                                    // Custom green bullseye/circle icon
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(20.dp),
                                            shape = CircleShape,
                                            color = Color.Transparent,
                                            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF059669))
                                        ) {}
                                        Surface(
                                            modifier = Modifier.size(8.dp),
                                            shape = CircleShape,
                                            color = Color(0xFF059669)
                                        ) {}
                                    }

                                    Spacer(modifier = Modifier.width(18.dp))

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { fromFocusRequester.requestFocus() }
                                    ) {
                                        Text(
                                            text = strings.fromCity,
                                            fontSize = 13.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        BasicTextField(
                                            value = fromLocation,
                                            onValueChange = {
                                                fromLocation = it
                                                isFromFocused = true
                                            },
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF1E293B)
                                            ),
                                            cursorBrush = SolidColor(Color(0xFF059669)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(fromFocusRequester)
                                                .onFocusChanged { isFromFocused = it.isFocused },
                                            decorationBox = { innerTextField ->
                                                if (fromLocation.isEmpty()) {
                                                    Text(
                                                        text = "Enter starting city...",
                                                        fontSize = 17.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        )
                                    }
                                }

                                // Dropdown suggestions container for From
                                AnimatedVisibility(
                                    visible = fromSuggestions.isNotEmpty(),
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 40.dp, end = 56.dp, top = 6.dp, bottom = 6.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF8FAFC),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column {
                                            fromSuggestions.forEachIndexed { index, city ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            fromLocation = city
                                                            isFromFocused = false
                                                            focusManager.clearFocus()
                                                            keyboardController?.hide()
                                                        }
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Place,
                                                        contentDescription = null,
                                                        tint = Color(0xFF059669),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = city,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextDark
                                                    )
                                                }
                                                if (index < fromSuggestions.size - 1) {
                                                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 40.dp, end = 56.dp),
                                    thickness = 0.8.dp,
                                    color = Color(0xFFE2E8F0)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // To Input
                                val toSuggestions = remember(toLocation, isToFocused) {
                                    if (isToFocused && toLocation.isNotBlank()) {
                                        getFuzzySuggestions(toLocation)
                                    } else emptyList()
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { toFocusRequester.requestFocus() }
                                        .padding(end = 56.dp, top = 4.dp, bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Place,
                                        contentDescription = "To",
                                        tint = DestRedPin,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(18.dp))
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { toFocusRequester.requestFocus() }
                                    ) {
                                        Text(
                                            text = strings.toCity,
                                            fontSize = 13.sp,
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                         BasicTextField(
                                            value = toLocation,
                                            onValueChange = {
                                                toLocation = it
                                                isToFocused = true
                                            },
                                            singleLine = true,
                                            textStyle = TextStyle(
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF1E293B)
                                            ),
                                            cursorBrush = SolidColor(Color(0xFF059669)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(toFocusRequester)
                                                .onFocusChanged { isToFocused = it.isFocused },
                                            decorationBox = { innerTextField ->
                                                if (toLocation.isEmpty()) {
                                                    Text(
                                                        text = "Enter destination city...",
                                                        fontSize = 17.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        )
                                    }
                                }

                                // Dropdown suggestions container for To
                                AnimatedVisibility(
                                    visible = toSuggestions.isNotEmpty(),
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 40.dp, end = 56.dp, top = 6.dp, bottom = 6.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF8FAFC),
                                        shadowElevation = 2.dp
                                    ) {
                                        Column {
                                            toSuggestions.forEachIndexed { index, city ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            toLocation = city
                                                            isToFocused = false
                                                            focusManager.clearFocus()
                                                            keyboardController?.hide()
                                                        }
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Place,
                                                        contentDescription = null,
                                                        tint = DestRedPin,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = city,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextDark
                                                    )
                                                }
                                                if (index < toSuggestions.size - 1) {
                                                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Swap Floating Button (Clean circular button with up-down arrows)
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 3.dp,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .clickable {
                                        val temp = fromLocation
                                        fromLocation = toLocation
                                        toLocation = temp
                                    }
                            ) {
                                Box(modifier = Modifier.padding(10.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = "Swap Locations",
                                        tint = Color(0xFF334155),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Date Selector (Opens Modern Custom Calendar Dialog)
                        Surface(
                            color = Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    showCalendarDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = strings.departureDate, fontSize = 12.sp, color = TextSub)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = travelDate,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark
                                    )
                                }
                                IconButton(onClick = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                    showCalendarDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select Date",
                                        tint = TextDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Search Buses Button (Directs to Search Results Screen)
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                val trimmedFrom = fromLocation.trim()
                                val trimmedTo = toLocation.trim()

                                if (trimmedFrom.isBlank() && trimmedTo.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.inputNeededBothCities)
                                    }
                                    return@Button
                                }
                                if (trimmedFrom.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.inputNeededFrom)
                                    }
                                    return@Button
                                }
                                if (trimmedTo.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.inputNeededTo)
                                    }
                                    return@Button
                                }
                                if (trimmedFrom.equals(trimmedTo, ignoreCase = true)) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.sameCityError)
                                    }
                                    return@Button
                                }

                                com.example.bustracking.data.AppPreferences.addRecentSearch(context, trimmedFrom, trimmedTo)
                                val encodedFrom = java.net.URLEncoder.encode(trimmedFrom, "UTF-8")
                                val encodedTo = java.net.URLEncoder.encode(trimmedTo, "UTF-8")
                                val encodedDate = java.net.URLEncoder.encode(travelDate, "UTF-8")
                                onNavigate("search_results?from=$encodedFrom&to=$encodedTo&date=$encodedDate")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                        ) {
                            Text(
                                text = strings.searchBuses,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Searches Header
                Text(
                    text = strings.recentSearches,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Recent Searches Card List (Dynamic from AppPreferences)
                val recentSearchesList = remember(fromLocation, toLocation) {
                    com.example.bustracking.data.AppPreferences.getRecentSearches(context)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        recentSearchesList.take(3).forEachIndexed { index, pair ->
                            if (index > 0) {
                                HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
                            }
                            RecentSearchItem(
                                from = pair.first,
                                to = pair.second,
                                onClick = {
                                    fromLocation = pair.first
                                    toLocation = pair.second
                                    com.example.bustracking.data.AppPreferences.addRecentSearch(context, pair.first, pair.second)
                                    val encodedFrom = java.net.URLEncoder.encode(pair.first, "UTF-8")
                                    val encodedTo = java.net.URLEncoder.encode(pair.second, "UTF-8")
                                    val encodedDate = java.net.URLEncoder.encode("Today", "UTF-8")
                                    onNavigate("search_results?from=$encodedFrom&to=$encodedTo&date=$encodedDate")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCalendarDialog) {
        ModernCalendarDialog(
            initialDateStr = travelDate,
            onDateSelected = { newDate ->
                travelDate = newDate
            },
            onDismiss = {
                showCalendarDialog = false
            }
        )
    }
}

@Composable
fun RecentSearchItem(
    from: String,
    to: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = from,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "  ➔  ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSub
                )
                Text(
                    text = to,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun ModernCalendarDialog(
    initialDateStr: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentCal = remember { java.util.Calendar.getInstance() }
    val todayYear = currentCal.get(java.util.Calendar.YEAR)
    val todayMonth = currentCal.get(java.util.Calendar.MONTH)
    val todayDay = currentCal.get(java.util.Calendar.DAY_OF_MONTH)

    var displayYear by remember { mutableStateOf(todayYear) }
    var displayMonth by remember { mutableStateOf(todayMonth) }

    var selectedYear by remember { mutableStateOf(todayYear) }
    var selectedMonth by remember { mutableStateOf(todayMonth) }
    var selectedDay by remember { mutableStateOf(todayDay) }

    val monthNames = remember {
        listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    val daysOfWeek = remember { listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa") }

    // Quick shortcuts
    val tomorrowCal = remember {
        java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, 1) }
    }
    val tomYear = tomorrowCal.get(java.util.Calendar.YEAR)
    val tomMonth = tomorrowCal.get(java.util.Calendar.MONTH)
    val tomDay = tomorrowCal.get(java.util.Calendar.DAY_OF_MONTH)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                // Quick Select Chips (Today & Tomorrow)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isSelectedToday = selectedYear == todayYear && selectedMonth == todayMonth && selectedDay == todayDay
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelectedToday) BrandGreen else Color(0xFFF1F5F9),
                        border = if (isSelectedToday) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedYear = todayYear
                                selectedMonth = todayMonth
                                selectedDay = todayDay
                                displayYear = todayYear
                                displayMonth = todayMonth
                            }
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelectedToday) Color.White else Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }

                    val isSelectedTomorrow = selectedYear == tomYear && selectedMonth == tomMonth && selectedDay == tomDay
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelectedTomorrow) BrandGreen else Color(0xFFF1F5F9),
                        border = if (isSelectedTomorrow) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedYear = tomYear
                                selectedMonth = tomMonth
                                selectedDay = tomDay
                                displayYear = tomYear
                                displayMonth = tomMonth
                            }
                    ) {
                        Text(
                            text = "Tomorrow",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelectedTomorrow) Color.White else Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Month / Year Navigation Header
                val isPrevDisabled = (displayYear < todayYear) || (displayYear == todayYear && displayMonth <= todayMonth)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!isPrevDisabled) {
                                if (displayMonth == 0) {
                                    displayMonth = 11
                                    displayYear -= 1
                                } else {
                                    displayMonth -= 1
                                }
                            }
                        },
                        enabled = !isPrevDisabled,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = if (isPrevDisabled) Color(0xFFCBD5E1) else Color(0xFF1E293B),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${monthNames[displayMonth]} $displayYear",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    IconButton(
                        onClick = {
                            if (displayMonth == 11) {
                                displayMonth = 0
                                displayYear += 1
                            } else {
                                displayMonth += 1
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = Color(0xFF1E293B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Days of week header (Su Mo Tu We Th Fr Sa)
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Days Grid
                val monthCal = remember(displayYear, displayMonth) {
                    java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.YEAR, displayYear)
                        set(java.util.Calendar.MONTH, displayMonth)
                        set(java.util.Calendar.DAY_OF_MONTH, 1)
                    }
                }
                val daysInMonth = monthCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                val firstDayOfWeek = monthCal.get(java.util.Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday...
                val emptyLeadingCells = firstDayOfWeek - 1
                val totalCells = emptyLeadingCells + daysInMonth
                val totalRows = (totalCells + 6) / 7

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (rowIndex in 0 until totalRows) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (colIndex in 0..6) {
                                val cellIndex = rowIndex * 7 + colIndex
                                val dayNum = cellIndex - emptyLeadingCells + 1

                                if (dayNum in 1..daysInMonth) {
                                    val isSelected = (displayYear == selectedYear && displayMonth == selectedMonth && dayNum == selectedDay)
                                    val isToday = (displayYear == todayYear && displayMonth == todayMonth && dayNum == todayDay)

                                    val isPast = if (displayYear < todayYear) true
                                    else if (displayYear == todayYear && displayMonth < todayMonth) true
                                    else if (displayYear == todayYear && displayMonth == todayMonth && dayNum < todayDay) true
                                    else false

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .clickable(enabled = !isPast) {
                                                selectedDay = dayNum
                                                selectedMonth = displayMonth
                                                selectedYear = displayYear
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Surface(
                                                modifier = Modifier.size(34.dp),
                                                shape = CircleShape,
                                                color = BrandGreen
                                            ) {}
                                        } else if (isToday) {
                                            Surface(
                                                modifier = Modifier.size(34.dp),
                                                shape = CircleShape,
                                                color = Color.Transparent,
                                                border = BorderStroke(1.5.dp, BrandGreen)
                                            ) {}
                                        }

                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isSelected -> Color.White
                                                isPast -> Color(0xFFCBD5E1)
                                                isToday -> BrandGreen
                                                else -> Color(0xFF1E293B)
                                            }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val selCal = java.util.Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, selectedDay)
                            }
                            val isChosenToday = (selectedYear == todayYear && selectedMonth == todayMonth && selectedDay == todayDay)
                            val isChosenTomorrow = (selectedYear == tomYear && selectedMonth == tomMonth && selectedDay == tomDay)
                            val dayMonthYear = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(selCal.time)
                            val dayOfWeek = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault()).format(selCal.time)

                            val formattedStr = when {
                                isChosenToday -> "Today, $dayMonthYear"
                                isChosenTomorrow -> "Tomorrow, $dayMonthYear"
                                else -> "$dayOfWeek, $dayMonthYear"
                            }
                            onDateSelected(formattedStr)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Text(
                            text = "Confirm Date",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}