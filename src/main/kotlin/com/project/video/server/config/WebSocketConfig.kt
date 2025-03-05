package com.project.video.server.config

import com.project.video.server.handler.VideoWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
open class WebSocketConfig: WebSocketConfigurer {

    // 注册webSocket处理器
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(videoWebSocketHandler(), "/video-stream").setAllowedOrigins("*")
    }

    @Bean
    open fun videoWebSocketHandler(): VideoWebSocketHandler {
        return VideoWebSocketHandler()
    }


}