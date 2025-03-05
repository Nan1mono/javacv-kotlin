package com.project.video.server.handler

import com.project.video.server.core.VideoCatch
import com.project.video.server.core.VideoInitializer
import kotlinx.coroutines.*
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler

/**
 * webSocket 视频处理器
 */
class VideoWebSocketHandler : BinaryWebSocketHandler() {

    // 视频处理器，用于视频采集
    private lateinit var grabber: FFmpegFrameGrabber
    private lateinit var job: Job

    // 建立连接后自动调用
    override fun afterConnectionEstablished(session: WebSocketSession) {
        grabber = VideoInitializer.initGrabber()
        runBlocking {
            job = launch(Dispatchers.IO) {
                VideoCatch.catch(grabber, session)
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        VideoCatch.stop(grabber)
        runBlocking {
            job.cancelAndJoin()
        }
    }

}