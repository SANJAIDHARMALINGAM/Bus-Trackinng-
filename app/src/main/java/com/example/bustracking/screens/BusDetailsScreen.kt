package com.example.bustracking.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.R
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.navigation.BrandGreen
import kotlinx.coroutines.launch

data class TimelineStop(
    val time: String,
    val name: String,
    val isPassed: Boolean = false,
    val isCurrent: Boolean = false,
    val tag: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusDetailsScreen(
    busId: String,
    onBackClick: () -> Unit,
    onTrackBusClick: (String) -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val busInfo = when (busId) {
        "204" -> Triple("KSRTC Rajahamsa Executive", "KA-09-F-5678", "₹200")
        "42A" -> Triple("KSRTC Karnataka Sarige", "KA-09-F-9012", "₹120")
        "305B" -> Triple("NWKRTC Sleeper Deluxe", "KA-25-F-3344", "₹350")
        "510" -> Triple("VRL Multi-Axle Volvo", "KA-01-AB-7890", "₹400")
        else -> Triple("KSRTC Airavat Club Class", "KA-09-F-1234", "₹250")
    }

    val stops = listOf(
        TimelineStop("07:30 AM", "Bengaluru (Majestic)", isPassed = true, tag = "On Time"),
        TimelineStop("08:15 AM", "Ramanagara", isPassed = true),
        TimelineStop("08:45 AM", "Mandya Bypass", isCurrent = true),
        TimelineStop("09:20 AM", "Srirangapatna", isPassed = false),
        TimelineStop("10:15 AM", "Mysuru", isPassed = false)
    )

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bus Details",
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
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Bus details copied to clipboard")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Bus Banner Photo (Matches bus_details.png)
            Image(
                painter = painterResource(id = R.drawable.bus_avatar),
                contentDescription = "Bus Exterior",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header Info
                Text(
                    text = busInfo.first,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = busInfo.second,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Route Timeline Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        stops.forEachIndexed { index, stop ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Timeline Node
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(28.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                if (stop.isPassed || stop.isCurrent) Color(0xFF3B82F6) else Color.White,
                                                CircleShape
                                            )
                                            .clip(CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Color.White, CircleShape)
                                        )
                                    }

                                    if (index < stops.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(34.dp)
                                                .background(if (stop.isPassed) Color(0xFF93C5FD) else Color(0xFFE2E8F0))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Time
                                Text(
                                    text = stop.time,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.width(75.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // Stop Name & Optional On Time Badge
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stop.name,
                                        fontSize = 14.sp,
                                        color = if (stop.isCurrent) BrandGreen else Color(0xFF334155),
                                        fontWeight = if (stop.isCurrent) FontWeight.Bold else FontWeight.Medium
                                    )

                                    if (stop.tag != null) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFDEF7EC)
                                        ) {
                                            Text(
                                                text = stop.tag,
                                                fontSize = 11.sp,
                                                color = Color(0xFF0E9F6E),
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Bottom Action Row with Total Fare & Live Track CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL FARE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = busInfo.third,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Button(
                        onClick = { onTrackBusClick(busId) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 24.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text(
                            text = "Live Track",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}