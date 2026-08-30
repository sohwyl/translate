package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhraseEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSearchDialog(
    allPhrases: List<PhraseEntity>,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onPhraseSelect: (PhraseEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember {
        listOf("السلام علیکم", "ساعات", "ماء", "فندق", "کربلاء", "طریق", "مستشفی", "چای")
    }

    val filteredPhrases = remember(searchQuery, allPhrases) {
        if (searchQuery.isBlank()) {
            // Top popular recommendations
            allPhrases.take(12)
        } else {
            allPhrases.filter {
                it.arabicText.contains(searchQuery, ignoreCase = true) ||
                        it.persianTranslation.contains(searchQuery, ignoreCase = true) ||
                        it.iraqiPronunciation.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = if (isDarkTheme) NightBackground else DayBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Search Input Header matching Screen 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) NightCard else DayCard)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = GoldenAmber
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Search Field Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(if (isDarkTheme) NightCard else DayCard)
                        .border(
                            1.dp,
                            if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = GoldenAmber,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "جستجو کنید...",
                                    color = if (isDarkTheme) TextMutedDark else Color(0xFF888888),
                                    fontSize = 14.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight,
                                unfocusedTextColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            modifier = Modifier.weight(1f)
                        )

                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاک کردن",
                                    tint = TextMutedDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recent Searches (جستجوهای اخیر)
            if (searchQuery.isEmpty()) {
                Text(
                    text = "جستجوهای اخیر",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSearches.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDarkTheme) Color(0xFF0F2C20) else Color(0xFFEBE6D8))
                                .border(
                                    1.dp,
                                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { searchQuery = tag }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 12.5.sp,
                                color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "پیشنهادات محبوب",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            } else {
                Text(
                    text = "نتایج جستجو (${filteredPhrases.size} عبارت)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) GoldenAmber else GoldenAmberDark,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phrases Results List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filteredPhrases, key = { it.id }) { phrase ->
                    SearchPhraseCard(
                        phrase = phrase,
                        isDarkTheme = isDarkTheme,
                        onClick = { onPhraseSelect(phrase) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPhraseCard(
    phrase: PhraseEntity,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NightCard else DayCard
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Star Badge Icon on the left
            Icon(
                painter = painterResource(R.drawable.ic_modern_star_badge),
                contentDescription = null,
                tint = GoldenAmber,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Phrase Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = phrase.arabicText,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = phrase.persianTranslation,
                    fontSize = 12.5.sp,
                    color = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight
                )
            }
        }
    }
}
