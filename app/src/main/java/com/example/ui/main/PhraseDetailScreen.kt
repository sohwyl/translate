package com.example.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PhraseEntity
import com.example.ui.components.EslimiCorner
import com.example.ui.components.EslimiCornerBreathingOrnament
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhraseDetailScreen(
    phrase: PhraseEntity,
    isFavorite: Boolean,
    isPlaying: Boolean,
    isDarkTheme: Boolean = true,
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    onPlayAudio: (PhraseEntity, Float) -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }

    // Pulsing animation for audio waves when playing
    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.35f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.6f else 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                    // Favorite Toggle Button
                    IconButton(onClick = { onToggleFavorite(phrase) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "علاقه‌مندی",
                            tint = if (isFavorite) RedHeart else (if (isDarkTheme) GoldenAmber else DayEmerald)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
                )
            )
        },
        containerColor = if (isDarkTheme) DarkEmeraldBg else LightCreamBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Ambient corner ornament
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 120.dp,
                corner = EslimiCorner.TOP_RIGHT
            )
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.BottomStart),
                isDarkTheme = isDarkTheme,
                sizeDp = 120.dp,
                corner = EslimiCorner.BOTTOM_LEFT
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Prominent Phrase Box
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDarkTheme) Color(0xFF13362A) else Color(0xFFE5EDE9))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = phrase.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) GoldenAmber else DayEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Large Voweled Arabic Text
                    Text(
                        text = phrase.iraqiPronunciation.ifEmpty { phrase.arabicText },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = getArabicFontFamily(arabicFontType),
                        color = if (isDarkTheme) TextPrimaryDark else DayText,
                        textAlign = TextAlign.Center,
                        lineHeight = 44.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Persian Translation
                    Text(
                        text = phrase.persianTranslation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = getPersianFontFamily(persianFontType),
                        color = if (isDarkTheme) GoldenAmber else DayEmerald,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 2. Central Soundwave & Glowing Audio Player
                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing wave
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .scale(waveScale2)
                            .clip(CircleShape)
                            .background(GoldenAmber.copy(alpha = if (isPlaying) 0.12f else 0.04f))
                            .border(1.dp, GoldenAmber.copy(alpha = if (isPlaying) 0.35f else 0.1f), CircleShape)
                    )

                    // Middle pulsing wave
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(waveScale1)
                            .clip(CircleShape)
                            .background(GoldenAmber.copy(alpha = if (isPlaying) 0.22f else 0.08f))
                            .border(1.dp, GoldenAmber.copy(alpha = if (isPlaying) 0.5f else 0.2f), CircleShape)
                    )

                    // Central Golden Audio Speaker Button
                    Surface(
                        onClick = { onPlayAudio(phrase, selectedSpeed) },
                        modifier = Modifier.size(86.dp),
                        shape = CircleShape,
                        color = GoldenAmber,
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "پخش صوتی",
                                tint = DarkEmeraldBg,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Speed Selector Pill (کند | عادی | تند)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDarkTheme) Color(0xFF0D281E) else Color(0xFFE8E2D5))
                        .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SpeedChip(label = "کند (0.75x)", speed = 0.75f, currentSpeed = selectedSpeed, isDarkTheme = isDarkTheme) {
                        selectedSpeed = 0.75f
                    }
                    SpeedChip(label = "عادی (1.0x)", speed = 1.0f, currentSpeed = selectedSpeed, isDarkTheme = isDarkTheme) {
                        selectedSpeed = 1.0f
                    }
                    SpeedChip(label = "تند (1.25x)", speed = 1.25f, currentSpeed = selectedSpeed, isDarkTheme = isDarkTheme) {
                        selectedSpeed = 1.25f
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Quick Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        icon = Icons.Default.ContentCopy,
                        label = "کپی متن",
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Arabic Phrase", phrase.iraqiPronunciation.ifEmpty { phrase.arabicText })
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "متن عربی کپی شد", Toast.LENGTH_SHORT).show()
                        }
                    )

                    ActionButton(
                        icon = Icons.Default.Repeat,
                        label = "تکرار صوتی",
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            onPlayAudio(phrase, 0.75f)
                        }
                    )

                    ActionButton(
                        icon = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        label = if (isFavorite) "ذخیره شد" else "علاقه‌مندی",
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            onToggleFavorite(phrase)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Usage Example Card (مثال کاربرد)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .border(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFFFFFFF)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مثال کاربردی در مسیر",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) GoldenAmber else DayEmerald
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isDarkTheme) GoldenAmber else DayEmerald,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { onPlayAudio(phrase, selectedSpeed) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Example Arabic
                        Text(
                            text = phrase.arabicText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = getArabicFontFamily(arabicFontType),
                            color = if (isDarkTheme) TextPrimaryDark else DayText,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Example Persian
                        Text(
                            text = phrase.persianTranslation,
                            fontSize = 13.5.sp,
                            fontFamily = getPersianFontFamily(persianFontType),
                            color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedChip(
    label: String,
    speed: Float,
    currentSpeed: Float,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val isSelected = speed == currentSpeed
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) GoldenAmber else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DarkEmeraldBg else (if (isDarkTheme) TextSecondaryDark else DayText)
        )
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isDarkTheme) Color(0xFF0F2E22) else Color(0xFFF3EFE6),
        border = BorderStroke(1.dp, if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)),
        modifier = Modifier.size(width = 98.dp, height = 76.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDarkTheme) GoldenAmber else DayEmerald,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) TextPrimaryDark else DayText,
                textAlign = TextAlign.Center
            )
        }
    }
}
