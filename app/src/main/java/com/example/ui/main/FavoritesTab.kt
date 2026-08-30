package com.example.ui.main

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.NotesManager
import com.example.data.PersonalNote
import com.example.data.PhraseEntity
import com.example.ui.components.FullScreenPhraseDialog
import com.example.ui.components.PersonalNoteDialog
import com.example.ui.components.PhraseCard
import com.example.ui.theme.*

@Composable
fun FavoritesTab(
    favoritePhrases: List<PhraseEntity>,
    isLargeText: Boolean,
    isDarkTheme: Boolean = true,
    isGoldActivated: Boolean = false,
    speakingPhraseId: Int?,
    hapticsEnabled: Boolean = true,
    searchQuery: String = "",
    arabicFontType: String = "امیری",
    persianFontType: String = "وزیرمتن",
    fontWeightOffset: Int = 0,
    onPlayAudio: (PhraseEntity) -> Unit,
    onToggleFavorite: (PhraseEntity) -> Unit,
    onActivateVipClick: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val context = LocalContext.current
    val notesManager = remember { NotesManager.getInstance(context) }
    val userNotes by notesManager.notesFlow.collectAsState()

    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: همه, 1: عبارات, 2: یادداشت‌ها
    var fullScreenPhrase by remember { mutableStateOf<PhraseEntity?>(null) }
    var editingNote by remember { mutableStateOf<PersonalNote?>(null) }
    var showNewNoteDialog by remember { mutableStateOf(false) }

    val filterTitles = listOf("همه", "عبارات", "یادداشت‌ها")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        // Header Row matching Screen 6
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "علاقه‌مندی‌ها",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) TextPrimaryDark else DayText
            )

            // Add Note Button
            IconButton(
                onClick = { showNewNoteDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GoldenAmber)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "یادداشت جدید",
                    tint = DarkEmeraldBg,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Segmented Tabs Bar: "همه" | "عبارات" | "یادداشت‌ها" (Screen 6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(23.dp))
                .background(if (isDarkTheme) NightCard else DayCard)
                .border(
                    1.dp,
                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                    RoundedCornerShape(23.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            filterTitles.forEachIndexed { index, title ->
                val isSelected = selectedFilterIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) (if (isDarkTheme) Color(0xFF133E2B) else Color(0xFFE5DECC))
                            else Color.Transparent
                        )
                        .clickable { selectedFilterIndex = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) GoldenAmber else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content based on selected tab
        when (selectedFilterIndex) {
            0 -> {
                // "همه" Tab
                if (favoritePhrases.isEmpty() && userNotes.isEmpty()) {
                    EmptyFavoritesView(isDarkTheme, onNavigateToHome)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (userNotes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "یادداشت‌های من (${userNotes.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                            items(userNotes, key = { it.id }) { note ->
                                NoteCardItem(
                                    note = note,
                                    isDarkTheme = isDarkTheme,
                                    onEdit = { editingNote = note },
                                    onDelete = { notesManager.deleteNote(note.id) }
                                )
                            }
                        }

                        if (favoritePhrases.isNotEmpty()) {
                            item {
                                Text(
                                    text = "عبارات نشان‌شده (${favoritePhrases.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenAmber,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )
                            }
                            items(favoritePhrases, key = { it.id }) { phrase ->
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
                                    onOpenFullScreen = { fullScreenPhrase = phrase },
                                    onToggleFavorite = { onToggleFavorite(phrase) },
                                    onOpenVipDialog = onActivateVipClick
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // "عبارات" Tab
                if (favoritePhrases.isEmpty()) {
                    EmptyFavoritesView(isDarkTheme, onNavigateToHome)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(favoritePhrases, key = { it.id }) { phrase ->
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
                                onOpenFullScreen = { fullScreenPhrase = phrase },
                                onToggleFavorite = { onToggleFavorite(phrase) },
                                onOpenVipDialog = onActivateVipClick
                            )
                        }
                    }
                }
            }

            2 -> {
                // "یادداشت‌ها" Tab
                if (userNotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_modern_note_edit),
                                contentDescription = null,
                                tint = GoldenAmber,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "هنوز یادداشتی ثبت نکرده‌اید",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) TextPrimaryDark else DayText
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showNewNoteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber)
                            ) {
                                Text("نوشتن یادداشت جدید", color = DarkEmeraldBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(userNotes, key = { it.id }) { note ->
                            NoteCardItem(
                                note = note,
                                isDarkTheme = isDarkTheme,
                                onEdit = { editingNote = note },
                                onDelete = { notesManager.deleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    fullScreenPhrase?.let { phrase ->
        FullScreenPhraseDialog(
            phrase = phrase,
            isPlaying = speakingPhraseId != null && (speakingPhraseId == phrase.id || (phrase.numeric_id > 0 && speakingPhraseId == phrase.numeric_id)),
            arabicFontType = arabicFontType,
            persianFontType = persianFontType,
            onPlayAudio = { onPlayAudio(phrase) },
            onDismiss = { fullScreenPhrase = null }
        )
    }

    if (showNewNoteDialog) {
        PersonalNoteDialog(
            isDarkTheme = isDarkTheme,
            onDismiss = { showNewNoteDialog = false },
            onSaveNote = { notesManager.saveNote(it) }
        )
    }

    editingNote?.let { noteToEdit ->
        PersonalNoteDialog(
            note = noteToEdit,
            isDarkTheme = isDarkTheme,
            onDismiss = { editingNote = null },
            onSaveNote = { notesManager.saveNote(it) }
        )
    }
}

@Composable
private fun NoteCardItem(
    note: PersonalNote,
    isDarkTheme: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.dp,
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                RoundedCornerShape(18.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) NightCard else DayCard
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.linkedPhraseArabic != null) {
                    Text(
                        text = "پیوست: ${note.linkedPhraseArabic}",
                        fontSize = 12.sp,
                        color = GoldenAmber,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "ویرایش",
                            tint = GoldenAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = FavoriteRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content,
                fontSize = 14.5.sp,
                color = if (isDarkTheme) TextPrimaryDark else DayText,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun EmptyFavoritesView(
    isDarkTheme: Boolean,
    onNavigateToHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFEBE7DD),
                    RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF0A2218) else Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArbaeenRoadIllustration(
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "هنوز عبارتی نشان نکرده‌اید",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else DayText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "در مسیر پیاده‌روی، عبارت‌های پرکاربردی که به قلبتان نزدیک است را با دکمه قلب نشان کنید تا اینجا در دسترستان باشند.",
                    fontSize = 13.sp,
                    color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onNavigateToHome,
                    modifier = Modifier
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = DarkEmeraldBg,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دیدن عبارات",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkEmeraldBg
                    )
                }
            }
        }
    }
}


@Composable
fun ArbaeenRoadIllustration(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val skyBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF071F15), Color(0xFF0F3628))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF1DC), Color(0xFFE3F1EC))
        )
    }

    val roadColor = if (isDarkTheme) Color(0xFF162B22) else Color(0xFFB9CFC6)
    val horizonColor = GoldenAmber
    val moonBgColor = if (isDarkTheme) Color(0xFF071F15) else Color(0xFFFFF1DC)

    Box(
        modifier = modifier.background(skyBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val horizonY = h * 0.55f

            if (isDarkTheme) {
                // Night Sky: Crescent Moon
                drawCircle(
                    color = GoldenAmber.copy(alpha = 0.95f),
                    radius = 24.dp.toPx(),
                    center = Offset(w * 0.8f, h * 0.22f)
                )
                drawCircle(
                    color = moonBgColor,
                    radius = 20.dp.toPx(),
                    center = Offset(w * 0.76f, h * 0.20f)
                )

                // Star details
                drawCircle(Color.White.copy(alpha = 0.9f), 2.dp.toPx(), Offset(w * 0.15f, h * 0.25f))
                drawCircle(Color.White.copy(alpha = 0.6f), 1.5.dp.toPx(), Offset(w * 0.35f, h * 0.15f))
                drawCircle(GoldenAmber.copy(alpha = 0.8f), 3.dp.toPx(), Offset(w * 0.9f, h * 0.12f))
                drawCircle(Color.White.copy(alpha = 0.7f), 2.dp.toPx(), Offset(w * 0.55f, h * 0.28f))
                drawCircle(Color.White.copy(alpha = 0.5f), 1.5.dp.toPx(), Offset(w * 0.7f, h * 0.1f))
            } else {
                // Day Sky: Beautiful Rising Golden Sun
                drawCircle(
                    color = GoldenAmber.copy(alpha = 0.4f),
                    radius = 45.dp.toPx(),
                    center = Offset(w * 0.8f, h * 0.25f)
                )
                drawCircle(
                    color = GoldenAmber,
                    radius = 20.dp.toPx(),
                    center = Offset(w * 0.8f, h * 0.25f)
                )

                // Sunbeams
                val sunCenter = Offset(w * 0.8f, h * 0.25f)
                for (i in 0 until 8) {
                    val angle = (i * Math.PI / 4).toFloat()
                    val startDist = 24.dp.toPx()
                    val endDist = 38.dp.toPx()
                    drawLine(
                        color = GoldenAmber.copy(alpha = 0.7f),
                        start = Offset(
                            sunCenter.x + Math.cos(angle.toDouble()).toFloat() * startDist,
                            sunCenter.y + Math.sin(angle.toDouble()).toFloat() * startDist
                        ),
                        end = Offset(
                            sunCenter.x + Math.cos(angle.toDouble()).toFloat() * endDist,
                            sunCenter.y + Math.sin(angle.toDouble()).toFloat() * endDist
                        ),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Cloud Silhouettes
                drawCircle(Color.White.copy(alpha = 0.6f), 28.dp.toPx(), Offset(w * 0.18f, h * 0.32f))
                drawCircle(Color.White.copy(alpha = 0.6f), 35.dp.toPx(), Offset(w * 0.25f, h * 0.35f))
                drawCircle(Color.White.copy(alpha = 0.6f), 24.dp.toPx(), Offset(w * 0.32f, h * 0.34f))
            }

            // Beautiful Holy Shrine Dome Silhouette at Horizon Center
            val shrineColor = if (isDarkTheme) Color(0xFF0A2218) else Color(0xFF8BA599)
            
            // Draw Dome
            val domePath = Path().apply {
                moveTo(w * 0.46f, horizonY)
                cubicTo(
                    w * 0.46f, horizonY - 18.dp.toPx(),
                    w * 0.54f, horizonY - 18.dp.toPx(),
                    w * 0.54f, horizonY
                )
                close()
            }
            drawPath(domePath, shrineColor)
            
            // Dome spire
            drawLine(
                color = GoldenAmber,
                start = Offset(w * 0.5f, horizonY - 18.dp.toPx()),
                end = Offset(w * 0.5f, horizonY - 24.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )

            // Draw Left Minaret
            val leftMinaret = Path().apply {
                moveTo(w * 0.42f, horizonY)
                lineTo(w * 0.42f, horizonY - 22.dp.toPx())
                lineTo(w * 0.435f, horizonY - 24.dp.toPx())
                lineTo(w * 0.435f, horizonY)
                close()
            }
            drawPath(leftMinaret, shrineColor)

            // Draw Right Minaret
            val rightMinaret = Path().apply {
                moveTo(w * 0.565f, horizonY)
                lineTo(w * 0.565f, horizonY - 24.dp.toPx())
                lineTo(w * 0.58f, horizonY - 22.dp.toPx())
                lineTo(w * 0.58f, horizonY)
                close()
            }
            drawPath(rightMinaret, shrineColor)

            // Golden Glow behind Dome
            drawCircle(
                color = GoldenAmber.copy(alpha = 0.3f),
                radius = 16.dp.toPx(),
                center = Offset(w * 0.5f, horizonY - 10.dp.toPx())
            )

            // Perspective Road
            val roadPath = Path().apply {
                moveTo(w * 0.48f, horizonY)
                lineTo(w * 0.52f, horizonY)
                lineTo(w * 0.88f, h)
                lineTo(w * 0.12f, h)
                close()
            }
            drawPath(roadPath, roadColor)

            // Dotted center line of road going into horizon
            val steps = 8
            for (i in 0 until steps) {
                val ratioStart = i.toFloat() / steps
                val ratioEnd = (i + 0.6f) / steps
                val yStart = horizonY + (h - horizonY) * ratioStart
                val yEnd = horizonY + (h - horizonY) * ratioEnd
                
                // perspective narrowing of line
                val lineWidth = (1.dp.toPx() + (5.dp.toPx() - 1.dp.toPx()) * ratioStart)
                
                drawLine(
                    color = GoldenAmber.copy(alpha = 0.7f),
                    start = Offset(w * 0.5f, yStart),
                    end = Offset(w * 0.5f, yEnd),
                    strokeWidth = lineWidth
                )
            }

            // Silhouettes of Pilgrims (Zaer) walking along the road
            val pilgrimColor = if (isDarkTheme) Color(0xFF03100B) else Color(0xFF435C51)
            
            // Pilgrim 1 (Left side, larger, closer)
            drawCircle(pilgrimColor, 5.dp.toPx(), Offset(w * 0.38f, h * 0.82f)) // head
            drawPath( // body
                Path().apply {
                    moveTo(w * 0.35f, h * 0.95f)
                    lineTo(w * 0.41f, h * 0.95f)
                    lineTo(w * 0.4f, h * 0.83f)
                    lineTo(w * 0.36f, h * 0.83f)
                    close()
                },
                pilgrimColor
            )
            // Pilgrim Flag 1
            drawLine(
                color = Color.Black,
                start = Offset(w * 0.35f, h * 0.92f),
                end = Offset(w * 0.35f, h * 0.74f),
                strokeWidth = 1.5.dp.toPx()
            )
            drawPath( // Flag banner
                Path().apply {
                    moveTo(w * 0.35f, h * 0.74f)
                    lineTo(w * 0.28f, h * 0.76f)
                    lineTo(w * 0.35f, h * 0.79f)
                    close()
                },
                FavoriteRed
            )

            // Pilgrim 2 (Right side, slightly further away)
            drawCircle(pilgrimColor, 4.dp.toPx(), Offset(w * 0.60f, h * 0.74f)) // head
            drawPath( // body
                Path().apply {
                    moveTo(w * 0.57f, h * 0.85f)
                    lineTo(w * 0.62f, h * 0.85f)
                    lineTo(w * 0.61f, h * 0.75f)
                    lineTo(w * 0.58f, h * 0.75f)
                    close()
                },
                pilgrimColor
            )

            // Pilgrim 3 (Center road, small, far away)
            drawCircle(pilgrimColor, 2.5.dp.toPx(), Offset(w * 0.52f, h * 0.64f)) // head
            drawPath( // body
                Path().apply {
                    moveTo(w * 0.51f, h * 0.70f)
                    lineTo(w * 0.53f, h * 0.70f)
                    lineTo(w * 0.53f, h * 0.65f)
                    lineTo(w * 0.51f, h * 0.65f)
                    close()
                },
                pilgrimColor
            )

            // Glowing Heart above the shrine (Beacon of love)
            val heartCenter = Offset(w * 0.5f, horizonY - 32.dp.toPx())
            val sizeHeart = 6.dp.toPx()
            drawPath(
                Path().apply {
                    moveTo(heartCenter.x, heartCenter.y - sizeHeart * 0.3f)
                    cubicTo(
                        heartCenter.x - sizeHeart * 0.5f, heartCenter.y - sizeHeart * 1.0f,
                        heartCenter.x - sizeHeart * 1.1f, heartCenter.y - sizeHeart * 0.4f,
                        heartCenter.x, heartCenter.y + sizeHeart * 0.8f
                    )
                    cubicTo(
                        heartCenter.x + sizeHeart * 1.1f, heartCenter.y - sizeHeart * 0.4f,
                        heartCenter.x + sizeHeart * 0.5f, heartCenter.y - sizeHeart * 1.0f,
                        heartCenter.x, heartCenter.y - sizeHeart * 0.3f
                    )
                    close()
                },
                FavoriteRed
            )
            // Heart outer glow
            drawCircle(
                color = FavoriteRed.copy(alpha = 0.3f),
                radius = 14.dp.toPx(),
                center = heartCenter
            )
        }
    }
}
