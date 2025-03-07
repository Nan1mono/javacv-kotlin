package com.project.video.client.send

import com.project.video.client.send.core.VideoCatch
import com.project.video.client.config.VideoInitializer
import com.project.video.client.send.socket.VideoSendClient
import com.project.video.toolkit.ReadYamlUtils
import javafx.application.Application
import javafx.scene.image.ImageView
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.bytedeco.javacv.OpenCVFrameGrabber
import java.net.URI

class SendClient : Application() {

    private var imageView: ImageView = ImageView()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var grabber: OpenCVFrameGrabber = VideoInitializer.initCatchGrabber()

    private lateinit var sendClient: VideoSendClient

    // 初始化连接头，用以表示信息
    private val header = HashMap<String, String>().also {
        val readConfigProperty = ReadYamlUtils.readConfigProperty("send") as Map<*, *>
        it["name"] =  readConfigProperty["name"].toString()
        it["type"] = "send"
        it["direction"] = readConfigProperty["direction"].toString()
    }

    override fun start(stage: Stage) {
        VideoInitializer.initDisplay(imageView, stage)
        stage.show()
        // 启动摄像头采集
        grabber.start()
        // 开启socket链接
        sendClient = VideoSendClient(URI("ws://localhost:8333"), header, grabber)
        sendClient.connect()
        // 实时渲染当前摄像头画面
        scope.launch {
            VideoCatch.displayVideo(grabber, imageView)
        }
    }
}

fun main() {
    Application.launch(SendClient::class.java)
}