package com.example.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CategoryUtils
import com.example.ui.components.CategoryItem
import com.example.ui.components.StaggeredEntrance
import com.example.ui.theme.*

@Composable
fun CategoriesTab(
    categories: List<CategoryItem>,
    isDarkTheme: Boolean = true,
    onSelectCategory: (String) -> Unit
) {
    val headerTextColor = if (isDarkTheme) GoldenAmber else DayEmerald
    val cardBg = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFEBE7DD)
    val iconBoxBg = if (isDarkTheme) Color(0xFF1B4031) else Color(0xFFF4F0E6)
    val iconTint = if (isDarkTheme) GoldenAmber else DayEmerald
    val categoryTitleColor = if (isDarkTheme) TextPrimaryDark else DayText

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalCategoryCount = categories.count { it.name != "همه" }
            Column {
                Text(
                    text = "دسته‌بندی موضوعی عبارت‌ها",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerTextColor
                )
                Text(
                    text = "$totalCategoryCount دسته تخصصی مکالمات و نیازمندی‌های زائرین و موکب‌داران",
                    fontSize = 12.sp,
                    color = if (isDarkTheme) TextSecondaryDark else Color(0xFF6B7280)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(categories) { index, category ->
                val isFree = category.isFree || CategoryUtils.isCategoryFree(category.name)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1.0f, label = "cardScale")

                val drawableRes = getCategoryDrawableIcon(category.name)

                StaggeredEntrance(index = index) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(122.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                if (!isFree) (if (isDarkTheme) GoldenAmber.copy(alpha = 0.5f) else DayEmerald.copy(alpha = 0.5f)) else cardBorder,
                                RoundedCornerShape(18.dp)
                            )
                            .clickable(interactionSource = interactionSource, indication = null) {
                                onSelectCategory(category.name)
                            },
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(iconBoxBg)
                                        .border(1.dp, GoldenAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!isFree) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = GoldenAmber,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(drawableRes),
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!isFree) (if (isDarkTheme) GoldenAmber else DayEmerald) else (if (isDarkTheme) Color(0xFF13362A) else Color(0xFFEBF5F3)))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (!isFree) "🔒 طلایی" else "${category.count} عبارت",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isFree) (if (isDarkTheme) DarkEmeraldBg else Color.White) else (if (isDarkTheme) GoldenAmber else DayEmerald)
                                    )
                                }
                            }

                            Text(
                                text = category.name,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryTitleColor,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryDrawableIcon(categoryName: String): Int {
    return when {
        categoryName.contains("سلام") || categoryName.contains("احوالپرسی") || categoryName.contains("احترام") -> R.drawable.ic_modern_handshake
        categoryName.contains("زیارت") || categoryName.contains("حرم") || categoryName.contains("مذهبی") -> R.drawable.ic_modern_mosque
        categoryName.contains("موکب") || categoryName.contains("خادم") || categoryName.contains("استقبال") || categoryName.contains("اسکان") -> R.drawable.ic_modern_tea_dallah
        categoryName.contains("مسیر") || categoryName.contains("آدرس") || categoryName.contains("عمود") -> R.drawable.ic_modern_compass
        categoryName.contains("غذا") || categoryName.contains("نوشیدنی") || categoryName.contains("چای") -> R.drawable.ic_modern_tea_dallah
        categoryName.contains("تاکسی") || categoryName.contains("حمل") || categoryName.contains("سفر") -> R.drawable.ic_modern_taxi
        categoryName.contains("خرید") || categoryName.contains("بازار") || categoryName.contains("فروشنده") -> R.drawable.ic_modern_market
        categoryName.contains("درمان") || categoryName.contains("دارو") || categoryName.contains("پزشکی") -> R.drawable.ic_modern_medical
        categoryName.contains("پیاده") || categoryName.contains("اربعین") -> R.drawable.ic_modern_arbaeen_flag
        categoryName.contains("پول") || categoryName.contains("بانک") || categoryName.contains("صرافی") -> R.drawable.ic_modern_coins
        categoryName.contains("موبایل") || categoryName.contains("اینترنت") || categoryName.contains("شارژ") -> R.drawable.ic_modern_phone_wifi
        categoryName.contains("اضطراری") || categoryName.contains("شرایط") || categoryName.contains("امنیت") -> R.drawable.ic_modern_shield_alert
        else -> R.drawable.ic_modern_mosque
    }
}
