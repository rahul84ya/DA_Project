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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.example.data.model.Mandal
import com.example.data.model.OwnershipResult
import com.example.data.model.Village
import com.example.ui.components.SearchableSelector
import com.example.ui.components.SelectableItem
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DemoBadgeBg
import com.example.ui.theme.DemoBadgeBorder
import com.example.ui.theme.DemoBadgeText
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.IndiaGreenLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMedium
import com.example.ui.theme.WarningRed
import com.example.ui.viewmodel.LandRecordsUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OwnershipCheckScreen(
    state: LandRecordsUiState,
    onDistrictSelected: (District) -> Unit,
    onMandalSelected: (Mandal) -> Unit,
    onVillageSelected: (Village) -> Unit,
    onSurveyNumberChanged: (String) -> Unit,
    onConsentToggled: (Boolean) -> Unit,
    onVerify: () -> Unit,
    onReset: () -> Unit,
    onShowFeedback: (String) -> Unit,
    onQuickSample: ((districtId: String, mandalId: String, villageId: String, surveyNumber: String) -> Unit)? = null,
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
                color = SaffronPrimary.copy(alpha = 0.08f),
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
                        text = "Tap to load a sample land parcel for ownership check:",
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
                                onConsentToggled(true)
                            },
                            label = { Text("NTR • Gunadala (104/1)", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                            border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SaffronPrimary)
                        )
                        SuggestionChip(
                            onClick = {
                                onQuickSample("AP_GNT", "MND_GNT_04", "VLG_GNT_04_2", "89/1")
                                onConsentToggled(true)
                            },
                            label = { Text("Guntur • Nowlur (89/1)", fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.White),
                            border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SaffronPrimary)
                        )
                    }
                }
            }
        }

        // Ownership Form Card
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
                // Header with Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Title & Ownership Verification",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "Verify pattadar share and titleholder reference",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    if (state.selectedDistrict != null) {
                        TextButton(
                            onClick = onReset,
                            modifier = Modifier.testTag("reset_ownership_button")
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
                    testTag = "ownership_district_selector",
                    onItemSelected = { item ->
                        val dist = state.districts.find { it.id == item.id }
                        if (dist != null) onDistrictSelected(dist)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Mandal Dropdown
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
                    testTag = "ownership_mandal_selector",
                    onItemSelected = { item ->
                        val mnd = state.mandals.find { it.id == item.id }
                        if (mnd != null) onMandalSelected(mnd)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Village Dropdown
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
                    testTag = "ownership_village_selector",
                    onItemSelected = { item ->
                        val vlg = state.villages.find { it.id == item.id }
                        if (vlg != null) onVillageSelected(vlg)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Survey Number
                val isSurveyEnabled = state.selectedVillage != null
                Column {
                    Text(
                        text = "4. Survey / Land Number (సర్వే నంబర్)",
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
                                text = if (isSurveyEnabled) "e.g. 104/1, 142/1, 204/1A" else "Select Village to enter Survey No.",
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
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            if (state.ownershipConsentChecked) onVerify()
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
                            .testTag("ownership_survey_input")
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

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                // MANDATORY Consent Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.ownershipConsentChecked) SaffronPrimary.copy(alpha = 0.05f) else SurfaceVariant)
                        .border(
                            1.dp,
                            if (state.ownershipConsentChecked) SaffronPrimary.copy(alpha = 0.4f) else BorderSubtle,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onConsentToggled(!state.ownershipConsentChecked) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = state.ownershipConsentChecked,
                        onCheckedChange = onConsentToggled,
                        colors = CheckboxDefaults.colors(
                            checkedColor = SaffronPrimary,
                            uncheckedColor = TextMuted
                        ),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                            .testTag("ownership_consent_checkbox")
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "I confirm I am authorized to view this land information.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "The official portal applies its own access and personal-data policies.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
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

                // Verify Action Button
                val canSubmit = state.ownershipConsentChecked && !state.isLoading
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onVerify()
                    },
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronPrimary,
                        disabledContainerColor = SaffronPrimary.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_ownership_button")
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
                            imageVector = Icons.Default.Security,
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ownership Results Card
        AnimatedVisibility(visible = state.ownershipResult != null) {
            state.ownershipResult?.let { ownership ->
                OwnershipResultCard(
                    ownership = ownership,
                    onDownloadSlip = { onShowFeedback("Download Title Slip: Coming soon in authorized release.") },
                    onShareRecord = { onShowFeedback("Share Link: Coming soon in authorized release.") }
                )
            }
        }

        if (state.ownershipResult == null && !state.isLoading) {
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
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Legal Verification Disclaimer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "Legal ownership must be verified through official records and revenue authorities. Ownership data shown is fictional in demo mode.",
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
fun OwnershipResultCard(
    ownership: OwnershipResult,
    onDownloadSlip: () -> Unit,
    onShareRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .testTag("ownership_result_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with reference number and share badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ref: ${ownership.recordReferenceNumber}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Text(
                        text = "Survey No. ${ownership.surveyNumber}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
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
                        text = "Share: ${ownership.sharePercentage}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndiaGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Demo Mode & Legal Notice Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DemoBadgeBg)
                    .border(1.dp, DemoBadgeBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = DemoBadgeText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Demo Data — Fictional Ownership Sample",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DemoBadgeText
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Legal ownership must be verified through official records and authorities. Official verification required before executing agreements or deeds.",
                        fontSize = 11.sp,
                        color = DemoBadgeText.copy(alpha = 0.9f),
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ownership Details
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ResultDetailRow("Owner / Pattadar Name", ownership.ownerName, isHighlight = true)
                ResultDetailRow("Relation Name", ownership.relationName)
                ResultDetailRow("Ownership Classification", ownership.ownershipType)
                ResultDetailRow("Registered Land Extent", ownership.landExtent)
                ResultDetailRow("Undivided Share", ownership.sharePercentage)
                ResultDetailRow("Record Reference No.", ownership.recordReferenceNumber)
                ResultDetailRow("Khata (Account) No.", ownership.khataNumber)
                ResultDetailRow("Registration Jurisdiction", ownership.registrationOffice)
                ResultDetailRow("Jurisdiction", "${ownership.village}, ${ownership.mandal}")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDownloadSlip,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextNavyDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = IndiaGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Slip (Demo)", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onShareRecord,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextNavyDark),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = TextNavyMedium,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontSize = 13.sp)
                }
            }
        }
    }
}
