package com.project.video.server.core

import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.WebSocketSession
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

class VideoInitializer {

    companion object {
        private lateinit var grabber: FFmpegFrameGrabber


        /**
         * 初始化视频次啊及其
         * @return [FFmpegFrameGrabber]
         */
        fun initGrabber(): FFmpegFrameGrabber {
            grabber = FFmpegFrameGrabber("0").apply {
                // 设置采集器大小
                grabber.imageWidth = 640
                grabber.imageHeight = 480
            }.also { it.start() }
            return grabber
        }

    }

}