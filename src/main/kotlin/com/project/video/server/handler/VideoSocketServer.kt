package com.project.video.server.handler

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArrayList

class VideoSocketServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {

    // 准备两个集合，一个是发送方socket，一个接收方socket
    private val sendClientList = CopyOnWriteArrayList<WebSocket>()
    private val receiveClientList = CopyOnWriteArrayList<WebSocket>()

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        println("新客户端连接：${conn.remoteSocketAddress}")
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

    fun sendToReceiveClient(){

    }

}