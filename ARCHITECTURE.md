# Urmi Shield Architecture 🏗️

## High-Level Overview

Urmi Shield is a Clean Architecture Android application designed for high-performance call screening.

### Layers

1.  **UI Layer (Presentation)**
    -   **Jetpack Compose**: All UI is declarative.
    -   **Navigation**: Single Activity (`MainActivity`) with `NavHost`.
    -   **Screens**: `Dashboard`, `History`, `Settings`, `Onboarding`.

2.  **Service Layer (Domain/Logic)**
    -   **UrmiShieldScreeningService**: The entry point for call interception.
    -   **IntelligenceService**: The heavy lifter. Orchestrates Audio -> AI -> Defense.
    -   **RuleEngine**: Fast-path logic for Whitelist/Emergency bypass.

3.  **Data Layer (Repository)**
    -   **Room Database**: Stores `CallLogEntity` and `NumberListEntity`. Encrypted with SQLCipher.
    -   **DataStore**: Stores user preferences (`SettingsRepository`).
    -   **Worker**: Downloads updates (`ScamPatternWorker`).

4.  **AI Layer (Core)**
    -   **LiteRTClassifier**: TensorFlow Lite wrapper for deepfake detection.
    -   **SpeechRecognizerManager**: Wrapper around Android SpeechRecognizer.
    -   **ScamDetector**: Regex/Pattern matching engine.
    -   **TrustScoreManager**: The "Brain" that calculates risk.

## Data Flow Pipeline

The following diagram illustrates the zero-latency interception and parallel AI analysis pipeline that occurs entirely on-device when a call is received:

```mermaid
sequenceDiagram
    participant OS as Android OS
    participant Service as UrmiShieldScreeningService
    participant RuleEngine as RuleEngine (DB)
    participant Intelligence as IntelligenceService
    participant AI as LiteRT & Regex

    OS->>Service: onScreenCall(CallDetails)
    activate Service
    
    Service->>RuleEngine: Check Whitelist
    alt Is Whitelisted
        RuleEngine-->>Service: Allow
        Service-->>OS: respondToCall(ALLOW)
    else Not Whitelisted
        Service->>Service: Check STIR/SHAKEN
        alt STIR/SHAKEN Failed
            Service-->>OS: respondToCall(BLOCK, REJECT)
        else Verified or Unknown
            Service->>Intelligence: startAnalysis()
            Service-->>OS: respondToCall(SILENCE)
        end
    end
    deactivate Service

    activate Intelligence
    Intelligence->>OS: Capture Foreground Audio (PCM)
    OS-->>Intelligence: AudioStream
    
    par Deepfake Detection
        Intelligence->>AI: analyzeTexture(AudioStream)
        AI-->>Intelligence: DeepfakeProbability
    and Intent Detection
        Intelligence->>AI: transcribeAndMatch(AudioStream)
        AI-->>Intelligence: ScamIntentSeverity
    end

    Intelligence->>Intelligence: TrustScoreManager.aggregate()

    alt High Risk
        Intelligence->>OS: Inject TTS Stalling Audio
        Intelligence->>OS: Vibrate & Show RED Overlay
        Intelligence->>RuleEngine: Log Blocked Call
    else Safe
        Intelligence->>OS: Show GREEN Overlay (Allow)
        Intelligence->>RuleEngine: Log Safe Call
    end
    deactivate Intelligence
```
## Security Controls

-   **Zero-Network**: No upload code exists.
-   **Obfuscation**: AI logic is hidden via R8.
-   **Encryption**: DB is encrypted at rest.
-   **Permissions**: Least privilege; runtime requests only.
