package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavTabItem(
    val id: Int,
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

@Composable
fun FloatingNavigationBar(
    selectedTab: Int,
    favoriteCount: Int,
    isDarkTheme: Boolean = true,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavTabItem(0, "خانه", Icons.Default.Home, Icons.Default.Home),
        NavTabItem(1, "دسته‌ها", Icons.Default.GridView, Icons.Default.GridView),
        NavTabItem(2, "علاقه‌مندی", Icons.Default.Favorite, Icons.Default.FavoriteBorder),
        NavTabItem(3, "تنظیمات", Icons.Default.Settings, Icons.Default.Settings)
    )

    // Animated heart scale when favorites count increases
    val favoriteScale by animateFloatAsState(
        targetValue = if (favoriteCount > 0) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "favoriteScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                if (isDarkTheme) Color(0xF012281E) else Color(0xF7FFFFFF)
            )
            .border(
                1.5.dp,
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3),
                RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = selectedTab == item.id
                val isFavoritesTab = item.id == 2

                // Colors
                val activeBgColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDarkTheme) GoldenAmber else DayEmerald
                    } else {
                        Color.Transparent
                    },
                    label = "navBg"
                )

                val activeContentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDarkTheme) DarkEmeraldBg else Color.White
                    } else if (isFavoritesTab && favoriteCount > 0) {
                        RedHeart
                    } else {
                        if (isDarkTheme) TextMutedDark else Color(0xFF8A9A93)
                    },
                    label = "navContent"
                )

                // Icon logic
                val icon = if (isFavoritesTab) {
                    if (favoriteCount > 0) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                } else {
                    item.filledIcon
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(activeBgColor)
                        .clickable { onTabSelected(item.id) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.scale(if (isFavoritesTab) favoriteScale else 1.0f),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = item.title,
                                tint = activeContentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeContentColor
                            )
                        }

                        // Favorites Count Badge
                        if (isFavoritesTab && favoriteCount > 0 && !isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(RedHeart)
                                    .padding(horizontal = 5.dp, vertical = 1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$favoriteCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
