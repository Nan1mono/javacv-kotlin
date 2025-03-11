package com.project.video.client.config

import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
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
                imageWidth = 1290
                imageHeight = 480
            }
            return grabber
        }

        fun initDisplay(hBox: HBox, stage: Stage) {
            stage.apply {
                // 窗口标题，与原代码一致
                stage.title = "我的窗口"
                stage.scene = Scene(hBox, 1290.0, 480.0)
            }
        }

    }

}