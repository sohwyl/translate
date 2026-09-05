package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.billing.BazaarBillingManager
import com.example.security.EncryptedStorageHelper
import com.example.ui.navigation.AppNavHost
import com.example.ui.navigation.Screen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.IraqiArabicTranslatorTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var bazaarBillingManager: BazaarBillingManager
    private lateinit var encryptedStorageHelper: EncryptedStorageHelper
    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        const val REQUEST_CODE_BAZAAR_PURCHASE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        encryptedStorageHelper = EncryptedStorageHelper(this)

        setContent {
            val onboardingCompleted by mainViewModel.onboardingCompleted.collectAsStateWithLifecycle()
            val userRole by mainViewModel.userRole.collectAsStateWithLifecycle()
            val darkTheme by mainViewModel.darkTheme.collectAsStateWithLifecycle()
            val largeTextEnabled by mainViewModel.largeTextEnabled.collectAsStateWithLifecycle()
            val fontScale by mainViewModel.fontScale.collectAsStateWithLifecycle()
            val fontWeightOffset by mainViewModel.fontWeightOffset.collectAsStateWithLifecycle()
            val isGoldActivated by mainViewModel.isGoldVersionActivated.collectAsStateWithLifecycle()
            val hapticsEnabled by mainViewModel.hapticsEnabled.collectAsStateWithLifecycle()
            val voiceGender by mainViewModel.voiceGender.collectAsStateWithLifecycle()
            val arabicFontType by mainViewModel.arabicFontType.collectAsStateWithLifecycle()
            val persianFontType by mainViewModel.persianFontType.collectAsStateWithLifecycle()

            val displayedPhrases by mainViewModel.displayedPhrases.collectAsStateWithLifecycle()
            val favoritePhrases by mainViewModel.favoritePhrases.collectAsStateWithLifecycle()
            val categories by mainViewModel.categories.collectAsStateWithLifecycle()
            val selectedCategory by mainViewModel.selectedCategory.collectAsStateWithLifecycle()
            val selectedCategories by mainViewModel.selectedCategories.collectAsStateWithLifecycle()
            val searchQuery by mainViewModel.searchQuery.collectAsStateWithLifecycle()
            val speakingPhraseId by mainViewModel.speakingPhraseId.collectAsStateWithLifecycle()
            val showFavoriteLimitDialog by mainViewModel.showFavoriteLimitDialog.collectAsStateWithLifecycle()
            val playbackSpeed by mainViewModel.playbackSpeed.collectAsStateWithLifecycle()

            // Shown briefly on every launch (cold start of this Activity), regardless
            // of onboarding/login state — a full-screen animated brand moment.
            var showSplash by remember { mutableStateOf(true) }

            // Initialize Cafe Bazaar Billing Manager on Activity launch
            LaunchedEffect(Unit) {
                bazaarBillingManager = BazaarBillingManager(this@MainActivity, object : BazaarBillingManager.BillingListener {
                    override fun onServiceConnected() {
                        // Instantly check VIP status upon connecting to Cafe Bazaar
                        bazaarBillingManager.checkVipStatus(
                            BazaarBillingManager.SKU_PREMIUM_UPGRADE,
                            object : BazaarBillingManager.VipStatusListener {
                                override fun onVipStatusChecked(isVipActive: Boolean, sku: String?, purchaseData: String?) {
                                    if (isVipActive) {
                                        mainViewModel.setGoldActivated(true)
                                        if (purchaseData != null) {
                                            encryptedStorageHelper.savePurchaseToken(purchaseData)
                                        }
                                    } else {
                                        // Check fallback SKU vip_membership as well
                                        bazaarBillingManager.checkVipStatus(
                                            BazaarBillingManager.SKU_VIP_PASS,
                                            object : BazaarBillingManager.VipStatusListener {
                                                override fun onVipStatusChecked(isPassActive: Boolean, s: String?, p: String?) {
                                                    if (isPassActive) {
                                                        mainViewModel.setGoldActivated(true)
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    override fun onServiceDisconnected() {
                        // Disconnected gracefully
                    }

                    override fun onError(message: String?) {
                        android.util.Log.d("MainActivity", "Bazaar service initial connection: " + (message ?: "Not available"))
                    }
                })
                bazaarBillingManager.connectService()
            }

            val navController = rememberNavController()
            val startDestination = if (onboardingCompleted) Screen.Main.route else Screen.Onboarding.route

            // Crossfade so the app content is never composed (and can never
            // flash on screen) until the splash has actually finished; the
            // fade itself is what visually bridges the two.
            Crossfade(
                targetState = showSplash,
                animationSpec = tween(durationMillis = 550),
                label = "splash_crossfade"
            ) { splashVisible ->
                if (splashVisible) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
            // RTL layout direction for Persian and Arabic
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                IraqiArabicTranslatorTheme(darkTheme = darkTheme) {
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        phrases = displayedPhrases,
                        favoritePhrases = favoritePhrases,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        selectedCategories = selectedCategories,
                        currentRole = userRole,
                        isDarkTheme = darkTheme,
                        isLargeText = largeTextEnabled,
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
                        onCategorySelected = { mainViewModel.selectCategory(it) },
                        onMultiCategoryApply = { mainViewModel.setSelectedCategories(it) },
                        onCategoryDeselect = { mainViewModel.toggleCategoryInMultiSelect(it) },
                        onRoleChange = { mainViewModel.setRole(it) },
                        onDarkThemeToggle = { mainViewModel.setDarkTheme(it) },
                        onLargeTextToggle = { mainViewModel.setLargeText(it) },
                        onFontScaleChange = { mainViewModel.setFontScale(it) },
                        onFontWeightChange = { mainViewModel.setFontWeightOffset(it) },
                        onArabicFontTypeChange = { mainViewModel.setArabicFontType(it) },
                        onPersianFontTypeChange = { mainViewModel.setPersianFontType(it) },
                        onPlaybackSpeedChange = { mainViewModel.setPlaybackSpeed(it) },
                        onHapticsToggle = { mainViewModel.setHapticsEnabled(it) },
                        onVoiceGenderChange = { mainViewModel.setVoiceGender(it) },
                        onSearchQueryChange = { mainViewModel.setSearchQuery(it) },
                        onPlayAudio = { mainViewModel.playAudio(it) },
                        onToggleFavorite = { mainViewModel.toggleFavorite(it) },
                        onActivateGold = { mainViewModel.setGoldActivated(it) },
                        onStartBazaarPurchase = {
                            if (::bazaarBillingManager.isInitialized) {
                                bazaarBillingManager.launchVipPurchase(
                                    this@MainActivity,
                                    BazaarBillingManager.SKU_PREMIUM_UPGRADE,
                                    REQUEST_CODE_BAZAAR_PURCHASE,
                                    object : BazaarBillingManager.PurchaseFlowListener {
                                        override fun onPurchaseFlowStarted() {
                                            // Flow launched
                                        }

                                        override fun onItemAlreadyOwned(sku: String?) {
                                            mainViewModel.setGoldActivated(true)
                                            Toast.makeText(
                                                this@MainActivity,
                                                "این محصول قبلاً توسط شما خریداری شده است ✨",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        override fun onPurchaseCanceled() {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "فرآیند خرید لغو شد.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onPurchaseFailed(responseCode: Int, message: String?) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                message ?: "خطا در شروع فرآیند خرید کافه بازار",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            } else {
                                mainViewModel.setGoldActivated(true)
                            }
                        },
                        onRestoreBazaarPurchase = {
                            if (::bazaarBillingManager.isInitialized) {
                                bazaarBillingManager.checkVipStatus(
                                    BazaarBillingManager.SKU_PREMIUM_UPGRADE,
                                    object : BazaarBillingManager.VipStatusListener {
                                        override fun onVipStatusChecked(isVipActive: Boolean, sku: String?, purchaseData: String?) {
                                            if (isVipActive) {
                                                mainViewModel.setGoldActivated(true)
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "خرید شما با موفقیت بازیابی و فعال شد ✨",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "هیچ خرید فعالی برای حساب کافه بازار شما یافت نشد.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                )
                            }
                        },
                        onDismissFavoriteLimitDialog = { mainViewModel.dismissFavoriteLimitDialog() },
                        onFinishOnboarding = { role, dark, large ->
                            mainViewModel.completeOnboarding(role, dark, large)
                        },
                        onResetOnboarding = { mainViewModel.resetOnboarding() },
                        onResetSettingsOnly = { mainViewModel.resetSettingsOnly() }
                    )
                }
            }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::bazaarBillingManager.isInitialized) {
            bazaarBillingManager.handleActivityResult(
                requestCode,
                REQUEST_CODE_BAZAAR_PURCHASE,
                resultCode,
                data,
                object : BazaarBillingManager.PurchaseResultListener {
                    override fun onPurchaseSuccess(
                        purchaseData: String?,
                        signature: String?,
                        orderId: String?,
                        sku: String?,
                        purchaseToken: String?
                    ) {
                        mainViewModel.setGoldActivated(true)
                        if (purchaseToken != null) {
                            encryptedStorageHelper.savePurchaseToken(purchaseToken)
                        }
                        Toast.makeText(
                            this@MainActivity,
                            "ارتقای ویژه به نسخه کامل با موفقیت انجام شد ✨",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onItemAlreadyOwned(sku: String?) {
                        mainViewModel.setGoldActivated(true)
                        Toast.makeText(
                            this@MainActivity,
                            "این محصول قبلاً توسط شما خریداری شده است ✨",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onPurchaseCanceled() {
                        Toast.makeText(
                            this@MainActivity,
                            "پرداخت توسط کاربر لغو شد.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onSignatureVerificationFailed() {
                        Toast.makeText(
                            this@MainActivity,
                            "خطای امنیتی: تایید امضای دیجیتال کافه بازار ناموفق بود.",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onPayloadMismatch() {
                        Toast.makeText(
                            this@MainActivity,
                            "خطای امنیتی: عدم تطابق شناسه تراکنش (Developer Payload Mismatch).",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onPurchaseFailed(responseCode: Int, message: String?) {
                        Toast.makeText(
                            this@MainActivity,
                            message ?: "پرداخت با خطا مواجه شد.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bazaarBillingManager.isInitialized) {
            bazaarBillingManager.disconnectService()
        }
        // BUG FIX: this used to also call AudioPlayerHelper.getInstance(this).release()
        // here. AudioPlayerHelper is a process-wide singleton, and MainActivity.onDestroy()
        // fires on EVERY activity destruction — including a simple screen rotation, where
        // the Activity is destroyed+recreated but the ViewModel (and its `audioPlayer`
        // reference) survives. release() sets the singleton's TextToSpeech engine to null
        // and never re-initializes it (TTS setup only happens once, in the constructor), so
        // after a single rotation the TTS fallback voice would silently stop working for the
        // rest of the app's process lifetime. Releasing the shared audio resources is
        // MainViewModel.onCleared()'s job — it only fires when the ViewModel itself is
        // actually being cleared (the activity finishing for good), which is the correct,
        // single point of ownership for this singleton's lifecycle.
    }
}
