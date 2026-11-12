package com.example

import com.example.data.model.LandRecordType
import com.example.data.service.APLandRecordsService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {
    private lateinit var service: APLandRecordsService

    @Before
    fun setUp() {
        service = APLandRecordsService()
    }

    @Test
    fun testDistrictsListNotEmpty() {
        val districts = service.getDistricts()
        assertTrue("Districts list should not be empty", districts.isNotEmpty())
        assertTrue("Should include Krishna district", districts.any { it.name == "Krishna" })
        assertTrue("Should include Guntur district", districts.any { it.name == "Guntur" })
    }

    @Test
    fun testDependentMandalsAndVillages() {
        val mandals = service.getMandals("AP_KRI")
        assertTrue("Krishna mandals should not be empty", mandals.isNotEmpty())

        val firstMandal = mandals.first()
        val villages = service.getVillages(firstMandal.id)
        assertTrue("Villages for ${firstMandal.name} should not be empty", villages.isNotEmpty())
    }

    @Test
    fun testSearchLandRecordDemoMode() = runBlocking {
        val result = service.searchLandRecord(
            districtName = "Krishna",
            mandalName = "Vijayawada Rural",
            villageName = "Nunna",
            recordType = LandRecordType.ADANGAL,
            surveyNumber = "104/1"
        )

        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)
        assertEquals("104/1", data?.surveyNumber)
        assertEquals("Krishna", data?.district)
        assertEquals("Vijayawada Rural", data?.mandal)
        assertEquals("Nunna", data?.village)
        assertFalse(data?.isOfficialApi ?: true)
        assertTrue(data?.sourceNote?.contains("Demo data") == true)
    }

    @Test
    fun testVerifyOwnershipRequiresConsent() = runBlocking {
        val resultWithoutConsent = service.verifyOwnership(
            districtName = "Krishna",
            mandalName = "Vijayawada Rural",
            villageName = "Nunna",
            surveyNumber = "104/1",
            hasConsent = false
        )
        assertTrue(resultWithoutConsent.isFailure)

        val resultWithConsent = service.verifyOwnership(
            districtName = "Krishna",
            mandalName = "Vijayawada Rural",
            villageName = "Nunna",
            surveyNumber = "104/1",
            hasConsent = true
        )
        assertTrue(resultWithConsent.isSuccess)
        val data = resultWithConsent.getOrNull()
        assertNotNull(data)
        assertNotNull(data?.ownerName)
        assertNotNull(data?.recordReferenceNumber)
    }
}
