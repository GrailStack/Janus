package org.xujin.janus.core.netty;

import org.xujin.janus.config.ConfigRepo;
import org.xujin.janus.config.ServerConfig;
import org.xujin.janus.config.netty.NettyServerConfig;
import org.xujin.janus.config.util.CheckUtils;
import org.xujin.janus.core.netty.server.handler.NettyServerHandler;
import org.xujin.janus.core.netty.utils.IpUtils;
import org.xujin.janus.core.netty.utils.ThreadPoolUtils;
import org.xujin.janus.daemon.admin.processor.OnlineProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 网关的Netty Server
 * @author xujin
 */
@Slf4j
@Data
public class NettyServer {

    private NettyServerConfig serverConfig;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;


    public NettyServer() {
        ServerConfig serverConfig = ConfigRepo.getServerConfig();
        this.serverConfig = serverConfig.getServerConf();
        checkServerConfig(this.serverConfig);
    }

    /**
     * 服务开启
     */
    public void start(int port) {
        //TODO 上生产时去掉
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        initEventPool();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, serverConfig.isReUseAddr())
                .option(ChannelOption.SO_BACKLOG, serverConfig.getBackLog())
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getRevBuf())
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSndBuf())
                .childOption(ChannelOption.TCP_NODELAY, serverConfig.isTcpNoDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, serverConfig.isKeepalive())
                .childHandler(initChildHandler());

        bootstrap.bind(port).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                changeOnLine(true);
                log.info("服务端启动成功【" + IpUtils.getHost() + ":" + port + "】");
            } else {
                log.error("服务端启动失败【" + IpUtils.getHost() + ":" + port + "】,cause:"
                        + channelFuture.cause().getMessage());
                shutdown(port);
            }
        });

    }

    private void checkServerConfig(NettyServerConfig nettyServerConfig) {
        CheckUtils.checkNotNull(serverConfig, "nettyServerConfig cannot be null");
        CheckUtils.checkParam(serverConfig.getBackLog() > 0, "backlog param illegal");
        CheckUtils.checkParam(serverConfig.getBossThread() > 0, "bossThread param illegal");
        CheckUtils.checkParam(serverConfig.getHeart() > 0, "heart param illegal");
        CheckUtils.checkParam(serverConfig.getMaxChunkSize() > 0, "maxChunkSize param illegal");
        CheckUtils.checkParam(serverConfig.getMaxHeaderSize() > 0, "maxHttpSize param illegal");
        CheckUtils.checkParam(serverConfig.getMaxHttpLength() > 0, "maxHttpLength param illegal");
        CheckUtils.checkParam(serverConfig.getRevBuf() > 0, "revBuf param illegal");
        CheckUtils.checkParam(serverConfig.getSndBuf() > 0, "senBuf param illegal");
        CheckUtils.checkParam(serverConfig.getWorkThread() > 0, "workThread param illegal");
    }

    @SuppressWarnings("rawtypes")
    private ChannelInitializer initChildHandler() {
        return new ChannelInitializer<SocketChannel>() {
            final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtils.makeServerThreadPool(NettyServer.class.getSimpleName());
            //网关Server的Http请求入口Handler
            final NettyServerHandler nettyServerHandler = new NettyServerHandler(serverHandlerPool);

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //initHandler(ch.pipeline(), serverConfig);
                ch.pipeline().addLast(new IdleStateHandler(180, 0, 0));
                ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));
                // We want to allow longer request lines, headers, and chunks
                // respectively.
                ch.pipeline().addLast("decoder", new HttpRequestDecoder(
                        serverConfig.getMaxInitialLineLength(),
                        serverConfig.getMaxHeaderSize(),
                        serverConfig.getMaxChunkSize()));
                //ch.pipeline().addLast("inflater", new HttpContentDecompressor());
                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(serverConfig.getMaxHttpLength()));
                //响应解码器
                ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                //目的是支持异步大文件传输（）
                ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                //网关Server的Netty HTTP连接处理类
                ch.pipeline().addLast("process", nettyServerHandler);
            }
        };
    }

    /**
     * 初始化EventPool 参数
     */
    private void initEventPool() {
        bossGroup = new NioEventLoopGroup(serverConfig.getBossThread(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Janus_Boss_" + index.incrementAndGet());
            }
        });
        workGroup = new NioEventLoopGroup(serverConfig.getWorkThread(), new ThreadFactory() {
            private AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Janus_Work_" + index.incrementAndGet());
            }
        });

    }

    /**
     * 关闭资源
     */
    public void shutdown(int port) {
        log.info("disconnected: server stop");
        if (workGroup != null && bossGroup != null) {
            try {
                bossGroup.shutdownGracefully().sync();// 优雅关闭
                workGroup.shutdownGracefully().sync();
                log.info("服务端关闭资源成功【" + IpUtils.getHost() + ":" + port + "】");

            } catch (InterruptedException e) {
                log.info("服务端关闭资源失败【" + IpUtils.getHost() + ":" + port + "】");
                System.exit(-1);
            }
        }
    }

    private static void changeOnLine(boolean changeStatus) {
        OnlineProcessor.online = changeStatus;
    }

}
