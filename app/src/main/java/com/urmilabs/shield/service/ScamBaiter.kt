package com.urmilabs.shield.service

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class ScamBaiter(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val isInitialized = MutableStateFlow(false)

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isInitialized.value = true
        }
    }

    fun speak(text: String) {
        if (isInitialized.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
        tts?.shutdown()
    }
}
