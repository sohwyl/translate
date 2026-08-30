package com.example.ui.main

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhraseEntity
import com.example.data.UserPreferences
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    phrases: List<PhraseEntity>,
    favoritePhrases: List<PhraseEntity>,
    categories: List<CategoryItem>,
    selectedCategory: String,
    selectedCategories: Set<String> = emptySet(),
    currentRole: String,
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    fontScale: Float = 1.0f,
    fontWeightOffset: Int = 0,
    hapticsEnabled: Boolean = true,
    voiceGender: String = UserPreferences.VOICE_MALE,
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    playbackSpeed: Float = 1.0f,
    isGoldActivated: Boolean,
    searchQuery: String,
    speakingPhraseId: Int?,
    showFavoriteLimitDialog: Boolean = false,
    onCategorySelected: (String) -> Unit,
    onMultiCategoryApply: (Set<String>) -> Unit = {},
    onCategoryDeselect: (String) -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    onRoleChange: (String) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit,
    onFontScaleChange: (Float) -> Unit = {},
    onFontWeightChange: (Int) -> Unit = {},
    onArabicFontTypeChange: (String) -> Unit = {},
    onPersianFontTypeChange: (String) -> Unit = {},
    onPlaybackSpeedChange: (Float) -> Unit = {},
    onHapticsToggle: (Boolean) -> Unit = {},
    onVoiceGenderChange: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onPlayAudio: (PhraseEntity) -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onActivateGold: (Boolean) -> Unit,
    onStartBazaarPurchase: (() -> Unit)? = null,
    onRestoreBazaarPurchase: (() -> Unit)? = null,
    onDismissFavoriteLimitDialog: () -> Unit = {},
    onResetOnboarding: () -> Unit,
    onResetSettingsOnly: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Categories, 2: Favorites, 3: Settings
    var isSearchActive by remember { mutableStateOf(false) }
    var showVipDialog by remember { mutableStateOf(false) }
    var showMultiSelectModal by remember { mutableStateOf(false) }
    var currentToastData by remember { mutableStateOf<ToastData?>(null) }

    val context = LocalContext.current
    val bazaarPurchaseManager = remember {
        com.example.billing.BazaarPurchaseManager(
            context.applicationContext,
            com.example.data.UserPreferences(context.applicationContext)
        )
    }

    val showToast: (String, Boolean) -> Unit = { message, isFavorite ->
        currentToastData = ToastData(message = message, isFavoriteAction = isFavorite)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            // App Logo Icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldenAmber)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = "لوگو",
                                    tint = DarkEmeraldBg,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "مترجم عربی عراقی",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                                )
                                Text(
                                    text = "ویژه پیاده‌روی اربعین",
                                    fontSize = 11.sp,
                                    color = if (isDarkTheme) GoldenAmber else DayEmerald,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    actions = {
                        // Search Toggle Button
                        IconButton(
                            onClick = {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) onSearchQueryChange("")
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF))
                                .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "بستن جستجو" else "جستجو",
                                tint = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Theme Toggle Button
                        IconButton(
                            onClick = { onDarkThemeToggle(!isDarkTheme) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF))
                                .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brightness4,
                                contentDescription = "تم",
                                tint = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
                    )
                )
            },
            bottomBar = {
                // Floating Modern Navigation Bar
                FloatingNavigationBar(
                    selectedTab = selectedTab,
                    favoriteCount = favoritePhrases.size,
                    isDarkTheme = isDarkTheme,
                    onTabSelected = { selectedTab = it }
                )
            },
            containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
        ) { paddingValues ->
            com.example.ui.components.ArbaeenAmbientBackground(
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                // Live Search Input Field
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        placeholder = {
                            Text(
                                "جستجو در عربی، فارسی یا تلفظ عراقی...",
                                fontSize = 13.sp,
                                color = if (isDarkTheme) TextMutedDark else Color(0xFF8A9A93)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                onSearchQueryChange("")
                                isSearchActive = false
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "بستن جستجو",
                                    tint = if (isDarkTheme) TextMutedDark else Color(0xFF8A9A93)
                                )
                            }
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDarkTheme) GoldenAmber else DayEmerald,
                            unfocusedBorderColor = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3),
                            focusedContainerColor = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF),
                            unfocusedContainerColor = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF),
                            focusedTextColor = if (isDarkTheme) TextPrimaryDark else DayText,
                            unfocusedTextColor = if (isDarkTheme) TextPrimaryDark else DayText
                        ),
                        singleLine = true
                    )
                }

                // Live Search Results Count Badge
                AnimatedVisibility(visible = isSearchActive && searchQuery.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نتایج جستجو برای «$searchQuery»:",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDarkTheme) GoldenAmber.copy(alpha = 0.2f) else DayEmerald.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${phrases.size} عبارت منطبق",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        }
                    }
                }

                // Tab Content Switcher
                when (selectedTab) {
                    0 -> HomeTab(
                        phrases = phrases,
                        allPhrases = phrases,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        selectedCategories = selectedCategories,
                        currentRole = currentRole,
                        isDarkTheme = isDarkTheme,
                        isLargeText = isLargeText,
                        fontScale = fontScale,
                        fontWeightOffset = fontWeightOffset,
                        isGoldActivated = isGoldActivated,
                        speakingPhraseId = speakingPhraseId,
                        hapticsEnabled = hapticsEnabled,
                        searchQuery = searchQuery,
                        arabicFontType = arabicFontType,
                        persianFontType = persianFontType,
                        onCategorySelected = onCategorySelected,
                        onOpenMultiSelectModal = { showMultiSelectModal = true },
                        onDeselectCategory = onCategoryDeselect,
                        onRoleChange = onRoleChange,
                        onPlayAudio = onPlayAudio,
                        onToggleFavorite = onToggleFavorite,
                        onShowToast = showToast,
                        onActivateVipClick = { showVipDialog = true },
                        onOpenCategoryScreen = { categoryName ->
                            onNavigateToCategory(categoryName)
                        }
                    )
                    1 -> CategoriesTab(
                        categories = categories,
                        isDarkTheme = isDarkTheme,
                        onSelectCategory = { categoryName ->
                            onNavigateToCategory(categoryName)
                        }
                    )
                    2 -> FavoritesTab(
                        favoritePhrases = favoritePhrases,
                        isLargeText = isLargeText,
                        isDarkTheme = isDarkTheme,
                        isGoldActivated = isGoldActivated,
                        speakingPhraseId = speakingPhraseId,
                        hapticsEnabled = hapticsEnabled,
                        searchQuery = searchQuery,
                        arabicFontType = arabicFontType,
                        persianFontType = persianFontType,
                        fontWeightOffset = fontWeightOffset,
                        onPlayAudio = onPlayAudio,
                        onToggleFavorite = { phrase ->
                            onToggleFavorite(phrase)
                            showToast("از علاقه‌مندی‌ها حذف شد", true)
                        },
                        onActivateVipClick = { showVipDialog = true },
                        onNavigateToHome = { selectedTab = 0 }
                    )
                    3 -> SettingsTab(
                        currentRole = currentRole,
                        isDarkTheme = isDarkTheme,
                        isLargeText = isLargeText,
                        fontScale = fontScale,
                        fontWeightOffset = fontWeightOffset,
                        hapticsEnabled = hapticsEnabled,
                        voiceGender = voiceGender,
                        arabicFontType = arabicFontType,
                        persianFontType = persianFontType,
                        playbackSpeed = playbackSpeed,
                        isGoldActivated = isGoldActivated,
                        onRoleChange = onRoleChange,
                        onDarkThemeToggle = onDarkThemeToggle,
                        onLargeTextToggle = onLargeTextToggle,
                        onFontScaleChange = onFontScaleChange,
                        onFontWeightChange = onFontWeightChange,
                        onArabicFontTypeChange = onArabicFontTypeChange,
                        onPersianFontTypeChange = onPersianFontTypeChange,
                        onPlaybackSpeedChange = onPlaybackSpeedChange,
                        onHapticsToggle = onHapticsToggle,
                        onVoiceGenderChange = onVoiceGenderChange,
                        onActivateGoldClick = { showVipDialog = true },
                        onResetSettingsOnly = onResetSettingsOnly
                    )
                }
            }
        }
        }

        // Animated Toast Notification Overlay (Standard Bottom Snackbar position)
        AnimatedToastNotification(
            toastData = currentToastData,
            isDarkTheme = isDarkTheme,
            onDismiss = { currentToastData = null },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 92.dp)
        )

        // Multi-Select Category Bottom Sheet
        if (showMultiSelectModal) {
            MultiSelectCategoryBottomSheet(
                categories = categories,
                selectedCategories = selectedCategories,
                isDarkTheme = isDarkTheme,
                onDismissRequest = { showMultiSelectModal = false },
                onApplySelection = { newCategories ->
                    onMultiCategoryApply(newCategories)
                    showMultiSelectModal = false
                }
            )
        }

        // Modal VIP Gold Activation Dialog
        if (showVipDialog) {
            VipActivationDialog(
                isActivated = isGoldActivated,
                onConfirmActivate = {
                    onActivateGold(true)
                    showVipDialog = false
                    showToast("نسخه طلایی با موفقیت فعال شد ✨", false)
                },
                onDismiss = { showVipDialog = false },
                bazaarPurchaseManager = bazaarPurchaseManager,
                onStartBazaarPurchase = onStartBazaarPurchase,
                onRestoreBazaarPurchase = onRestoreBazaarPurchase
            )
        }

        // Modal Favorite Limit Dialog for Free Users
        if (showFavoriteLimitDialog) {
            FavoriteLimitDialog(
                onUpgradeClick = {
                    onDismissFavoriteLimitDialog()
                    showVipDialog = true
                },
                onDismiss = onDismissFavoriteLimitDialog
            )
        }
    }
}
