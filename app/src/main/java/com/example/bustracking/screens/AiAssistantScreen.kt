package com.example.bustracking.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.bustracking.data.AppLanguage
import com.example.bustracking.data.AppLanguageManager
import com.example.bustracking.data.AppStrings
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.navigation.BrandGreen
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: String,
    val quickActionRoute: Pair<String, String>? = null // from, to for direct search navigation
)

enum class MessageSender {
    USER, AI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    onBackClick: () -> Unit,
    onNavigateToSearch: (String, String) -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val strings = AppStrings.current
    val currentLang = AppLanguageManager.currentLanguage.value
    val isKannada = currentLang == AppLanguage.KANNADA

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    var inputText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var messages by remember(currentLang) {
        mutableStateOf(
            listOf(
                ChatMessage(
                    id = "1",
                    sender = MessageSender.AI,
                    text = strings.aiGreeting,
                    timestamp = if (isKannada) "ಈಗಷ್ಟೇ" else "Just now"
                )
            )
        )
    }

    fun sendUserMessage(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = MessageSender.USER,
            text = trimmed,
            timestamp = if (isKannada) "ಈಗಷ್ಟೇ" else "Just now"
        )

        val qLower = trimmed.lowercase()
        val hasKannadaChars = trimmed.any { it in '\u0C80'..'\u0CFF' }
        val replyInKannada = isKannada || hasKannadaChars

        val (aiReply, actionRoute) = if (replyInKannada) {
            when {
                qLower.contains("101") || qLower.contains("bus 101") || qLower.contains("ಬಸ್ 101") -> {
                    Pair(
                        "• ಬಸ್ #101: ಬೆಂಗಳೂರು (ಮೆಜೆಸ್ಟಿಕ್) ➔ ಮೈಸೂರು (ಉಪನಗರ ಬಸ್ ನಿಲ್ದಾಣ).\n• ಸ್ಥಿತಿ: ಚಲನೆಯಲ್ಲಿದೆ - ಸರಿಯಾದ ಸಮಯಕ್ಕೆ (ಐರಾವತ ಕ್ಲಬ್ ಕ್ಲಾಸ್ AC).\n• ಪ್ರಸ್ತುತ ಸ್ಥಳ: ಮಂಡ್ಯ ಬೈಪಾಸ್ ಹತ್ತಿರ (ವೇಗ: 68 km/h).\n• ಅಂದಾಜು ಆಗಮನ: 35 ನಿಮಿಷಗಳಲ್ಲಿ.",
                        Pair("Bengaluru", "Mysuru")
                    )
                }
                qLower.contains("pass") || qLower.contains("ಪಾಸ್") -> {
                    Pair(
                        "• ಕರ್ನಾಟಕ ಶಕ್ತಿ ಯೋಜನೆ ಮತ್ತು ವಿದ್ಯಾರ್ಥಿ ಬಸ್ ಪಾಸ್‌ಗಳು ಎಲ್ಲಾ ಕೆಎಸ್‌ಆರ್‌ಟಿಸಿ ಬಸ್‌ಗಳಲ್ಲಿ ಮಾನ್ಯವಾಗಿವೆ.\n• ಪ್ರೊಫೈಲ್ ಪುಟದಲ್ಲಿ ನಿಮ್ಮ ಪಾಸ್ ವಿವರಗಳನ್ನು ವೀಕ್ಷಿಸಬಹುದು.",
                        null
                    )
                }
                qLower.contains("ಮೈಸೂರು") || qLower.contains("mysur") || qLower.contains("mysor") -> {
                    Pair(
                        "• ಮೆಜೆಸ್ಟಿಕ್‌ನಿಂದ ಪ್ರತಿ 15 ನಿಮಿಷಕ್ಕೆ ಬಸ್‌ಗಳು ಲಭ್ಯ.\n• ದರ: ₹120 (ಸಾರಿಗೆ) - ₹250 (ಐರಾವತ ಎಸಿ).\n• ಪ್ರಯಾಣ ಸಮಯ: ~2 ಗಂಟೆ 45 ನಿಮಿಷ.",
                        Pair("Bengaluru", "Mysuru")
                    )
                }
                qLower.contains("ಶಿವಮೊಗ್ಗ") || qLower.contains("shivamog") || qLower.contains("shimog") -> {
                    Pair(
                        "• ಮುಂದಿನ ನೇರ ಬಸ್‌ಗಳು: ಬೆಳಗ್ಗೆ 06:30, 08:00, 11:30 ಮೆಜೆಸ್ಟಿಕ್‌ನಿಂದ.\n• ದರ: ₹180 - ₹200 (ರಾಜಹಂಸ).",
                        Pair("Bengaluru", "Shivamogga")
                    )
                }
                qLower.contains("ದರ") || qLower.contains("ಬೆಲೆ") || qLower.contains("ಟಿಕೆಟ್") || qLower.contains("fare") || qLower.contains("price") -> {
                    Pair(
                        "• ಕರ್ನಾಟಕ ಸಾರಿಗೆ: ₹120 - ₹150\n• ರಾಜಹಂಸ: ₹180 - ₹220\n• ಐರಾವತ ಎಸಿ: ₹250 - ₹350\nವಿದ್ಯಾರ್ಥಿ ಮತ್ತು ಹಿರಿಯ ನಾಗರಿಕರಿಗೆ ರಿಯಾಯಿತಿ ಲಭ್ಯ.",
                        null
                    )
                }
                qLower.contains("ಲೈವ್") || qLower.contains("ಟ್ರ್ಯಾಕ್") || qLower.contains("track") || qLower.contains("live") -> {
                    Pair(
                        "• ಮುಖಪುಟದಲ್ಲಿ ನಿಮ್ಮ ಮಾರ್ಗವನ್ನು ಹುಡುಕಿ.\n• ಬಸ್ ಕಾರ್ಡ್ ಮೇಲೆ 'Track Bus' ಕ್ಲಿಕ್ ಮಾಡಿ ನೈಜ ಸಮಯದ ಜಿಪಿಎಸ್ ಲೊಕೇಶನ್ ವೀಕ್ಷಿಸಿ.",
                        null
                    )
                }
                qLower.contains("ಹುಬ್ಬಳ್ಳಿ") || qLower.contains("hubballi") || qLower.contains("hubli") -> {
                    Pair(
                        "• ಬೆಂಗಳೂರಿನಿಂದ ಹುಬ್ಬಳ್ಳಿಗೆ ಎಕ್ಸ್‌ಪ್ರೆಸ್ ಬಸ್‌ಗಳು ಲಭ್ಯ.\n• ದರ: ₹300 - ₹550. ಪ್ರಯಾಣ ಸಮಯ: ~6.5 ಗಂಟೆ.",
                        Pair("Hubballi", "Bengaluru")
                    )
                }
                qLower.contains("ಮಂಗಳೂರು") || qLower.contains("mangal") || qLower.contains("ಕುಕ್ಕೆ") || qLower.contains("ಧರ್ಮಸ್ಥಳ") -> {
                    Pair(
                        "• ಮೆಜೆಸ್ಟಿಕ್‌ನಿಂದ ರಾತ್ರಿ ಮತ್ತು ಹಗಲು ಬಸ್‌ಗಳು ಲಭ್ಯ.\n• ದರ: ₹350 - ₹650. ಪ್ರಯಾಣ ಸಮಯ: ~7 ಗಂಟೆ.",
                        Pair("Bengaluru", "Mangaluru")
                    )
                }
                else -> {
                    Pair(
                        "\"$trimmed\" ಬಗ್ಗೆ ಮಾಹಿತಿ: ನೇರ ಬಸ್ ಸಮಯ, ದರ ಮತ್ತು ಸೀಟುಗಳ ಲಭ್ಯತೆಗಾಗಿ ಮುಖಪುಟದಲ್ಲಿ ಹುಡುಕಿ.",
                        null
                    )
                }
            }
        } else {
            when {
                qLower.contains("101") || qLower.contains("bus 101") -> {
                    Pair(
                        "• Bus #101: Bengaluru (Majestic) ➔ Mysuru (Suburban Bus Stand).\n• Status: Active & On Time (Airavat Club Class AC).\n• Current Location: Near Mandya Bypass (Speed: 68 km/h).\n• Estimated Arrival: in ~35 mins.",
                        Pair("Bengaluru", "Mysuru")
                    )
                }
                qLower.contains("pass") -> {
                    Pair(
                        "• Karnataka Shakti Scheme & Student Bus Passes are active and accepted across all KSRTC buses.\n• View pass details anytime in your Profile.",
                        null
                    )
                }
                qLower.contains("mysuru") || qLower.contains("mysore") || qLower.contains("ಮೈಸೂರು") -> {
                    Pair(
                        "• Frequent buses every 15 mins from Majestic & Satellite Stand.\n• Fare: ₹120 (Sarige) - ₹250 (Airavat AC).\n• Travel time: ~2h 45m.",
                        Pair("Bengaluru", "Mysuru")
                    )
                }
                qLower.contains("shivamogga") || qLower.contains("shimoga") || qLower.contains("ಶಿವಮೊಗ್ಗ") -> {
                    Pair(
                        "• Next direct departures from Majestic: 06:30 AM, 08:00 AM, 11:30 AM.\n• Fare: ₹180 - ₹200 (Rajahamsa / Non-AC).",
                        Pair("Bengaluru", "Shivamogga")
                    )
                }
                qLower.contains("fare") || qLower.contains("price") || qLower.contains("ticket") || qLower.contains("cost") || qLower.contains("ದರ") -> {
                    Pair(
                        "• Karnataka Sarige: ₹120 - ₹150\n• Rajahamsa: ₹180 - ₹220\n• Airavat AC: ₹250 - ₹350\nConcessions available for students & senior citizens.",
                        null
                    )
                }
                qLower.contains("track") || qLower.contains("live") || qLower.contains("gps") || qLower.contains("location") || qLower.contains("ಲೈವ್") -> {
                    Pair(
                        "• Search your route on Home screen.\n• Tap any bus card and click 'Track Bus' for real-time map GPS.",
                        null
                    )
                }
                qLower.contains("hubballi") || qLower.contains("hubli") || qLower.contains("ಹುಬ್ಬಳ್ಳಿ") -> {
                    Pair(
                        "• Frequent express NWKRTC & KSRTC buses from Bengaluru.\n• Fare: ₹300 - ₹550. Travel time: ~6h 30m.",
                        Pair("Hubballi", "Bengaluru")
                    )
                }
                qLower.contains("mangaluru") || qLower.contains("mangalore") || qLower.contains("dharmasthala") || qLower.contains("kukke") -> {
                    Pair(
                        "• Daily express and sleeper services from Bengaluru.\n• Fare: ₹350 - ₹650. Travel time: ~7h.",
                        Pair("Bengaluru", "Mangaluru")
                    )
                }
                else -> {
                    Pair(
                        "For \"$trimmed\": Please search origin & destination on Home screen for instant schedules and seat availability.",
                        null
                    )
                }
            }
        }

        val aiMsg = ChatMessage(
            id = (System.currentTimeMillis() + 1).toString(),
            sender = MessageSender.AI,
            text = aiReply,
            timestamp = if (isKannada) "ಈಗಷ್ಟೇ" else "Just now",
            quickActionRoute = actionRoute
        )

        messages = messages + userMsg + aiMsg
        inputText = ""

        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var showVoiceDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showVoiceDialog = true
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isKannada) "ಧ್ವನಿ ಇನ್‌ಪುಟ್‌ಗಾಗಿ ಮೈಕ್ರೊಫೋನ್ ಅನುಮತಿ ಅಗತ್ಯವಿದೆ" else "Microphone permission is required for voice input"
                )
            }
        }
    }

    fun startVoiceInput() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            showVoiceDialog = true
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDEF7EC),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = "AI",
                                    tint = BrandGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = strings.aiTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
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
                currentRoute = "ai",
                onNavigate = onNavigateBottom
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    if (msg.sender == MessageSender.USER) {
                        // User Bubble
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp),
                                color = BrandGreen,
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth(0.82f)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    } else {
                        // AI Bubble
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDEF7EC),
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(top = 2.dp)
                                ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.SmartToy,
                                        contentDescription = null,
                                        tint = BrandGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                                Surface(
                                    shape = RoundedCornerShape(2.dp, 16.dp, 16.dp, 16.dp),
                                    color = Color.White,
                                    shadowElevation = 1.dp
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = msg.text,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1E293B),
                                            lineHeight = 20.sp
                                        )

                                        // Optional interactive route search action
                                        if (msg.quickActionRoute != null) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Card(
                                                onClick = {
                                                    onNavigateToSearch(
                                                        msg.quickActionRoute.first,
                                                        msg.quickActionRoute.second
                                                    )
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.DirectionsBus,
                                                        contentDescription = null,
                                                        tint = BrandGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = if (isKannada) "ಬಸ್‌ಗಳನ್ನು ವೀಕ್ಷಿಸಿ: ${msg.quickActionRoute.first} ➔ ${msg.quickActionRoute.second}" else "View Buses: ${msg.quickActionRoute.first} ➔ ${msg.quickActionRoute.second}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BrandGreen
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input Row
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = strings.aiInputPlaceholder,
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { startVoiceInput() }) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Voice Input",
                                    tint = BrandGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC),
                            focusedBorderColor = BrandGreen,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSend = {
                            if (inputText.trim().isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(strings.aiInputNeeded)
                                }
                            } else {
                                sendUserMessage(inputText)
                            }
                        }),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = if (inputText.isNotBlank()) BrandGreen else Color(0xFF94A3B8),
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                if (inputText.trim().isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(strings.aiInputNeeded)
                                    }
                                } else {
                                    sendUserMessage(inputText)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showVoiceDialog) {
        VoiceCommandDialog(
            isKannada = isKannada,
            onDismiss = { showVoiceDialog = false },
            onCommandRecognized = { recognizedText ->
                showVoiceDialog = false
                inputText = recognizedText
                sendUserMessage(recognizedText)
            }
        )
    }
}

/**
 * Custom In-App Voice Command Assistant Modal Dialog.
 * Replaces the third-party Google speech popup with a sleek, interactive,
 * and reliable brand-aligned voice interface.
 */
@Composable
fun VoiceCommandDialog(
    isKannada: Boolean,
    onDismiss: () -> Unit,
    onCommandRecognized: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedLang by remember { mutableStateOf(if (isKannada) "kn-IN" else "en-IN") }
    val isKannadaSelected = selectedLang == "kn-IN"

    var isListening by remember { mutableStateOf(false) }
    var listeningStatus by remember {
        mutableStateOf(
            if (isKannadaSelected) "ಆಲಿಸಲಾಗುತ್ತಿದೆ... ಮಾತನಾಡಿ" else "Listening... speak now"
        )
    }
    var transcribedText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "voicePulse")

    // Pulsing Ripple Animation for Mic
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )

    // Equalizer waveform bars
    val bar1 by infiniteTransition.animateFloat(initialValue = 10f, targetValue = 28f, animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "b1")
    val bar2 by infiniteTransition.animateFloat(initialValue = 18f, targetValue = 44f, animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse), label = "b2")
    val bar3 by infiniteTransition.animateFloat(initialValue = 12f, targetValue = 36f, animationSpec = infiniteRepeatable(tween(360, easing = LinearEasing), RepeatMode.Reverse), label = "b3")
    val bar4 by infiniteTransition.animateFloat(initialValue = 22f, targetValue = 48f, animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse), label = "b4")
    val bar5 by infiniteTransition.animateFloat(initialValue = 12f, targetValue = 30f, animationSpec = infiniteRepeatable(tween(460, easing = LinearEasing), RepeatMode.Reverse), label = "b5")

    fun startListening(lang: String) {
        hasError = false
        errorMessage = ""
        transcribedText = ""
        listeningStatus = if (lang == "kn-IN") "ಆಲಿಸಲಾಗುತ್ತಿದೆ... ಮಾತನಾಡಿ" else "Listening... speak now"

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            hasError = true
            errorMessage = if (lang == "kn-IN") "ಧ್ವನಿ ಗುರುತಿಸುವಿಕೆ ಲಭ್ಯವಿಲ್ಲ. ಕೆಳಗಿನ ಪ್ರಶ್ನೆಗಳನ್ನು ಕ್ಲಿಕ್ ಮಾಡಿ." else "Speech recognizer unavailable. Tap any command below."
            isListening = false
            return
        }

        try {
            speechRecognizer?.destroy()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                    listeningStatus = if (lang == "kn-IN") "ಆಲಿಸಲಾಗುತ್ತಿದೆ... ಮಾತನಾಡಿ" else "Listening... speak your question"
                }

                override fun onBeginningOfSpeech() {
                    listeningStatus = if (lang == "kn-IN") "ಧ್ವನಿ ಪತ್ತೆಯಾಗಿದೆ..." else "Hearing your voice..."
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    listeningStatus = if (lang == "kn-IN") "ಸಂಸ್ಕರಿಸಲಾಗುತ್ತಿದೆ..." else "Processing audio..."
                }

                override fun onError(error: Int) {
                    isListening = false
                    hasError = true
                    errorMessage = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> if (lang == "kn-IN") "ಧ್ವನಿ ಕೇಳಿಸಲಿಲ್ಲ. ಮತ್ತೊಮ್ಮೆ ಮಾತನಾಡಿ ಅಥವಾ ಕೆಳಗಿನ ಆಯ್ಕೆ ಕ್ಲಿಕ್ ಮಾಡಿ." else "Didn't catch that. Tap mic to retry or pick a quick command."
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> if (lang == "kn-IN") "ಸಮಯ ಮೀರಿದೆ. ಮೈಕ್ ಟ್ಯಾಪ್ ಮಾಡಿ." else "Speech timed out. Tap mic to retry."
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> if (lang == "kn-IN") "ಮೈಕ್ರೊಫೋನ್ ಅನುಮತಿ ನೀಡಿ." else "Microphone permission needed."
                        SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> if (lang == "kn-IN") "ನೆಟ್‌ವರ್ಕ್ ಸಂಪರ್ಕ ದೋಷ." else "Network issue. Please check connection."
                        else -> if (lang == "kn-IN") "ಧ್ವನಿ ಗುರುತಿಸಲು ಸಾಧ್ಯವಾಗಲಿಲ್ಲ. ಕೆಳಗಿನ ಆಯ್ಕೆಗಳನ್ನು ಕ್ಲಿಕ್ ಮಾಡಿ." else "Could not capture audio. Tap any command below."
                    }
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull()
                    if (!spoken.isNullOrBlank()) {
                        transcribedText = spoken
                        onCommandRecognized(spoken)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partialMatches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partial = partialMatches?.firstOrNull()
                    if (!partial.isNullOrBlank()) {
                        transcribedText = partial
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            recognizer.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            isListening = false
            hasError = true
            errorMessage = if (lang == "kn-IN") "ಧ್ವನಿ ಪ್ರಾರಂಭಿಸಲು ಸಾಧ್ಯವಾಗಲಿಲ್ಲ" else "Could not start voice recognizer"
        }
    }

    LaunchedEffect(selectedLang) {
        startListening(selectedLang)
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
    }

    val quickVoiceSuggestions = remember(selectedLang) {
        if (selectedLang == "kn-IN") {
            listOf(
                "🚌 ಬಸ್ 101 ಎಲ್ಲಿದೆ?",
                "📍 ಮೈಸೂರಿಗೆ ಮುಂದಿನ ಬಸ್",
                "⏱️ ಶಿವಮೊಗ್ಗ ವೇಳಾಪಟ್ಟಿ",
                "💰 ಕೆಎಸ್‌ಆರ್‌ಟಿಸಿ ಟಿಕೆಟ್ ದರ",
                "🎫 ಬಸ್ ಪಾಸ್ ವಿವರ"
            )
        } else {
            listOf(
                "🚌 Where is Bus 101?",
                "📍 Next bus to Mysuru",
                "⏱️ Shivamogga schedule",
                "💰 KSRTC ticket fares",
                "🎫 Active bus passes"
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Sparkle Icon and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDEF7EC),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isKannadaSelected) "ಧ್ವನಿ ಸಹಾಯಕ" else "Voice Assistant",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = if (isKannadaSelected) "ಕನ್ನಡ ಮತ್ತು ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಮಾತನಾಡಿ" else "Speak in English or Kannada",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Selector Pills
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // English
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedLang == "en-IN") BrandGreen else Color.Transparent,
                        modifier = Modifier.clickable {
                            if (selectedLang != "en-IN") selectedLang = "en-IN"
                        }
                    ) {
                        Text(
                            text = "English (India)",
                            fontSize = 12.sp,
                            fontWeight = if (selectedLang == "en-IN") FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedLang == "en-IN") Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Kannada
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedLang == "kn-IN") BrandGreen else Color.Transparent,
                        modifier = Modifier.clickable {
                            if (selectedLang != "kn-IN") selectedLang = "kn-IN"
                        }
                    ) {
                        Text(
                            text = "ಕನ್ನಡ (Kannada)",
                            fontSize = 12.sp,
                            fontWeight = if (selectedLang == "kn-IN") FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedLang == "kn-IN") Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pulsing Mic with Concentric Rings
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(120.dp)
                ) {
                    if (isListening) {
                        // Outer Ripple Ring
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(rippleScale)
                                .alpha(rippleAlpha)
                                .background(BrandGreen.copy(alpha = 0.35f), CircleShape)
                        )
                        // Middle Soft Ring
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(Color(0xFFD1FAE5), CircleShape)
                        )
                    }

                    // Center Mic Button
                    Surface(
                        shape = CircleShape,
                        color = if (isListening) BrandGreen else Color(0xFF64748B),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .size(68.dp)
                            .clickable {
                                if (isListening) {
                                    try { speechRecognizer?.stopListening() } catch (_: Exception) {}
                                    isListening = false
                                } else {
                                    startListening(selectedLang)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isListening) Icons.Filled.Mic else Icons.Filled.MicOff,
                                contentDescription = "Toggle Mic",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sound Wave Equalizer Bars
                if (isListening) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(26.dp)
                    ) {
                        listOf(bar1, bar2, bar3, bar4, bar5).forEach { barH ->
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(barH.dp)
                                    .background(BrandGreen, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status Text
                Text(
                    text = if (hasError) errorMessage else listeningStatus,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (hasError) Color(0xFFDC2626) else if (isListening) BrandGreen else Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Transcribed Text Display (if user spoke or partially transcribed)
                if (transcribedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "“$transcribedText”",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onCommandRecognized(transcribedText) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isKannadaSelected) "ಕಳುಹಿಸಿ" else "Ask AI",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick Voice Commands Section
                Text(
                    text = if (isKannadaSelected) "ಅಥವಾ ಧ್ವನಿ ಆಜ್ಞೆಗಳನ್ನು ಟ್ಯಾಪ್ ಮಾಡಿ:" else "Or tap a quick voice command:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickVoiceSuggestions.forEach { suggestion ->
                        val cleanQuery = suggestion.substringAfter(" ").trim()
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCommandRecognized(cleanQuery)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E293B)
                                )
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = BrandGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Emulator test / fallback demo button
                TextButton(
                    onClick = {
                        val demoCmd = if (isKannadaSelected) "ಬಸ್ 101 ಎಲ್ಲಿದೆ?" else "Where is bus 101?"
                        onCommandRecognized(demoCmd)
                    }
                ) {
                    Text(
                        text = if (isKannadaSelected) "🎧 ಡೆಮೊ ಧ್ವನಿ ಪರೀಕ್ಷಿಸಿ (Bus 101)" else "🎧 Test Demo Voice Query (Bus 101)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandGreen
                    )
                }
            }
        }
    }
}
