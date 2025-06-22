# 📸 Shutterfly Image Manipulation App

*An intuitive Android application for image manipulation with advanced drag & drop functionality*

**🎯 Built for Shutterfly Technical Interview**

---

[![Build Status](https://github.com/guyron7777/shutterfly/actions/workflows/android.yml/badge.svg)](https://github.com/guyron7777/shutterfly/actions)

---

## 🌟 Features Overview

| Feature | Status | Description |
|---------|--------|-------------|
| 🎠 **Image Carousel** | ✅ | Horizontal scrollable carousel with 8+ sample images |
| 🎨 **Canvas Workspace** | ✅ | Square canvas area for image manipulation |
| 🖱️ **Drag & Drop** | ✅ | Intuitive drag from carousel to canvas |
| 📱 **Multi-Image Support** | ✅ | Drop and manage multiple images simultaneously |
| ✋ **Pan Gestures** | ✅ | Move images freely within canvas boundaries |
| 🔍 **Pinch-to-Zoom** | ✅ | Smooth zoom with aspect ratio preservation |

---

## 🏗️ Architecture & Design

### 📐 Modern Android Architecture

```
Clean Architecture + MVVM + Hilt DI
├── UI Layer (Jetpack Compose)
├── ViewModel (State Management)
├── Domain Layer (Use Cases, Business Logic)
├── Data Layer (Repositories)
└── Dependency Injection (Hilt)
```

- **Hilt** for dependency injection
- **StateFlow** for reactive state management
- **Sealed classes** for UI actions and state
- **Unit tests** for all layers

---

## 🧩 Key Components

- **UI Components:** `ImageCarousel`, `CanvasArea`, `DraggableCanvasImage`, `GlobalDragOverlay`
- **State Management:** `ImageManipulatorViewModel`, `GlobalDragState`, `CanvasState`, `DragStateManager`
- **Domain:** Use cases for all business logic
- **Data:** Repository pattern for image resources

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Giraffe (2022.3.1) or newer
- **Android SDK**: API level 24+
- **Kotlin**: 1.8.0+
- **Gradle**: 7.4+

### Quick Setup

```bash
git clone https://github.com/guyron7777/shutterfly.git
cd shutterfly
./gradlew assembleDebug
```

1. **Clone** the repository
2. **Open** in Android Studio
3. **Sync** Gradle files
4. **Build** and **Run** on device/emulator

---

## 📁 Project Structure

```
app/src/main/java/com/guyron/shutterfly/
├── ui/
│   ├── component/         # Composables
│   ├── state/             # UI state, actions, DragStateManager
│   ├── viewmodel/         # ViewModels
│   └── theme/             # Compose theme
├── domain/
│   ├── model/             # Data models
│   ├── usecase/           # Business logic
│   └── repository/        # Repository interfaces
├── data/
│   └── repoaitory/        # Repository implementations
├── di/                    # Hilt modules
└── MainActivity.kt
```

---

## 🧪 Testing & Quality

### Running Unit Tests

```bash
./gradlew test
```

- **Unit tests**: All business logic, state managers, and ViewModels are covered.
- **Test location**: `src/test/java/com/guyron/shutterfly/`
- **Tools**: JUnit, Truth, Mockito

### Example

```kotlin
@Test
fun `AddImage action adds image to state`() = runTest {
    val position = Offset(5f, 5f)
    viewModel.handleAction(ImageManipulatorAction.AddImage(1, position))
    val state = viewModel.state.value
    assertThat(state.canvasState.images).hasSize(1)
}
```

---

## ⚙️ CI/CD

This project uses **GitHub Actions** for continuous integration:

- On every push and pull request:
  - Builds the app
  - Runs all unit tests
- See `.github/workflows/android.yml` for details

---

## 🔧 Technical Highlights

- **Precise Touch Detection**: Exact image boundary matching
- **Global Drag Tracking**: Drag across the entire screen
- **Real-time Performance**: Optimized Compose state management
- **Aspect Ratio Preservation**: Advanced ContentScale handling

---

## 🚀 Future Enhancements

- 📁 Image Import from device
- 💾 Save/Export canvas
- 🎨 Filters and effects
- ↩️ Enhanced undo/redo
- 🔄 Image rotation
- 🌐 Network image support

---

## 👨‍💻 About

**Built for Shutterfly Technical Interview**

- Modern Android development with Jetpack Compose
- Clean Architecture, Hilt DI, and robust testing
- Performance and UX focused

---

## 📞 Contact

Questions? Reach out for technical discussions about architecture, performance, or creative solutions in this project.

---

*Showcasing modern Android development with Jetpack Compose and Clean Architecture*