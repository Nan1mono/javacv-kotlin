package com.project.video.client

import com.project.video.client.socket.VideoSocketClient
import com.project.video.init.VideoInitializer
import javafx.application.Application
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

}

fun main() {
    Application.launch(Client::class.java)
}