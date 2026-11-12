# Dhara – Project README

**Team Name:** Dhara
**Team Members:**

| S.No | Name | Roll No. |
|---|---|---|
| 1 | Rahul Kumar Chaurasiya | 25CSB1A77 |
| 2 | Pritkumar Gopalbhai Patel | 25CSB1A07 |
| 3 | Ugyen Jigme Samphel | 25CSB1A79 |
| 4 | G. Radhashyam | 25CSB1A47 |
| 5 | Samareddi Nandha Vardhan Nihal | 25CSB1A58 |
| 6 | J. Nikhil Nandhan | 25CSB1A57 |

**Repository:** https://github.com/rahul84ya/DA_Project.git
**Deliverable:** Dhara Android application (`dhara.apk`)

---

## 1. Project Overview

Dhara is an Android mobile application built with a modern Kotlin / Jetpack Compose stack. Its package identifier (`com.aistudio.aplandverify.xqkt`) indicates it is aimed at land-record verification (AP = Andhra Pradesh), and it integrates Firebase services including generative-AI capabilities.

> Please edit this section with 2-3 lines in your own words describing the problem your project solves.

## 2. Problem Statement

<Describe the problem: e.g., difficulty in verifying land records / ownership details, risk of fraud, manual and slow processes.>


> Adjust this list to match the features actually implemented in your app.

## 5. Technology Stack

| Layer | Technology |
|---|---|
| Platform | Android (APK) |
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Material Icons Extended |
| Architecture / Lifecycle | AndroidX Lifecycle, Activity Compose |
| Local Storage | Room |
| Authentication | Google Sign-In (Play Services) |
| Backend / Cloud | Firebase (Firebase AI, App Check) |
| Build System | Gradle |

## 6. App Details

| Item | Value |
|---|---|
| App name | Dhara |
| Package name | com.aistudio.aplandverify.xqkt |
| Main activity | com.example.MainActivity |
| Permissions | INTERNET, ACCESS_NETWORK_STATE |

## 7. System Architecture

```
User  ->  Android App (Jetpack Compose UI)
              |-- Room (local database)
              |-- Google Sign-In (authentication)
              |-- Firebase AI (AI features)
              `-- Firebase App Check (security)
```

## 8. Installation & Setup

### Install the APK
1. Copy `dhara.apk` to an Android device.
2. Enable **Install from unknown sources** in device settings.
3. Open the APK and tap **Install**.

### Build from Source
```bash
git clone https://github.com/rahul84ya/DA_Project.git
cd DA_Project
# Open in Android Studio, let Gradle sync, then Run
```
You will need your own `google-services.json` (Firebase) in the `app/` folder.

## 9. How to Use

1. Launch the Dhara app.
2. Sign in with your Google account.
3. <Describe the main workflow, e.g., enter land details / survey number and verify.>
4. <View results / reports.>

## 10. Folder Structure

```
DA_Project/
|-- app/            # Android application source
|-- build.gradle
`-- README.md
```
> Update to match the actual repository layout.

## 11. Future Enhancements

- <Offline mode improvements>
- <Multi-language support (Telugu / Hindi / English)>
- <Additional data sources and reporting>

## 12. Contact

Team Dhara – Rahul Kumar Chaurasiya (25CSB1A77)
