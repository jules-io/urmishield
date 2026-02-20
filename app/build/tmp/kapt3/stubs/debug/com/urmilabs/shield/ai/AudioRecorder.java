package com.urmilabs.shield.ai;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0017\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\t\u001a\u00020\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\n0\fJ\u0006\u0010\u000e\u001a\u00020\nR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/urmilabs/shield/ai/AudioRecorder;", "", "()V", "audioRecord", "Landroid/media/AudioRecord;", "bufferSize", "", "isRecording", "", "startRecording", "", "onAudioData", "Lkotlin/Function1;", "", "stopRecording", "app_debug"})
public final class AudioRecorder {
    @org.jetbrains.annotations.Nullable()
    private android.media.AudioRecord audioRecord;
    private boolean isRecording = false;
    private final int bufferSize = 0;

    public AudioRecorder() {
        super();
    }

    public final void startRecording(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super short[], kotlin.Unit> onAudioData) {
    }

    public final void stopRecording() {
    }
}