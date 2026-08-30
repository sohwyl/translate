package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryUtils
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectCategoryBottomSheet(
    categories: List<CategoryItem>,
    selectedCategories: Set<String>,
    isDarkTheme: Boolean = true,
    onDismissRequest: () -> Unit,
    onApplySelection: (Set<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var tempSelectedSet by remember(selectedCategories) { mutableStateOf(selectedCategories.toMutableSet()) }

    val sheetBg = if (isDarkTheme) DarkEmeraldBg else Color(0xFFFAFAFA)
    val cardBg = if (isDarkTheme) DarkEmeraldCard else Color(0xFFFFFFFF)
    val cardBorder = if (isDarkTheme) DarkEmeraldCardBorder else Color(0xFFE2DDD3)
    val textColor = if (isDarkTheme) TextPrimaryDark else DayText
    val subtitleColor = if (isDarkTheme) TextSecondaryDark else Color(0xFF64748B)
    val accentTint = if (isDarkTheme) GoldenAmber else DayEmerald

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isDarkTheme) Color(0xFF2A5243) else Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxHeight(0.85f)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = accentTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "انتخاب چندتایی دسته‌بندی‌ها",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        val activeCount = if (tempSelectedSet.contains("همه")) 0 else tempSelectedSet.size
                        Text(
                            text = if (activeCount == 0) "همه دسته‌بندی‌ها فعال هستند" else "$activeCount دسته‌بندی انتخاب شده است",
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = subtitleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Box inside BottomSheet
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("جستجوی دسته‌بندی...", fontSize = 13.sp, color = subtitleColor) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = accentTint) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "پاک کردن", tint = subtitleColor)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentTint,
                    unfocusedBorderColor = cardBorder,
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row: Select All & Clear All Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val allNames = categories.map { it.name }.filter { it != "همه" }.toMutableSet()
                        tempSelectedSet = allNames
                    }
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, tint = accentTint, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("انتخاب همه", fontSize = 12.sp, color = accentTint, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        tempSelectedSet = mutableSetOf("همه")
                    }
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, tint = subtitleColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پاکسازی فیلتر", fontSize = 12.sp, color = subtitleColor)
                }
            }

            Divider(color = cardBorder, thickness = 0.8.dp)

            Spacer(modifier = Modifier.height(8.dp))

            // Filtered Category List
            val filteredCategories = categories.filter { cat ->
                if (cat.name == "همه") return@filter true
                searchQuery.isEmpty() || cat.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCategories, key = { it.name }) { cat ->
                    val isChecked = if (cat.name == "همه") {
                        tempSelectedSet.contains("همه") || tempSelectedSet.isEmpty()
                    } else {
                        !tempSelectedSet.contains("همه") && tempSelectedSet.contains(cat.name)
                    }

                    val isFree = cat.isFree || CategoryUtils.isCategoryFree(cat.name)

                    val rowBg = if (isChecked) {
                        if (isDarkTheme) Color(0xFF133C2E) else Color(0xFFEBF5F3)
                    } else {
                        cardBg
                    }

                    val rowBorder = if (isChecked) accentTint else cardBorder

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(rowBg)
                            .border(1.dp, rowBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                if (cat.name == "همه") {
                                    tempSelectedSet = mutableSetOf("همه")
                                } else {
                                    val newSet = tempSelectedSet.toMutableSet()
                                    newSet.remove("همه")
                                    if (newSet.contains(cat.name)) {
                                        newSet.remove(cat.name)
                                    } else {
                                        newSet.add(cat.name)
                                    }
                                    if (newSet.isEmpty()) {
                                        newSet.add("همه")
                                    }
                                    tempSelectedSet = newSet
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (cat.name == "همه") {
                                    tempSelectedSet = mutableSetOf("همه")
                                } else {
                                    val newSet = tempSelectedSet.toMutableSet()
                                    newSet.remove("همه")
                                    if (checked) {
                                        newSet.add(cat.name)
                                    } else {
                                        newSet.remove(cat.name)
                                    }
                                    if (newSet.isEmpty()) {
                                        newSet.add("همه")
                                    }
                                    tempSelectedSet = newSet
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = accentTint,
                                uncheckedColor = subtitleColor
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        if (!isFree) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "نسخه ویژه",
                                tint = GoldenAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = cat.name,
                            fontSize = 14.sp,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )

                        // Count Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDarkTheme) Color(0xFF1B4031) else Color(0xFFE2E8F0))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${cat.count} عبارت",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) GoldenAmber else DayEmerald
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apply Button
            Button(
                onClick = {
                    onApplySelection(tempSelectedSet)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentTint,
                    contentColor = if (isDarkTheme) DarkEmeraldBg else Color.White
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                val applyCount = if (tempSelectedSet.contains("همه")) 0 else tempSelectedSet.size
                val btnLabel = if (applyCount == 0) "اعمال همه دسته‌بندی‌ها" else "اعمال فیلتر ($applyCount دسته‌بندی)"
                Text(
                    text = btnLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
