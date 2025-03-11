package com.project.video.server

import com.project.video.server.handler.VideoSocketServer
import com.project.video.toolkit.ReadYamlUtils

class Server {



    companion object{
        private val serverConfig = ReadYamlUtils.readConfig()["server"] as Map<*, *>
        private val serverPort = serverConfig["port"] as Int

        @JvmStatic
        fun main(args: Array<String>) {
            val videoSocketServer = VideoSocketServer(serverPort)
            videoSocketServer.start()
        }
    }
}