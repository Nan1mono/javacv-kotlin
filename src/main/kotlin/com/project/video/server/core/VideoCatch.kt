package com.project.video.server.core

import kotlinx.coroutines.delay
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.opencv.global.opencv_core
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.WebSocketSession
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.imageio.ImageIO

class VideoCatch {

    companion object {
        // 帧转换器
        private val converter = Java2DFrameConverter()

        // 帧与画面转换器，用于调整渲染出的画面
        private val frameConverter = OpenCVFrameConverter.ToMat()

        // 运行状态标记
        var isRunning = false

        suspend fun catch(grabber: FFmpegFrameGrabber, session: WebSocketSession) {
            isRunning = true
            // 运行状态标记为true，并且socketSession为开时，开始传输画面
            while (isRunning && session.isOpen) {
                // 开始抓取帧（原始帧）
                val originFrame = grabber.grab()
                // 转换为mat用于反转画面
                val mat = frameConverter.convert(originFrame)
                // 反转帧
                val flippedFrame = frameConverter.convert(mat)
                // 反转画面
                opencv_core.flip(mat, mat, 1)
                // 将画面帧转换为图片
                val bufferedImage = converter.convert(flippedFrame)
                val imgStream = ByteArrayOutputStream()
                ImageIO.write(bufferedImage, "jpg", imgStream)
                // 转换成字节数据
                val message = BinaryMessage(ByteBuffer.wrap(imgStream.toByteArray()))
                if (session.isOpen) {
                    session.sendMessage(message)
                }
                delay(33)
            }
        }

        fun stop(grabber: FFmpegFrameGrabber){
            isRunning = false
            grabber.stop()
            grabber.release()
        }
    }


}