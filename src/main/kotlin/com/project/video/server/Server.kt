package com.project.video.server

import com.project.video.server.handler.VideoSocketServer

class Server

fun main() {
    val videoSocketServer = VideoSocketServer(8333)
    videoSocketServer.start()
}