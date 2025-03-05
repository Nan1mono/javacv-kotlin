package com.project.video.server

import com.project.video.server.config.WebSocketConfig
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.apache.catalina.startup.Tomcat
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.web.socket.server.standard.ServerEndpointExporter
import java.io.File

class Server : Application() {

    // tomcat服务器，用于对外提供链接
    private lateinit var tomcat: Tomcat
    private var imageView: ImageView = ImageView()

    override fun start(stage: Stage) {
        val root = StackPane(imageView)
        val scene = Scene(root, 640.0, 480.0)
        // 窗口标题，与原代码一致
        stage.title = "摄像头窗口"
        stage.scene = scene
        // 显示窗口
        stage.show()
    }

    private fun startTomcat() {
        tomcat = Tomcat().apply {
            setPort(8705)
            addContext("", File(".").absolutePath)
        }
        val context = AnnotationConfigApplicationContext(WebSocketConfig::class.java)
        ServerEndpointExporter().apply {
            applicationContext = context
            afterPropertiesSet()
        }
        tomcat.start()
        println("Tomcat服务器启动在 http://localhost:8705")
    }
}

fun main() {
    Application.launch(Server::class.java)
}