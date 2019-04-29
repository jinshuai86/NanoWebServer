# NanoWebServer
基于IO多路复用的WebServer，采用LT形式的epoll作为实现。整体采用多Reactor模型。

## Finished
- [x] 多Reactor模型设计
- [x] 接收请求返回响应

## TODO
- [ ] 解析HTTP报文
- [ ] 子Reactor采用线程池