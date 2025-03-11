package com.project.video.server.handler

import com.project.video.server.entity.SocketClient
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.concurrent.CopyOnWriteArrayList

class VideoSocketServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {

    companion object {
        // 准备两个集合，接收与发送方
        val CONN_LIST = CopyOnWriteArrayList<SocketClient>()
    }


    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        // 获取连接头内容
        val name = handshake.getFieldValue("name")
        val direction = handshake.getFieldValue("direction")
        CONN_LIST.add(SocketClient(name, direction, conn))
        println("新客户端连接：${conn.remoteSocketAddress},name=$name,direction=$direction")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        println("客户端断开连接：${conn.remoteSocketAddress},code=$code,reason=$reason")
        CONN_LIST.removeIf { it.socket == conn }
    }

    override fun onMessage(conn: WebSocket, message: String) {
        if (message == "request") {
            conn.send("connect successful!")
        }
    }

    override fun onMessage(conn: WebSocket, message: ByteBuffer) {
        // 判断消息是否来自发送方
        val send = CONN_LIST.stream().filter { it.socket == conn }.findFirst().orElse(null)
        // 如果发送方已注册到集合中，获取其接收链接并向其发送消息
        if (send != null) {
            val receive =
                CONN_LIST.stream().filter { it.direction == send.name }.findFirst().orElse(null)
            receive?.socket?.send(message)
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
        println("WebSocket 服务器已启动")
    }

}