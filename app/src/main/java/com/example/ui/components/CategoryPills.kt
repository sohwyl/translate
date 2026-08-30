package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryUtils
import com.example.ui.theme.*

data class CategoryItem(
    val name: String,
    val count: Int,
    val isFree: Boolean = true
)

private fun getCategoryIcon(name: String): ImageVector {
    return when {
        name.contains("سلامت") || name.contains("درمان") || name.contains("اورژانس") || name.contains("Health") -> Icons.Default.MedicalServices
        name.contains("آدرس") || name.contains("مسیر") || name.contains("عمود") || name.contains("Directions") -> Icons.Default.Navigation
        name.contains("خرید") || name.contains("صرافی") || name.contains("سیم‌کارت") || name.contains("Shopping") -> Icons.Default.ShoppingCart
        name.contains("غذا") || name.contains("آشامیدنی") || name.contains("موکب") || name.contains("Food") -> Icons.Default.Restaurant
        name.contains("سلام") || name.contains("تعارفات") -> Icons.Default.EmojiEmotions
        name.contains("حمل‌ونقل") || name.contains("کرایه") -> Icons.Default.DirectionsBus
        name.contains("اماکن") || name.contains("زیارتی") -> Icons.Default.Place
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryPills(
    categories: List<CategoryItem>,
    selectedCategory: String,
    selectedCategories: Set<String> = emptySet(),
    isDarkTheme: Boolean = true,
    onCategorySelected: (String) -> Unit,
    onOpenMultiSelectModal: () -> Unit = {},
    onDeselectCategory: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeMultiSelectCount = if (selectedCategories.contains("همه") || selectedCategories.isEmpty()) 0 else selectedCategories.size

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        // Multi-Select Filter Button at start
        item {
            val isMultiActive = activeMultiSelectCount > 0
            val multiBg = if (isMultiActive) {
                if (isDarkTheme) GoldenAmber else DayEmerald
            } else {
                if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
            }
            val multiBorder = if (isMultiActive) {
                if (isDarkTheme) GoldenAmberLight else DayEmerald
            } else {
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3)
            }
            val multiTextColor = if (isMultiActive) {
                if (isDarkTheme) DarkEmeraldBg else Color.White
            } else {
                if (isDarkTheme) GoldenAmber else DayEmerald
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(multiBg)
                    .border(1.dp, multiBorder, RoundedCornerShape(24.dp))
                    .clickable { onOpenMultiSelectModal() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "انتخاب چندتایی دسته‌بندی‌ها",
                        tint = multiTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMultiActive) "فیلتر ($activeMultiSelectCount)" else "چند انتخابی",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = multiTextColor
                    )
                }
            }
        }

        items(categories) { cat ->
            val isSelected = if (selectedCategories.isNotEmpty() && !selectedCategories.contains("همه")) {
                selectedCategories.contains(cat.name)
            } else {
                selectedCategory == cat.name || (selectedCategory.startsWith("همه") && cat.name.startsWith("همه"))
            }

            val isFree = cat.isFree || CategoryUtils.isCategoryFree(cat.name)
            val topicIcon = getCategoryIcon(cat.name)

            val pillBg = if (isSelected) {
                if (isDarkTheme) GoldenAmber else DayAccent
            } else {
                if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
            }

            val pillBorder = if (isSelected) {
                if (isDarkTheme) GoldenAmberLight else DayAccent
            } else {
                if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3)
            }

            val textColor = if (isSelected) {
                if (isDarkTheme) DarkEmeraldBg else Color.White
            } else {
                if (isDarkTheme) TextPrimaryDark else DayText
            }

            val iconColor = if (isSelected) {
                if (isDarkTheme) DarkEmeraldBg else Color.White
            } else {
                if (isDarkTheme) GoldenAmber else DayEmerald
            }

            val badgeBg = if (isSelected) {
                if (isDarkTheme) Color(0x33000000) else Color(0x33FFFFFF)
            } else {
                if (isDarkTheme) Color(0xFF1B4031) else Color(0xFFEBF5F3)
            }

            val badgeTextColor = if (isSelected) {
                if (isDarkTheme) DarkEmeraldBg else Color.White
            } else {
                if (isDarkTheme) GoldenAmber else DayEmerald
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(pillBg)
                    .border(
                        1.dp,
                        pillBorder,
                        RoundedCornerShape(24.dp)
                    )
                    .clickable { onCategorySelected(cat.name) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isFree) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "نسخه ویژه",
                            tint = if (isSelected) DarkEmeraldBg else GoldenAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    } else {
                        Icon(
                            imageVector = topicIcon,
                            contentDescription = cat.name,
                            tint = iconColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Text(
                        text = cat.name,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(badgeBg)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${cat.count}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    }

                    if (isSelected && activeMultiSelectCount > 1 && cat.name != "همه") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "حذف فیلتر",
                            tint = if (isDarkTheme) DarkEmeraldBg else Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onDeselectCategory(cat.name) }
                        )
                    }
                }
            }
        }
    }
}
