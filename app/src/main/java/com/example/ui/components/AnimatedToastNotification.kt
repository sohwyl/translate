package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class ToastData(
    val message: String,
    val isFavoriteAction: Boolean = false,
    val isSuccess: Boolean = true
)

@Composable
fun AnimatedToastNotification(
    toastData: ToastData?,
    isDarkTheme: Boolean = true,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(toastData) {
        if (toastData != null) {
            delay(2400)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = toastData != null,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
        modifier = modifier
    ) {
        toastData?.let { data ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isDarkTheme) Color(0xFF132A20) else Color(0xFFFFFFFF)
                    )
                    .border(
                        1.5.dp,
                        if (data.isFavoriteAction) RedHeart else (if (isDarkTheme) GoldenAmber else DayEmerald),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (data.isFavoriteAction) RedHeart.copy(alpha = 0.2f)
                                else if (isDarkTheme) GoldenAmber.copy(alpha = 0.2f)
                                else DayEmerald.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (data.isFavoriteAction) Icons.Default.Favorite else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (data.isFavoriteAction) RedHeart else (if (isDarkTheme) GoldenAmber else DayEmerald),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = data.message,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) TextPrimaryDark else DayText
                    )
                }
            }
        }
    }
}
