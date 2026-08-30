package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.PhraseEntity
import com.example.ui.components.CategoryItem
import com.example.ui.main.MainScreen
import com.example.ui.main.PhraseListScreen
import com.example.ui.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object PhraseList : Screen("phrase_list/{categoryName}") {
        fun createRoute(categoryName: String) = "phrase_list/${categoryName}"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String,
    phrases: List<PhraseEntity>,
    favoritePhrases: List<PhraseEntity>,
    categories: List<CategoryItem>,
    selectedCategory: String,
    selectedCategories: Set<String> = emptySet(),
    currentRole: String,
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    fontScale: Float,
    fontWeightOffset: Int,
    hapticsEnabled: Boolean = true,
    voiceGender: String = "MALE",
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    playbackSpeed: Float = 1.0f,
    isGoldActivated: Boolean,
    searchQuery: String,
    speakingPhraseId: Int?,
    showFavoriteLimitDialog: Boolean,
    onCategorySelected: (String) -> Unit,
    onMultiCategoryApply: (Set<String>) -> Unit = {},
    onCategoryDeselect: (String) -> Unit = {},
    onRoleChange: (String) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onFontWeightChange: (Int) -> Unit,
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
    onDismissFavoriteLimitDialog: () -> Unit,
    onFinishOnboarding: (role: String, darkTheme: Boolean, largeText: Boolean) -> Unit,
    onResetOnboarding: () -> Unit,
    onResetSettingsOnly: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                initialRole = currentRole,
                initialDarkTheme = isDarkTheme,
                initialLargeText = isLargeText,
                onFinishOnboarding = { role, darkTheme, largeText ->
                    onFinishOnboarding(role, darkTheme, largeText)
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                phrases = phrases,
                favoritePhrases = favoritePhrases,
                categories = categories,
                selectedCategory = selectedCategory,
                selectedCategories = selectedCategories,
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
                searchQuery = searchQuery,
                speakingPhraseId = speakingPhraseId,
                showFavoriteLimitDialog = showFavoriteLimitDialog,
                onCategorySelected = { category ->
                    onCategorySelected(category)
                },
                onMultiCategoryApply = onMultiCategoryApply,
                onCategoryDeselect = onCategoryDeselect,
                onNavigateToCategory = { categoryName ->
                    navController.navigate(Screen.PhraseList.createRoute(categoryName))
                },
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
                onSearchQueryChange = onSearchQueryChange,
                onPlayAudio = onPlayAudio,
                onToggleFavorite = onToggleFavorite,
                onActivateGold = onActivateGold,
                onStartBazaarPurchase = onStartBazaarPurchase,
                onRestoreBazaarPurchase = onRestoreBazaarPurchase,
                onDismissFavoriteLimitDialog = onDismissFavoriteLimitDialog,
                onResetOnboarding = {
                    onResetOnboarding()
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onResetSettingsOnly = onResetSettingsOnly
            )
        }

        composable(
            route = Screen.PhraseList.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "همه"
            PhraseListScreen(
                categoryName = categoryName,
                phrases = phrases,
                isLargeText = isLargeText,
                isDarkTheme = isDarkTheme,
                isGoldActivated = isGoldActivated,
                speakingPhraseId = speakingPhraseId,
                hapticsEnabled = hapticsEnabled,
                arabicFontType = arabicFontType,
                persianFontType = persianFontType,
                fontWeightOffset = fontWeightOffset,
                onPlayAudio = onPlayAudio,
                onToggleFavorite = onToggleFavorite,
                onActivateVipClick = { onActivateGold(true) },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
