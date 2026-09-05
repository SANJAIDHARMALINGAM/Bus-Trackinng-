package com.example.bustracking.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.example.bustracking.data.AppStrings
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.navigation.BrandGreen

data class BusSearchResult(
    val id: String,
    val title: String,
    val busNumber: String,
    val departureTime: String,
    val departureCity: String,
    val arrivalTime: String,
    val arrivalCity: String,
    val duration: String,
    val fare: String,
    val status: String,
    val isDelayed: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    fromCity: String = "",
    toCity: String = "",
    travelDate: String = "Today, 24 Aug 2026",
    onBackClick: () -> Unit,
    onBusSelect: (String) -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val strings = AppStrings.current

    val hasValidRoute = fromCity.isNotBlank() && toCity.isNotBlank()

    var searchInput by remember { mutableStateOf("") }
    var activeSearchQuery by remember { mutableStateOf("") }

    val allRouteBuses = remember(fromCity, toCity) {
        listOf(
            BusSearchResult(
                id = "101",
                title = "KSRTC Airavat Club Class",
                busNumber = "KA-09-F-1234",
                departureTime = "07:30 AM",
                departureCity = fromCity.ifBlank { "Bengaluru" },
            arrivalTime = "10:15 AM",
            arrivalCity = toCity.ifBlank { "Mysuru" },
            duration = "2h 45m",
            fare = "₹250",
            status = "On Time"
        ),
        BusSearchResult(
            id = "204",
            title = "KSRTC Rajahamsa Executive",
            busNumber = "KA-09-F-5678",
            departureTime = "08:00 AM",
            departureCity = fromCity.ifBlank { "Bengaluru" },
            arrivalTime = "11:00 AM",
            arrivalCity = toCity.ifBlank { "Mysuru" },
            duration = "3h 00m",
            fare = "₹200",
            status = "Delayed (15m)",
            isDelayed = true
        ),
        BusSearchResult(
            id = "42A",
            title = "KSRTC Karnataka Sarige",
            busNumber = "KA-09-F-9012",
            departureTime = "08:30 AM",
            departureCity = fromCity.ifBlank { "Bengaluru" },
            arrivalTime = "12:00 PM",
            arrivalCity = toCity.ifBlank { "Mysuru" },
            duration = "3h 30m",
            fare = "₹120",
            status = "On Time"
        ),
        BusSearchResult(
            id = "305B",
            title = "NWKRTC Sleeper Deluxe",
            busNumber = "KA-25-F-3344",
            departureTime = "09:15 AM",
            departureCity = fromCity.ifBlank { "Bengaluru" },
            arrivalTime = "01:30 PM",
            arrivalCity = toCity.ifBlank { "Mysuru" },
            duration = "4h 15m",
            fare = "₹350",
            status = "On Time"
        ),
        BusSearchResult(
            id = "510",
            title = "VRL Multi-Axle Volvo",
            busNumber = "KA-01-AB-7890",
            departureTime = "10:00 AM",
            departureCity = fromCity.ifBlank { "Bengaluru" },
            arrivalTime = "01:15 PM",
            arrivalCity = toCity.ifBlank { "Mysuru" },
            duration = "3h 15m",
            fare = "₹400",
            status = "On Time"
        )
    )}

    val filteredBuses = remember(activeSearchQuery, allRouteBuses, hasValidRoute) {
        if (!hasValidRoute) emptyList()
        else {
            val query = activeSearchQuery.trim().lowercase()
            if (query.isBlank()) allRouteBuses
            else {
                allRouteBuses.filter { bus ->
                    bus.title.lowercase().contains(query) ||
                    bus.busNumber.lowercase().contains(query) ||
                    bus.departureCity.lowercase().contains(query) ||
                    bus.arrivalCity.lowercase().contains(query)
                }
            }
        }
    }

    fun executeSearch() {
        focusManager.clearFocus()
        keyboardController?.hide()
        val trimmed = searchInput.trim()
        if (trimmed.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(strings.inputNeededSearchQuery)
            }
        } else {
            activeSearchQuery = trimmed
        }
    }

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (hasValidRoute) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = fromCity,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "  ➔  ",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = toCity,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                            Text(
                                text = travelDate.ifBlank { strings.today },
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    } else {
                        Text(
                            text = strings.availableBuses,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
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
                currentRoute = "search_results",
                onNavigate = onNavigateBottom
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!hasValidRoute) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = strings.inputNeeded,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.enterRouteToViewBuses,
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onBackClick,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = strings.whereDoYouWantToGo,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // Search Input Header
                item {
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            activeSearchQuery = it
                        },
                        placeholder = { Text("Search buses, operators...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (searchInput.trim().isEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.inputNeededSearchQuery)
                                    }
                                } else {
                                    activeSearchQuery = searchInput.trim()
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B))
                            }
                        },
                        trailingIcon = {
                            if (searchInput.isNotBlank()) {
                                IconButton(onClick = {
                                    searchInput = ""
                                    activeSearchQuery = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF64748B))
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (searchInput.trim().isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(strings.inputNeededSearchQuery)
                                }
                            } else {
                                activeSearchQuery = searchInput.trim()
                            }
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BrandGreen,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // Results List
                if (filteredBuses.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (activeSearchQuery.isNotBlank()) "No buses found for \"$activeSearchQuery\"" else "No buses available",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try searching with a different bus name or number",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                } else {
                    items(filteredBuses, key = { it.id }) { bus ->
                        BusResultCard(
                            bus = bus,
                            onClick = { onBusSelect(bus.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusResultCard(
    bus: BusSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header: Name & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bus.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = bus.busNumber,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (bus.isDelayed) Color(0xFFFDE8E8) else Color(0xFFDEF7EC)
                ) {
                    Text(
                        text = bus.status,
                        color = if (bus.isDelayed) Color(0xFFE02424) else Color(0xFF0E9F6E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Timing Row with connecting duration bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = bus.departureTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = bus.departureCity,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Middle Bar
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = bus.duration,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Canvas(modifier = Modifier.fillMaxWidth().height(8.dp)) {
                        val midY = size.height / 2
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = Offset(20f, midY),
                            end = Offset(size.width - 20f, midY),
                            strokeWidth = 2.5f
                        )
                        drawCircle(
                            color = Color(0xFF64748B),
                            radius = 4.5f,
                            center = Offset(20f, midY)
                        )
                        drawCircle(
                            color = Color(0xFF64748B),
                            radius = 4.5f,
                            center = Offset(size.width - 20f, midY)
                        )
                    }
                }

                // Arrival
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = bus.arrivalTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = bus.arrivalCity,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fare
            Text(
                text = bus.fare,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
        }
    }
}
