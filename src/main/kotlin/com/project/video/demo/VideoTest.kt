package com.project.video.demo

import com.project.video.demo.core.VideoCatch
import com.project.video.demo.init.Init
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.bytedeco.javacv.*
import java.nio.Buffer
import java.nio.ByteBuffer
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.concurrent.thread

class VideoTest : Application() {
    // 用于显示视频帧
    private val imageView = ImageView()

    // 用于 Frame 和 Mat 转换
    private val frameConverter = OpenCVFrameConverter.ToMat()

    // 帧转换器
    private val converter = Java2DFrameConverter()

    // 控制采集线程
    private var isRunning = true

    // 视频采集器
    private lateinit var grabber: OpenCVFrameGrabber

    // FFmpeg视频解码器
    private lateinit var recorder: FFmpegFrameRecorder

    override fun start(stage: Stage) {
        // 初始化视频采集器
        grabber = Init.initOpenCVFrameGrabber()
        recorder = Init.initFfmpegRecord(grabber)
        // 创建 JavaFX 窗口
        Init.initFxSwing(imageView, stage)
        // 窗口关闭时停止采集
        stage.setOnCloseRequest {
            isRunning = false
            // 窗口关闭时，释放资源
            cleanup()
            Platform.exit()
        }

        // 通过独立的线程，是采集和展示互不干扰，避免造成两个线程阻塞
        thread {
            while (isRunning && stage.isShowing) {
                VideoCatch.catch(grabber, frameConverter, converter, imageView, recorder)
            }
        }
    }


    /**
     * 释放资源
     */
    private fun cleanup() {
        recorder.stop()
        recorder.release()
        grabber.release()
    }

}

fun main() {
    Application.launch(VideoTest::class.java)
}