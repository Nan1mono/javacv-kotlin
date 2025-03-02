package com.project.video.demo

import com.project.video.demo.init.Init
import javafx.application.Application
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.bytedeco.javacv.*
import org.bytedeco.opencv.global.opencv_core
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.TargetDataLine
import kotlin.concurrent.thread

class VideoTest : Application() {
    // 用于显示视频帧
    private val imageView = ImageView()
    private val frameConverter = OpenCVFrameConverter.ToMat() // 用于 Frame 和 Mat 转换

    // 帧转换器
    private val converter = Java2DFrameConverter()

    // 控制采集线程
    private var isRunning = true

    // 视频采集器
    private lateinit var grabber: OpenCVFrameGrabber

    // FFmpeg视频解码器
    private lateinit var recorder: FFmpegFrameRecorder

    // 音频解析器
    private lateinit var audioLine: TargetDataLine

    override fun start(stage: Stage) {
        // 初始化视频采集器
        grabber = Init.initOpenCVFrameGrabber()
        // 初始化音频解码器
        val format = AudioFormat(192000F, 16, 1, true, false)
        audioLine = Init.initAudioRecorder(format)
        // 初始化FFmpeg视频解码器
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
                val startTime = System.currentTimeMillis()
                try {
                    val grab = grabber.grab()
                    val mat = frameConverter.convert(grab)
                    val flippedFrame = frameConverter.convert(mat)
                    opencv_core.flip(mat, mat, 1)
                    // 现在开始采集过程
                    // 将OpenCV的视频返回帧处理成Java Swing可以处理的BufferedImage
                    val bufferedImage = converter.convert(flippedFrame)
                    // 将BufferedImage转换成JavaFX的Image，第二个null为不使用预分配的WritableImage（性能优化选项）
                    val fxImage = SwingFXUtils.toFXImage(bufferedImage, null)
                    // 在主线程上更新UI，即视频采集到的画面通过主线渲染到提前设定好的imageView
                    Platform.runLater {
                        imageView.image = fxImage
                    }
                    try {// 设置编码并保存
                        recorder.record(flippedFrame)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("Record failed: ${e.message}")
                    }
                    // 释放反转帧
                    flippedFrame.close()
                    // 释放原始视频帧
                    grab.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // 每次循环采集时间，用户控制采集频率以实现不同帧率
                val endTime = System.currentTimeMillis()
                val elapsed = endTime - startTime
                val sleepTime = (33 - elapsed).coerceAtLeast(0) // 动态调整睡眠时间
                Thread.sleep(sleepTime)
            }
            cleanup()
        }
    }


    /**
     * 释放资源
     */
    private fun cleanup() {
        recorder.stop()
        recorder.release()
        audioLine.stop()
        audioLine.close()
        grabber.release()
    }

}

fun main() {
    Application.launch(VideoTest::class.java)
}