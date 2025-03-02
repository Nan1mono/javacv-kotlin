package com.project.video.demo.init

import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.bytedeco.ffmpeg.global.avcodec.*
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.OpenCVFrameGrabber
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.DataLine.*
import javax.sound.sampled.TargetDataLine

class Init {

    companion object {
        fun initOpenCVFrameGrabber(): OpenCVFrameGrabber {
            val grabber = OpenCVFrameGrabber(0)
            // 设置采集器大小
            grabber.imageWidth = 640
            grabber.imageHeight = 480
            // 启动采集器
            grabber.start()
            return grabber
        }

        fun initFfmpegRecord(grabber: OpenCVFrameGrabber): FFmpegFrameRecorder {
            // 初始化FFmpeg视频解码器
            val recorder = FFmpegFrameRecorder("output.mp4", grabber.imageWidth, grabber.imageHeight)
            // 设置视频编码格式为H264
            recorder.videoCodec = AV_CODEC_ID_H264
            // 设置视频输出格式为H264
            recorder.format = "mp4"
            // 设置帧率为30帧
            recorder.frameRate = 30.0
            // 设置画面质量（质量越小越高），默认为23
            recorder.videoQuality = 23.0

            // 设置音频格式为OPUS
            recorder.audioCodec = AV_CODEC_ID_AAC
            recorder.audioChannels = 1
            recorder.audioBitrate = 192000 // Opus 推荐比特率
            recorder.sampleRate = 44100 // 与音频采集一致


            recorder.start()
            return recorder
        }

        fun initFxSwing(imageView: ImageView, stage: Stage) {
            val root = StackPane(imageView)
            val scene = Scene(root, 640.0, 480.0)
            // 窗口标题，与原代码一致
            stage.title = "摄像头窗口"
            stage.scene = scene
            // 显示窗口
            stage.show()
        }

        fun initAudioRecorder(format: AudioFormat): TargetDataLine {
            // 设置音频格式：44.1kHz, 16-bit, 单声道
            // 创建音频数据线对象
            val info = Info(TargetDataLine::class.java, format)
            // 从音频输入对象中获取相应的音频数据
            val audioLine = AudioSystem.getLine(info) as TargetDataLine
            // 数据转码
            audioLine.open(format)
            // 开启服务
            audioLine.start()
            return audioLine
        }
    }

}