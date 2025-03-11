package com.project.video.server.entity

import org.java_websocket.WebSocket

class SocketClient(
    // 客户端/服务端名称
    var name: String,
    // 接收端名称/发送端名称
    var direction: String,
    // socket链接服务
    var socket: WebSocket
)