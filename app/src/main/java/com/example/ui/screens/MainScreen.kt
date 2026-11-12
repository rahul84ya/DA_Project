package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.ActiveTab
import com.example.ui.components.ALL_PLACEHOLDER_FEATURES
import com.example.ui.components.AppHeader
import com.example.ui.components.HeroSection
import com.example.ui.components.PlaceholderDetailSheet
import com.example.ui.components.ServicesOverviewSheet
import com.example.ui.theme.BackgroundNeutral
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.IndiaGreen
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextNavyDark
import com.example.ui.theme.TextNavyMedium
import com.example.ui.viewmodel.LandRecordsViewModel

@Composable
fun MainScreen(
    viewModel: LandRecordsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showServicesSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundNeutral,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppHeader(
                isOfficialMode = uiState.isOfficialMode,
                onOpenServicesMenu = { showServicesSheet = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Container width constraint for tablet / desktop responsiveness
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Single concise hero
                HeroSection()

                // Primary Tab Navigation (Indian Tricolour theme with saffron indicator)
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    val currentTabIndex = when (uiState.activeTab) {
                        ActiveTab.LAND_RECORD_SEARCH -> 0
                        ActiveTab.OWNERSHIP_CHECK -> 1
                        ActiveTab.SURVEY_MAP -> 2
                        ActiveTab.MEEBHOOMI_PORTAL -> 3
                    }

                    TabRow(
                        selectedTabIndex = currentTabIndex,
                        containerColor = Color.White,
                        contentColor = SaffronPrimary,
                        indicator = { tabPositions ->
                            if (currentTabIndex < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentTabIndex]),
                                    color = SaffronPrimary,
                                    height = 3.dp
                                )
                            }
                        },
                        divider = { HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp) }
                    ) {
                        Tab(
                            selected = uiState.activeTab == ActiveTab.LAND_RECORD_SEARCH,
                            onClick = { viewModel.switchTab(ActiveTab.LAND_RECORD_SEARCH) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = if (uiState.activeTab == ActiveTab.LAND_RECORD_SEARCH) SaffronPrimary else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Search",
                                        fontWeight = if (uiState.activeTab == ActiveTab.LAND_RECORD_SEARCH) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (uiState.activeTab == ActiveTab.LAND_RECORD_SEARCH) SaffronPrimary else TextNavyMedium
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_land_record_search")
                        )

                        Tab(
                            selected = uiState.activeTab == ActiveTab.OWNERSHIP_CHECK,
                            onClick = { viewModel.switchTab(ActiveTab.OWNERSHIP_CHECK) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (uiState.activeTab == ActiveTab.OWNERSHIP_CHECK) SaffronPrimary else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ownership",
                                        fontWeight = if (uiState.activeTab == ActiveTab.OWNERSHIP_CHECK) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (uiState.activeTab == ActiveTab.OWNERSHIP_CHECK) SaffronPrimary else TextNavyMedium
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_ownership_check")
                        )

                        Tab(
                            selected = uiState.activeTab == ActiveTab.SURVEY_MAP,
                            onClick = { viewModel.switchTab(ActiveTab.SURVEY_MAP) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = null,
                                        tint = if (uiState.activeTab == ActiveTab.SURVEY_MAP) SaffronPrimary else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Survey Map",
                                        fontWeight = if (uiState.activeTab == ActiveTab.SURVEY_MAP) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (uiState.activeTab == ActiveTab.SURVEY_MAP) SaffronPrimary else TextNavyMedium
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_survey_map")
                        )

                        Tab(
                            selected = uiState.activeTab == ActiveTab.MEEBHOOMI_PORTAL,
                            onClick = { viewModel.switchTab(ActiveTab.MEEBHOOMI_PORTAL) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = if (uiState.activeTab == ActiveTab.MEEBHOOMI_PORTAL) SaffronPrimary else TextMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Portal",
                                        fontWeight = if (uiState.activeTab == ActiveTab.MEEBHOOMI_PORTAL) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = if (uiState.activeTab == ActiveTab.MEEBHOOMI_PORTAL) SaffronPrimary else TextNavyMedium
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_meebhoomi_portal")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Tab Content
                when (uiState.activeTab) {
                    ActiveTab.LAND_RECORD_SEARCH -> {
                        LandRecordSearchScreen(
                            state = uiState,
                            onDistrictSelected = viewModel::onDistrictSelected,
                            onMandalSelected = viewModel::onMandalSelected,
                            onVillageSelected = viewModel::onVillageSelected,
                            onRecordTypeSelected = viewModel::onRecordTypeSelected,
                            onSurveyNumberChanged = viewModel::onSurveyNumberChanged,
                            onSearch = viewModel::searchLandRecord,
                            onReset = viewModel::resetFilters,
                            onShowFeedback = viewModel::showFeatureComingSoon,
                            onViewOnMap = viewModel::viewOnMapFromSearch
                        )
                    }
                    ActiveTab.OWNERSHIP_CHECK -> {
                        OwnershipCheckScreen(
                            state = uiState,
                            onDistrictSelected = viewModel::onDistrictSelected,
                            onMandalSelected = viewModel::onMandalSelected,
                            onVillageSelected = viewModel::onVillageSelected,
                            onSurveyNumberChanged = viewModel::onSurveyNumberChanged,
                            onConsentToggled = viewModel::onOwnershipConsentToggled,
                            onVerify = viewModel::verifyOwnership,
                            onReset = viewModel::resetFilters,
                            onShowFeedback = viewModel::showFeatureComingSoon
                        )
                    }
                    ActiveTab.SURVEY_MAP -> {
                        SurveyMapScreen(
                            state = uiState,
                            onDistrictSelected = viewModel::onDistrictSelected,
                            onMandalSelected = viewModel::onMandalSelected,
                            onVillageSelected = viewModel::onVillageSelected,
                            onSurveyNumberChanged = viewModel::onSurveyNumberChanged,
                            onViewOnMap = { viewModel.queryCadastralMap() },
                            onReset = viewModel::resetFilters,
                            onSelectParcelIndex = viewModel::selectParcelIndex,
                            onToggleSatellite = viewModel::toggleMapSatellite,
                            onToggleNeighbors = viewModel::toggleMapNeighbors,
                            onQuickMapSample = viewModel::selectQuickMapSample
                        )
                    }
                    ActiveTab.MEEBHOOMI_PORTAL -> MeeBhoomiPortalScreen()
                }

                // Quick Placeholders Strip (Document Vault, EC, Alerts, FMB, Help)
                PlaceholdersShortcutStrip(
                    onSelect = { feature ->
                        if (feature.tag == "fmb") {
                            viewModel.viewOnMapFromSearch()
                        } else {
                            viewModel.openPlaceholder(feature)
                        }
                    }
                )

                // Simple Clean Footer
                AppFooter()
            }
        }
    }

    // Services Overview Sheet
    if (showServicesSheet) {
        ServicesOverviewSheet(
            onSelectFeature = { feature ->
                showServicesSheet = false
                if (feature.tag == "fmb") {
                    viewModel.viewOnMapFromSearch()
                } else {
                    viewModel.openPlaceholder(feature)
                }
            },
            onDismiss = { showServicesSheet = false }
        )
    }

    // Individual Feature Placeholder Sheet
    uiState.activePlaceholder?.let { feature ->
        PlaceholderDetailSheet(
            feature = feature,
            onDismiss = viewModel::dismissPlaceholder,
            onShowToast = viewModel::showFeatureComingSoon
        )
    }
}

@Composable
fun PlaceholdersShortcutStrip(
    onSelect: (com.example.data.model.PlaceholderFeature) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Other Land Services",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextNavyDark
            )
            Text(
                text = "Planned Features",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ALL_PLACEHOLDER_FEATURES.take(3).forEach { feature ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(feature) }
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = feature.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextNavyDark,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Coming soon",
                            fontSize = 10.sp,
                            color = SaffronPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = BorderSubtle)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "dhara",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextNavyDark
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "An independent, lightweight Andhra Pradesh land records discovery tool. Demo mode uses synthetic cadastral data. For statutory legal certifications, always consult the official Revenue / Sub-Registrar departments.",
            fontSize = 11.sp,
            color = TextMuted,
            lineHeight = 15.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "v1.0-prototype",
                fontSize = 10.sp,
                color = TextMuted
            )
            Text(
                text = "•",
                fontSize = 10.sp,
                color = TextMuted
            )
            Text(
                text = "Official API Ready",
                fontSize = 10.sp,
                color = IndiaGreen,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "•",
                fontSize = 10.sp,
                color = TextMuted
            )
            Text(
                text = "No Scraping Compliance",
                fontSize = 10.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
