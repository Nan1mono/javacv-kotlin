package com.project.video.client.send

import com.project.video.client.send.core.VideoCatch
import com.project.video.client.config.VideoInitializer
import com.project.video.server.handler.VideoSocketServer
import javafx.application.Application
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.OpenCVFrameGrabber

class SendClient : Application() {

    private var imageView: ImageView = ImageView()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val port = 8333

    private var grabber: OpenCVFrameGrabber = VideoInitializer.initCatchGrabber()

    private lateinit var socketServer: VideoSocketServer

    override fun start(stage: Stage) {
        VideoInitializer.initDisplay(imageView, stage)
        stage.show()
        // 启动摄像头采集
        grabber.start()
        // 开启socket链接
        socketServer = VideoSocketServer(port)
        socketServer.start()
        // 实时渲染当前摄像头画面
        scope.launch {
            VideoCatch.displayVideo(grabber, imageView)
        }
    }

    override fun stop() {
        VideoCatch.stop(grabber, socketServer)
    }
}

fun main() {
    Application.launch(SendClient::class.java)
}