package com.project.video.client.receive

import com.project.video.client.receive.socket.VideoSocketClient
import com.project.video.client.config.VideoInitializer
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.stage.Stage
import java.net.URI

class Client: Application() {

    private var imageView = ImageView()

    private var client = VideoSocketClient(URI("ws://localhost:8333"), imageView)

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
    Application.launch(Client::class.java)
}