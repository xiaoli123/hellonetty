package com.xiaoli.study.chapter1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Echo服务端
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        //自定义的处理器, 用来接收请求处理请求
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //指定EventLoopGroup以接收客户端事件,此处用nio
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //指定EventLoopGroup以处理客户端事件,此处用nio
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //使用引导程序构建和启动服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup) //设置loopGroup
                    .channel(NioServerSocketChannel.class) //设置channel类型
                    .localAddress(new InetSocketAddress(port)) //设置绑定的端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { //设置channelInitializer, 并重写它的initChannel方法在pipeline中添加serverHandler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            // 异步绑定服务器; 调用sync方法阻塞等待直到绑定完成
            ChannelFuture f = bootstrap.bind().sync();
            // 获取channel的closeFuture, 并且阻塞当前线程, 知道它完成(不懂)
            f.channel().closeFuture().sync();
        } finally {
            //释放资源
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new EchoServer(8899).start();
    }
}
