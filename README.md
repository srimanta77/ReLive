# ReLive — Digital Wellbeing & Parental Control App

> *Reclaim control over technology. Build healthier digital habits.*

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-orange?style=flat-square)
![Phase](https://img.shields.io/badge/Phase-5%20In%20Progress-brightgreen?style=flat-square)

---

## 📱 What is ReLive?

ReLive is an AI-powered **Digital Wellbeing ecosystem** for Android that helps users:

- 📊 Track and manage **screen time** with real-time dashboard
- 👨‍👩‍👧 Enable **parental controls** with password protection
- ⏱️ Set **daily screen time limits** with alerts
- 📈 View **daily and weekly usage reports** with charts
- 🎯 Stay productive with **Focus Mode** and **Study Mode** (Pomodoro)
- 💧 Track **water intake** with daily reminders
- ✅ Build healthy routines with **Habit Tracker**
- 🧘 AI coaching and health tracking *(coming soon)*

Inspired by Google Digital Wellbeing, Apple Screen Time, Forest, and Family Link — combined into one intelligent platform.

---

## ✨ Current Features (Phase 5 In Progress)

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

### 📊 Wellness Tab
- **Dashboard** — Real-time today's screen time + app usage list
- **Daily Report** — Most used app, app count, progress bars
- **Weekly Report** — 7-day bar chart, total week time, daily average
- **Limits** — Set daily screen time goals (1h/2h/3h/4h), limit exceeded alerts
- **Water** — Track daily water intake (8 glasses goal), hydration tips

### 🎯 Commit Tab
- **Focus Mode** — Pomodoro timer (25/5/15 min), sessions tracker, pulse animation
- **Study Mode** — Subject tracker, custom goal (30/45/60/90 min), session history
- **Habits** — Daily habit tracker with progress, add custom habits

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
│   │                   WeeklyReport, ScreenTimeLimit, ParentModeSettings,
│   │                   FocusModeScreen, StudyModeScreen, HabitTrackerScreen,
│   │                   WaterReminderScreen
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
| Phase 4 | Focus Mode, Study Mode, Habit Tracker | ✅ Complete |
| Phase 5 | Health System — Water Reminder, Sleep, Mood, Fitness | 🔄 In Progress |
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
