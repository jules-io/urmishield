# Urmi Shield: Accessibility-First Implementation Plan

This document outlines the atomic-level steps required to transform Urmi Shield from a call screening app into an accessibility-first Cognitive Assistant for phone calls. All steps will be implemented exactly as described.

---

### **Phase 1-4: Prototype Implementation (COMPLETE)**

The initial refactor to an accessibility-first model is complete. The app now functions as a proof-of-concept.

---

### **Phase 5: Production Hardening & Robustness (COMPLETE)**

This phase has elevated the prototype to a production-quality application by addressing brittleness, performance, and error handling.

---

### **Phase 6: UI Polish & Performance (COMPLETE)**

1.  **Draggable Overlay:** The `OverlayManager` now handles touch events, allowing the user to move the transcription overlay during a call.
2.  **Battery Optimization:** The `UrmiShieldAccessibilityService` now listens for system battery events and disables scam detection when the battery is low.

---

### **Phase 7: Testing Framework (COMPLETE)**

1.  **Unit Tests:** Added JUnit and MockK to the project and created a robust unit test for the `ScamDetector` to verify its keyword detection logic against default and custom patterns.
2.  **Next Steps:** The testing framework is now in place for developers to add further unit and integration tests (e.g., for the `UrmiShieldAccessibilityService` using Robolectric).

---
