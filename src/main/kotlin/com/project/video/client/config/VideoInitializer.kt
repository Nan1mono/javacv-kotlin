package com.project.video.client.config

import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.OpenCVFrameGrabber

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

    }

}