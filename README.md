# dhara

dhara is a modern, accessible prop-tech Android application for Andhra Pradesh land-record discovery and ownership verification. Built with Kotlin and Jetpack Compose, the app provides guided location-based parcel lookup inspired by official record structures of Andhra Pradesh revenue services (MeeBhoomi / Webland) and APSAC cadastral GIS.

---

## 🏛️ Features Included

1. **Land Record Search:**
   - Guided, dependent selection hierarchy: District → Tahsil/Mandal → Village → Land Record Type → Survey Number.
   - Downstream dropdowns remain disabled until prior choices are made.
   - Record types supported: Adangal (Village Account No. 3), 1-B (Record of Rights), FMB Map, and E-Passbook.
   - Action buttons: View Record, Download Summary, Report Discrepancy, and View on Map.

2. **Ownership Check:**
   - Dedicated ownership verification tab using the dependent revenue hierarchy.
   - Mandatory user authorization consent check before verification.
   - Displays Pattadar name, relationship, ownership classification, registered extent, and undivided share percentage.

3. **Cadastral Survey Map (APSAC GIS):**
   - Vector and GIS parcel boundary visualization using Andhra Pradesh Space Applications Centre (APSAC) spatial data.
   - Interactive parcel selection, centroid details, and surrounding boundaries reference.

4. **Official Portal Integration (MeeBhoomi):**
   - Integrated user-controlled view of the official `meebhoomi.ap.gov.in` portal.
   - Users interact directly with state government services without scraping or automated credential access.

5. **Local Administrative Directory:**
   - Fast, offline LGD-derived dataset snapshot covering all 28 Andhra Pradesh districts, 688 mandals, and 17,954 villages.

---

## 🛡️ Compliance & Privacy Notice

- **No Scraping:** This application strictly respects government service boundaries and does not automate CAPTCHA solving, crawling, or scraping.
- **Informational Discovery:** Statutory legal transactions (mortgages, sales deeds, registered partitions) require certified extracts from the Sub-Registrar Office (Registration & Stamps Department) and the Mandal Revenue Officer (Tahsildar).
