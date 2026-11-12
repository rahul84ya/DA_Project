package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.District
import com.example.data.model.LandRecordResult
import com.example.data.model.LandRecordType
import com.example.data.model.Mandal
import com.example.data.model.Village
import com.example.ui.components.ResultVerificationNotice
import com.example.ui.components.SearchableSelector
import com.example.ui.components.SelectableItem
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeText
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.IndiaGreenLight
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMedium
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.LandRecordsUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandRecordSearchScreen(
    state: LandRecordsUiState,
    onDistrictSelected: (District) -> Unit,
    onMandalSelected: (Mandal) -> Unit,
    onVillageSelected: (Village) -> Unit,
    onRecordTypeSelected: (LandRecordType) -> Unit,
    onSurveyNumberChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onReset: () -> Unit,
    onShowFeedback: (String) -> Unit,
    onQuickSample: ((districtId: String, mandalId: String, villageId: String, surveyNumber: String) -> Unit)? = null,
    onViewOnMap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Quick Demo Fill banner for instant 1-click test
        if (state.selectedDistrict == null && onQuickSample != null) {
            Surface(
                color = SaffronContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quick 1-Tap Demo Samples",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap any sample to fill District, Mandal, Village & Survey No. instantly:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                onQuickSample("AP_NTR", "MND_NTR_01", "VLG_NTR_01_2", "104/1")
                            },
                            label = { Text("NTR • Gunadala (104/1)", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                            border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SaffronPrimary)
                        )
                        SuggestionChip(
                            onClick = {
                                onQuickSample("AP_GNT", "MND_GNT_04", "VLG_GNT_04_2", "89/1")
                            },
                            label = { Text("Guntur • Nowlur (89/1)", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                            border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SaffronPrimary)
                        )
                        SuggestionChip(
                            onClick = {
                                onQuickSample("AP_VSP", "MND_VSP_02", "VLG_VSP_02_1", "215/2")
                            },
                            label = { Text("Vizag • Madhurawada (215/2)", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                            border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SaffronPrimary)
                        )
                    }
                }
            }
        }

        // Search Form Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with step helper
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Guided Location Search",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "Step 1 of 2: Select revenue jurisdiction",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    if (state.selectedDistrict != null) {
                        TextButton(
                            onClick = onReset,
                            modifier = Modifier.testTag("reset_filters_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", color = SaffronPrimary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. District Dropdown
                SearchableSelector(
                    label = "1. District (జిల్లా)",
                    selectedValue = state.selectedDistrict?.name,
                    placeholder = "Select Andhra Pradesh District",
                    items = state.districts.map {
                        SelectableItem(it.id, it.name, "District Code: ${it.code}")
                    },
                    isEnabled = true,
                    emptyMessage = "No districts found",
                    testTag = "district_selector",
                    onItemSelected = { item ->
                        val dist = state.districts.find { it.id == item.id }
                        if (dist != null) onDistrictSelected(dist)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Mandal Dropdown (Disabled until District selected)
                val isMandalEnabled = state.selectedDistrict != null
                SearchableSelector(
                    label = "2. Tahsil / Mandal (మండలం)",
                    selectedValue = state.selectedMandal?.name,
                    placeholder = if (isMandalEnabled) "Select Tahsil / Mandal" else "First choose a District above",
                    items = state.mandals.map {
                        SelectableItem(it.id, it.name, state.selectedDistrict?.name)
                    },
                    isEnabled = isMandalEnabled,
                    disabledHint = if (!isMandalEnabled) "Select District to unlock Mandals" else null,
                    emptyMessage = "No mandals found",
                    testTag = "mandal_selector",
                    onItemSelected = { item ->
                        val mnd = state.mandals.find { it.id == item.id }
                        if (mnd != null) onMandalSelected(mnd)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Village Dropdown (Disabled until Mandal selected)
                val isVillageEnabled = state.selectedMandal != null
                SearchableSelector(
                    label = "3. Village (గ్రామం)",
                    selectedValue = state.selectedVillage?.name,
                    placeholder = if (isVillageEnabled) "Select Revenue Village" else "First choose a Mandal above",
                    items = state.villages.map {
                        SelectableItem(it.id, it.name, "Census Code: ${it.censusCode}")
                    },
                    isEnabled = isVillageEnabled,
                    disabledHint = if (!isVillageEnabled) "Select Mandal to unlock Villages" else null,
                    emptyMessage = "No villages found",
                    testTag = "village_selector",
                    onItemSelected = { item ->
                        val vlg = state.villages.find { it.id == item.id }
                        if (vlg != null) onVillageSelected(vlg)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                // 4. Land Record Type Selection (Adangal, 1-B / RoR, FMB Map, E-Passbook)
                Text(
                    text = "4. Land Record Type",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextNavyDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LandRecordType.values().forEach { recordType ->
                        val isSelected = state.selectedRecordType == recordType
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SaffronPrimary else BorderSubtle,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) SaffronContainer else Color.White)
                                .clickable { onRecordTypeSelected(recordType) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSelected) SaffronPrimary else TextMuted,
                                        shape = CircleShape
                                    )
                                    .background(if (isSelected) SaffronPrimary else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = recordType.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SaffronDark else TextNavyDark
                                )
                                Text(
                                    text = recordType.description,
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Survey / Land Number (Disabled until Village is selected)
                val isSurveyEnabled = state.selectedVillage != null
                Column {
                    Text(
                        text = "5. Survey / Land Number (సర్వే నంబర్)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSurveyEnabled) TextNavyDark else TextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = state.surveyNumberInput,
                        onValueChange = onSurveyNumberChanged,
                        enabled = isSurveyEnabled,
                        placeholder = {
                            Text(
                                text = if (isSurveyEnabled) "e.g. 104/1, 142/2A, 89/1" else "Select Village to enter Survey No.",
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            if (state.surveyNumberInput.isNotEmpty() && isSurveyEnabled) {
                                IconButton(onClick = { onSurveyNumberChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            onSearch()
                        }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderMedium,
                            disabledBorderColor = BorderSubtle,
                            disabledContainerColor = SurfaceVariant,
                            focusedTextColor = TextNavyDark,
                            unfocusedTextColor = TextNavyDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("survey_number_input")
                    )

                    if (isSurveyEnabled && state.availableSurveyNumbers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Sample survey numbers for quick test:",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.availableSurveyNumbers.take(5).forEach { survey ->
                                SuggestionChip(
                                    onClick = { onSurveyNumberChanged(survey) },
                                    label = { Text(survey, fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (state.surveyNumberInput == survey) SaffronPrimary.copy(alpha = 0.15f) else Color.White
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        enabled = true,
                                        borderColor = if (state.surveyNumberInput == survey) SaffronPrimary else BorderSubtle
                                    )
                                )
                            }
                        }
                    }
                }

                // Error Message Display
                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningRed.copy(alpha = 0.08f))
                            .border(1.dp, WarningRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = WarningRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.errorMessage,
                                fontSize = 12.sp,
                                color = WarningRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Official portal action. No records are retrieved by Dhara itself.
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onSearch()
                    },
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronPrimary,
                        disabledContainerColor = SaffronPrimary.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("search_record_button")
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Opening Official Portal...", color = Color.White, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Official MeeBhoomi Portal",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // View Survey on Map Shortcut Button
                OutlinedButton(
                    onClick = {
                        keyboardController?.hide()
                        onViewOnMap()
                    },
                    enabled = isSurveyEnabled && state.surveyNumberInput.isNotBlank(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SaffronPrimary,
                        disabledContentColor = TextMuted
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSurveyEnabled && state.surveyNumberInput.isNotBlank()) SaffronPrimary else BorderSubtle
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("search_screen_view_on_map_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Survey on Map (మ్యాప్ లో చూడండి)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Card Section
        AnimatedVisibility(visible = state.recordResult != null) {
            state.recordResult?.let { record ->
                LandRecordResultCard(
                    record = record,
                    onViewMap = onViewOnMap,
                    onViewRecord = { onShowFeedback("View Record Document: Coming soon in authorized release.") },
                    onDownloadPdf = { onShowFeedback("Download PDF Record: Coming soon in authorized release.") },
                    onReportDiscrepancy = { onShowFeedback("Report Discrepancy: Coming soon in authorized release.") }
                )
            }
        }

        if (state.recordResult == null && !state.isLoading) {
            // Helpful guidance note
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Search Instructions",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "Select your District, Mandal, and Village above to unlock the survey number search for Adangal and 1-B registers.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LandRecordResultCard(
    record: LandRecordResult,
    onViewMap: () -> Unit = {},
    onViewRecord: () -> Unit,
    onDownloadPdf: () -> Unit,
    onReportDiscrepancy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .testTag("land_record_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with survey number badge and record type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Survey No: ${record.surveyNumber}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                    Text(
                        text = "${record.recordType.displayName} Extract",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SaffronPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IndiaGreenLight)
                        .border(1.dp, IndiaGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = record.extent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndiaGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mandatory Notice Banner
            ResultVerificationNotice(isOfficialApi = record.isOfficialApi)

            Spacer(modifier = Modifier.height(14.dp))

            // Information Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultDetailRow("District", record.district)
                ResultDetailRow("Tahsil / Mandal", record.mandal)
                ResultDetailRow("Village", record.village)
                ResultDetailRow("Khata (Account) No.", record.khataNumber)
                ResultDetailRow("Pattadar / Holder Name", record.pattadarName, isHighlight = true)
                ResultDetailRow("Land Classification", record.landClassification)
                ResultDetailRow("Crop / Land Use", record.cropOrLandUse)
                ResultDetailRow("Soil & Water Source", "${record.soilType} • ${record.waterSource}")
                ResultDetailRow("Last Updated Date", record.lastUpdatedDate)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary View Cadastral Map Button
            Button(
                onClick = onViewMap,
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("action_result_view_on_map")
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View on Survey Map (సర్వే మ్యాప్)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Text(
                text = "Record Actions",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextNavyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewRecord,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextNavyDark),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_view_record")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onDownloadPdf,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextNavyDark),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_download_pdf")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onReportDiscrepancy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("action_report_discrepancy")
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Discrepancy", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ResultDetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isHighlight) SaffronPrimary.copy(alpha = 0.05f) else SurfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            color = if (isHighlight) TextNavyDark else TextNavyDark,
            modifier = Modifier.weight(1.3f)
        )
    }
}
