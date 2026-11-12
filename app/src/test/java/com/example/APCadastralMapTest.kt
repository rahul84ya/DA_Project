package com.example

import com.example.data.model.ActiveTab
import com.example.data.model.District
import com.example.data.model.Mandal
import com.example.data.model.Village
import com.example.data.service.APCadastralMapService
import com.example.data.service.LocalAPLocationService
import com.example.ui.viewmodel.LandRecordsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class APCadastralMapTest {

    private val cadastralService = APCadastralMapService()
    private val locationService = LocalAPLocationService()
    private lateinit var viewModel: LandRecordsViewModel

    @Before
    fun setup() {
        viewModel = LandRecordsViewModel(
            locationService = locationService,
            cadastralMapService = cadastralService
        )
    }

    @Test
    fun `verify GeoJSON parser parses APSAC parcel polygon coordinates and metadata`() {
        val sampleGeoJson = """
        {
          "type": "FeatureCollection",
          "crs": {"type": "name", "properties": {"name": "EPSG:4326"}},
          "features": [
            {
              "type": "Feature",
              "id": 3320441,
              "geometry": {
                "type": "Polygon",
                "coordinates": [
                  [
                    [80.328679, 16.202362],
                    [80.328332, 16.202514],
                    [80.327514, 16.202873],
                    [80.327193, 16.202009],
                    [80.328679, 16.202362]
                  ]
                ]
              },
              "properties": {
                "objectid": 3320441,
                "dmvcd": "0736004",
                "dstrctnam": "Guntur",
                "mandalnam": "Prathipadu",
                "villagenam": "Prathipadu",
                "parcelnam": "32",
                "parceltyp": " ",
                "parceldsc": "Parcel Boundary",
                "st_area(shape)": 3.11352e-06,
                "st_length(shape)": 0.007482
              }
            }
          ]
        }
        """.trimIndent()

        val parsed = cadastralService.parseGeoJsonFeatures(sampleGeoJson)
        assertEquals(1, parsed.size)
        val parcel = parsed.first()
        assertEquals(3320441L, parcel.objectId)
        assertEquals("32", parcel.surveyNumber)
        assertEquals("Guntur", parcel.district)
        assertEquals("Prathipadu", parcel.mandal)
        assertEquals("Prathipadu", parcel.village)
        assertEquals("0736004", parcel.dmvCode)
        assertEquals("Parcel Boundary", parcel.parcelDescription)

        // Verify polygon coordinates
        assertEquals(1, parcel.coordinates.size)
        val ring = parcel.coordinates.first()
        assertEquals(5, ring.size)

        // Check LatLng projection (GeoJSON is [lon, lat])
        assertEquals(16.202362, ring.first().latitude, 0.0001)
        assertEquals(80.328679, ring.first().longitude, 0.0001)

        // Verify centroid and bounding box
        assertTrue(parcel.centroid.latitude in 16.0..17.0)
        assertTrue(parcel.centroid.longitude in 80.0..81.0)
        val bbox = parcel.boundingBox
        assertEquals(4, bbox.size)
        assertTrue(bbox[0] <= bbox[2]) // minLat <= maxLat
        assertTrue(bbox[1] <= bbox[3]) // minLng <= maxLng
    }

    @Test
    fun `verify live or mock query on real APSAC parcel endpoint returns valid polygon`() = runTest {
        // Query Guntur -> Prathipadu -> Prathipadu -> Survey No. 32
        val result = cadastralService.queryParcel(
            districtName = "Guntur",
            mandalName = "Prathipadu",
            villageName = "Prathipadu",
            surveyNumber = "32",
            includeNeighbors = true
        )

        // Verify result metadata
        assertEquals("Guntur", result.queryDistrict)
        assertEquals("Prathipadu", result.queryMandal)
        assertEquals("Prathipadu", result.queryVillage)
        assertEquals("32", result.querySurveyNumber)
        assertTrue("Source should identify APSAC", result.source.contains("APSAC"))

        if (result.success) {
            assertTrue("Should return matched parcel", result.matchedParcels.isNotEmpty())
            val matched = result.matchedParcels.first()
            assertEquals("32", matched.surveyNumber)
            assertEquals("Guntur", matched.district)
            assertEquals("Prathipadu", matched.mandal)
            assertEquals("Prathipadu", matched.village)
            assertTrue("Polygon coordinates must not be empty", matched.coordinates.isNotEmpty())
            assertTrue("Polygon must have at least 3 vertices", matched.coordinates.first().size >= 3)
            // Latitude in Guntur region (~16° N), Longitude (~80° E)
            assertTrue(matched.centroid.latitude in 15.5..17.0)
            assertTrue(matched.centroid.longitude in 79.5..81.5)
        }
    }

    @Test
    fun `verify ViewModel survey map navigation and state controls`() {
        val initialState = viewModel.uiState.value
        assertEquals(ActiveTab.LAND_RECORD_SEARCH, initialState.activeTab)
        assertFalse(initialState.isMapSatelliteMode)
        assertTrue(initialState.showMapNeighbors)

        // 1. Switch to Survey Map tab
        viewModel.switchTab(ActiveTab.SURVEY_MAP)
        assertEquals(ActiveTab.SURVEY_MAP, viewModel.uiState.value.activeTab)

        // 2. Select District Guntur
        val guntur = locationService.getDistricts().first { it.id == "AP_GNT" }
        viewModel.onDistrictSelected(guntur)
        assertEquals("Guntur", viewModel.uiState.value.selectedDistrict?.name)

        // 3. Select Mandal Prathipadu
        val prathipaduMandal = locationService.getMandals(guntur.id).first { it.name == "Prathipadu" }
        viewModel.onMandalSelected(prathipaduMandal)
        assertEquals("Prathipadu", viewModel.uiState.value.selectedMandal?.name)

        // 4. Select Village Prathipadu
        val prathipaduVillage = locationService.getVillages(prathipaduMandal.id).first { it.name == "Prathipadu" }
        viewModel.onVillageSelected(prathipaduVillage)
        assertEquals("Prathipadu", viewModel.uiState.value.selectedVillage?.name)

        // 5. Enter Survey Number
        viewModel.onSurveyNumberChanged("32")
        assertEquals("32", viewModel.uiState.value.surveyNumberInput)

        // 6. Test Satellite and Neighbors toggle
        viewModel.toggleMapSatellite()
        assertTrue(viewModel.uiState.value.isMapSatelliteMode)
        viewModel.toggleMapSatellite()
        assertFalse(viewModel.uiState.value.isMapSatelliteMode)

        viewModel.toggleMapNeighbors()
        assertFalse(viewModel.uiState.value.showMapNeighbors)
        viewModel.toggleMapNeighbors()
        assertTrue(viewModel.uiState.value.showMapNeighbors)

        // 7. Test parcel index selection
        viewModel.selectParcelIndex(1)
        assertEquals(1, viewModel.uiState.value.selectedParcelIndex)

        // 8. Test reset
        viewModel.resetFilters()
        val resetState = viewModel.uiState.value
        assertNull(resetState.selectedDistrict)
        assertNull(resetState.selectedMandal)
        assertNull(resetState.selectedVillage)
        assertEquals("", resetState.surveyNumberInput)
        assertNull(resetState.mapQueryResult)
        assertEquals(0, resetState.selectedParcelIndex)
    }

    @Test
    fun `verify transition from search screen to survey map`() {
        val dist = locationService.getDistricts().first { it.id == "AP_KRI" }
        val mnd = locationService.getMandals(dist.id).first { it.name == "Pedana" }
        val vlg = locationService.getVillages(mnd.id).first { it.name == "Jinjeru" }

        viewModel.selectQuickSample(dist.id, mnd.id, vlg.id, "166")
        assertEquals(ActiveTab.LAND_RECORD_SEARCH, viewModel.uiState.value.activeTab)

        // Trigger View on Map from Search
        viewModel.viewOnMapFromSearch()
        assertEquals(ActiveTab.SURVEY_MAP, viewModel.uiState.value.activeTab)
        assertEquals("166", viewModel.uiState.value.surveyNumberInput)
        assertEquals("Jinjeru", viewModel.uiState.value.selectedVillage?.name)
    }
}
