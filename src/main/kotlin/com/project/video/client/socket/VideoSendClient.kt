package com.project.video.client.socket

import com.project.video.client.core.VideoCatch
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.OpenCVFrameGrabber
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.ByteBuffer

class VideoSendClient(
    uri: URI,
    header: HashMap<String, String>,
    private val grabber: OpenCVFrameGrabber,
    private val receiveImage: ImageView
) : WebSocketClient(uri, header) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onOpen(handshake: ServerHandshake) {
        println("客户端链接成功，开始发送视频画面....")
        send("request")
        scope.launch {
            VideoCatch.sendVideoStream(grabber, this@VideoSendClient, scope.coroutineContext)
        }
    }

    override fun onMessage(message: String) {

    }

    // 处理流
    override fun onMessage(bytes: ByteBuffer) {
        val inputStream = ByteArrayInputStream(bytes.array())
        Platform.runLater {
            receiveImage.image = Image(inputStream)
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println("客户端链接关闭")
    }

    override fun onError(e: Exception) {

    }
}