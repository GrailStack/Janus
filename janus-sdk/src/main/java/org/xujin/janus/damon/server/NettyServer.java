package org.xujin.janus.damon.server;

import org.xujin.janus.damon.Server;
import org.xujin.janus.damon.codec.NettyDecoder;
import org.xujin.janus.damon.codec.NettyEncoder;
import org.xujin.janus.damon.exchange.NettyMsg;
import org.xujin.janus.damon.idle.IIdleHandler;
import org.xujin.janus.damon.idle.UserEventTriggerHandler;
import org.xujin.janus.damon.serializer.AbstractSerializer;
import org.xujin.janus.damon.utils.ThreadPoolUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyServer implements Server {

    //private Thread thread;
    private ExecutorService nettyServerSingleThreadExecutor;

    private final AbstractSerializer serializer = AbstractSerializer.SerializeEnum.PROTOSTUFF.getSerializer();

    @Override
    public void start(int port, Map processerMap, int readIdleTime, IIdleHandler readIdleHandler, int writeIdleTime, IIdleHandler writeIdleHandler, int allIdleTime, IIdleHandler allIdleHandler) throws Exception {

        nettyServerSingleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setName("netty-server");
            return thread;
        });

        nettyServerSingleThreadExecutor.execute(() -> {
            final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.makeServerThreadPool(NettyServer.class.getSimpleName());
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            //ChannelInitializer唯一，可以在这里添加单例
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                         //读超时时间设置
                                        .addLast(new IdleStateHandler(readIdleTime,writeIdleTime,allIdleTime, TimeUnit.MILLISECONDS))
                                        .addLast(new NettyDecoder(NettyMsg.class, serializer))
                                        .addLast(new NettyEncoder(NettyMsg.class, serializer))
                                        .addLast(new UserEventTriggerHandler(readIdleHandler, writeIdleHandler, allIdleHandler))
                                        .addLast(new NettyServerHandler(serverHandlerPool, processerMap));
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(port).sync();

                log.info(" janus-cmd remoting server start success, nettype = {}, port = {}", NettyServer.class.getName(), port);

                // wait util stop
                future.channel().closeFuture().sync();

            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    log.info(" janus-cmd remoting server stop.");
                } else {
                    log.error(" janus-cmd remoting server error.", e);
                }
            } finally {

                // stop
                try {
                    serverHandlerPool.shutdown();    // shutdownNow
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        });

//        thread = new Thread(() -> {
//        });
//        thread.setDaemon(true);
//        thread.start();

    }

    @Override
    public void stop() throws Exception {

        // destroy server thread
//        if (thread != null && thread.isAlive()) {
//            thread.interrupt();
//        }
        nettyServerSingleThreadExecutor.shutdown();

        // on stop
        //stopCallBack();
        log.info(" janus-cmd remoting server destroy success.");
    }

}
