# 基于JavaCV实现的视频通话

## 项目介绍

在学习JavaCV项目中，完成的一个小型项目。

技术方案：

- JavaFX
- Java-WebSocket
- JavaCV

目前支持：

- 点对点视频通话
- 双向视频

项目包含Server和Client两端，分别用于服务端和客户端。

## 如何配置

- server.port：服务端webSocket端口
- client.uri：连接服务端WebSocket的IP与端口
- client.name：启动的客户端名称
- client.direction：画面传输的目标客户端名称