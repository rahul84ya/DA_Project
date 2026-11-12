package com.example.data.service

import com.example.data.model.District
import com.example.data.model.Mandal
import com.example.data.model.Village
import com.example.data.source.APAdministrativeData

/**
 * Local implementation of [APLocationService] querying the normalized
 * Andhra Pradesh Local Government Directory (LGD) administrative dataset.
 *
 * This provides an isolated data access layer that can be swapped with a remote API client
 * without modifying ViewModel or UI layers.
 */
class LocalAPLocationService : APLocationService {

    override fun getDistricts(): List<District> {
        return APAdministrativeData.DISTRICTS
    }

    override fun getMandals(districtId: String): List<Mandal> {
        if (districtId.isBlank()) return emptyList()
        return APAdministrativeData.MANDALS_BY_DISTRICT[districtId] ?: emptyList()
    }

    override fun getVillages(mandalId: String): List<Village> {
        if (mandalId.isBlank()) return emptyList()
        return APAdministrativeData.VILLAGES_BY_MANDAL[mandalId] ?: emptyList()
    }

    override fun getDistrictById(districtId: String): District? {
        return APAdministrativeData.DISTRICT_BY_ID[districtId]
    }

    override fun getMandalById(mandalId: String): Mandal? {
        return APAdministrativeData.MANDAL_BY_ID[mandalId]
    }

    override fun getVillageById(villageId: String): Village? {
        return APAdministrativeData.VILLAGE_BY_ID[villageId]
    }

    override fun getAvailableSurveyNumbers(villageId: String): List<String> {
        // LGD publishes administrative boundaries, not survey-number registers.
        // Survey numbers must be entered by the user or chosen on MeeBhoomi.
        return emptyList()
    }
}
