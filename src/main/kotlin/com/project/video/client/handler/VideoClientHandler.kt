package com.project.video.client.handler

import com.project.video.client.socket.VideoSocketClient
import com.project.video.init.VideoInitializer
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter

class VideoClientHandler(
    private val imageView: ImageView,
    private val socketClient: VideoSocketClient,
    private val grabber: FFmpegFrameGrabber
) {
    private val converter = Java2DFrameConverter()

    suspend fun display() = withContext(Dispatchers.IO) {
        while (socketClient.isOpen) {
            val frame = grabber.grab() ?: continue
            val bufferedImage = converter.convert(frame)
            val fxImage = SwingFXUtils.toFXImage(bufferedImage, null)
            Platform.runLater { imageView.image = fxImage }
        }
    }

}