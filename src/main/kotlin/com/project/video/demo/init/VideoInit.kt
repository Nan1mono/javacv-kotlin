package com.project.video.demo.init

import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.bytedeco.ffmpeg.global.avcodec.*
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.OpenCVFrameGrabber

class VideoInit {

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

    }

}