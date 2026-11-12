package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.model.CadastralParcel
import com.example.data.model.District
import com.example.data.model.Mandal
import com.example.data.model.Village
import com.example.ui.components.CadastralMapView
import com.example.ui.components.SearchableSelector
import com.example.ui.components.SelectableItem
import com.example.ui.theme.BorderMedium
import com.example.ui.theme.BorderSubtle
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
fun SurveyMapScreen(
    state: LandRecordsUiState,
    onDistrictSelected: (District) -> Unit,
    onMandalSelected: (Mandal) -> Unit,
    onVillageSelected: (Village) -> Unit,
    onSurveyNumberChanged: (String) -> Unit,
    onViewOnMap: () -> Unit,
    onReset: () -> Unit,
    onSelectParcelIndex: (Int) -> Unit,
    onToggleSatellite: () -> Unit,
    onToggleNeighbors: () -> Unit,
    onQuickMapSample: (districtId: String, mandalId: String, villageId: String, surveyNumber: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Quick 1-Tap Demo Samples for Real AP Cadastral GIS
        if (state.selectedDistrict == null) {
            Surface(
                color = SaffronContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
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
                            text = "Verified AP Cadastral Map Demos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap any sample to load verified government parcel boundary polygons directly on the map:",
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
                                onQuickMapSample("AP_GNT", "MND_5081", "VLG_590326", "32")
                            },
                            label = { Text("Guntur • Prathipadu (32)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White,
                                labelColor = SaffronDark
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = SaffronPrimary.copy(alpha = 0.5f)
                            )
                        )
                        SuggestionChip(
                            onClick = {
                                onQuickMapSample("AP_KRI", "MND_5030", "VLG_589642", "166")
                            },
                            label = { Text("Krishna • Jinjeru (166)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White,
                                labelColor = SaffronDark
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = SaffronPrimary.copy(alpha = 0.5f)
                            )
                        )
                        SuggestionChip(
                            onClick = {
                                onQuickMapSample("AP_KRI", "MND_5030", "VLG_589645", "10")
                            },
                            label = { Text("Krishna • Balliparru (10)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White,
                                labelColor = SaffronDark
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = SaffronPrimary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }

        // Form Card for Location Hierarchy and Survey Number
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cadastral Survey Map (సర్వే మ్యాప్)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Text(
                            text = "Official APSAC Geospatial Parcel Boundary Viewer",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    if (state.selectedDistrict != null || state.surveyNumberInput.isNotEmpty()) {
                        IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Form",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. District Selector
                SearchableSelector(
                    label = "1. District (జిల్లా) *",
                    selectedValue = state.selectedDistrict?.name,
                    placeholder = "Select Andhra Pradesh District",
                    items = state.districts.map { SelectableItem(id = it.id, title = it.name, subtitle = "AP Revenue Code: ${it.code}") },
                    isEnabled = true,
                    emptyMessage = "No districts found",
                    testTag = "map_district_selector",
                    onItemSelected = { item ->
                        state.districts.find { it.id == item.id }?.let { onDistrictSelected(it) }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Mandal Selector
                val isMandalEnabled = state.selectedDistrict != null
                SearchableSelector(
                    label = "2. Mandal / Tahsil (మండలం) *",
                    selectedValue = state.selectedMandal?.name,
                    placeholder = if (isMandalEnabled) "Select Tahsil / Mandal" else "First select District",
                    items = state.mandals.map { SelectableItem(id = it.id, title = it.name, subtitle = "Mandal Code: ${it.code}") },
                    isEnabled = isMandalEnabled,
                    disabledHint = "Please select District first",
                    emptyMessage = "No mandals found",
                    testTag = "map_mandal_selector",
                    onItemSelected = { item ->
                        state.mandals.find { it.id == item.id }?.let { onMandalSelected(it) }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Village Selector
                val isVillageEnabled = state.selectedMandal != null
                SearchableSelector(
                    label = "3. Village (గ్రామం) *",
                    selectedValue = state.selectedVillage?.name,
                    placeholder = if (isVillageEnabled) "Select Revenue Village" else "First select Mandal",
                    items = state.villages.map { SelectableItem(id = it.id, title = it.name, subtitle = "Census Code: ${it.censusCode}") },
                    isEnabled = isVillageEnabled,
                    disabledHint = "Please select Mandal first",
                    emptyMessage = "No villages found",
                    testTag = "map_village_selector",
                    onItemSelected = { item ->
                        state.villages.find { it.id == item.id }?.let { onVillageSelected(it) }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Survey Number Input
                val isSurveyEnabled = state.selectedVillage != null
                Column {
                    Text(
                        text = "4. Survey / Land Number (సర్వే నంబర్) *",
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
                                text = if (isSurveyEnabled) "e.g. 32, 166, 10, 104/1" else "Select Village to enter Survey No.",
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
                            onViewOnMap()
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
                            .testTag("map_survey_number_input")
                    )

                    if (isSurveyEnabled && state.availableSurveyNumbers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Suggested survey numbers for this village:",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.availableSurveyNumbers.take(6).forEach { survey ->
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
                if (state.mapErrorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningRed.copy(alpha = 0.08f))
                            .border(1.dp, WarningRed.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = WarningRed,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.mapErrorMessage,
                                fontSize = 12.sp,
                                color = WarningRed,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // View on Map Action Button
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onViewOnMap()
                    },
                    enabled = !state.isMapLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronPrimary,
                        disabledContainerColor = SaffronPrimary.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("view_on_map_button")
                ) {
                    if (state.isMapLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Querying Official AP GIS...", color = Color.White, fontWeight = FontWeight.SemiBold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View on Map (సర్వే మ్యాప్)",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multi-Match Selection Row (if multiple parcels match the same survey number)
        val result = state.mapQueryResult
        if (result != null && result.success && result.matchedParcels.size > 1) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Multiple Sub-divisions / Parcels Found (${result.matchedParcels.size} matches):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNavyDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        result.matchedParcels.forEachIndexed { index, parcel ->
                            FilterChip(
                                selected = state.selectedParcelIndex == index,
                                onClick = { onSelectParcelIndex(index) },
                                label = {
                                    Text(
                                        text = "Match #${index + 1}: S.No ${parcel.surveyNumber} (#${parcel.objectId})",
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaffronPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Map View Display
        if (result != null && result.success && result.matchedParcels.isNotEmpty()) {
            val selectedParcel = result.matchedParcels.getOrElse(state.selectedParcelIndex) { result.matchedParcels.first() }

            CadastralMapView(
                selectedParcel = selectedParcel,
                neighboringParcels = result.neighboringParcels,
                isSatelliteMode = state.isMapSatelliteMode,
                showNeighbors = state.showMapNeighbors,
                onToggleSatellite = onToggleSatellite,
                onToggleNeighbors = onToggleNeighbors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Parcel Attributes Card
            ParcelDetailsCard(parcel = selectedParcel)

            Spacer(modifier = Modifier.height(12.dp))

            // Official Source Attribution & Legal Notice
            AttributionNoticeCard(notice = result.attributionNotice)
        } else if (!state.isMapLoading && state.mapQueryResult == null) {
            // Initial Empty Guidance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cadastral Map Discovery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNavyDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Select District, Mandal, Village, and Survey Number above, then tap 'View on Map' to retrieve and render the official parcel boundaries.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParcelDetailsCard(
    parcel: CadastralParcel,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = IndiaGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified AP Cadastral Boundary",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndiaGreen
                    )
                }

                Surface(
                    color = IndiaGreenLight,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Official GIS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndiaGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderSubtle)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Survey No. (సర్వే నెం.)", fontSize = 11.sp, color = TextMuted)
                    Text(parcel.surveyNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextNavyDark)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Village / Mandal", fontSize = 11.sp, color = TextMuted)
                    Text("${parcel.village}, ${parcel.mandal}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextNavyDark)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Estimated Extent", fontSize = 11.sp, color = TextMuted)
                    Text(parcel.estimatedExtentText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SaffronDark)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("District", fontSize = 11.sp, color = TextMuted)
                    Text(parcel.district, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextNavyDark)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Centroid GPS Coordinates
            val centroid = parcel.centroid
            Surface(
                color = SurfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Centroid GPS:", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = String.format(java.util.Locale.US, "%.5f° N, %.5f° E", centroid.latitude, centroid.longitude),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextNavyDark
                    )
                }
            }
        }
    }
}

@Composable
fun AttributionNoticeCard(
    notice: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = SaffronPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Official Data Source & Legal Disclaimer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextNavyDark
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "• Data Provider: Andhra Pradesh Space Applications Centre (APSAC), ITE&C Dept, Govt of Andhra Pradesh.\n• Service: ArcGIS Cadastral MapServer (Layer 0: Parcel / administrative.sde.ap_parcel_boundary).\n• Statutory Notice: Cadastral spatial parcel boundaries are indexed for discovery and survey inspection. Under the AP Survey and Boundaries Act and Registration rules, statutory legal ownership must be verified through the official Tahsildar / Sub-Registrar Office.",
                fontSize = 10.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )
        }
    }
}
