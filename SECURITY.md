# Security Policy 🛡️

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in Urmi Shield, please email security@urmilabs.com. We prioritize user safety.

## Privacy by Design (Zero-Network Policy)

Urmi Shield is built with a **Zero-Network** policy.

1.  **Audio Processing**: All audio analysis (STFT, Deepfake Detection) happens on-device using local buffers. Audio is never uploaded.
2.  **Transcription**: Speech-to-Text is performed using the Android Speech Recognizer.
3.  **Data Persistence**: Call logs and scam detection events are stored in a local, encrypted Room database (SQLCipher).
4.  **Updates**: Scam patterns are downloaded via a restricted, unidirectional Worker.

## Encryption & Obfuscation

-   **Database**: Full-Disk Encryption using SQLCipher (AES-256).
-   **Code**: R8/ProGuard obfuscation protects proprietary AI logic (`ScamDetector`, `LiteRTClassifier`).
-   **Sandbox**: Sensitive files are stored in `Context.MODE_PRIVATE`.

## Compliance

Urmi Shield adheres to the Android Enterprise security standards and Google Play Safety guidelines.
