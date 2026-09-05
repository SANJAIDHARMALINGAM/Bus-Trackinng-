package com.example.bustracking.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.painterResource
import com.example.bustracking.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.data.AppLanguage
import com.example.bustracking.data.AppLanguageManager
import com.example.bustracking.data.AppStrings
import com.example.bustracking.navigation.AppBottomNavigationBar
import kotlinx.coroutines.launch

private val ComplaintGreen = Color(0xFF2E7D32)
private val SosRed = Color(0xFFD32F2F)

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val context = LocalContext.current
    val strings = AppStrings.current
    val currentLang = AppLanguageManager.currentLanguage.value
    val isKannada = currentLang == AppLanguage.KANNADA

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showComplaintDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }

    // Expanded states for FAQs
    var expandedFaqIndices by remember { mutableStateOf(setOf<Int>()) }

    val faqs = remember(isKannada) {
        if (isKannada) {
            listOf(
                FaqItem(
                    question = "ನೈಜ ಸಮಯದಲ್ಲಿ ಬಸ್ ಅನ್ನು ಟ್ರ್ಯಾಕ್ ಮಾಡುವುದು ಹೇಗೆ?",
                    answer = "ನಿಮ್ಮ ಬಸ್ ಅನ್ನು ಲೈವ್ ಆಗಿ ಟ್ರ್ಯಾಕ್ ಮಾಡಲು, ಮುಖಪುಟದಲ್ಲಿ ಪ್ರಾರಂಭ ಮತ್ತು ತಲುಪುವ ಸ್ಥಳವನ್ನು ಹುಡುಕಿ. ಲಭ್ಯವಿರುವ ಬಸ್‌ಗಳ ಪಟ್ಟಿಯಲ್ಲಿ ಬಸ್ ಕಾರ್ಡ್ ಆಯ್ಕೆಮಾಡಿ 'Track Bus' ಕ್ಲಿಕ್ ಮಾಡಿ. ನಕ್ಷೆಯಲ್ಲಿ ಬಸ್‌ನ ನಿಖರ ಜಿಪಿಎಸ್ ಲೊಕೇಶನ್ ಮತ್ತು ತಲುಪುವ ಸಮಯ ಕಾಣಿಸುತ್ತದೆ."
                ),
                FaqItem(
                    question = "ಟಿಕೆಟ್ ರದ್ದುಮಾಡುವುದು ಮತ್ತು ಮರುಪಾವತಿ ಪಡೆಯುವುದು ಹೇಗೆ?",
                    answer = "ನಿಮ್ಮ ಪ್ರೊಫೈಲ್ / ಖಾತೆಯ ಬುಕಿಂಗ್ ವಿಭಾಗದಲ್ಲಿ ಬಸ್ ಹೊರಡುವ 2 ಗಂಟೆಗಳ ಮೊದಲು ಟಿಕೆಟ್ ರದ್ದುಗೊಳಿಸಬಹುದು. ರದ್ದತಿ ಅನುಮೋದನೆಯಾದ ನಂತರ 3 ರಿಂದ 5 ವ್ಯವಹಾರ ದಿನಗಳಲ್ಲಿ ಹಣ ನಿಮ್ಮ ಮೂಲ ಪಾವತಿ ಖಾತೆಗೆ ಮರುಪಾವತಿಯಾಗುತ್ತದೆ."
                ),
                FaqItem(
                    question = "ಯಾವ ಪಾವತಿ ವಿಧಾನಗಳು ಲಭ್ಯವಿವೆ?",
                    answer = "ಯುಪಿಐ (Google Pay, PhonePe, Paytm), ನೆಟ್ ಬ್ಯಾಂಕಿಂಗ್, ಡೆಬಿಟ್/ಕ್ರೆಡಿಟ್ ಕಾರ್ಡ್‌ಗಳು ಮತ್ತು ಜನಪ್ರಿಯ ವ್ಯಾಲೆಟ್‌ಗಳ ಮೂಲಕ ಸುಲಭವಾಗಿ ಪಾವತಿ ಮಾಡಬಹುದು."
                ),
                FaqItem(
                    question = "ವಿದ್ಯಾರ್ಥಿಗಳು ಮತ್ತು ಹಿರಿಯ ನಾಗರಿಕರಿಗೆ ರಿಯಾಯಿತಿ ಇದೆಯೇ?",
                    answer = "ಹೌದು, ಕರ್ನಾಟಕ ಸರ್ಕಾರದ ಮಾನ್ಯತೆ ಪಡೆದ ವಿದ್ಯಾರ್ಥಿ ಪಾಸ್ ಹೊಂದಿರುವವರು ಮತ್ತು 60 ವರ್ಷ ಮೇಲ್ಪಟ್ಟ ಹಿರಿಯ ನಾಗರಿಕರು ಕರ್ನಾಟಕ ಸಾರಿಗೆ ಮತ್ತು ರಾಜಹಂಸ ಬಸ್‌ಗಳಲ್ಲಿ ರಿಯಾಯಿತಿ ಸೌಲಭ್ಯ ಪಡೆಯಬಹುದು."
                ),
                FaqItem(
                    question = "ಸಾಮಾನು ಕಳೆದುಹೋದರೆ ಯಾರನ್ನು ಸಂಪರ್ಕಿಸಬೇಕು?",
                    answer = "ಪ್ರಯಾಣದ ವೇಳೆ ಸಾಮಾನು ಕಳೆದುಹೋದರೆ ತಕ್ಷಣ ನಮ್ಮ ಉಚಿತ ಗ್ರಾಹಕ ಸೇವಾ ಸಂಖ್ಯೆ 1800-1234-5678 ಗೆ ಕರೆ ಮಾಡಿ ಅಥವಾ 'ದೂರು ದಾಖಲಿಸಿ' ಬಟನ್ ಕ್ಲಿಕ್ ಮಾಡಿ ಬಸ್ ನೋಂದಣಿ ಸಂಖ್ಯೆ ಮತ್ತು ಸಾಮಾನು ವಿವರಗಳನ್ನು ಸಲ್ಲಿಸಿ."
                )
            )
        } else {
            listOf(
                FaqItem(
                    question = "How do I track a bus in real-time?",
                    answer = "To track your bus live, search your route from the Home screen. From the Available Buses list, select any bus card and tap 'Track Bus' to view live GPS position, current speed, and countdown on the interactive map."
                ),
                FaqItem(
                    question = "How do I cancel a ticket and get a refund?",
                    answer = "You can cancel booked tickets up to 2 hours before departure from your Profile/Account bookings section. Refunds are automatically processed to the original payment source within 3-5 business days."
                ),
                FaqItem(
                    question = "What payment methods are accepted?",
                    answer = "We support all major payment modes including UPI (Google Pay, PhonePe, Paytm), Net Banking, Credit/Debit Cards, and popular digital wallets."
                ),
                FaqItem(
                    question = "Are there concessions for students and senior citizens?",
                    answer = "Yes! Valid Karnataka student pass holders and senior citizens (age 60+) receive applicable concessional fares on Karnataka Sarige and Rajahamsa services upon presenting valid ID."
                ),
                FaqItem(
                    question = "What should I do if I lose luggage on the bus?",
                    answer = "Immediately call our 24/7 toll-free Customer Care at 1800-1234-5678 or tap the 'Raise a Complaint' button to register your bus number, route, and item details with our lost & found team."
                )
            )
        }
    }

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.helpSupport,
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
                currentRoute = "help",
                onNavigate = onNavigateBottom
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Screen Header matching XML: android:text="Help & Support" android:textSize="22sp"
            Text(
                text = strings.helpSupport,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Top Action Buttons (Raise a Complaint & SOS / Emergency)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Raise a Complaint Button (complaint_green: #2E7D32, cornerRadius 16dp)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showComplaintDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ComplaintGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_complaint),
                            contentDescription = "Raise a Complaint",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isKannada) "ದೂರು\nದಾಖಲಿಸಿ" else "Raise a\nComplaint",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 19.sp
                        )
                    }
                }

                // SOS / Emergency Button (sos_red: #D32F2F, cornerRadius 16dp)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showSosDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SosRed),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sos),
                            contentDescription = "SOS / Emergency",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isKannada) "ತುರ್ತು /\nಎಸ್‌ಒಎಸ್" else "SOS /\nEmergency",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Frequently Asked Questions Section Header
            Text(
                text = if (isKannada) "ಪದೇ ಪದೇ ಕೇಳಲಾಗುವ ಪ್ರಶ್ನೆಗಳು" else "Frequently Asked Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // FAQ Container with Expandable Card Items (Matching item_faq.xml)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                faqs.forEachIndexed { index, faq ->
                    val isExpanded = expandedFaqIndices.contains(index)
                    val arrowRotation by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        label = "faqArrowRotation"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // FAQ Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedFaqIndices = if (isExpanded) {
                                            expandedFaqIndices - index
                                        } else {
                                            expandedFaqIndices + index
                                        }
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.question,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .rotate(arrowRotation)
                                )
                            }

                            // FAQ Answer (Expandable, matching tvAnswer)
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Text(
                                    text = faq.answer,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Contact Us Section Header
            Text(
                text = if (isKannada) "ನಮ್ಮನ್ನು ಸಂಪರ್ಕಿಸಿ" else "Contact Us",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Contact Card 1: Customer Care (1800-1234-5678)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable {
                        try {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:180012345678"))
                            context.startActivity(dialIntent)
                        } catch (e: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Calling 1800-1234-5678")
                            }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Phone Support",
                        tint = ComplaintGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isKannada) "ಗ್ರಾಹಕ ಸೇವೆ" else "Customer Care",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1800-1234-5678",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            // Contact Card 2: Email Support (support@karnatakabus.in)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@karnatakabus.in")
                                putExtra(Intent.EXTRA_SUBJECT, "Karnataka Bus Support Inquiry")
                            }
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Emailing support@karnatakabus.in")
                            }
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Support",
                        tint = ComplaintGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isKannada) "ಇಮೇಲ್ ಬೆಂಬಲ" else "Email Support",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "support@karnatakabus.in",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }

    // Interactive Raise a Complaint Dialog
    if (showComplaintDialog) {
        var category by remember { mutableStateOf("Bus Delay") }
        var busNumber by remember { mutableStateOf("") }
        var complaintText by remember { mutableStateOf("") }

        val categories = listOf("Bus Delay", "Cleanliness / AC", "Staff Behavior", "Ticket Issue", "Other")

        AlertDialog(
            onDismissRequest = { showComplaintDialog = false },
            title = {
                Text(
                    text = if (isKannada) "ದೂರು ದಾಖಲಿಸಿ" else "Raise a Complaint",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isKannada) "ದೂರಿನ ಪ್ರಕಾರವನ್ನು ಆಯ್ಕೆಮಾಡಿ:" else "Select Issue Category:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ComplaintGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = busNumber,
                        onValueChange = { busNumber = it },
                        label = { Text(if (isKannada) "ಬಸ್ ಸಂಖ್ಯೆ / ಮಾರ್ಗ (ಐಚ್ಛಿಕ)" else "Bus Number / Route (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = complaintText,
                        onValueChange = { complaintText = it },
                        label = { Text(if (isKannada) "ದೂರಿನ ವಿವರಗಳು" else "Complaint Description") },
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showComplaintDialog = false
                        val complaintId = "KB-${(1000..9999).random()}"
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isKannada) "ದೂರು #$complaintId ಯಶಸ್ವಿಯಾಗಿ ದಾಖಲಾಗಿದೆ! 24 ಗಂಟೆಗಳಲ್ಲಿ ಪರಿಹರಿಸಲಾಗುವುದು."
                                else "Complaint #$complaintId submitted! Our team will resolve it within 24 hours."
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComplaintGreen)
                ) {
                    Text(if (isKannada) "ಸಲ್ಲಿಸಿ" else "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showComplaintDialog = false }) {
                    Text(if (isKannada) "ರದ್ದುಮಾಡಿ" else "Cancel")
                }
            }
        )
    }

    // Interactive SOS / Emergency Dialog
    if (showSosDialog) {
        AlertDialog(
            onDismissRequest = { showSosDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SosRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isKannada) "🚨 ತುರ್ತು ಸಹಾಯ (SOS)" else "🚨 Emergency Assistance (SOS)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isKannada) "ತುರ್ತು ಸಂದರ್ಭದಲ್ಲಿ ಕೆಳಗಿನ ಸಹಾಯವಾಣಿಗಳಿಗೆ ನೇರವಾಗಿ ಸಂಪರ್ಕಿಸಿ:"
                        else "For immediate emergency support during transit, tap to call:",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showSosDialog = false
                            try {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                            } catch (e: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SosRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LocalPolice, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isKannada) "ಪೊಲೀಸ್ ತುರ್ತು ಕರೆ (112)" else "Call Police Helpline (112)", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            showSosDialog = false
                            try {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108")))
                            } catch (e: Exception) {}
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isKannada) "ಆಂಬ್ಯುಲೆನ್ಸ್ (108)" else "Call Ambulance (108)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            showSosDialog = false
                            try {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:1091")))
                            } catch (e: Exception) {}
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isKannada) "ಮಹಿಳಾ ಸಹಾಯವಾಣಿ (1091)" else "Women Safety Helpline (1091)")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSosDialog = false }) {
                    Text(if (isKannada) "ಮುಚ್ಚಿ" else "Close")
                }
            }
        )
    }
}
