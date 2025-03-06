package com.project.video.client.send.core

import com.project.video.server.handler.VideoSocketServer
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import kotlinx.coroutines.*
import org.bytedeco.javacv.*
import org.bytedeco.opencv.global.opencv_core
import org.java_websocket.WebSocket
import java.io.*
import javax.imageio.ImageIO

class VideoCatch {

    companion object {
        // 帧转换器
        private val converter = Java2DFrameConverter()

        // 帧与画面转换器，用于调整渲染出的画面
        private val frameConverter = OpenCVFrameConverter.ToMat()

        // 运行状态标记
        private var isRunning = false

        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


        suspend fun sendVideoStream(grabber: OpenCVFrameGrabber, webSocket: WebSocket) {
            isRunning = true
            // 这里进行文件解码与发送
            while (isRunning && webSocket.isOpen) {
                // 开始抓取帧（原始帧）
                val originFrame = grabber.grab()
                val bufferedImage = converter.convert(originFrame)
                val byteArrayOutputStream = ByteArrayOutputStream()
                withContext(Dispatchers.IO) {
                    ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream) // 转换为 JPEG
                    val imageData = byteArrayOutputStream.toByteArray()
                    if (webSocket.isOpen){
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

        private fun turnFrame(frame: Frame): Frame {
            // 转换为mat用于反转画面
            val mat = frameConverter.convert(frame)
            // 反转帧
            return frameConverter.convert(mat).also {
                // 翻转画面
                opencv_core.flip(mat, mat, 1)
            }
        }

        fun stop(grabber: OpenCVFrameGrabber, socketServer: VideoSocketServer) {
            isRunning = false
            grabber.stop()
            grabber.release()
            Platform.exit()
            // 断开socket服务
            socketServer.stop()
        }
    }


}