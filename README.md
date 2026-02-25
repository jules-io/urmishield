![Urmi Shield Banner](https://via.placeholder.com/1200x300.png?text=Urmi+Shield+%E2%80%94+Zero-Trust+Call+Protection)

# Urmi Shield 🛡️

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=31)
[![Build Status](https://github.com/urmilabs/urmi-shield/workflows/Android%20CI/badge.svg)](https://github.com/urmilabs/urmi-shield/actions)
[![Kotlin](https://img.shields.io/badge/kotlin-2.0.0-blue.svg?logo=kotlin)](http://kotlinlang.org)

Urmi Shield is a high-fidelity, on-device AI call screening engine designed specifically to protect vulnerable users (such as seniors) from advanced social engineering and deepfake audio scams. 

Built with **Jetpack Compose**, **LiteRT (TensorFlow Lite)**, and **Clean Architecture**, it provides zero-trust communications filtering without ever sending voice data to the cloud.

---

## ✨ Features

- 🛑 **Zero-Latency Interception**: Hooking directly into Android's `CallScreeningService`, it verifies STIR/SHAKEN status and drops unauthenticated robocalls instantly.
- 🧠 **On-Device Deepfake Detection**: Uses a local LiteRT neural network to analyze audio textures in milliseconds to flag synthesized voices.
- 🗣️ **Conversational Intent Analysis**: Transcribes caller audio on-device and uses regex-based intent matching to detect high-urgency scam scripts (e.g., "grandson in jail", "IRS payment").
- 🤖 **Defensive Acoustic Countermeasures**: Automatically injects TTS "stalling" audio back into the call to waste scammers' time and break automated pacing.
- 📱 **Google-Level Material 3 UI**: Beautiful, accessible, high-contrast dashboards built dynamically with Compose glassmorphism and animated vectors.
- 🔒 **Zero-Network Architecture**: No tracking, no uploads, no cloud APIs. What happens on the device, stays on the device.

---

## 📸 Screenshots

*(Replace with actual screenshots from your device/emulator)*

<div align="center">
  <img src="https://via.placeholder.com/250x500.png?text=Dashboard" width="24%">
  <img src="https://via.placeholder.com/250x500.png?text=Call+Incoming" width="24%">
  <img src="https://via.placeholder.com/250x500.png?text=Threat+Detected" width="24%">
  <img src="https://via.placeholder.com/250x500.png?text=History" width="24%">
</div>

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Java 17
- Android SDK 36

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/urmilabs/urmi-shield.git
   cd urmi-shield
   ```

2. **Add the Deepfake Model (CRITICAL):**
   The application requires a trained TensorFlow Lite model. You must place `deepfake_detector.tflite` into the `app/src/main/assets/` directory. Without this, the AI pipeline will gracefully degrade but protection will be limited.

3. **Firebase Configuration (Optional):**
   If you wish to use remote configuration or crashlytics, add your `google-services.json` to the `app/` directory. The app will gracefully disable these features if the file is missing.

4. **Build and Install:**
   ```bash
   ./gradlew installDebug
   ```

---

## 🏗️ Architecture

Urmi Shield is built strictly on Android's recommended architecture layered design.
For a deep dive into the service boundaries, the Intelligence orchestration, and the data layer, please read the [Architecture Overview](ARCHITECTURE.md).

---

## 🤝 Contributing

We welcome contributions from the community! Whether it's adding a new test, translating the app, or improving the ML model handling.

Please read our [Contributing Guidelines](CONTRIBUTING.md) to get started. Be sure to review our [Code of Conduct](CODE_OF_CONDUCT.md).

## 🛡️ Security

If you believe you have found a security vulnerability, please refer to our [Security Policy](SECURITY.md) for reporting instructions. Do not open public issues for security exploits.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
