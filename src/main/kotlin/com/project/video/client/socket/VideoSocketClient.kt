package com.project.video.client.socket

import com.project.video.client.handler.VideoClientHandler
import com.project.video.init.VideoInitializer
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.ByteArrayInputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.net.URI
import java.nio.ByteBuffer


class VideoSocketClient(uri: URI, private val imageView: ImageView) : WebSocketClient(uri) {

    private val pipedOutputStream = PipedOutputStream()
    private val pipedInputStream = PipedInputStream(pipedOutputStream)
    private val grabber = VideoInitializer.initReceiveGrabber(pipedInputStream)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onOpen(handshake: ServerHandshake) {
        println("客户端链接成功，开始渲染到数据管道....")
        // 开始渲染到管道
        grabber.start()
        val handler = VideoClientHandler(imageView, this, grabber)
        scope.launch {
            handler.display()
        }
    }

    override fun onMessage(message: String) {

    }

    // 处理流
    override fun onMessage(bytes: ByteBuffer) {
        // 处理二进制消息
        val lengthBytes = ByteArray(4)
        bytes.get(lengthBytes) // 读取长度前缀
        val length = ByteBuffer.wrap(lengthBytes).int
        val data = ByteArray(length)
        bytes.get(data) // 读取数据
        // 将数据写入管道流
        pipedOutputStream.write(data)
        pipedOutputStream.flush()
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println("客户端链接关闭")
        pipedOutputStream.close()
        pipedInputStream.close()
        grabber.stop()
        grabber.release()
    }

    override fun onError(e: Exception) {

    }
}