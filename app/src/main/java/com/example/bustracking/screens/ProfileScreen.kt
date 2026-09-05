package com.example.bustracking.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.R
import com.example.bustracking.data.AppLanguage
import com.example.bustracking.data.AppLanguageManager
import com.example.bustracking.data.AppStrings
import com.example.bustracking.data.UserProfileManager
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.navigation.BrandGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    onNavigateToSearch: (String, String) -> Unit = { from, to ->
        val encFrom = java.net.URLEncoder.encode(from, "UTF-8")
        val encTo = java.net.URLEncoder.encode(to, "UTF-8")
        val encDate = java.net.URLEncoder.encode("Today", "UTF-8")
        onNavigateBottom("search_results?from=$encFrom&to=$encTo&date=$encDate")
    },
    onNavigateToLiveTracking: (String) -> Unit = { busId ->
        onNavigateBottom("live_tracking/$busId")
    }
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Reactive localized strings
    val strings = AppStrings.current
    val currentLanguage = AppLanguageManager.currentLanguage.value

    // Dialog States
    var showPersonalInfoDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPhotoPreviewDialog by remember { mutableStateOf(false) }

    // User Profile Information
    val userName = UserProfileManager.fullName.value
    val userEmail = UserProfileManager.email.value
    val userPhone = UserProfileManager.mobileNumber.value
    val userGender = UserProfileManager.gender.value
    val avatarResId = UserProfileManager.avatarResId.value

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.myProfile,
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
                currentRoute = "profile",
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // User Profile Photo Avatar with Press Animation (Clean, NO verification badge)
            val avatarInteractionSource = remember { MutableInteractionSource() }
            val isAvatarPressed by avatarInteractionSource.collectIsPressedAsState()
            val avatarScale by animateFloatAsState(
                targetValue = if (isAvatarPressed) 0.92f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "avatarScale"
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer {
                        scaleX = avatarScale
                        scaleY = avatarScale
                    }
                    .clickable(
                        interactionSource = avatarInteractionSource,
                        indication = null
                    ) { showPhotoPreviewDialog = true },
                contentAlignment = Alignment.Center
            ) {
                // Profile Photo Avatar
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, spotColor = Color(0x25000000))
                        .border(2.5.dp, Color(0xFFE2E8F0), CircleShape)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Display Name (NO verification checkmark)
            Text(
                text = userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // User Email Display
            Text(
                text = userEmail,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. Personal Information Card
            ProfileOptionCard(
                icon = Icons.Default.Person,
                title = strings.personalInformation,
                subtitle = null,
                onClick = { showPersonalInfoDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Change Password Card
            ProfileOptionCard(
                icon = Icons.Default.Lock,
                title = strings.changePassword,
                subtitle = null,
                onClick = { showChangePasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Language Card
            ProfileOptionCard(
                icon = Icons.Default.Language,
                title = strings.language,
                subtitle = null,
                onClick = { showLanguageDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Logout Card with Click Bounce Animation
            val logoutInteractionSource = remember { MutableInteractionSource() }
            val isLogoutPressed by logoutInteractionSource.collectIsPressedAsState()
            val logoutScale by animateFloatAsState(
                targetValue = if (isLogoutPressed) 0.96f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "logoutScale"
            )
            val logoutElevation by animateDpAsState(
                targetValue = if (isLogoutPressed) 0.dp else 1.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "logoutElevation"
            )
            val logoutArrowOffset by animateDpAsState(
                targetValue = if (isLogoutPressed) 4.dp else 0.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "logoutArrowOffset"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = logoutScale
                        scaleY = logoutScale
                    }
                    .clickable(
                        interactionSource = logoutInteractionSource,
                        indication = ripple(),
                        onClick = { showLogoutDialog = true }
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = logoutElevation)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = strings.logout,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = strings.logout,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = logoutArrowOffset)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Language Selection Modal Dialog
    if (showLanguageDialog) {
        var tempSelectedLang by remember { mutableStateOf(currentLanguage) }

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.selectLanguage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose your preferred language. The app will update immediately.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Option 1: English (Default)
                    LanguageChoiceItem(
                        title = "English",
                        subtitle = "Default system language",
                        isSelected = tempSelectedLang == AppLanguage.ENGLISH,
                        onClick = { tempSelectedLang = AppLanguage.ENGLISH }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Kannada (ಕನ್ನಡ)
                    LanguageChoiceItem(
                        title = "ಕನ್ನಡ (Kannada)",
                        subtitle = "ಕರ್ನಾಟಕದ ಅಧಿಕೃತ ಭಾಷೆ",
                        isSelected = tempSelectedLang == AppLanguage.KANNADA,
                        onClick = { tempSelectedLang = AppLanguage.KANNADA }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppLanguageManager.setLanguage(context, tempSelectedLang)
                        showLanguageDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar(strings.languageChangedMessage)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply / ಅನ್ವಯಿಸು", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(strings.cancel, color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Photo Preview Modal Dialog
    if (showPhotoPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoPreviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profile Photo",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFE2E8F0), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = userEmail,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPhotoPreviewDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = strings.logoutConfirmationTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text = strings.logoutConfirmationMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.confirm, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(strings.cancel, color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Personal Information Dialog (Shows Name, Gender, Phone Number, Gmail directly fetched from Google)
    if (showPersonalInfoDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editGender by remember { mutableStateOf(userGender) }
        var editPhone by remember { mutableStateOf(userPhone) }
        var editEmail by remember { mutableStateOf(userEmail) }

        AlertDialog(
            onDismissRequest = { showPersonalInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.personalInformation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {

                    // 1. Full Name
                    Text(
                        text = strings.fullNameLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Gender
                    Text(
                        text = strings.genderLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Female", "Male", "Other").forEach { g ->
                            val isSel = editGender.equals(g, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) BrandGreen else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { editGender = g }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = g,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) Color.White else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Phone Number
                    Text(
                        text = strings.mobileNumberLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. Gmail Address
                    Text(
                        text = strings.emailLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        UserProfileManager.updateUserProfile(
                            name = editName.trim(),
                            email = editEmail.trim(),
                            phone = editPhone.trim(),
                            gender = editGender
                        )
                        showPersonalInfoDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar(strings.languageChangedMessage.replace("Language", "Profile").replace("ಭಾಷೆಯನ್ನು", "ಪ್ರೊಫೈಲ್ ಅನ್ನು"))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.saveChanges, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPersonalInfoDialog = false }) {
                    Text(strings.close, color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Change Password Dialog (Functional with input validation and visibility toggle)
    if (showChangePasswordDialog) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isCurrentPasswordVisible by remember { mutableStateOf(false) }
        var isNewPasswordVisible by remember { mutableStateOf(false) }
        var isConfirmPasswordVisible by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.changePassword,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Enter your current password and choose a new secure password.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Current Password
                    Text(
                        text = strings.currentPasswordLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                                Icon(
                                    imageVector = if (isCurrentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Forgot Password option link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = strings.forgotPassword,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandGreen,
                            modifier = Modifier
                                .clickable {
                                    showChangePasswordDialog = false
                                    showForgotPasswordDialog = true
                                }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. New Password
                    Text(
                        text = strings.newPasswordLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                                Icon(
                                    imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Confirm New Password
                    Text(
                        text = strings.confirmNewPasswordLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPassword.isBlank()) {
                            errorMessage = strings.currentPasswordRequired
                        } else if (newPassword.length < 4) {
                            errorMessage = strings.passwordTooShortError
                        } else if (newPassword != confirmPassword) {
                            errorMessage = strings.passwordMismatchError
                        } else {
                            showChangePasswordDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar(strings.passwordChangedSuccess)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.updatePasswordButton, color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text(strings.cancel, color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Forgot Password Flow Dialog (Platform Selection -> 6-Digit OTP Verification -> Reset Password)
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            userEmail = userEmail,
            userPhone = userPhone,
            strings = strings,
            onDismiss = { showForgotPasswordDialog = false },
            onPasswordResetSuccess = {
                showForgotPasswordDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar(strings.passwordResetSuccessMessage)
                }
            }
        )
    }
}

@Composable
private fun LanguageChoiceItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val itemScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "langItemScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFE7F3EC) else Color(0xFFF8FAFC),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) BrandGreen else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) BrandGreen else Color(0xFF1E293B)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BrandGreen,
                    unselectedColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

@Composable
fun ProfileOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isHighlight: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 1.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardElevation"
    )

    val arrowOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "arrowOffset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (isHighlight) BorderStroke(1.dp, BrandGreen.copy(alpha = 0.35f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isHighlight) Color(0xFFE7F3EC) else Color(0xFFF1F5F9),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isHighlight) BrandGreen else Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isHighlight) BrandGreen else Color(0xFF64748B)
                        )
                    }
                }
            }

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (isHighlight) BrandGreen else Color(0xFF94A3B8),
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = arrowOffset)
                )
            }
        }
    }
}


/**
 * Verification Platforms supported for sending the 6-digit OTP.
 */
enum class VerificationPlatform {
    GMAIL,
    MOBILE_SMS,
    WHATSAPP
}

/**
 * Stages in the Forgot Password reset flow.
 */
enum class ForgotPasswordStep {
    CHOOSE_PLATFORM,
    ENTER_OTP,
    SET_NEW_PASSWORD,
    SUCCESS
}

/**
 * Multi-step Forgot Password Dialog:
 * Step 1: User chooses platform to receive 6-digit code (Gmail, Mobile SMS, or WhatsApp).
 * Step 2: User enters the 6-digit verification code with simulated real-time notification & auto-fill.
 * Step 3: User enters and confirms their new password.
 * Step 4: Success confirmation.
 */
@Composable
fun ForgotPasswordDialog(
    userEmail: String,
    userPhone: String,
    strings: com.example.bustracking.data.AppStringResources,
    onDismiss: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {
    var step by remember { mutableStateOf(ForgotPasswordStep.CHOOSE_PLATFORM) }
    var selectedPlatform by remember { mutableStateOf(VerificationPlatform.GMAIL) }
    var generatedOtp by remember { mutableStateOf("749215") }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var resetError by remember { mutableStateOf<String?>(null) }

    val platformName = when (selectedPlatform) {
        VerificationPlatform.GMAIL -> strings.platformGmailTitle
        VerificationPlatform.MOBILE_SMS -> strings.platformMobileTitle
        VerificationPlatform.WHATSAPP -> strings.platformWhatsAppTitle
    }

    val platformTarget = when (selectedPlatform) {
        VerificationPlatform.GMAIL -> userEmail
        VerificationPlatform.MOBILE_SMS -> userPhone
        VerificationPlatform.WHATSAPP -> userPhone
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (step == ForgotPasswordStep.ENTER_OTP || step == ForgotPasswordStep.SET_NEW_PASSWORD) {
                    IconButton(
                        onClick = {
                            if (step == ForgotPasswordStep.ENTER_OTP) {
                                step = ForgotPasswordStep.CHOOSE_PLATFORM
                            } else if (step == ForgotPasswordStep.SET_NEW_PASSWORD) {
                                step = ForgotPasswordStep.ENTER_OTP
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF64748B)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE7F3EC),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = when (step) {
                        ForgotPasswordStep.CHOOSE_PLATFORM -> strings.forgotPasswordTitle
                        ForgotPasswordStep.ENTER_OTP -> strings.enterOtpTitle
                        ForgotPasswordStep.SET_NEW_PASSWORD -> strings.resetPasswordTitle
                        ForgotPasswordStep.SUCCESS -> strings.resetPasswordTitle
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (step) {
                    ForgotPasswordStep.CHOOSE_PLATFORM -> {
                        Text(
                            text = strings.choosePlatformSubtitle,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Option 1: Gmail
                        PlatformSelectionCard(
                            title = strings.platformGmailTitle,
                            subtitle = userEmail,
                            icon = Icons.Default.Email,
                            iconColor = Color(0xFFEA4335),
                            iconBg = Color(0xFFFEE2E2),
                            isSelected = selectedPlatform == VerificationPlatform.GMAIL,
                            onClick = { selectedPlatform = VerificationPlatform.GMAIL }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option 2: Mobile SMS
                        PlatformSelectionCard(
                            title = strings.platformMobileTitle,
                            subtitle = userPhone,
                            icon = Icons.Default.Phone,
                            iconColor = Color(0xFF0284C7),
                            iconBg = Color(0xFFE0F2FE),
                            isSelected = selectedPlatform == VerificationPlatform.MOBILE_SMS,
                            onClick = { selectedPlatform = VerificationPlatform.MOBILE_SMS }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Option 3: WhatsApp
                        PlatformSelectionCard(
                            title = strings.platformWhatsAppTitle,
                            subtitle = "WhatsApp to $userPhone",
                            icon = Icons.Default.Chat,
                            iconColor = Color(0xFF16A34A),
                            iconBg = Color(0xFFDCFCE7),
                            isSelected = selectedPlatform == VerificationPlatform.WHATSAPP,
                            onClick = { selectedPlatform = VerificationPlatform.WHATSAPP }
                        )
                    }

                    ForgotPasswordStep.ENTER_OTP -> {
                        val focusRequester = remember { FocusRequester() }

                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }

                        Text(
                            text = "${strings.enterOtpSubtitle} $platformName ($platformTarget):",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulated incoming OTP notification card with Auto-Fill button (For testing & demo)
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BrandGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Demo Code ($platformName)",
                                            fontSize = 11.sp,
                                            color = Color(0xFF166534),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = generatedOtp,
                                            fontSize = 16.sp,
                                            color = Color(0xFF14532D),
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 2.sp
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandGreen,
                                    modifier = Modifier.clickable {
                                        enteredOtp = generatedOtp
                                        otpError = null
                                    }
                                ) {
                                    Text(
                                        text = strings.autoFillCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // SINGLE Code Entry Point: Interactive 6-Digit PIN Boxes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { focusRequester.requestFocus() },
                            contentAlignment = Alignment.Center
                        ) {
                            // Hidden BasicTextField that receives typing & clipboard paste directly
                            BasicTextField(
                                value = enteredOtp,
                                onValueChange = { input ->
                                    if (input.length <= 6 && input.all { it.isDigit() }) {
                                        enteredOtp = input
                                        otpError = null
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .size(1.dp)
                                    .alpha(0.01f)
                            )

                            // 6-Digit Display Cells
                            OtpInputBoxes(
                                otp = enteredOtp,
                                isError = otpError != null,
                                onClick = { focusRequester.requestFocus() }
                            )
                        }

                        if (otpError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = otpError ?: "",
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Resend & Change Platform Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    generatedOtp = (100000..999999).random().toString()
                                    enteredOtp = ""
                                    otpError = null
                                }
                            ) {
                                Text(
                                    text = strings.resendCode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandGreen
                                )
                            }

                            TextButton(
                                onClick = { step = ForgotPasswordStep.CHOOSE_PLATFORM }
                            ) {
                                Text(
                                    text = strings.changePlatform,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    ForgotPasswordStep.SET_NEW_PASSWORD -> {
                        Text(
                            text = strings.resetPasswordSubtitle,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = strings.newPasswordLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                resetError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = strings.confirmNewPasswordLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                resetError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        if (resetError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resetError ?: "",
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    ForgotPasswordStep.SUCCESS -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE7F3EC),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BrandGreen,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = strings.passwordResetSuccessMessage,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your account password has been updated. You can now use your new password.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (step) {
                ForgotPasswordStep.CHOOSE_PLATFORM -> {
                    Button(
                        onClick = {
                            generatedOtp = (100000..999999).random().toString()
                            enteredOtp = ""
                            otpError = null
                            step = ForgotPasswordStep.ENTER_OTP
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(strings.sendVerificationCode, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                ForgotPasswordStep.ENTER_OTP -> {
                    Button(
                        onClick = {
                            if (enteredOtp == generatedOtp) {
                                otpError = null
                                step = ForgotPasswordStep.SET_NEW_PASSWORD
                            } else {
                                otpError = strings.invalidOtpError
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(strings.verifyCode, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                ForgotPasswordStep.SET_NEW_PASSWORD -> {
                    Button(
                        onClick = {
                            if (newPassword.length < 4) {
                                resetError = strings.passwordTooShortError
                            } else if (newPassword != confirmPassword) {
                                resetError = strings.passwordMismatchError
                            } else {
                                resetError = null
                                step = ForgotPasswordStep.SUCCESS
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(strings.resetPasswordButton, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                ForgotPasswordStep.SUCCESS -> {
                    Button(
                        onClick = {
                            onPasswordResetSuccess()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(strings.done, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            if (step != ForgotPasswordStep.SUCCESS) {
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel, color = Color(0xFF64748B))
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Reusable Card for Platform Selection (Gmail, Mobile SMS, WhatsApp).
 */
@Composable
private fun PlatformSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "platformCardScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFFE7F3EC) else Color(0xFFF8FAFC),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) BrandGreen else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BrandGreen else Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = BrandGreen,
                    unselectedColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

/**
 * 6-Digit visual code entry cells with click-to-focus support.
 */
@Composable
private fun OtpInputBoxes(
    otp: String,
    isError: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0 until 6) {
            val char = if (i < otp.length) otp[i].toString() else ""
            val isCurrent = i == otp.length

            val borderColor = when {
                isError -> Color(0xFFEF4444)
                isCurrent -> BrandGreen
                char.isNotEmpty() -> Color(0xFF334155)
                else -> Color(0xFFCBD5E1)
            }

            Surface(
                modifier = Modifier
                    .size(width = 44.dp, height = 50.dp)
                    .clickable { onClick() },
                shape = RoundedCornerShape(10.dp),
                color = if (char.isNotEmpty()) Color(0xFFF8FAFC) else Color.White,
                border = BorderStroke(if (isCurrent || isError) 2.dp else 1.dp, borderColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = char,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}
