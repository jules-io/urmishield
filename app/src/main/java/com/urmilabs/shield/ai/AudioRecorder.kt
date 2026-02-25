package com.urmilabs.shield.ai

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer

interface AudioRecorder {
    fun startRecording(): Flow<ShortArray>
    fun stopRecording()
}

class RealAudioRecorder : AudioRecorder {
    private var isRecording = false
    private var audioRecord: AudioRecord? = null

    override fun startRecording(): Flow<ShortArray> = flow {
        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        val buffer = ShortArray(bufferSize / 2)
        while (isRecording) {
            val readResult = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (readResult > 0) {
                emit(buffer.copyOf())
            }
        }
    }

    override fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
