package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun VipBanner(
    isActivated: Boolean,
    isDarkTheme: Boolean = true,
    onActivateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) DarkEmeraldSurface else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) {
        Brush.horizontalGradient(listOf(GoldenAmber, GoldenAmberLight, GoldenAmberDark))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFEBE7DD), Color(0xFFEBE7DD)))
    }
    val crownBoxBg = if (isDarkTheme) Color(0xFF1B4031) else Color(0xFFFBF6EA)
    val crownIconTint = GoldenAmber
    val titleTextColor = if (isDarkTheme) GoldenAmber else DayText
    val descTextColor = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                1.dp,
                cardBorder,
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Breathing Eslimi Corner Ornament
            EslimiCornerBreathingOrnament(
                modifier = Modifier.align(Alignment.TopEnd),
                isDarkTheme = isDarkTheme,
                sizeDp = 64.dp,
                corner = EslimiCorner.TOP_RIGHT
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Crown Icon Container
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(crownBoxBg)
                        .border(1.dp, GoldenAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "نسخه طلایی",
                        tint = crownIconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isActivated) "نسخه طلایی (فعال است ✨)" else "نسخه طلایی مترجم",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleTextColor
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "دسترسی به ۶۰۰ عبارت ویژه، ترجمه صوتی با کیفیت بالا و آفلاین.",
                        fontSize = 11.sp,
                        color = descTextColor,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GoldenAmber, GoldenAmberLight)
                                )
                            )
                            .clickable { onActivateClick() }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isActivated) "مشاهده امکانات نسخه طلایی" else "فعالسازی نسخه طلایی",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NightBackground
                            )
                            if (!isActivated) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NightBackground)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "۱۵,۰۰۰ تومان",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldenAmber
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
