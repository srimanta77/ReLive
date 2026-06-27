# ReLive — Digital Wellbeing & Parental Control App

> *Reclaim control over technology. Build healthier digital habits.*

![Platform](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Language](https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square&logo=kotlin)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-In%20Development-orange?style=flat-square)

---

## 📱 What is ReLive?

ReLive is an AI-powered **Digital Wellbeing ecosystem** for Android that helps users:

- 📊 Track and manage **screen time**
- 👨‍👩‍👧 Enable **parental controls** with password protection
- 🔒 Block distracting apps during **focus/study/work modes**
- 🧘 Build healthier habits through **AI coaching**
- 💧 Track **health metrics** — water intake, sleep, mood, exercise
- 🚨 Enable **emergency protection** features

Inspired by Google Digital Wellbeing, Apple Screen Time, Forest, and Family Link — combined into one intelligent platform.

---

## ✨ Current Features (Phase 2)

- ✅ Splash screen with animated logo and glow effect
- ✅ Home screen with Material 3 dark theme
- ✅ Activity tracking system (add, view, delete)
- ✅ Parent Mode with SHA-256 password protection
- ✅ Foreground Service with persistent notification
- ✅ Encrypted password storage (EncryptedSharedPreferences)
- ✅ Room database with DAO and Repository pattern
- ✅ DataStore for lightweight preferences
- ✅ Bottom navigation (Home, Commit, Wellness)

---

## 🏗️ Architecture

```
UI (Jetpack Compose + Material 3)
        ↓
ViewModel (StateFlow + SharedFlow)
        ↓
Repository
        ↓
Room Database (SQLite)
```

**Pattern:** MVVM (Model-View-ViewModel)  
**Async:** Kotlin Coroutines + Flow  
**DI:** Manual (Firebase/Hilt planned for later phases)

---

## 📁 Project Structure

```
app/src/main/java/in/srimantamondal/relive/
│
├── data/
│   ├── db/          → Room Database, DAO
│   ├── model/       → Data classes (ActivityRecord)
│   └── repository/  → ReLiveRepository
│
├── ui/
│   ├── screens/     → HomeScreen.kt
│   ├── theme/       → Material 3 colors, typography
│   └── HomeViewModel.kt
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
| Phase 2 | Parent Mode, Foreground Service, Security | 🔄 In Progress |
| Phase 3 | Usage Tracking Engine, Screen Time, Daily Reports | 🔜 Next |
| Phase 4 | App Blocking, Focus Mode, Study Mode, AI Suggestions | 📋 Planned |
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
| Security | EncryptedSharedPreferences, SHA-256 |
| Async | Coroutines + Flow |
| Service | Android Foreground Service |
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

---

## 👨‍💻 Developer

**Srimanta Mondal**  
M.Sc. Cyber Security & Forensics — Gujarat University  
Cybersecurity Researcher | Android Developer | Entrepreneur

[![GitHub](https://img.shields.io/badge/GitHub-srimanta77-black?style=flat-square&logo=github)](https://github.com/srimanta77)
[![Website](https://img.shields.io/badge/Website-srimantamondal.in-blue?style=flat-square)](https://srimantamondal.in)

---

## 📄 License

This project is currently private and under active development.  
© 2026 Srimanta Mondal. All rights reserved.
