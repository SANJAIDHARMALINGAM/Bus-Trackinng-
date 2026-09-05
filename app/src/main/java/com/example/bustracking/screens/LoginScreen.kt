package com.example.bustracking.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bustracking.R
import com.example.bustracking.data.AuthResult
import com.example.bustracking.data.BackendManager
import com.example.bustracking.data.EmailValidator
import com.example.bustracking.data.UserProfileManager
import kotlinx.coroutines.launch

// Brand Palette
private val BrandForestGreen = Color(0xFF136B3B)
private val LightUnderlineGray = Color(0xFFE5E7EB)
private val TextGrayLabel = Color(0xFF374151)
private val PlaceholderGray = Color(0xFF9CA3AF)
private val BorderGray = Color(0xFFD1D5DB)
private val OrContinueGray = Color(0xFF6B7280)
private val PolicyLinkGreen = Color(0xFF136B3B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    // 0 = Sign In, 1 = Sign Up
    var selectedTab by remember { mutableIntStateOf(0) }

    // Sign In Fields
    var signInEmail by remember { mutableStateOf("") }
    var signInPassword by remember { mutableStateOf("") }
    var isSignInPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up Fields (Full Name, Gender, Mobile Number, Email Address, Password, Confirm Password)
    var signUpFullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Female") }
    var signUpMobile by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }
    var isSignUpPasswordVisible by remember { mutableStateOf(false) }
    var isSignUpConfirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showBackendConfigDialog by remember { mutableStateOf(false) }
    var showGoogleAccountChooser by remember { mutableStateOf(false) }
    var backendBaseUrlInput by remember { mutableStateOf(BackendManager.BASE_URL) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Action handler for Sign In
    val executeSignIn = {
        val email = signInEmail.trim()
        if (email.isBlank()) {
            scope.launch { snackbarHostState.showSnackbar("Please enter your Gmail address") }
        } else if (!EmailValidator.isValidGmail(email)) {
            scope.launch { snackbarHostState.showSnackbar("Only verified Gmail addresses (@gmail.com) are allowed.") }
        } else if (signInPassword.length < 4) {
            scope.launch { snackbarHostState.showSnackbar("Password must be at least 4 characters") }
        } else {
            isLoading = true
            scope.launch {
                val result = BackendManager.authService.signInWithEmail(email, signInPassword)
                isLoading = false
                when (result) {
                    is AuthResult.Success -> {
                        val computedName = email.substringBefore("@").replace(".", " ")
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        UserProfileManager.updateUserProfile(
                            name = computedName,
                            email = email,
                            phone = "9876543210"
                        )
                        onLoginSuccess()
                    }
                    is AuthResult.Error -> snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    // Action handler for Sign Up
    val executeSignUp = {
        val email = signUpEmail.trim()
        if (signUpFullName.trim().isBlank()) {
            scope.launch { snackbarHostState.showSnackbar("Please enter your Full Name") }
        } else if (signUpMobile.trim().length < 10) {
            scope.launch { snackbarHostState.showSnackbar("Please enter a valid 10-digit mobile number") }
        } else if (email.isBlank()) {
            scope.launch { snackbarHostState.showSnackbar("Please enter your Gmail address") }
        } else if (!EmailValidator.isValidGmail(email)) {
            scope.launch { snackbarHostState.showSnackbar("Only verified Gmail addresses (@gmail.com) are accepted.") }
        } else if (signUpPassword.length < 4) {
            scope.launch { snackbarHostState.showSnackbar("Password must be at least 4 characters") }
        } else if (signUpPassword != signUpConfirmPassword) {
            scope.launch { snackbarHostState.showSnackbar("Passwords do not match!") }
        } else {
            isLoading = true
            scope.launch {
                val result = BackendManager.authService.signUpWithDetails(
                    fullName = signUpFullName,
                    gender = selectedGender,
                    mobileNumber = signUpMobile,
                    email = email,
                    password = signUpPassword
                )
                isLoading = false
                when (result) {
                    is AuthResult.Success -> {
                        UserProfileManager.updateUserProfile(
                            name = signUpFullName.trim(),
                            email = email,
                            phone = signUpMobile.trim()
                        )
                        snackbarHostState.showSnackbar("Account registered successfully!")
                        onLoginSuccess()
                    }
                    is AuthResult.Error -> snackbarHostState.showSnackbar(result.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { com.example.bustracking.components.AppSnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (selectedTab == 1) {
                            selectedTab = 0
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Already on Welcome/Login screen") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1F2937),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = if (selectedTab == 0) "Sign In" else "Sign Up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                IconButton(onClick = { showBackendConfigDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Backend Settings",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Circular Bus Avatar with Shadow
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.25f))
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bus_avatar),
                    contentDescription = "KSRTC Bus Avatar",
                    modifier = Modifier
                        .size(122.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign In / Sign Up Tabs
            Row(modifier = Modifier.fillMaxWidth()) {
                // Sign In Tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedTab = 0 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == 0) BrandForestGreen else TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (selectedTab == 0) 3.dp else 1.dp)
                            .background(if (selectedTab == 0) BrandForestGreen else LightUnderlineGray)
                    )
                }

                // Sign Up Tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { selectedTab = 1 },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == 1) BrandForestGreen else TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (selectedTab == 1) 3.dp else 1.dp)
                            .background(if (selectedTab == 1) BrandForestGreen else LightUnderlineGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // TAB 0: SIGN IN FORM
            if (selectedTab == 0) {
                // Email Field (Gmail verified only)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Gmail Address",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signInEmail,
                        onValueChange = { signInEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter verified gmail (e.g. user@gmail.com)",
                                color = PlaceholderGray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signInPassword,
                        onValueChange = { signInPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Enter password",
                                color = PlaceholderGray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (isSignInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isSignInPasswordVisible = !isSignInPasswordVisible }) {
                                Icon(
                                    imageVector = if (isSignInPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isSignInPasswordVisible) "Hide password" else "Show password",
                                    tint = PlaceholderGray
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                executeSignIn()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                // Forgot Password
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = BrandForestGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            val email = signInEmail.trim()
                            if (EmailValidator.isValidGmail(email)) {
                                scope.launch {
                                    BackendManager.authService.resetPassword(email)
                                    snackbarHostState.showSnackbar("Password reset link sent to $email")
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Enter a valid verified Gmail address first to reset password")
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sign In Button
                Button(
                    onClick = { executeSignIn() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = BrandForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandForestGreen)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // TAB 1: SIGN UP FORM (As per Image 3 specifications)
            if (selectedTab == 1) {
                // 1. Full Name
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Full Name *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signUpFullName,
                        onValueChange = { signUpFullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter full name", color = PlaceholderGray, fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Select Gender (Male, Female, Other)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select Gender *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Male", "Female", "Other").forEach { gender ->
                            val isSelected = selectedGender == gender
                            Button(
                                onClick = { selectedGender = gender },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) BrandForestGreen else Color.White,
                                    contentColor = if (isSelected) Color.White else TextGrayLabel
                                ),
                                border = if (!isSelected) BorderStroke(1.dp, BorderGray) else null
                            ) {
                                Text(
                                    text = gender,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Mobile Number
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mobile Number *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signUpMobile,
                        onValueChange = { signUpMobile = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter 10-digit mobile number", color = PlaceholderGray, fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Email Address
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email Address *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signUpEmail,
                        onValueChange = { signUpEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your @gmail.com address", color = PlaceholderGray, fontSize = 14.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Password *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signUpPassword,
                        onValueChange = { signUpPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Create password", color = PlaceholderGray, fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (isSignUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isSignUpPasswordVisible = !isSignUpPasswordVisible }) {
                                Icon(
                                    imageVector = if (isSignUpPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = PlaceholderGray
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Confirm Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Confirm Password *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrayLabel
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = signUpConfirmPassword,
                        onValueChange = { signUpConfirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirm your password", color = PlaceholderGray, fontSize = 14.sp) },
                        singleLine = true,
                        visualTransformation = if (isSignUpConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isSignUpConfirmPasswordVisible = !isSignUpConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (isSignUpConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = PlaceholderGray
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                executeSignUp()
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandForestGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Action Button
                Button(
                    onClick = { executeSignUp() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = BrandForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandForestGreen)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Verify & Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // "or continue with" divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = LightUnderlineGray)
                Text(
                    text = "  or continue with  ",
                    color = OrContinueGray,
                    fontSize = 13.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = LightUnderlineGray)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Continue with Google to Sign Up / Sign In
            OutlinedButton(
                onClick = {
                    showGoogleAccountChooser = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bus_avatar),
                        contentDescription = "Google",
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selectedTab == 1) "Continue with Google to Sign Up" else "Continue with Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switch between Sign In and Sign Up at bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedTab == 0) "Don't have an account? " else "Already have an account? ",
                    fontSize = 13.sp,
                    color = TextGrayLabel
                )
                Text(
                    text = if (selectedTab == 0) "Sign Up" else "Sign In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandForestGreen,
                    modifier = Modifier.clickable {
                        selectedTab = if (selectedTab == 0) 1 else 0
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Backend Configuration Modal
        if (showBackendConfigDialog) {
            AlertDialog(
                onDismissRequest = { showBackendConfigDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = BrandForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Backend Connection Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "For Backend Developers:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandForestGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Email authentication strictly requires valid Gmail accounts (@gmail.com).",
                            fontSize = 12.sp,
                            color = TextGrayLabel
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = backendBaseUrlInput,
                            onValueChange = { backendBaseUrlInput = it },
                            label = { Text("API Base URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            BackendManager.BASE_URL = backendBaseUrlInput
                            showBackendConfigDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Backend Base URL updated to: ${BackendManager.BASE_URL}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandForestGreen)
                    ) {
                        Text("Save URL")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBackendConfigDialog = false }) {
                        Text("Close", color = TextGrayLabel)
                    }
                }
            )
        }

        // Google Account Chooser Dialog (Directly fetches Name, Gender, Phone Number, Gmail)
        if (showGoogleAccountChooser) {
            AlertDialog(
                onDismissRequest = { showGoogleAccountChooser = false },
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.gmail_profile_avatar),
                                    contentDescription = "Google Account",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sign in with Google",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "Choose an account to continue to Bus Tracking",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(6.dp))

                        // Account 1: Thejashwini P (Verified Gmail Account)
                        val acc1 = UserProfileManager.defaultGoogleAccount
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable {
                                    UserProfileManager.applyGoogleProfile(acc1)
                                    if (selectedTab == 1) {
                                        signUpFullName = acc1.fullName
                                        selectedGender = acc1.gender
                                        signUpMobile = "9876543210"
                                        signUpEmail = acc1.email
                                    }
                                    showGoogleAccountChooser = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Fetched from Google: ${acc1.fullName} (${acc1.gender}) • ${acc1.email}")
                                    }
                                    onLoginSuccess()
                                },
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = acc1.avatarResId),
                                    contentDescription = acc1.fullName,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, BrandForestGreen, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = acc1.fullName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = acc1.email,
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Gender: ${acc1.gender} • Mobile: ${acc1.mobileNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = BrandForestGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Account 2: Alternate Google Account
                        val acc2 = UserProfileManager.alternateGoogleAccount
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                .clickable {
                                    UserProfileManager.applyGoogleProfile(acc2)
                                    if (selectedTab == 1) {
                                        signUpFullName = acc2.fullName
                                        selectedGender = acc2.gender
                                        signUpMobile = "9845012345"
                                        signUpEmail = acc2.email
                                    }
                                    showGoogleAccountChooser = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Fetched from Google: ${acc2.fullName} (${acc2.gender}) • ${acc2.email}")
                                    }
                                    onLoginSuccess()
                                },
                            color = Color(0xFFF9FAFB)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE0E7FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "TC",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF4338CA)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = acc2.fullName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = acc2.email,
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Gender: ${acc2.gender} • Mobile: ${acc2.mobileNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF4338CA)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Data will be fetched directly and synchronized to your account profile.",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGoogleAccountChooser = false }) {
                        Text("Cancel", color = Color(0xFF6B7280))
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}