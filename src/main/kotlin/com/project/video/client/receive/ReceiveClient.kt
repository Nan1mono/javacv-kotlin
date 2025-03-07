package com.project.video.client.receive

import com.project.video.client.receive.socket.VideoReceiveClient
import com.project.video.client.config.VideoInitializer
import com.project.video.toolkit.ReadYamlUtils
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.stage.Stage
import java.net.URI

class ReceiveClient : Application() {

    private var imageView = ImageView()

    // 初始化连接头，用以表示信息
    private val header = HashMap<String, String>().also {
        val readConfigProperty = ReadYamlUtils.readConfigProperty("receive") as Map<*, *>
        it["name"] =  readConfigProperty["name"].toString()
        it["type"] = "receive"
        it["direction"] = readConfigProperty["direction"].toString()
    }

    private var client = VideoReceiveClient(URI("ws://localhost:8333"), header, imageView)

    override fun start(stage: Stage) {
        VideoInitializer.initDisplay(imageView, stage)
        stage.show()
        client.connect()
    }

    override fun stop() {
        client.close()
        Platform.exit()
    }

}

fun main() {
    Application.launch(ReceiveClient::class.java)
}