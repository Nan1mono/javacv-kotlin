package com.project.video.init

import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.OpenCVFrameGrabber
import java.io.PipedInputStream

class VideoInitializer {

    companion object {
        private lateinit var grabber: OpenCVFrameGrabber

        /**
         * 初始化视频采集器
         * @return [FFmpegFrameGrabber]
         */
        fun initCatchGrabber(): OpenCVFrameGrabber {
            grabber = OpenCVFrameGrabber(0).apply {
                // 设置采集器大小
                imageWidth = 640
                imageHeight = 480
            }
            return grabber
        }

        fun initDisplay(imageView: ImageView, stage: Stage) {
            stage.apply {
                // 窗口标题，与原代码一致
                stage.title = "摄像头窗口"
                stage.scene = Scene(StackPane(imageView), 640.0, 480.0)
            }
        }

        // 初始化record编码器，用于将画面编码为H264编解码
        fun initRecorder(grabber: OpenCVFrameGrabber): FFmpegFrameRecorder {
            return FFmpegFrameRecorder(
                "temp_video.h264",
                grabber.imageWidth,
                grabber.imageHeight
            ).apply {
                // 视频编码格式
                videoCodec = AV_CODEC_ID_H264
                format = "h264"
                // 帧率
                frameRate = 30.0
                // 码率
                videoBitrate = 500000
                setOption("preset", "ultrafast")
                setOption("tune", "zerolatency")
            }
        }

        // 初始化视频接收器
        fun initReceiveGrabber(pipedInputStream: PipedInputStream): FFmpegFrameGrabber {
            return FFmpegFrameGrabber(pipedInputStream).apply {
                videoCodec = org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264
                format = "h264"
                imageWidth = 640
                imageHeight = 480
            }
        }

    }

}