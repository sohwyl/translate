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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.CategoryUtils
import com.example.data.PhraseEntity
import com.example.ui.components.EslimiCorner
import com.example.ui.components.EslimiCornerBreathingOrnament
import com.example.ui.components.PhraseCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseListScreen(
    categoryName: String,
    phrases: List<PhraseEntity>,
    isLargeText: Boolean,
    isDarkTheme: Boolean,
    isGoldActivated: Boolean,
    speakingPhraseId: Int?,
    hapticsEnabled: Boolean = true,
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    fontWeightOffset: Int = 0,
    onPlayAudio: (PhraseEntity) -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onActivateVipClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("همه") } // "همه", "علاقه‌مندی", "اخیر"
    var detailPhrase by remember { mutableStateOf<PhraseEntity?>(null) }

    val rawCategoryPhrases = remember(categoryName, phrases) {
        if (categoryName == "همه" || categoryName.startsWith("همه")) {
            phrases
        } else {
            val matched = phrases.filter { 
                it.category == categoryName ||
                it.category.contains(categoryName) || 
                categoryName.contains(it.category) ||
                (categoryName.contains("احوالپرسی") && it.category.contains("احوالپرسی")) ||
                (categoryName.contains("سلام") && it.category.contains("احوالپرسی")) ||
                (categoryName.contains("موکب") && it.category.contains("موکب")) ||
                (categoryName.contains("غذا") && it.category.contains("غذا")) ||
                (categoryName.contains("دارو") && it.category.contains("دارو")) ||
                (categoryName.contains("پزشکی") && it.category.contains("درمان")) ||
                (categoryName.contains("حمل") && it.category.contains("حمل")) ||
                (categoryName.contains("سفر") && (it.category.contains("تاکسی") || it.category.contains("مسیر"))) ||
                (categoryName.contains("اربعین") && it.category.contains("اربعین"))
            }
            if (matched.isNotEmpty()) matched else phrases
        }
    }

    val filteredPhrases = remember(rawCategoryPhrases, selectedFilter) {
        when (selectedFilter) {
            "علاقه‌مندی" -> rawCategoryPhrases.filter { it.isFavorite }
            "اخیر" -> rawCategoryPhrases.take(15)
            else -> rawCategoryPhrases
        }
    }

    val isCategoryFree = CategoryUtils.isCategoryFree(categoryName)

    val categoryHeaderImage = remember(categoryName) {
        when {
            categoryName.contains("موکب") || categoryName.contains("غذا") || categoryName.contains("پذیرایی") -> R.drawable.img_iraqi_tea_host
            categoryName.contains("اربعین") || categoryName.contains("مسیر") || categoryName.contains("تاکسی") || categoryName.contains("سفر") -> R.drawable.img_pilgrims_road
            categoryName.contains("زیارت") || categoryName.contains("حرم") || categoryName.contains("مذهبی") -> R.drawable.img_shrine_twilight
            else -> R.drawable.img_holy_shrine_dome
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = categoryName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) TextPrimaryDark else DayText
                        )
                        Text(
                            text = "${filteredPhrases.size} عبارت با تلفظ صوتی",
                            fontSize = 11.5.sp,
                            color = if (isDarkTheme) GoldenAmber else DayEmerald
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت",
                            tint = if (isDarkTheme) GoldenAmber else DayEmerald
                        )
                    }
                },
                actions = {
                    if (!isCategoryFree && !isGoldActivated) {
                        Surface(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = GoldenAmber,
                            onClick = onActivateVipClick
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = DarkEmeraldBg,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ارتقا",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkEmeraldBg
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
                )
            )
        },
        containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ambient corner ornament
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 90.dp,
                corner = EslimiCorner.TOP_RIGHT
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Category Atmospheric Mini-Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(top = 4.dp, bottom = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE5DFC9), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(categoryHeaderImage),
                            contentDescription = categoryName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
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
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = categoryName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "مکالمه روان و آسان با لهجه محلی عراقی",
                                    fontSize = 11.5.sp,
                                    color = GoldenAmber
                                )
                            }

                            Icon(
                                painter = painterResource(getCategoryDrawableIcon(categoryName)),
                                contentDescription = null,
                                tint = GoldenAmber,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Horizontal Filter Chips Row ("همه" | "علاقه‌مندی" | "اخیر")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("همه", "علاقه‌مندی", "اخیر")
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) GoldenAmber
                                    else (if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFE8E2D5))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) GoldenAmber
                                    else (if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) DarkEmeraldBg else (if (isDarkTheme) TextPrimaryDark else DayText)
                            )
                        }
                    }
                }

                // Phrase Cards List
                if (filteredPhrases.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedFilter == "علاقه‌مندی") "هنوز عبارتی در این دسته به علاقه‌مندی‌ها اضافه نشده است." else "عبارتی یافت نشد.",
                            fontSize = 13.sp,
                            color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                    ) {
                        items(filteredPhrases, key = { it.id }) { phrase ->
                            val index = filteredPhrases.indexOf(phrase)
                            StaggeredEntrance(key = phrase.id, index = index) {
                                PhraseCard(
                                    phrase = phrase,
                                    isLargeText = isLargeText,
                                    isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id)),
                                    isDarkTheme = isDarkTheme,
                                    isGoldActivated = isGoldActivated,
                                    hapticsEnabled = hapticsEnabled,
                                    arabicFontType = arabicFontType,
                                    persianFontType = persianFontType,
                                    fontWeightOffset = fontWeightOffset,
                                    onPlayAudio = { onPlayAudio(phrase) },
                                    onOpenFullScreen = { detailPhrase = phrase },
                                    onToggleFavorite = { onToggleFavorite(phrase) },
                                    onOpenVipDialog = onActivateVipClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Phrase Detail Screen
    detailPhrase?.let { phrase ->
        Dialog(
            onDismissRequest = { detailPhrase = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PhraseDetailScreen(
                phrase = phrase,
                isFavorite = phrase.isFavorite,
                isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id)),
                isDarkTheme = isDarkTheme,
                arabicFontType = arabicFontType,
                persianFontType = persianFontType,
                onPlayAudio = { p, _ -> onPlayAudio(p) },
                onToggleFavorite = { p -> onToggleFavorite(p) },
                onBackClick = { detailPhrase = null }
            )
        }
    }
}
