package com.project.video.server.socket

import com.project.video.server.core.VideoCatch
import javafx.scene.image.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.OpenCVFrameGrabber
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

class VideoSocketServer(
    port: Int,
    private val recorder: FFmpegFrameRecorder,
    private val grabber: OpenCVFrameGrabber
) :
    WebSocketServer(InetSocketAddress(port)) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("新客户端连接：${conn.remoteSocketAddress}")
        scope.launch {
            // 这里调用采集与传输逻辑
            VideoCatch.sendVideoStream(grabber, recorder, conn)
        }
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("客户端断开：${conn.remoteSocketAddress}")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        // 可选：处理客户端消息
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("WebSocket 服务器已启动")
    }

    fun sendStream(coon: WebSocket) {

    }
}