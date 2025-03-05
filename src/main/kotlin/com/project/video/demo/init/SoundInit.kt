package com.project.video.demo.init

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class SoundInit {

    companion object {
        const val AUDIO_CHANNELS = 2

        const val SAMPLE_RATE = 48000

        fun initAudioLine(): TargetDataLine {
            val audioLine = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED ,
                SAMPLE_RATE.toFloat(),
                16,
                AUDIO_CHANNELS,
                4,
                48000F,
                false,
            ).let {
                val info = DataLine.Info(TargetDataLine::class.java, it)
                if (!AudioSystem.isLineSupported(info)){
                    throw Exception("不支持音频格式")
                }
                val targetDataLine = AudioSystem.getTargetDataLine(it)
                targetDataLine.open(it)
                targetDataLine.start()
                targetDataLine
            }
            return audioLine
        }
    }

}