package com.project.video.server

import com.project.video.server.handler.VideoSocketServer

class Server {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val videoSocketServer = VideoSocketServer(8333)
            videoSocketServer.start()
        }
    }
}