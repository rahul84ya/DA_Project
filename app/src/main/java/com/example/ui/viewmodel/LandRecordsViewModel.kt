package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActiveTab
import com.example.data.model.District
import com.example.data.model.LandRecordResult
import com.example.data.model.LandRecordType
import com.example.data.model.Mandal
import com.example.data.model.OwnershipResult
import com.example.data.model.PlaceholderFeature
import com.example.data.model.Village
import com.example.data.model.CadastralMapQueryResult
import com.example.data.model.CadastralParcel
import com.example.data.service.APCadastralMapService
import com.example.data.service.APLandRecordsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LandRecordsUiState(
    val activeTab: ActiveTab = ActiveTab.LAND_RECORD_SEARCH,
    val isOfficialMode: Boolean = false,
    
    // Shared / Dependent Dropdown selections
    val districts: List<District> = emptyList(),
    val mandals: List<Mandal> = emptyList(),
    val villages: List<Village> = emptyList(),
    val availableSurveyNumbers: List<String> = emptyList(),

    val selectedDistrict: District? = null,
    val selectedMandal: Mandal? = null,
    val selectedVillage: Village? = null,
    val selectedRecordType: LandRecordType = LandRecordType.ADANGAL,
    val surveyNumberInput: String = "",

    // Ownership Specific
    val ownershipConsentChecked: Boolean = false,

    // Execution States
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recordResult: LandRecordResult? = null,
    val ownershipResult: OwnershipResult? = null,

    // Cadastral Survey Map State (Real AP GIS / APSAC)
    val isMapLoading: Boolean = false,
    val mapQueryResult: CadastralMapQueryResult? = null,
    val selectedParcelIndex: Int = 0,
    val mapErrorMessage: String? = null,
    val isMapSatelliteMode: Boolean = false,
    val showMapNeighbors: Boolean = true,
    
    // Notification / Toast
    val snackbarMessage: String? = null,
    
    // Placeholder sheet state
    val activePlaceholder: PlaceholderFeature? = null
)

class LandRecordsViewModel(
    private val service: APLandRecordsService = APLandRecordsService(),
    private val locationService: com.example.data.service.APLocationService = com.example.data.service.LocalAPLocationService(),
    private val cadastralMapService: APCadastralMapService = APCadastralMapService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LandRecordsUiState())
    val uiState: StateFlow<LandRecordsUiState> = _uiState.asStateFlow()

    init {
        loadDistricts()
    }

    private fun loadDistricts() {
        val list = locationService.getDistricts()
        _uiState.update { 
            it.copy(
                districts = list,
                isOfficialMode = service.isOfficialMode
            ) 
        }
    }

    fun switchTab(tab: ActiveTab) {
        _uiState.update { 
            it.copy(
                activeTab = tab,
                errorMessage = null
            )
        }
    }

    fun onDistrictSelected(district: District) {
        val mandals = locationService.getMandals(district.id)
        _uiState.update {
            it.copy(
                selectedDistrict = district,
                mandals = mandals,
                selectedMandal = null,
                villages = emptyList(),
                selectedVillage = null,
                availableSurveyNumbers = emptyList(),
                surveyNumberInput = "",
                recordResult = null,
                ownershipResult = null,
                errorMessage = null
            )
        }
    }

    fun onMandalSelected(mandal: Mandal) {
        val villages = locationService.getVillages(mandal.id)
        _uiState.update {
            it.copy(
                selectedMandal = mandal,
                villages = villages,
                selectedVillage = null,
                availableSurveyNumbers = emptyList(),
                surveyNumberInput = "",
                recordResult = null,
                ownershipResult = null,
                errorMessage = null
            )
        }
    }

    fun onVillageSelected(village: Village) {
        val surveys = locationService.getAvailableSurveyNumbers(village.id)
        _uiState.update {
            it.copy(
                selectedVillage = village,
                availableSurveyNumbers = surveys,
                surveyNumberInput = "",
                recordResult = null,
                ownershipResult = null,
                errorMessage = null
            )
        }
    }

    fun selectQuickSample(districtId: String, mandalId: String, villageId: String, surveyNumber: String) {
        val dist = _uiState.value.districts.find { it.id == districtId } ?: locationService.getDistrictById(districtId) ?: return
        val mandals = locationService.getMandals(dist.id)
        val mnd = mandals.find { it.id == mandalId } ?: locationService.getMandalById(mandalId) ?: mandals.firstOrNull() ?: return
        val villages = locationService.getVillages(mnd.id)
        val vlg = villages.find { it.id == villageId } ?: locationService.getVillageById(villageId) ?: villages.firstOrNull() ?: return
        val surveys = locationService.getAvailableSurveyNumbers(vlg.id)
        
        _uiState.update {
            it.copy(
                selectedDistrict = dist,
                mandals = mandals,
                selectedMandal = mnd,
                villages = villages,
                selectedVillage = vlg,
                availableSurveyNumbers = surveys,
                surveyNumberInput = surveyNumber,
                recordResult = null,
                ownershipResult = null,
                errorMessage = null
            )
        }
    }

    fun onRecordTypeSelected(recordType: LandRecordType) {
        _uiState.update { it.copy(selectedRecordType = recordType) }
    }

    fun onSurveyNumberChanged(survey: String) {
        _uiState.update { it.copy(surveyNumberInput = survey, errorMessage = null) }
    }

    fun onOwnershipConsentToggled(checked: Boolean) {
        _uiState.update { it.copy(ownershipConsentChecked = checked, errorMessage = null) }
    }

    fun searchLandRecord() {
        val state = _uiState.value
        val district = state.selectedDistrict
        val mandal = state.selectedMandal
        val village = state.selectedVillage
        val survey = state.surveyNumberInput.trim()

        if (district == null) {
            _uiState.update { it.copy(errorMessage = "Please select a District first.") }
            return
        }
        if (mandal == null) {
            _uiState.update { it.copy(errorMessage = "Please select a Tahsil / Mandal.") }
            return
        }
        if (village == null) {
            _uiState.update { it.copy(errorMessage = "Please select a Village.") }
            return
        }
        if (survey.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter or select a Survey / Land Number.") }
            return
        }

        // A public portal session is the only supported route for land records.
        // Do not imitate private portal endpoints, CAPTCHA handling, or login flows.
        _uiState.update {
            it.copy(
                activeTab = ActiveTab.MEEBHOOMI_PORTAL,
                errorMessage = null,
                recordResult = null,
                snackbarMessage = "Continue on the official MeeBhoomi portal to view this record."
            )
        }
    }

    fun verifyOwnership() {
        val state = _uiState.value
        val district = state.selectedDistrict
        val mandal = state.selectedMandal
        val village = state.selectedVillage
        val survey = state.surveyNumberInput.trim()

        if (district == null) {
            _uiState.update { it.copy(errorMessage = "Please select a District.") }
            return
        }
        if (mandal == null) {
            _uiState.update { it.copy(errorMessage = "Please select a Tahsil / Mandal.") }
            return
        }
        if (village == null) {
            _uiState.update { it.copy(errorMessage = "Please select a Village.") }
            return
        }
        if (survey.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter or select a Survey / Land Number.") }
            return
        }
        if (!state.ownershipConsentChecked) {
            _uiState.update { it.copy(errorMessage = "Please confirm the authorization consent checkbox.") }
            return
        }

        _uiState.update {
            it.copy(
                activeTab = ActiveTab.MEEBHOOMI_PORTAL,
                errorMessage = null,
                ownershipResult = null,
                snackbarMessage = "Continue on the official MeeBhoomi portal to verify ownership."
            )
        }
    }

    fun resetFilters() {
        _uiState.update {
            it.copy(
                selectedDistrict = null,
                mandals = emptyList(),
                selectedMandal = null,
                villages = emptyList(),
                selectedVillage = null,
                availableSurveyNumbers = emptyList(),
                surveyNumberInput = "",
                recordResult = null,
                ownershipResult = null,
                errorMessage = null,
                mapQueryResult = null,
                mapErrorMessage = null,
                selectedParcelIndex = 0
            )
        }
    }

    fun queryCadastralMap(customSurveyNumber: String? = null) {
        val state = _uiState.value
        val district = state.selectedDistrict
        val mandal = state.selectedMandal
        val village = state.selectedVillage
        val survey = (customSurveyNumber ?: state.surveyNumberInput).trim()

        if (district == null) {
            _uiState.update { it.copy(mapErrorMessage = "Please select a District first.") }
            return
        }
        if (mandal == null) {
            _uiState.update { it.copy(mapErrorMessage = "Please select a Tahsil / Mandal.") }
            return
        }
        if (village == null) {
            _uiState.update { it.copy(mapErrorMessage = "Please select a Village.") }
            return
        }
        if (survey.isBlank()) {
            _uiState.update { it.copy(mapErrorMessage = "Please enter or select a Survey Number.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isMapLoading = true,
                    mapErrorMessage = null,
                    mapQueryResult = null,
                    selectedParcelIndex = 0
                )
            }

            val result = cadastralMapService.queryParcel(
                districtName = district.name,
                mandalName = mandal.name,
                villageName = village.name,
                surveyNumber = survey,
                includeNeighbors = state.showMapNeighbors
            )

            _uiState.update {
                it.copy(
                    isMapLoading = false,
                    mapQueryResult = result,
                    mapErrorMessage = if (!result.success) result.errorMessage else null
                )
            }
        }
    }

    fun selectParcelIndex(index: Int) {
        _uiState.update { it.copy(selectedParcelIndex = index) }
    }

    fun toggleMapSatellite() {
        _uiState.update { it.copy(isMapSatelliteMode = !it.isMapSatelliteMode) }
    }

    fun toggleMapNeighbors() {
        val newShow = !_uiState.value.showMapNeighbors
        _uiState.update { it.copy(showMapNeighbors = newShow) }
        if (_uiState.value.mapQueryResult?.success == true) {
            queryCadastralMap()
        }
    }

    fun viewOnMapFromSearch() {
        _uiState.update {
            it.copy(
                activeTab = ActiveTab.SURVEY_MAP,
                mapErrorMessage = null
            )
        }
        if (_uiState.value.selectedVillage != null && _uiState.value.surveyNumberInput.isNotBlank()) {
            queryCadastralMap()
        }
    }

    fun selectQuickMapSample(districtId: String, mandalId: String, villageId: String, surveyNumber: String) {
        selectQuickSample(districtId, mandalId, villageId, surveyNumber)
        queryCadastralMap(surveyNumber)
    }

    fun showFeatureComingSoon(featureName: String) {
        _uiState.update { it.copy(snackbarMessage = "$featureName: Coming soon in the authorized release.") }
    }

    fun openPlaceholder(feature: PlaceholderFeature) {
        _uiState.update { it.copy(activePlaceholder = feature) }
    }

    fun dismissPlaceholder() {
        _uiState.update { it.copy(activePlaceholder = null) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
