package com.project.video.server.core

import com.project.video.server.socket.VideoSocketServer
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import kotlinx.coroutines.*
import org.bytedeco.javacv.*
import org.bytedeco.opencv.global.opencv_core
import org.java_websocket.WebSocket
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer

class VideoCatch {

    companion object {
        // 帧转换器
        private val converter = Java2DFrameConverter()

        // 帧与画面转换器，用于调整渲染出的画面
        private val frameConverter = OpenCVFrameConverter.ToMat()

        // 运行状态标记
        private var isRunning = false

        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())



        suspend fun sendVideoStream(grabber: OpenCVFrameGrabber, recorder: FFmpegFrameRecorder, webSocket: WebSocket) {
            val file = File("temp_video.h264")
            val fileInputStream = withContext(Dispatchers.IO) {
                FileInputStream(file)
            }
            // 1MB 缓冲区，每次发送1MB数据
            val buffer = ByteArray(1024 * 1024)
            isRunning = true
            // 使用协程进行画面录制文件
            scope.launch {
                // 运行状态标记为true，并且socketSession为开时，开始传输画面
                while (isRunning) {
                    // 开始抓取帧（原始帧）
                    val originFrame = grabber.grab()
                    recorder.record(originFrame)
                    // 关闭原始帧和反转真
                    originFrame.close()
                    delay(33)
                }
            }
            // 这里进行文件解码与发送
            while (isRunning && webSocket.isOpen) {
                // 读取文件流
                withContext(Dispatchers.IO) {
                    val bytesRead = fileInputStream.read(buffer)
                    if (bytesRead > 0) {
                        val lengthBytes = ByteBuffer.allocate(4).putInt(bytesRead).array()
                        webSocket.send(lengthBytes) // 发送数据长度
                        webSocket.send(buffer.copyOfRange(0, bytesRead)) // 发送实际数据
                    }
                }
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

        fun stop(grabber: OpenCVFrameGrabber, recorder: FFmpegFrameRecorder, socketServer: VideoSocketServer) {
            isRunning = false
            recorder.stop()
            grabber.stop()
            grabber.release()
            Platform.exit()
            // 断开socket服务
            socketServer.stop()
            // 清理临时文件
            File("temp_video.h264").delete()
        }
    }


}