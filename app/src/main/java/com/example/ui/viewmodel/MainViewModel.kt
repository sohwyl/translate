package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ui.components.AudioPlayerHelper
import com.example.ui.components.CategoryItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = PhraseRepository(database.phraseDao())
    val userPreferences = UserPreferences(application)
    val audioPlayer = AudioPlayerHelper.getInstance(application)

    val onboardingCompleted = userPreferences.onboardingCompleted
    val userRole = userPreferences.userRole
    val darkTheme = userPreferences.darkTheme
    val largeTextEnabled = userPreferences.largeTextEnabled
    val fontScale = userPreferences.fontScale
    val fontWeightOffset = userPreferences.fontWeightOffset
    val isGoldVersionActivated = userPreferences.isGoldVersionActivated
    val hapticsEnabled = userPreferences.hapticsEnabled
    val voiceGender = userPreferences.voiceGender
    val arabicFontType = userPreferences.arabicFontType
    val persianFontType = userPreferences.persianFontType
    val playbackSpeed = userPreferences.playbackSpeed

    private val _selectedCategory = MutableStateFlow("همه")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCategories = MutableStateFlow<Set<String>>(setOf("همه"))
    val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showFavoriteLimitDialog = MutableStateFlow(false)
    val showFavoriteLimitDialog: StateFlow<Boolean> = _showFavoriteLimitDialog.asStateFlow()

    private val _allPhrases = MutableStateFlow<List<PhraseEntity>>(DatabaseInitializer.getInitialPhrases())
    val favoritePhrases: StateFlow<List<PhraseEntity>> = repository.favoritePhrases.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val speakingPhraseId = audioPlayer.speakingPhraseId

    init {
        viewModelScope.launch {
            try {
                repository.ensureDatabasePopulated()
            } catch (e: Exception) {
                // Non-blocking fallback
            }
            repository.allPhrases.collect { list ->
                if (list.isNotEmpty()) {
                    _allPhrases.value = list
                }
            }
        }
    }

    val categories: StateFlow<List<CategoryItem>> = _allPhrases.map { phrases ->
        val map = phrases.groupBy { it.category }
        val categoryList = mutableListOf<CategoryItem>()
        categoryList.add(CategoryItem("همه", phrases.size, isFree = true))
        
        // 5 Free Categories (Phase 1)
        val freeCats = listOf(
            "احوالپرسی و احترام",
            "زیارت و حرم",
            "موکب و خدمات",
            "مسیر و آدرس",
            "غذا و نوشیدنی"
        )

        // 5 VIP Premium Categories (Phase 2)
        val premiumCats = listOf(
            "خرید و بازار",
            "تاکسی و حمل‌ونقل",
            "اسکان و محل اقامت",
            "درمان و دارو",
            "پول، بانک و صرافی"
        )

        // 5 VIP Premium Categories (Phase 3)
        val phase3Cats = listOf(
            "موبایل، اینترنت و شارژ",
            "شرایط اضطراری",
            "خانواده و کودکان",
            "پیاده‌روی اربعین",
            "اصطلاحات مذهبی و زیارتی"
        )

        // 6 VIP Premium Categories (Phase 4)
        val phase4Cats = listOf(
            "اصطلاحات روزمره عراقی",
            "مکالمه با خادم موکب",
            "مکالمه با راننده تاکسی",
            "مکالمه با فروشنده",
            "اصطلاحات پرکاربرد عراقی",
            "اصطلاحات و تکیه‌کلام‌های عراقی"
        )

        // 9 VIP Premium Categories (Phase 5 — added in the 1000-phrase expansion)
        val phase5Cats = listOf(
            "مکالمه با پزشک و پرستار",
            "فرودگاه، گذرنامه و مرز",
            "حمل‌ونقل بین‌شهری",
            "رزرو و اجاره وسایل",
            "گم‌شدن و کمک‌خواهی",
            "زبان بدن، ادب و تعارفات عراقی",
            "دعا و زیارت‌نامه‌های کوتاه پرکاربرد",
            "گفتگوی دوستانه و آشناسازی",
            "اعداد، زمان و روزهای هفته کاربردی"
        )

        // Mokeb-owner-only categories (shown when relevant to the selected role)
        val mokebOwnerCats = listOf(
            "استقبال از زائر",
            "خدمات و مکالمه موکب‌دار"
        )

        val orderedNames = freeCats + premiumCats + phase3Cats + phase4Cats + phase5Cats + mokebOwnerCats

        for (name in orderedNames) {
            val count = map[name]?.size ?: 0
            if (count > 0) {
                categoryList.add(CategoryItem(name, count, isFree = CategoryUtils.isCategoryFree(name)))
            }
        }

        // Add any remaining categories
        map.forEach { (catName, catList) ->
            if (catName !in orderedNames && catName != "همه") {
                categoryList.add(CategoryItem(catName, catList.size, isFree = CategoryUtils.isCategoryFree(catName)))
            }
        }

        categoryList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf(CategoryItem("همه", DatabaseInitializer.getInitialPhrases().size, isFree = true))
    )

    val displayedPhrases: StateFlow<List<PhraseEntity>> = combine(
        _allPhrases,
        _selectedCategories,
        _searchQuery,
        isGoldVersionActivated,
        userRole
    ) { phrases, categoriesSet, query, goldActive, role ->
        val normalizedQuery = ArabicNormalizer.normalize(query)

        val filtered = phrases.filter { phrase ->
            val isAllSelected = categoriesSet.contains("همه") || categoriesSet.isEmpty()
            
            val matchesCategory = if (normalizedQuery.isNotEmpty()) {
                true // Global search across all categories
            } else if (isAllSelected) {
                true
            } else {
                categoriesSet.any { cat ->
                    phrase.category == cat ||
                    phrase.category.contains(cat) ||
                    cat.contains(phrase.category) ||
                    (cat.contains("احوالپرسی") && phrase.category.contains("احوالپرسی")) ||
                    (cat.contains("موکب") && phrase.category.contains("موکب")) ||
                    (cat.contains("غذا") && phrase.category.contains("غذا")) ||
                    (cat.contains("دارو") && phrase.category.contains("دارو")) ||
                    (cat.contains("پزشکی") && phrase.category.contains("درمان")) ||
                    (cat.contains("حمل") && phrase.category.contains("حمل")) ||
                    (cat.contains("سفر") && (phrase.category.contains("تاکسی") || phrase.category.contains("مسیر"))) ||
                    (cat.contains("پرسش") && phrase.category.contains("مسیر")) ||
                    (cat.contains("ضروری") && (phrase.category.contains("اضطراری") || phrase.category.contains("مسیر")))
                }
            }

            // Diacritics normalized multi-field search
            val matchesQuery = if (normalizedQuery.isEmpty()) {
                true
            } else {
                ArabicNormalizer.normalize(phrase.arabicText).contains(normalizedQuery) ||
                ArabicNormalizer.normalize(phrase.iraqiPronunciation).contains(normalizedQuery) ||
                ArabicNormalizer.normalize(phrase.persianTranslation).contains(normalizedQuery)
            }

            matchesCategory && matchesQuery
        }

        if (normalizedQuery.isNotEmpty()) {
            // Free results first, premium results below
            filtered.sortedWith(
                compareBy<PhraseEntity> { phrase ->
                    if (CategoryUtils.isPhraseLocked(phrase, goldActive)) 1 else 0
                }.thenBy { it.id }
            )
        } else if (role == UserPreferences.ROLE_MOKEB_OWNER) {
            filtered.sortedWith(compareByDescending { it.forRole == "MOKEB_OWNER" || it.category.contains("موکب") })
        } else {
            filtered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DatabaseInitializer.getInitialPhrases()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (category == "همه" || category.startsWith("همه")) {
            _selectedCategories.value = setOf("همه")
        } else {
            _selectedCategories.value = setOf(category)
        }
    }

    fun toggleCategoryInMultiSelect(category: String) {
        if (category == "همه" || category.startsWith("همه")) {
            _selectedCategory.value = "همه"
            _selectedCategories.value = setOf("همه")
            return
        }

        val currentSet = _selectedCategories.value.toMutableSet()
        currentSet.remove("همه")

        if (currentSet.contains(category)) {
            currentSet.remove(category)
        } else {
            currentSet.add(category)
        }

        if (currentSet.isEmpty()) {
            _selectedCategory.value = "همه"
            _selectedCategories.value = setOf("همه")
        } else {
            _selectedCategory.value = currentSet.first()
            _selectedCategories.value = currentSet
        }
    }

    fun setSelectedCategories(categories: Set<String>) {
        if (categories.isEmpty() || categories.contains("همه")) {
            _selectedCategory.value = "همه"
            _selectedCategories.value = setOf("همه")
        } else {
            _selectedCategory.value = categories.first()
            _selectedCategories.value = categories
        }
    }

    fun clearSelectedCategories() {
        _selectedCategory.value = "همه"
        _selectedCategories.value = setOf("همه")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(phrase: PhraseEntity) {
        if (!phrase.isFavorite) {
            if (!isGoldVersionActivated.value && favoritePhrases.value.size >= 10) {
                _showFavoriteLimitDialog.value = true
                return
            }
        }
        viewModelScope.launch {
            repository.toggleFavorite(phrase)
        }
    }

    fun dismissFavoriteLimitDialog() {
        _showFavoriteLimitDialog.value = false
    }

    fun playAudio(phrase: PhraseEntity) {
        val targetId = if (phrase.numeric_id > 0) phrase.numeric_id else phrase.id
        if (speakingPhraseId.value == targetId) {
            audioPlayer.stop()
        } else {
            audioPlayer.speak(targetId, phrase.iraqiPronunciation, voiceGender.value, playbackSpeed.value)
        }
    }

    fun setRole(role: String) {
        userPreferences.setUserRole(role)
    }

    fun setDarkTheme(enabled: Boolean) {
        userPreferences.setDarkTheme(enabled)
    }

    fun setLargeText(enabled: Boolean) {
        userPreferences.setLargeTextEnabled(enabled)
    }

    fun setFontScale(scale: Float) {
        userPreferences.setFontScale(scale)
    }

    fun setFontWeightOffset(offset: Int) {
        userPreferences.setFontWeightOffset(offset)
    }

    fun setGoldActivated(activated: Boolean) {
        userPreferences.setGoldVersionActivated(activated)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        userPreferences.setHapticsEnabled(enabled)
    }

    fun setVoiceGender(gender: String) {
        userPreferences.setVoiceGender(gender)
    }

    fun setArabicFontType(fontType: String) {
        userPreferences.setArabicFontType(fontType)
    }

    fun setPersianFontType(fontType: String) {
        userPreferences.setPersianFontType(fontType)
    }

    fun setPlaybackSpeed(speed: Float) {
        userPreferences.setPlaybackSpeed(speed)
    }

    fun resetSettingsOnly() {
        userPreferences.resetSettingsOnly()
    }

    fun completeOnboarding(role: String, darkTheme: Boolean, largeText: Boolean) {
        userPreferences.setUserRole(role)
        userPreferences.setDarkTheme(darkTheme)
        userPreferences.setLargeTextEnabled(largeText)
        userPreferences.setOnboardingCompleted(true)
    }

    fun resetOnboarding() {
        userPreferences.setOnboardingCompleted(false)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
