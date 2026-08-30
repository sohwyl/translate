package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersonalNote
import com.example.data.PhraseEntity
import com.example.ui.theme.*

@Composable
fun PersonalNoteDialog(
    note: PersonalNote? = null,
    linkedPhrase: PhraseEntity? = null,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSaveNote: (PersonalNote) -> Unit
) {
    var content by remember { mutableStateOf(note?.content ?: "") }
    var isBold by remember { mutableStateOf(note?.isBold ?: false) }
    var isItalic by remember { mutableStateOf(note?.isItalic ?: false) }
    var isUnderline by remember { mutableStateOf(note?.isUnderline ?: false) }
    var selectedColor by remember { mutableStateOf(note?.colorHex ?: "#F6C543") }

    val colorOptions = remember {
        listOf("#F6C543", "#117A65", "#E74C3C", "#9B5DE5", "#3A86FF")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header matching Screen 7
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = if (note == null) "یادداشت جدید" else "ویرایش یادداشت",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
                )

                // "ذخیره" Button
                Button(
                    onClick = {
                        if (content.isNotBlank()) {
                            onSaveNote(
                                PersonalNote(
                                    id = note?.id ?: System.currentTimeMillis().toString(),
                                    title = if (content.length > 25) content.take(25) + "..." else content,
                                    content = content,
                                    linkedPhraseId = linkedPhrase?.id ?: note?.linkedPhraseId,
                                    linkedPhraseArabic = linkedPhrase?.arabicText ?: note?.linkedPhraseArabic,
                                    colorHex = selectedColor,
                                    isBold = isBold,
                                    isItalic = isItalic,
                                    isUnderline = isUnderline
                                )
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ذخیره",
                        color = DarkEmeraldBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linked phrase badge if available
            if (linkedPhrase != null || note?.linkedPhraseArabic != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDarkTheme) Color(0xFF0E2A1E) else Color(0xFFEBE6D8))
                        .border(
                            1.dp,
                            GoldenAmber.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = GoldenAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "پیوست شده به: ${linkedPhrase?.arabicText ?: note?.linkedPhraseArabic}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldenAmber
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Note Text Editor Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkTheme) NightCard else DayCard)
                    .border(
                        1.dp,
                        if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                TextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= 500) {
                            content = it
                        }
                    },
                    placeholder = {
                        Text(
                            text = "یادداشت خود را بنویسید...",
                            color = if (isDarkTheme) TextMutedDark else Color(0xFF888888),
                            fontSize = 15.sp
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
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                        lineHeight = 26.sp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toolbar: B, I, U, Color Bullets, 0/500 (Screen 7)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) NightCard else DayCard
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFD6CFC0)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Formatting icons: B, I, U
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isBold = !isBold },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isBold) GoldenAmber.copy(alpha = 0.25f) else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatBold,
                                contentDescription = "Bold",
                                tint = if (isBold) GoldenAmber else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { isItalic = !isItalic },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isItalic) GoldenAmber.copy(alpha = 0.25f) else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatItalic,
                                contentDescription = "Italic",
                                tint = if (isItalic) GoldenAmber else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { isUnderline = !isUnderline },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isUnderline) GoldenAmber.copy(alpha = 0.25f) else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatUnderlined,
                                contentDescription = "Underline",
                                tint = if (isUnderline) GoldenAmber else (if (isDarkTheme) TextSecondaryDark else TextSecondaryLight),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Color palette circles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorOptions.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = selectedColor == hex
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 22.dp else 16.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isSelected) 2.dp else 0.dp,
                                        Color.White,
                                        CircleShape
                                    )
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }

                    // Character Counter
                    Text(
                        text = "${content.length}/500",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) TextMutedDark else Color(0xFF888888)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "افزودن به عبارت" link action matching Screen 7
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "افزودن به عبارت",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = GoldenAmber
                )
            }
        }
    }
}
