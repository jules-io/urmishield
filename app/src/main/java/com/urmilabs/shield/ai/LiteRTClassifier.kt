package com.urmilabs.shield.ai

import android.content.Context
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.io.File
import java.io.IOException

class LiteRTClassifier(private val context: Context) {
    private var classifier: AudioClassifier? = null
    var isModelAvailable: Boolean = false
        private set

    fun init() {
        val modelName = "deepfake_detector.tflite"
        val modelFile = File(context.filesDir, modelName)

        try {
            // For dynamic updates, the model should be in internal storage.
            // As a fallback, we can check assets if it's not found, but we'll stick to one for now.
            if (modelFile.exists()) {
                classifier = AudioClassifier.createFromFile(modelFile)
                isModelAvailable = true
            } else {
                // If the model doesn't exist in storage, we check the assets folder as a fallback.
                // This allows bundling a default model with the app.
                try {
                    classifier = AudioClassifier.createFromFile(context, modelName)
                    isModelAvailable = true
                } catch (e: IOException) {
                    isModelAvailable = false
                    System.err.println("ERROR: Model not found in assets. Deepfake detection is disabled.")
                }
            }
        } catch (e: Exception) {
            isModelAvailable = false
            e.printStackTrace()
        }
    }

    fun classify(audioBuffer: ShortArray): Float {
        if (classifier == null || !isModelAvailable) {
            return 0.0f // Model not available, return neutral score.
        }

        val tensor = classifier!!.createInputTensorAudio()
        tensor.load(audioBuffer)

        val results = classifier!!.classify(tensor)

        if (results.isNotEmpty() && results[0].categories.isNotEmpty()) {
            return results[0].categories[0].score
        }

        return 0.0f
    }

    fun close() {
        classifier?.close()
    }
}
