package com.project.video.client.receive.socket

import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.ByteBuffer


class VideoReceiveClient(uri: URI, header: HashMap<String, String>, private val imageView: ImageView) :
    WebSocketClient(uri, header) {

    override fun onOpen(handshake: ServerHandshake) {
        println("客户端链接成功，开始渲染视频画面....")
    }

    override fun onMessage(message: String) {
        println("客户端接收到消息：$message")
    }

    // 处理流
    override fun onMessage(bytes: ByteBuffer) {
        val inputStream = ByteArrayInputStream(bytes.array())
        Platform.runLater {
            imageView.image = Image(inputStream)
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println("客户端链接关闭")
    }

    override fun onError(e: Exception) {

    }
}