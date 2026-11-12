package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMedium

data class SelectableItem(
    val id: String,
    val title: String,
    val subtitle: String? = null
)

@Composable
fun SearchableSelector(
    label: String,
    selectedValue: String?,
    placeholder: String,
    items: List<SelectableItem>,
    isEnabled: Boolean,
    disabledHint: String? = null,
    emptyMessage: String = "No items found",
    testTag: String,
    onItemSelected: (SelectableItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val isEffectivelyInteractive = isEnabled && items.isNotEmpty()
    val effectivePlaceholder = when {
        isEnabled && items.isEmpty() -> emptyMessage
        else -> placeholder
    }
    val effectiveHint = when {
        isEnabled && items.isEmpty() -> emptyMessage
        !isEnabled -> disabledHint
        else -> null
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isEnabled) TextNavyDark else TextMuted
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = if (isEffectivelyInteractive) BorderMedium else BorderSubtle,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(if (isEffectivelyInteractive) Color.White else SurfaceVariant)
                .clickable(enabled = isEffectivelyInteractive) {
                    showDialog = true
                }
                .padding(horizontal = 14.dp, vertical = 13.dp)
                .testTag(testTag),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue ?: effectivePlaceholder,
                    fontSize = 15.sp,
                    color = when {
                        !isEnabled || items.isEmpty() -> TextMuted
                        selectedValue != null -> TextNavyDark
                        else -> TextMuted
                    },
                    fontWeight = if (selectedValue != null) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open $label selector",
                    tint = if (isEffectivelyInteractive) TextNavyMedium else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (effectiveHint != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = effectiveHint,
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }

    if (showDialog) {
        SelectorDialog(
            title = "Select $label",
            items = items,
            emptyMessage = emptyMessage,
            selectedId = items.find { it.title == selectedValue }?.id,
            onDismiss = { showDialog = false },
            onSelected = { item ->
                showDialog = false
                onItemSelected(item)
            }
        )
    }
}

@Composable
private fun SelectorDialog(
    title: String,
    items: List<SelectableItem>,
    emptyMessage: String,
    selectedId: String?,
    onDismiss: () -> Unit,
    onSelected: (SelectableItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) {
            items
        } else {
            items.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        (it.subtitle?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextNavyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Type to search...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedTextColor = TextNavyDark,
                        unfocusedTextColor = TextNavyDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("selector_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(color = BorderSubtle, thickness = 1.dp)

                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (items.isEmpty()) emptyMessage else "No matching items found",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = item.id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelected(item) }
                                    .background(if (isSelected) SaffronPrimary.copy(alpha = 0.08f) else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) SaffronPrimary else TextNavyDark
                                    )
                                    if (item.subtitle != null) {
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 12.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f), thickness = 0.5.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredItems.size} options",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextNavyMedium)
                    }
                }
            }
        }
    }
}
