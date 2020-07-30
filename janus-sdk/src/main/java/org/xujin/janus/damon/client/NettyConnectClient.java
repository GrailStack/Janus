package org.xujin.janus.damon.client;


import org.xujin.janus.damon.client.connect.ConnectClient;
import org.xujin.janus.damon.codec.NettyDecoder;
import org.xujin.janus.damon.codec.NettyEncoder;
import org.xujin.janus.damon.exchange.NettyMsg;
import org.xujin.janus.damon.idle.IIdleHandler;
import org.xujin.janus.damon.idle.UserEventTriggerHandler;
import org.xujin.janus.damon.serializer.AbstractSerializer;
import org.xujin.janus.damon.utils.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * netty pooled client
 * Netty连接客户端的设置
 * @author tbkk
 */
@Slf4j
public class NettyConnectClient extends ConnectClient {

    private EventLoopGroup group;
    private Channel channel;

    private final AbstractSerializer serializer = AbstractSerializer.SerializeEnum.PROTOSTUFF.getSerializer();

    /**
     * Netty客户端Bootstrap之ChannelOption配置
     * @param address
     * @throws Exception
     */
    @Override
    public void init(String address) throws Exception {

        Object[] array = IpUtil.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];

        this.group = new NioEventLoopGroup();
        //Netty客户端Bootstrap之ChannelOption配置
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {

                        int readIdleTime = ClientIdleHandlerPool.getSingleton().getReadIdleTime();
                        int writeIdleTime = ClientIdleHandlerPool.getSingleton().getWriteIdleTime();
                        int allIdleTime = ClientIdleHandlerPool.getSingleton().getAllIdleTime();
                        IIdleHandler readIdleHandler = ClientIdleHandlerPool.getSingleton().getReadIdleHandler();
                        IIdleHandler writeIdleHandler = ClientIdleHandlerPool.getSingleton().getWriteIdleHandler();
                        IIdleHandler allIdleHandler = ClientIdleHandlerPool.getSingleton().getAllIdleHandler();

                        channel.pipeline()
                                .addLast(new IdleStateHandler(readIdleTime,writeIdleTime,allIdleTime, TimeUnit.MILLISECONDS))
                                .addLast(new NettyEncoder(NettyMsg.class, serializer))
                                .addLast(new NettyDecoder(NettyMsg.class, serializer))
                                .addLast(new UserEventTriggerHandler(readIdleHandler, writeIdleHandler, allIdleHandler))
                                .addLast(new NettyClientHandler());
                    }
                })
                //启用TCP_NODELAY 禁用了Nagle算法，允许小包的发送，
                .option(ChannelOption.TCP_NODELAY, true)
                //启用keepalive机制做心跳检测
                .option(ChannelOption.SO_KEEPALIVE, true)
                //Netty Client的
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        this.channel = bootstrap.connect(host, port).sync().channel();
        // valid
        if (!isValidate()) {
            close();
            return;
        }
        log.debug(" janus-cmd netty client proxy, connect to server success at host:{}, " +
                "port:{}", host, port);
    }


    @Override
    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }

    @Override
    public void close() {
        if (this.channel != null && this.channel.isActive()) {
            this.channel.close();        // if this.channel.isOpen()
        }
        if (this.group != null && !this.group.isShutdown()) {
            this.group.shutdownGracefully();
        }
        log.debug(" janus-cmd netty client close.");
    }


    @Override
    public void send(NettyMsg msg) throws Exception {
        this.channel.writeAndFlush(msg).sync();
    }
}
