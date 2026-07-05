# ReLive — Digital Wellbeing & Parental Control App

> *Reclaim control over technology. Build healthier digital habits.*

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-orange?style=flat-square)
![Phase](https://img.shields.io/badge/Phase-3%20Complete-brightgreen?style=flat-square)

---

## 📱 What is ReLive?

ReLive is an AI-powered **Digital Wellbeing ecosystem** for Android that helps users:

- 📊 Track and manage **screen time** with real-time dashboard
- 👨‍👩‍👧 Enable **parental controls** with password protection
- ⏱️ Set **daily screen time limits** with alerts
- 📈 View **daily and weekly usage reports** with charts
- 🔒 Block distracting apps during **focus/study/work modes** *(coming soon)*
- 🧘 Build healthier habits through **AI coaching** *(coming soon)*
- 💧 Track **health metrics** — water intake, sleep, mood *(coming soon)*

Inspired by Google Digital Wellbeing, Apple Screen Time, Forest, and Family Link — combined into one intelligent platform.

---

## ✨ Current Features (Phase 3 Complete)

### 🏠 Home Screen
- Material 3 dark navy theme
- Parent Mode toggle with password protection
- Activity tracking system (add, view)
- Settings access via gear icon

### 🛡️ Parent Mode
- SHA-256 password hashing
- Foreground Service with persistent notification
- Encrypted password storage (EncryptedSharedPreferences)
- Password change via Settings screen

### 📊 Wellness Dashboard
- **Dashboard tab** — Real-time today's screen time + app usage list
- **Daily tab** — Full daily report with most used app, app count, progress bars
- **Weekly tab** — 7-day bar chart, total week time, daily average
- **Limits tab** — Set daily screen time goals (1h/2h/3h/4h), limit exceeded alerts

### 🔧 Technical
- Foreground Service with persistent notification
- ProGuard/R8 code obfuscation for release builds
- Usage Stats permission flow
- Dark status bar integration

---

## 🏗️ Architecture

```
UI (Jetpack Compose + Material 3)
        ↓
ViewModel (StateFlow + SharedFlow)
        ↓
Repository
        ↓
Room Database (SQLite) + DataStore
```

**Pattern:** MVVM (Model-View-ViewModel)  
**Async:** Kotlin Coroutines + Flow  
**Security:** EncryptedSharedPreferences + ProGuard

---

## 📁 Project Structure

```
app/src/main/java/in/srimantamondal/relive/
│
├── data/
│   ├── db/          → Room Database, DAO
│   ├── model/       → ActivityRecord
│   ├── repository/  → ReLiveRepository
│   └── usage/       → AppUsageManager, ScreenTimeLimitManager
│
├── ui/
│   ├── screens/     → HomeScreen, UsageDashboard, DailyReport,
│   │                   WeeklyReport, ScreenTimeLimit, ParentModeSettings
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
├── MainActivity.kt
└── SplashActivity.kt
```

---

## 🛣️ Development Roadmap

| Phase | Feature | Status |
|-------|---------|--------|
| Phase 1 | Foundation, MVVM, Room, DataStore, Splash, Navigation | ✅ Complete |
| Phase 2 | Parent Mode, Foreground Service, Security, Settings | ✅ Complete |
| Phase 3 | Usage Tracking, Screen Time Dashboard, Daily/Weekly Reports, Limits | ✅ Complete |
| Phase 4 | App Blocking, Focus Mode, Study Mode, AI Suggestions | 🔜 Next |
| Phase 5 | Health System, Water, Sleep, Mood, Fitness | 📋 Planned |
| Phase 6 | AI Coach, AI Therapist, AI Motivation | 📋 Planned |
| Phase 7 | Firebase, Cloud Backup, Cross-device Sync | 📋 Planned |
| Phase 8 | Testing, Optimization, Play Store Release | 📋 Planned |

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Database | Room (SQLite) |
| Preferences | DataStore |
| Security | EncryptedSharedPreferences, SHA-256, ProGuard |
| Async | Coroutines + Flow |
| Service | Android Foreground Service |
| Usage Tracking | Android UsageStatsManager |
| Future | Firebase, AI APIs |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 34
- Kotlin 2.0+
- Min SDK: 24 (Android 7.0)

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

---

## 👨‍💻 Developer

**Srimanta Mondal**  
M.Sc. Cyber Security & Forensics — Gujarat University  
Cybersecurity Researcher | Android Developer | Entrepreneur

[![GitHub](https://img.shields.io/badge/GitHub-srimanta77-black?style=flat-square&logo=github)](https://github.com/srimanta77)
[![Website](https://img.shields.io/badge/Website-srimantamondal.in-blue?style=flat-square)](https://srimantamondal.in)

---

## 📄 License

This project is under active development.  
© 2026 Srimanta Mondal. All rights reserved.
