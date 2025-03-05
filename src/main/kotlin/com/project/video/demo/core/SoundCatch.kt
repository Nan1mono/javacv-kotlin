package com.project.video.demo.core

import com.project.video.demo.init.SoundInit.Companion.AUDIO_CHANNELS
import com.project.video.demo.init.SoundInit.Companion.SAMPLE_RATE
import org.bytedeco.javacv.FFmpegFrameRecorder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.TargetDataLine

class SoundCatch {

    companion object {
        fun catch(audioLine: TargetDataLine, recorder: FFmpegFrameRecorder){
            val audioBufferSize = SAMPLE_RATE  * AUDIO_CHANNELS
            val audioBytes  = ByteArray(audioBufferSize)
            val nBytesRead  =audioLine.read(audioBytes, 0, audioLine.available())
            val nSamplesRead = nBytesRead / 2
            val samples = ShortArray(nSamplesRead)
            ByteBuffer.wrap(audioBytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(samples)
            val shortBuffer = ShortBuffer.wrap(samples, 0, nSamplesRead)
            recorder.recordSamples(SAMPLE_RATE, AUDIO_CHANNELS, shortBuffer)
        }

    }

}