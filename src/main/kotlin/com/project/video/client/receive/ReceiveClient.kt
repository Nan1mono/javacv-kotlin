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

    private val sendConfig = ReadYamlUtils.readConfigProperty("receive") as Map<*, *>

    private val socketUrl = sendConfig["uri"].toString()

    // 初始化连接头，用以表示信息
    private val header = HashMap<String, String>().also {
        it["name"] =  sendConfig["name"].toString()
        it["type"] = "receive"
        it["direction"] = sendConfig["direction"].toString()
    }

    private var client = VideoReceiveClient(URI("ws://$socketUrl"), header, imageView)

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