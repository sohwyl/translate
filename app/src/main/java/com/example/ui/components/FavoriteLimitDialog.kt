package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun FavoriteLimitDialog(
    isDarkTheme: Boolean = true,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialogBg = if (isDarkTheme) DarkEmeraldSurface else Color.White
    val titleColor = if (isDarkTheme) GoldenAmber else DayEmerald
    val textColor = if (isDarkTheme) TextPrimaryDark else DayText
    val mutedColor = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
    val iconBg = if (isDarkTheme) Color(0xFF1F4D3C) else Color(0xFFEBF5F3)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = dialogBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isDarkTheme) GoldenAmber else DayEmerald)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(iconBg)
                        .border(1.dp, if (isDarkTheme) GoldenAmber else DayEmerald, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "سقف نشان‌شده‌ها",
                        tint = FavoriteRed,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "سقف نشان‌کردن ۱۰ عبارت",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "در نسخه رایگان حداکثر ۱۰ عبارت را می‌توانید نشان‌کنید.\nبرای ذخیره نامحدود عبارات و باز شدن ۱۰۰۰ عبارت کاربردی، نسخه طلایی را فعال کنید.",
                    fontSize = 13.sp,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onUpgradeClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkTheme) GoldenAmber else DayEmerald),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ارتقا به نسخه طلایی (۱۵,۰۰۰ تومان)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text(text = "متوجه شدم", fontSize = 13.sp, color = mutedColor)
                }
            }
        }
    }
}
