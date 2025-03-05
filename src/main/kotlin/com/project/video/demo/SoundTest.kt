package com.project.video.demo

import com.project.video.demo.core.SoundCatch
import com.project.video.demo.init.SoundInit
import com.project.video.demo.init.SoundInit.Companion.AUDIO_CHANNELS
import com.project.video.demo.init.SoundInit.Companion.SAMPLE_RATE
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_AAC
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3
import org.bytedeco.javacv.FFmpegFrameRecorder
import kotlin.concurrent.thread

class SoundTest {

    private var isRunning = false

    private var audioLine = SoundInit.initAudioLine()

    private lateinit var recorder: FFmpegFrameRecorder

    private lateinit var thread: Thread

    fun record() {
        isRunning = true
        audioLine = SoundInit.initAudioLine()
        recorder = FFmpegFrameRecorder("output.aac", AUDIO_CHANNELS).apply {
            sampleRate = SAMPLE_RATE
            audioChannels = AUDIO_CHANNELS
            audioCodec = AV_CODEC_ID_AAC
        }.also {
            it.start()
        }
        thread = thread {
            while (isRunning) {
                SoundCatch.catch(audioLine, recorder)
            }
            recorder.stop()
            recorder.release()
            audioLine.stop()
            audioLine.close()
        }
        thread.start()
    }

    fun stop(){
        isRunning = false
        thread.join()
        println("录音结束...")
    }

}

fun main() {
    println("开始录音...")
    val soundTest = SoundTest()
    soundTest.record()
    // 录制10秒
    Thread.sleep(10000)
    soundTest.stop()
    println("录音结束...")
}