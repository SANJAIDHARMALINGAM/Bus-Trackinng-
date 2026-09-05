package com.example.bustracking.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.bustracking.data.AppLanguage
import com.example.bustracking.data.AppLanguageManager
import com.example.bustracking.data.AppPreferences
import com.example.bustracking.data.AppStrings
import com.example.bustracking.navigation.AppBottomNavigationBar
import com.example.bustracking.navigation.BrandGreen
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class FavRouteItem(
    val id: String,
    val from: String,
    val to: String,
    val frequency: String,
    var isFavorite: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onRouteClick: (String, String) -> Unit = { _, _ -> },
    onNavigateBottom: (String) -> Unit
) {
    val context = LocalContext.current
    val strings = AppStrings.current
    val currentLang = AppLanguageManager.currentLanguage.value
    val isKannada = currentLang == AppLanguage.KANNADA
    val haptic = LocalHapticFeedback.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchInput by remember { mutableStateOf("") }
    var activeSearchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedRouteIds by remember { mutableStateOf(setOf<String>()) }
    val isSelectionMode = selectedRouteIds.isNotEmpty()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = isSelectionMode) {
        selectedRouteIds = emptySet()
    }

    val defaultRoutes = remember {
        listOf(
            FavRouteItem("1", "Bengaluru", "Mysuru", "Frequent Route", isFavorite = true),
            FavRouteItem("2", "Shivamogga", "Bengaluru", "Frequent Route", isFavorite = true),
            FavRouteItem("3", "Mysuru", "Bengaluru", "Frequent Route", isFavorite = true),
            FavRouteItem("4", "Hubballi", "Bengaluru", "Occasional Route", isFavorite = false),
            FavRouteItem("5", "Bengaluru", "Mangaluru", "Occasional Route", isFavorite = false),
            FavRouteItem("6", "Belagavi", "Hubballi", "Daily Route", isFavorite = true),
            FavRouteItem("7", "Bengaluru", "Davangere", "Weekly Route", isFavorite = false)
        )
    }

    var routes by remember {
        mutableStateOf(loadStoredRoutes(context, defaultRoutes))
    }

    fun persistRoutes(newRoutes: List<FavRouteItem>) {
        routes = newRoutes
        saveStoredRoutes(context, newRoutes)
    }

    val filteredRoutes = remember(activeSearchQuery, routes) {
        val q = activeSearchQuery.trim().lowercase()
        if (q.isEmpty()) routes
        else {
            routes.filter { route ->
                route.from.lowercase().contains(q) ||
                route.to.lowercase().contains(q) ||
                "${route.from} ${route.to}".lowercase().contains(q) ||
                route.frequency.lowercase().contains(q)
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
            if (isSelectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isKannada) "${selectedRouteIds.size} ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ" else "${selectedRouteIds.size} Selected",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E293B)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedRouteIds = emptySet() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Selection",
                                tint = Color(0xFF1E293B)
                            )
                        }
                    },
                    actions = {
                        val allSelected = filteredRoutes.isNotEmpty() && filteredRoutes.all { it.id in selectedRouteIds }
                        IconButton(onClick = {
                            selectedRouteIds = if (allSelected) {
                                emptySet()
                            } else {
                                filteredRoutes.map { it.id }.toSet()
                            }
                        }) {
                            Icon(
                                imageVector = if (allSelected) Icons.Default.Deselect else Icons.Default.SelectAll,
                                contentDescription = if (allSelected) "Deselect All" else "Select All",
                                tint = BrandGreen
                            )
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Selected",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF1F5F9))
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = strings.favouriteRoutesTitle,
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
                        // Interactive "+" button to add a new favorite route
                        IconButton(
                            onClick = { showAddDialog = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFDEF7EC),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Favorite Route",
                                        tint = BrandGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentRoute = "favorites",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput = it
                        activeSearchQuery = it
                    },
                    placeholder = {
                        Text(
                            text = strings.searchFavouritePlaceholder,
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
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
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF64748B)
                            )
                        }
                    },
                    trailingIcon = {
                        if (searchInput.isNotBlank()) {
                            IconButton(onClick = {
                                searchInput = ""
                                activeSearchQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF64748B)
                                )
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

            // Empty state when search yields 0 matches
            if (filteredRoutes.isEmpty()) {
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
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = strings.noFavouriteRoutesFound,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${strings.noFavouriteRoutesMatch} \"$activeSearchQuery\".",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                items(filteredRoutes, key = { it.id }) { item ->
                    val isSelected = item.id in selectedRouteIds

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .combinedClickable(
                                onClick = {
                                    if (isSelectionMode) {
                                        selectedRouteIds = if (isSelected) {
                                            selectedRouteIds - item.id
                                        } else {
                                            selectedRouteIds + item.id
                                        }
                                    } else {
                                        onRouteClick(item.from, item.to)
                                    }
                                },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedRouteIds = if (isSelected) {
                                        selectedRouteIds - item.id
                                    } else {
                                        selectedRouteIds + item.id
                                    }
                                }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        border = if (isSelected) BorderStroke(1.5.dp, BrandGreen) else null,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF0FDF4) else Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
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
                                // Selection Checkbox (visible in selection mode)
                                AnimatedVisibility(
                                    visible = isSelectionMode,
                                    enter = fadeIn() + expandHorizontally(),
                                    exit = fadeOut() + shrinkHorizontally()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 12.dp)
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) BrandGreen else Color.Transparent)
                                            .border(2.dp, if (isSelected) BrandGreen else Color(0xFFCBD5E1), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                if (!isSelectionMode) {
                                    IconButton(onClick = {
                                        val updated = routes.map {
                                            if (it.id == item.id) it.copy(isFavorite = !it.isFavorite) else it
                                        }
                                        persistRoutes(updated)
                                    }) {
                                        Icon(
                                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (item.isFavorite) BrandGreen else Color(0xFF94A3B8),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.from,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "  ➔  ",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                        Text(
                                            text = item.to,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = item.frequency,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!isSelectionMode) {
                                    // Individual Delete Action
                                    IconButton(
                                        onClick = {
                                            val beforeDelete = routes
                                            val updated = routes.filter { it.id != item.id }
                                            persistRoutes(updated)
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = if (isKannada) "ತೆಗೆದುಹಾಕಲಾಗಿದೆ: ${item.from} ➔ ${item.to}" else "Removed ${item.from} ➔ ${item.to}",
                                                    actionLabel = if (isKannada) "ರದ್ದುಮಾಡಿ" else "Undo"
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    persistRoutes(beforeDelete)
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Remove route",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "View route",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BrandGreen.copy(alpha = 0.12f)
                                        ) {
                                            Text(
                                                text = if (isKannada) "ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ" else "Selected",
                                                color = BrandGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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

    // Batch Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        val count = selectedRouteIds.size
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (isKannada) "ಮೆಚ್ಚಿನ ಮಾರ್ಗಗಳನ್ನು ಅಳಿಸುವುದೇ?" else "Delete Favorite Routes?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Text(
                    text = if (isKannada)
                        "ಆಯ್ಕೆಮಾಡಿದ $count ಮೆಚ್ಚಿನ ಮಾರ್ಗಗಳನ್ನು ಅಳಿಸಲು ನೀವು ಖಚಿತವಾಗಿ ಬಯಸುವಿರಾ?"
                    else
                        "Are you sure you want to delete $count selected favorite ${if (count == 1) "route" else "routes"}?",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val beforeDelete = routes
                        val toDelete = selectedRouteIds
                        val remaining = routes.filterNot { it.id in toDelete }
                        persistRoutes(remaining)
                        selectedRouteIds = emptySet()
                        showDeleteConfirmDialog = false
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = if (isKannada) "$count ಮಾರ್ಗಗಳನ್ನು ಅಳಿಸಲಾಗಿದೆ" else "Deleted $count favorite ${if (count == 1) "route" else "routes"}",
                                actionLabel = if (isKannada) "ರದ್ದುಮಾಡಿ" else "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                persistRoutes(beforeDelete)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isKannada) "ಅಳಿಸಿ" else "Delete",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(
                        text = if (isKannada) "ರದ್ದು" else "Cancel",
                        color = Color(0xFF64748B)
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Interactive Add Favorite Route Dialog
    if (showAddDialog) {
        AddFavoriteRouteDialog(
            onDismiss = { showAddDialog = false },
            onAddRoute = { from, to, freq ->
                val newRoute = FavRouteItem(
                    id = UUID.randomUUID().toString(),
                    from = from,
                    to = to,
                    frequency = freq,
                    isFavorite = true
                )
                val updated = listOf(newRoute) + routes
                persistRoutes(updated)
                showAddDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Route $from ➔ $to ${strings.routeAddedSuccess}")
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddFavoriteRouteDialog(
    onDismiss: () -> Unit,
    onAddRoute: (from: String, to: String, frequency: String) -> Unit
) {
    val context = LocalContext.current
    val strings = AppStrings.current
    var fromCity by remember { mutableStateOf("") }
    var toCity by remember { mutableStateOf("") }
    val selectedFrequency = strings.frequentRouteFreq
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDEF7EC),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = BrandGreen,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.addFavouriteRouteTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Origin City Field
                Text(
                    text = strings.fromCityLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = fromCity,
                    onValueChange = {
                        fromCity = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. Bengaluru", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Place, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (fromCity.isNotBlank()) {
                            IconButton(onClick = { fromCity = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Swap Direction Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                val temp = fromCity
                                fromCity = toCity
                                toCity = temp
                                errorMessage = null
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Origin and Destination",
                                tint = BrandGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Destination City Field
                Text(
                    text = strings.toCityLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = toCity,
                    onValueChange = {
                        toCity = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. Mysuru", color = Color(0xFF94A3B8), fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (toCity.isNotBlank()) {
                            IconButton(onClick = { toCity = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandGreen,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Error Message Display
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons (Cancel & Add)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = strings.cancel,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val from = fromCity.trim()
                            val to = toCity.trim()
                            if (from.isEmpty() || to.isEmpty()) {
                                errorMessage = strings.fillBothCitiesError
                                return@Button
                            }
                            if (from.equals(to, ignoreCase = true)) {
                                errorMessage = strings.sameCityError
                                return@Button
                            }
                            onAddRoute(from, to, selectedFrequency)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text(
                            text = strings.addRouteButton,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun loadStoredRoutes(context: Context, defaultRoutes: List<FavRouteItem>): List<FavRouteItem> {
    val json = AppPreferences.getFavoriteRoutesJson(context) ?: return defaultRoutes
    return try {
        val array = JSONArray(json)
        val list = mutableListOf<FavRouteItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                FavRouteItem(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    from = obj.optString("from", ""),
                    to = obj.optString("to", ""),
                    frequency = obj.optString("frequency", "Frequent Route"),
                    isFavorite = obj.optBoolean("isFavorite", true)
                )
            )
        }
        if (list.isEmpty()) defaultRoutes else list
    } catch (e: Exception) {
        defaultRoutes
    }
}

private fun saveStoredRoutes(context: Context, routes: List<FavRouteItem>) {
    try {
        val array = JSONArray()
        routes.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("from", it.from)
            obj.put("to", it.to)
            obj.put("frequency", it.frequency)
            obj.put("isFavorite", it.isFavorite)
            array.put(obj)
        }
        AppPreferences.saveFavoriteRoutesJson(context, array.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

