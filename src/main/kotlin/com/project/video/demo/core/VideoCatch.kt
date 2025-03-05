package com.project.video.demo.core

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.javacv.OpenCVFrameGrabber
import org.bytedeco.opencv.global.opencv_core

class VideoCatch {

    companion object {
        fun catch(
            grabber: OpenCVFrameGrabber,
            frameConverter: OpenCVFrameConverter.ToMat,
            converter: Java2DFrameConverter,
            imageView: ImageView,
            recorder: FFmpegFrameRecorder,
        ) {
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
    }


}