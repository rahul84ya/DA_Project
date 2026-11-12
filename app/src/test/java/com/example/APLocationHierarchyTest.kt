package com.example

import com.example.data.service.LocalAPLocationService
import com.example.data.source.APAdministrativeData
import com.example.ui.viewmodel.LandRecordsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class APLocationHierarchyTest {

    private lateinit var locationService: LocalAPLocationService
    private lateinit var viewModel: LandRecordsViewModel

    @Before
    fun setUp() {
        locationService = LocalAPLocationService()
        viewModel = LandRecordsViewModel(locationService = locationService)
    }

    @Test
    fun `verify all current LGD Andhra Pradesh districts are loaded`() {
        val districts = locationService.getDistricts()
        assertEquals("LGD snapshot must have 28 districts", 28, districts.size)

        // Ensure all 26 district IDs and names are unique
        val uniqueIds = districts.map { it.id }.toSet()
        val uniqueNames = districts.map { it.name }.toSet()
        assertEquals(28, uniqueIds.size)
        assertEquals(28, uniqueNames.size)

        // Verify key districts are present
        val names = districts.map { it.name }
        assertTrue(names.contains("Guntur"))
        assertTrue(names.contains("Krishna"))
        assertTrue(names.contains("Visakhapatnam"))
        assertTrue(names.contains("NTR"))
        assertTrue(names.contains("Tirupati"))
        assertTrue(names.contains("Alluri Sitharama Raju"))
        assertTrue(names.contains("YSR Kadapa"))
    }

    @Test
    fun `verify all districts have authentic mandals and all mandals have villages`() {
        val districts = locationService.getDistricts()
        var totalMandals = 0
        var totalVillages = 0

        for (district in districts) {
            val mandals = locationService.getMandals(district.id)
            assertTrue("District ${district.name} must have mandals loaded", mandals.isNotEmpty())
            totalMandals += mandals.size

            for (mandal in mandals) {
                assertEquals("Mandal districtId must match parent district", district.id, mandal.districtId)
                val villages = locationService.getVillages(mandal.id)
                assertTrue("Mandal ${mandal.name} in ${district.name} must have villages loaded", villages.isNotEmpty())
                totalVillages += villages.size

                for (village in villages) {
                    assertEquals("Village mandalId must match parent mandal", mandal.id, village.mandalId)
                    assertEquals("Village districtId must match parent district", district.id, village.districtId)
                }
            }
        }

        assertEquals("Total mandals loaded should match dataset count", 688, totalMandals)
        assertEquals("Total villages loaded should match dataset count", 17_954, totalVillages)
    }

    @Test
    fun `verify cascading filtering by district and mandal`() {
        // District Guntur
        val gunturMandals = locationService.getMandals("AP_GNT")
        assertTrue(gunturMandals.any { it.name == "Phirangipuram" })
        assertTrue(gunturMandals.any { it.name == "Tenali" })
        assertFalse(gunturMandals.any { it.name == "Vijayawada Urban" }) // In NTR district, not Guntur

        // Mandal Phirangipuram
        val phirangipuram = gunturMandals.first { it.name == "Phirangipuram" }
        val phirangipuramVillages = locationService.getVillages(phirangipuram.id)
        assertTrue(phirangipuramVillages.any { it.name == "113 Thalluru" })
        assertFalse(phirangipuramVillages.any { it.name == "Gunadala" })
    }

    @Test
    fun `verify ViewModel cascading selection and dependency clearing`() {
        val initialState = viewModel.uiState.value
        assertEquals(28, initialState.districts.size)
        assertNull(initialState.selectedDistrict)
        assertNull(initialState.selectedMandal)
        assertNull(initialState.selectedVillage)
        assertEquals("", initialState.surveyNumberInput)

        // 1. Select District Guntur
        val guntur = initialState.districts.first { it.id == "AP_GNT" }
        viewModel.onDistrictSelected(guntur)

        val stateAfterDistrict = viewModel.uiState.value
        assertEquals("Guntur", stateAfterDistrict.selectedDistrict?.name)
        assertTrue(stateAfterDistrict.mandals.isNotEmpty())
        assertNull("Selected mandal must be null upon district selection", stateAfterDistrict.selectedMandal)
        assertTrue("Villages must be empty until mandal is selected", stateAfterDistrict.villages.isEmpty())
        assertNull("Selected village must be null upon district selection", stateAfterDistrict.selectedVillage)
        assertEquals("Survey number must be empty upon district selection", "", stateAfterDistrict.surveyNumberInput)

        // 2. Select Mandal Phirangipuram
        val phirangipuram = stateAfterDistrict.mandals.first { it.name == "Phirangipuram" }
        viewModel.onMandalSelected(phirangipuram)

        val stateAfterMandal = viewModel.uiState.value
        assertEquals("Phirangipuram", stateAfterMandal.selectedMandal?.name)
        assertTrue(stateAfterMandal.villages.isNotEmpty())
        assertNull("Selected village must be null upon mandal selection", stateAfterMandal.selectedVillage)
        assertEquals("Survey number must be empty upon mandal selection", "", stateAfterMandal.surveyNumberInput)

        // 3. Select Village 113 Thalluru
        val thalluru = stateAfterMandal.villages.first { it.name == "113 Thalluru" }
        viewModel.onVillageSelected(thalluru)

        val stateAfterVillage = viewModel.uiState.value
        assertEquals("113 Thalluru", stateAfterVillage.selectedVillage?.name)
        assertEquals("Survey number must start empty so user enters it", "", stateAfterVillage.surveyNumberInput)

        // 4. Enter Survey Number
        viewModel.onSurveyNumberChanged("89/1")
        assertEquals("89/1", viewModel.uiState.value.surveyNumberInput)

        // 5. Change District to Visakhapatnam -> Verify resets Mandal, Village, and Survey Number
        val vizag = initialState.districts.first { it.id == "AP_VSP" }
        viewModel.onDistrictSelected(vizag)

        val stateAfterDistrictChange = viewModel.uiState.value
        assertEquals("Visakhapatnam", stateAfterDistrictChange.selectedDistrict?.name)
        assertNull("Changing district MUST reset mandal", stateAfterDistrictChange.selectedMandal)
        assertTrue("Changing district MUST clear villages list", stateAfterDistrictChange.villages.isEmpty())
        assertNull("Changing district MUST reset village", stateAfterDistrictChange.selectedVillage)
        assertEquals("Changing district MUST reset survey number", "", stateAfterDistrictChange.surveyNumberInput)

        // 6. Select a Vizag mandal and then change mandal -> Verify resets Village and Survey Number
        val vizagRural = stateAfterDistrictChange.mandals.first { it.name == "Visakhapatnam (Rural)" }
        viewModel.onMandalSelected(vizagRural)
        val adavivaram = viewModel.uiState.value.villages.first { it.name == "Adavivaram" }
        viewModel.onVillageSelected(adavivaram)
        viewModel.onSurveyNumberChanged("215/2")

        assertEquals("Adavivaram", viewModel.uiState.value.selectedVillage?.name)
        assertEquals("215/2", viewModel.uiState.value.surveyNumberInput)

        // Now change mandal to Bheemunipatnam
        val bheemili = viewModel.uiState.value.mandals.first { it.name == "Bheemunipatnam" }
        viewModel.onMandalSelected(bheemili)

        val stateAfterMandalChange = viewModel.uiState.value
        assertEquals("Bheemunipatnam", stateAfterMandalChange.selectedMandal?.name)
        assertNull("Changing mandal MUST reset village", stateAfterMandalChange.selectedVillage)
        assertEquals("Changing mandal MUST reset survey number", "", stateAfterMandalChange.surveyNumberInput)

        // 7. Test Reset Filters
        viewModel.resetFilters()
        val resetState = viewModel.uiState.value
        assertNull(resetState.selectedDistrict)
        assertTrue(resetState.mandals.isEmpty())
        assertNull(resetState.selectedMandal)
        assertTrue(resetState.villages.isEmpty())
        assertNull(resetState.selectedVillage)
        assertEquals("", resetState.surveyNumberInput)
    }

    @Test
    fun `verify quick sample fills all hierarchy levels correctly`() {
        viewModel.selectQuickSample(
            districtId = "AP_NTR",
            mandalId = "MND_5011",
            villageId = "VLG_589219",
            surveyNumber = "104/1"
        )

        val state = viewModel.uiState.value
        assertEquals("NTR", state.selectedDistrict?.name)
        assertEquals("Vijayawada (Rural)", state.selectedMandal?.name)
        assertEquals("Ambapuram (Og)", state.selectedVillage?.name)
        assertEquals("104/1", state.surveyNumberInput)
        assertTrue(state.mandals.isNotEmpty())
        assertTrue(state.villages.isNotEmpty())
    }
}
