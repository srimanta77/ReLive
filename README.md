# ReLive — Digital Wellbeing & Parental Control App

> Reclaim control over technology. Build healthier digital habits.

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-orange?style=flat-square)
![Phase](https://img.shields.io/badge/Phase-7%20In%20Progress-brightgreen?style=flat-square)

---

## Overview

ReLive is an AI-powered digital wellbeing ecosystem for Android that helps users:

- Track and manage screen time with a real-time dashboard
- Enable parental controls with password protection
- Set daily screen time limits with alerts
- View daily and weekly usage reports with charts
- Stay productive with Focus Mode and Study Mode (Pomodoro)
- Track water intake, sleep, mood, and BMI/fitness
- Build healthy routines with a habit tracker
- Get guidance from an AI Coach
- Maintain a cloud-synced profile — sign in and pick up right where you left off, on any device

Inspired by Google Digital Wellbeing, Apple Screen Time, Forest, and Family Link, combined into one platform.

---

## Current Features (Phase 7 In Progress)

### Account & Profile (Phase 7)
![Firebase](https://img.shields.io/badge/-Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black) ![Firestore](https://img.shields.io/badge/-Firestore-039BE5?style=flat-square&logo=firebase&logoColor=white)
- Firebase Authentication — email/password signup and login
- Mandatory email verification — unverified accounts are blocked from entering the app, with no bypass
- Editable profile — name and photo, synced to Firestore
- Real-time cross-device sync — a change on one device updates live on another
- Firestore security rules — each user can only read/write their own profile document

### Home Screen
- Material 3 dark navy theme
- Parent Mode toggle with password protection
- Activity tracking system (add, view)
- Settings access via gear icon

### Parent Mode
![Security](https://img.shields.io/badge/-Security-4CAF50?style=flat-square&logo=shieldsdotio&logoColor=white)
- SHA-256 password hashing
- Foreground Service with persistent notification
- Encrypted password storage (EncryptedSharedPreferences)
- Password change via Settings screen

### Wellness Tab
![Analytics](https://img.shields.io/badge/-Analytics-673AB7?style=flat-square&logo=googleanalytics&logoColor=white)
- Dashboard — real-time today's screen time and app usage list
- Daily Report — most used app, app count, progress bars
- Weekly Report — 7-day bar chart, total week time, daily average
- Limits — daily screen time goals (1h/2h/3h/4h), limit-exceeded alerts
- Water Reminder — daily water intake tracking (8-glass goal), hydration tips
- Sleep Tracker — log and review sleep patterns
- Mood Tracker — daily mood logging and trends
- BMI Calculator — fitness/health snapshot

### Commit Tab
- Focus Mode — Pomodoro timer (25/5/15 min), session tracker, pulse animation
- Study Mode — subject tracker, custom goal (30/45/60/90 min), session history
- Habits — daily habit tracker with progress, custom habits

### AI Coach
![AI](https://img.shields.io/badge/-AI%20Powered-FF6F00?style=flat-square&logo=openai&logoColor=white)
- Conversational guidance layered on top of the wellness data above

### Technical
- Foreground Service with persistent notification
- ProGuard/R8 code obfuscation for release builds
- Usage Stats permission flow
- Dark status bar integration

---

## Architecture

```
UI (Jetpack Compose + Material 3)
        ↓
ViewModel (StateFlow + SharedFlow)
        ↓
Repository
        ↓
Room Database (SQLite) + DataStore   |   Firebase Auth + Firestore
```

**Pattern:** MVVM (Model-View-ViewModel)
**Async:** Kotlin Coroutines + Flow
**Security:** EncryptedSharedPreferences, ProGuard, Firestore security rules
**Cloud:** Firebase Authentication, Cloud Firestore (real-time sync)

---

## Project Structure

```
app/src/main/java/in/srimantamondal/relive/
│
├── data/
│   ├── db/          → Room Database, DAO
│   ├── model/       → ActivityRecord, UserProfile
│   ├── repository/  → ReLiveRepository, UserProfileRepository (Firestore)
│   └── usage/       → AppUsageManager, ScreenTimeLimitManager
│
├── ui/
│   ├── screens/     → HomeScreen, AuthScreen, EmailVerificationScreen,
│   │                   ProfileScreen, UsageDashboard, DailyReport,
│   │                   WeeklyReport, ScreenTimeLimit, ParentModeSettings,
│   │                   FocusModeScreen, StudyModeScreen, HabitTrackerScreen,
│   │                   WaterReminderScreen, SleepTrackerScreen,
│   │                   MoodTrackerScreen, BMICalculatorScreen, AICoachScreen
│   ├── theme/       → Material 3 colors, typography
│   ├── HomeViewModel.kt
│   └── UsageStatsHelper.kt
│
├── security/
│   └── PasswordManager.kt   → EncryptedSharedPreferences
│
├── parent/
│   └── ParentModeService.kt → Foreground Service
│
├── MainActivity.kt   → Auth-state routing (logged out / needs verification / logged in)
└── SplashActivity.kt
```

---

## Development Roadmap

| Phase | Feature | Status |
|-------|---------|--------|
| Phase 1 | Foundation, MVVM, Room, DataStore, Splash, Navigation | Complete |
| Phase 2 | Parent Mode, Foreground Service, Security, Settings | Complete |
| Phase 3 | Usage Tracking, Screen Time Dashboard, Daily/Weekly Reports, Limits | Complete |
| Phase 4 | Focus Mode, Study Mode, Habit Tracker | Complete |
| Phase 5 | Health System — Water Reminder, Sleep, Mood, BMI | Complete |
| Phase 6 | AI Coach | Complete |
| Phase 7 | Firebase Auth, Enforced Email Verification, Profile Editing, Firestore Cross-device Sync | In Progress |
| Phase 8 | App Signing, Play Store Release | Planned |

---

## Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM |
| Local Database | Room (SQLite) |
| Preferences | DataStore |
| Cloud Auth & DB | Firebase Authentication, Cloud Firestore |
| Security | EncryptedSharedPreferences, SHA-256, ProGuard, Firestore Security Rules |
| Async | Coroutines, Flow |
| Service | Android Foreground Service |
| Usage Tracking | Android UsageStatsManager |
| Future | AI APIs, Firebase Cloud Functions |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 34 (compileSdk / targetSdk)
- Kotlin 2.0+
- Min SDK: 24 (Android 7.0)
- A Firebase project with Authentication (Email/Password) and Firestore enabled — add your own `google-services.json` under `app/`

### Run Locally

```bash
git clone https://github.com/srimanta77/ReLive.git
cd ReLive
# Open in Android Studio and run on device/emulator
```

### Permissions Required
- `FOREGROUND_SERVICE` — Parent Mode background service
- `POST_NOTIFICATIONS` — Parent Mode notification (Android 13+)
- `PACKAGE_USAGE_STATS` — Screen time tracking (manual grant required)
- Internet — Firebase Authentication and Firestore sync

---

## Developer

**Srimanta Mondal**
Assistant Professor, Computer Science & Engineering — Dev Bhoomi Uttarakhand University
Teaches Cybersecurity & Digital Forensics | Android Developer | Entrepreneur

[![GitHub](https://img.shields.io/badge/GitHub-srimanta77-black?style=flat-square&logo=github)](https://github.com/srimanta77)
[![Website](https://img.shields.io/badge/Website-srimantamondal.in-blue?style=flat-square)](https://srimantamondal.in)

---

## License

This project is under active development.
© 2026 Srimanta Mondal. All rights reserved.
