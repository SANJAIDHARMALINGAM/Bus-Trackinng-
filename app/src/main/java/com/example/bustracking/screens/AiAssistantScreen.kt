package com.example.bustracking.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val suggestionChips = remember(currentLang) {
        listOf(
            strings.aiChipMysuru,
            strings.aiChipShivamogga,
            strings.aiChipFares,
            strings.aiChipLiveTrack
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

    // Voice recognition launcher for speech input
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenMatches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = spokenText
                sendUserMessage(spokenText)
            }
        }
    }

    fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            val primaryLang = if (isKannada) "kn-IN" else "en-IN"
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, primaryLang)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, primaryLang)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, strings.aiSpeakPrompt)
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isKannada) "ಧ್ವನಿ ಇನ್‌ಪುಟ್ ಲಭ್ಯವಿಲ್ಲ" else "Voice input is not supported on this device"
                )
            }
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
                        Column {
                            Text(
                                text = strings.aiTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1E293B)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.aiSubtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
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
            // Suggestion chips at top
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestionChips) { chip ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable {
                            val clean = if (chip.contains(" ")) chip.substringAfter(" ").trim() else chip.trim()
                            sendUserMessage(clean)
                        }
                    ) {
                        Text(
                            text = chip,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

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
}
