package com.example.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhraseEntity
import com.example.data.UserPreferences
import com.example.ui.components.*
import com.example.ui.theme.*

data class HomeGridCategory(
    val name: String,
    val displayName: String,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null
)

@Composable
fun HomeTab(
    phrases: List<PhraseEntity>,
    allPhrases: List<PhraseEntity> = phrases,
    categories: List<CategoryItem>,
    selectedCategory: String,
    selectedCategories: Set<String> = emptySet(),
    currentRole: String,
    isDarkTheme: Boolean,
    isLargeText: Boolean,
    isGoldActivated: Boolean,
    speakingPhraseId: Int?,
    hapticsEnabled: Boolean = true,
    searchQuery: String = "",
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    fontScale: Float = 1.0f,
    fontWeightOffset: Int = 0,
    onCategorySelected: (String) -> Unit,
    onOpenMultiSelectModal: () -> Unit = {},
    onDeselectCategory: (String) -> Unit = {},
    onRoleChange: (String) -> Unit,
    onPlayAudio: (PhraseEntity) -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onShowToast: (String, Boolean) -> Unit = { _, _ -> },
    onActivateVipClick: () -> Unit,
    onOpenCategoryScreen: (String) -> Unit = {}
) {
    var detailPhrase by remember { mutableStateOf<PhraseEntity?>(null) }
    var audioLearningPhrase by remember { mutableStateOf<PhraseEntity?>(null) }
    var showDailyPhraseDialog by remember { mutableStateOf(false) }
    var showDailyChallengeDialog by remember { mutableStateOf(false) }
    var showLearningProgressDialog by remember { mutableStateOf(false) }
    var showSmartSearchDialog by remember { mutableStateOf(false) }
    var notePhraseForEditor by remember { mutableStateOf<PhraseEntity?>(null) }

    // 9 Modern Categories linked to DB
    val gridCategories = remember {
        listOf(
            HomeGridCategory("احوالپرسی و احترام", "سلام و احترام", iconRes = R.drawable.ic_modern_handshake),
            HomeGridCategory("زیارت و حرم", "زیارت و حرم", iconRes = R.drawable.ic_modern_mosque),
            HomeGridCategory("موکب و خدمات", "موکب و پذیرایی", iconRes = R.drawable.ic_modern_tea_dallah),
            HomeGridCategory("مسیر و آدرس", "مسیر و آدرس", iconRes = R.drawable.ic_modern_compass),
            HomeGridCategory("غذا و نوشیدنی", "غذا و چای", iconRes = R.drawable.ic_modern_tea_dallah),
            HomeGridCategory("تاکسی و حمل‌ونقل", "سفر و ماشین", iconRes = R.drawable.ic_modern_taxi),
            HomeGridCategory("خرید و بازار", "خرید و بازار", iconRes = R.drawable.ic_modern_market),
            HomeGridCategory("درمان و دارو", "پزشکی و دارو", iconRes = R.drawable.ic_modern_medical),
            HomeGridCategory("پیاده‌روی اربعین", "پیاده‌روی اربعین", iconRes = R.drawable.ic_modern_arbaeen_flag)
        )
    }

    // Phrase of the Day item
    val phraseOfDay = remember(phrases, allPhrases) {
        val pool = if (phrases.isNotEmpty()) phrases else allPhrases
        pool.find { it.arabicText.contains("سلام") || it.iraqiPronunciation.contains("سلام") }
            ?: pool.firstOrNull()
            ?: PhraseEntity(
                id = 1,
                numeric_id = 1,
                arabicText = "السَّلاَمُ عَلَيْكُم",
                iraqiPronunciation = "السَّلاَمُ عَلَيْكُم",
                persianTranslation = "سلام علیکم",
                category = "احوالپرسی و احترام"
            )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
    ) {
        // 1. Greeting Header with Role Badge
        item {
            StaggeredEntrance(key = "home_greeting", index = 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "سلام 👋 زائر گرامی!",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "۶۰۰ عبارت کاربردی با لهجه اصیل عراقی",
                                fontSize = 12.5.sp,
                                color = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        }

                        // Role avatar / indicator
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, GoldenAmber, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(
                                    if (currentRole == UserPreferences.ROLE_MOKEB_OWNER)
                                        R.drawable.img_mokeb_host_avatar
                                    else
                                        R.drawable.img_pilgrim_avatar
                                ),
                                contentDescription = "آواتار نقش",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Feature Action Pills matching design screens: چالش روزانه, پیشرفت یادگیری, عبارت روز
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // چالش روزانه (Screen 9)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFAF6EE))
                                .border(1.dp, GoldenAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { showDailyChallengeDialog = true }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_modern_flame),
                                    contentDescription = null,
                                    tint = GoldenAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "چالش روزانه",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                                )
                            }
                        }

                        // پیشرفت یادگیری (Screen 8)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFAF6EE))
                                .border(1.dp, GoldenAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { showLearningProgressDialog = true }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_modern_sparkle),
                                    contentDescription = null,
                                    tint = GoldenAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "پیشرفت من",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                                )
                            }
                        }

                        // جستجوی هوشمند (Screen 2)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFAF6EE))
                                .border(1.dp, GoldenAmber.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable { showSmartSearchDialog = true }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_modern_search),
                                    contentDescription = null,
                                    tint = GoldenAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "جستجو",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }
        }


        // 2. Scenic Arbaeen Visual Hero Card
        item {
            StaggeredEntrance(key = "home_visual_hero", index = 1) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE5DFC9), RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(
                                if (currentRole == UserPreferences.ROLE_MOKEB_OWNER)
                                    R.drawable.img_iraqi_tea_host
                                else
                                    R.drawable.img_pilgrims_road
                            ),
                            contentDescription = "جلوه بصری اربعین",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.85f),
                                            Color.Black.copy(alpha = 0.35f)
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentRole == UserPreferences.ROLE_MOKEB_OWNER) "مهمان‌نوازی و خدمت موکب" else "طریق یا حسین (ع)",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "مکالمات ضروری عمودها، حرم و موکب‌های عراقی",
                                    fontSize = 11.5.sp,
                                    color = GoldenAmber
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = GoldenAmber,
                                modifier = Modifier.size(36.dp),
                                onClick = { onOpenCategoryScreen("پیاده‌روی اربعین") }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_modern_arbaeen_flag),
                                        contentDescription = "پیاده‌روی",
                                        tint = DarkEmeraldBg,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Categories Grid Section Header & 3x3 Grid
        item {
            StaggeredEntrance(key = "home_cat_title", index = 2) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "دسته‌بندی‌های کاربردی",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                        )

                        TextButton(
                            onClick = onOpenMultiSelectModal,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "مشاهده همه ۱۸ دسته",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                        }
                    }

                    // 3x3 Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (row in 0 until 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val itemIndex = row * 3 + col
                                    if (itemIndex < gridCategories.size) {
                                        val cat = gridCategories[itemIndex]
                                        Box(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            CategoryTile(
                                                category = cat,
                                                isDarkTheme = isDarkTheme,
                                                onClick = {
                                                    onCategorySelected(cat.name)
                                                    onOpenCategoryScreen(cat.name)
                                                }
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

        // 4. "Phrase of the Day" (عبارت روز) Card
        item {
            StaggeredEntrance(key = "phrase_of_the_day", index = 3) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = "عبارت روز",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(20.dp))
                            .clickable { detailPhrase = phraseOfDay },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFAF6EE)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Ambient ornament in corner
                            EslimiCornerBreathingOrnament(
                                modifier = Modifier.align(Alignment.TopEnd),
                                isDarkTheme = isDarkTheme,
                                sizeDp = 80.dp,
                                corner = EslimiCorner.TOP_RIGHT
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Top row: Bookmark icon on left
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            onToggleFavorite(phraseOfDay)
                                            onShowToast("به علاقه‌مندی‌ها اضافه شد ❤️", true)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (phraseOfDay.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "علاقه‌مندی",
                                            tint = GoldenAmber,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDarkTheme) Color(0xFF1B4938) else Color(0xFFE2DDD0))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "عبارت پیشنهادی امروز",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDarkTheme) GoldenAmber else DayEmerald
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Arabic Phrase Text
                                Text(
                                    text = phraseOfDay.iraqiPronunciation.ifEmpty { phraseOfDay.arabicText },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = getArabicFontFamily(arabicFontType),
                                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Persian Meaning
                                Text(
                                    text = phraseOfDay.persianTranslation,
                                    fontSize = 14.sp,
                                    fontFamily = getPersianFontFamily(persianFontType),
                                    color = if (isDarkTheme) GoldenAmber else DayEmerald,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Bottom Row: Speaker audio play button on the right
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isPlaying = speakingPhraseId == phraseOfDay.id || (phraseOfDay.numeric_id > 0 && speakingPhraseId == phraseOfDay.numeric_id)
                                    Surface(
                                        onClick = { onPlayAudio(phraseOfDay) },
                                        shape = CircleShape,
                                        color = GoldenAmber,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_modern_volume_speaker),
                                                contentDescription = "پخش صوتی",
                                                tint = DarkEmeraldBg,
                                                modifier = Modifier.size(22.dp)
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

        // 5. Phrases List Section Header
        val displayList = if (phrases.isNotEmpty()) phrases else allPhrases
        item {
            StaggeredEntrance(key = "phrases_list_header", index = 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory.isNotEmpty() && selectedCategory != "همه") "عبارات «$selectedCategory»" else "همه عبارات عراقی",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                    )

                    Text(
                        text = "${displayList.size} عبارت",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) GoldenAmber else DayEmerald
                    )
                }
            }
        }

        // Phrase Cards List
        items(displayList, key = { it.id }) { phrase ->
            val phraseIndex = displayList.indexOf(phrase)
            StaggeredEntrance(key = phrase.id, index = (phraseIndex + 5)) {
                PhraseCard(
                    phrase = phrase,
                    isLargeText = isLargeText,
                    isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id)),
                    isDarkTheme = isDarkTheme,
                    isGoldActivated = isGoldActivated,
                    hapticsEnabled = hapticsEnabled,
                    searchQuery = searchQuery,
                    arabicFontType = arabicFontType,
                    persianFontType = persianFontType,
                    fontWeightOffset = fontWeightOffset,
                    onPlayAudio = { onPlayAudio(phrase) },
                    onOpenFullScreen = { detailPhrase = phrase },
                    onToggleFavorite = {
                        onToggleFavorite(phrase)
                        val msg = if (!phrase.isFavorite) "به علاقه‌مندی‌ها اضافه شد ❤️" else "از علاقه‌مندی‌ها حذف شد"
                        onShowToast(msg, true)
                    },
                    onOpenVipDialog = onActivateVipClick
                )
            }
        }
    }

    // Phrase Detail Screen Modal when tapped
    detailPhrase?.let { phrase ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { detailPhrase = null },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PhraseDetailScreen(
                phrase = phrase,
                isFavorite = phrase.isFavorite,
                isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id)),
                isDarkTheme = isDarkTheme,
                arabicFontType = arabicFontType,
                persianFontType = persianFontType,
                onPlayAudio = { p, _ -> onPlayAudio(p) },
                onToggleFavorite = { p ->
                    onToggleFavorite(p)
                    val msg = if (!p.isFavorite) "به علاقه‌مندی‌ها اضافه شد ❤️" else "از علاقه‌مندی‌ها حذف شد"
                    onShowToast(msg, true)
                },
                onBackClick = { detailPhrase = null }
            )
        }
    }

    // Daily Phrase Dialog (Screen 3)
    if (showDailyPhraseDialog) {
        DailyPhraseDialog(
            allPhrases = if (allPhrases.isNotEmpty()) allPhrases else phrases,
            isDarkTheme = isDarkTheme,
            onDismiss = { showDailyPhraseDialog = false },
            onToggleFavorite = { p ->
                onToggleFavorite(p)
                onShowToast("به علاقه‌مندی‌ها اضافه شد ❤️", true)
            }
        )
    }

    // Daily Challenge Dialog (Screen 9)
    if (showDailyChallengeDialog) {
        DailyChallengeDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { showDailyChallengeDialog = false }
        )
    }

    // Learning Progress Dialog (Screen 8)
    if (showLearningProgressDialog) {
        val learned = if (allPhrases.isNotEmpty()) allPhrases.size else 600
        val favs = allPhrases.count { it.isFavorite }
        LearningProgressDialog(
            learnedCount = learned,
            favoritesCount = favs,
            streakDays = 12,
            isDarkTheme = isDarkTheme,
            onDismiss = { showLearningProgressDialog = false }
        )
    }

    // Smart Search Dialog (Screen 2)
    if (showSmartSearchDialog) {
        SmartSearchDialog(
            allPhrases = if (allPhrases.isNotEmpty()) allPhrases else phrases,
            isDarkTheme = isDarkTheme,
            onDismiss = { showSmartSearchDialog = false },
            onPhraseSelect = { p ->
                showSmartSearchDialog = false
                detailPhrase = p
            }
        )
    }

    // Audio Learning Player Dialog (Screen 5)
    audioLearningPhrase?.let { p ->
        AudioLearningDialog(
            phrase = p,
            isDarkTheme = isDarkTheme,
            onDismiss = { audioLearningPhrase = null },
            onToggleFavorite = {
                onToggleFavorite(p)
                onShowToast("علاقه‌مندی به‌روز شد", true)
            },
            onOpenNoteEditor = {
                notePhraseForEditor = p
            }
        )
    }

    // Personal Note Dialog (Screen 7)
    notePhraseForEditor?.let { p ->
        PersonalNoteDialog(
            linkedPhrase = p,
            isDarkTheme = isDarkTheme,
            onDismiss = { notePhraseForEditor = null },
            onSaveNote = { note ->
                notePhraseForEditor = null
                onShowToast("یادداشت با موفقیت ذخیره شد ✍️", false)
            }
        )
    }
}

@Composable
private fun CategoryTile(
    category: HomeGridCategory,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFFFFFF)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Modern Vector Icon container with gold glow
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFF194233) else Color(0xFFF3EFE6))
                    .border(1.dp, GoldenAmber.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (category.iconRes != null) {
                    Icon(
                        painter = painterResource(category.iconRes),
                        contentDescription = category.displayName,
                        tint = GoldenAmber,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (category.iconVector != null) {
                    Icon(
                        imageVector = category.iconVector,
                        contentDescription = category.displayName,
                        tint = GoldenAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = category.displayName,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
