package com.project.video.client.core

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import kotlinx.coroutines.*
import org.bytedeco.javacv.*
import org.java_websocket.WebSocket
import java.io.*
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

class VideoCatch {

    companion object {
        // 帧转换器
        private val converter = Java2DFrameConverter()

        // 运行状态标记
        private var isRunning = false

        suspend fun sendVideoStream(grabber: OpenCVFrameGrabber, webSocket: WebSocket, context: CoroutineContext) {
            isRunning = true
            // 这里进行文件解码与发送
            while (isRunning && webSocket.isOpen) {
                // 开始抓取帧（原始帧）
                val originFrame = grabber.grab()
                val bufferedImage = converter.convert(originFrame)
                val byteArrayOutputStream = ByteArrayOutputStream()
                withContext(context) {
                    ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream) // 转换为 JPEG
                    val imageData = byteArrayOutputStream.toByteArray()
                    if (webSocket.isOpen) {
                        webSocket.send(imageData)
                    }
                    // 关闭原始帧
                    originFrame.close()
                }
                delay(33)
            }

        }

        suspend fun displayVideo(grabber: OpenCVFrameGrabber, imageView: ImageView) = withContext(Dispatchers.Default) {
            isRunning = true
            while (isRunning) {
                // 开始抓取帧（原始帧）
                val originFrame = grabber.grab()
                // 将OpenCV的视频返回帧处理成Java Swing可以处理的BufferedImage
                val bufferedImage = converter.convert(originFrame)
                // 渲染到窗口
                Platform.runLater {
                    imageView.image = SwingFXUtils.toFXImage(bufferedImage, null)
                }
                // 释放原始视频帧
                originFrame.close()
            }
        }
    }

}