package com.project.video.client

import com.project.video.client.core.VideoCatch
import com.project.video.client.config.VideoInitializer
import com.project.video.client.socket.VideoSendClient
import com.project.video.toolkit.ReadYamlUtils
import javafx.application.Application
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.OpenCVFrameGrabber
import java.net.URI

class Client : Application() {

    private val sendImageView = ImageView().apply {
        fitWidth = 640.0
        fitHeight = 480.0
        isPreserveRatio = true
    }
    private val receiveImageView = ImageView().apply {
        fitWidth = 640.0
        fitHeight = 480.0
        isPreserveRatio = true
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var grabber: OpenCVFrameGrabber = VideoInitializer.initCatchGrabber()

    private lateinit var sendClient: VideoSendClient

    private val sendConfig = ReadYamlUtils.readConfigProperty("send") as Map<*, *>

    private val socketUrl = sendConfig["uri"].toString()

    // 初始化连接头，用以表示信息
    private val header = HashMap<String, String>().also {
        it["name"] =  sendConfig["name"].toString()
        it["type"] = "send"
        it["direction"] = sendConfig["direction"].toString()
    }

    override fun start(stage: Stage) {
        val hBox = HBox(10.0,sendImageView, receiveImageView)
        VideoInitializer.initDisplay(hBox, stage)
        stage.show()
        // 启动摄像头采集
        grabber.start()
        // 开启socket链接
        sendClient = VideoSendClient(URI("ws://$socketUrl"), header, grabber, receiveImageView)
        sendClient.connect()
        // 实时渲染当前摄像头画面
        scope.launch {
            VideoCatch.displayVideo(grabber, sendImageView)
        }
    }
}

fun main() {
    Application.launch(Client::class.java)
}