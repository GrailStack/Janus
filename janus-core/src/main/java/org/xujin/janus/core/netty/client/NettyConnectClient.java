package org.xujin.janus.core.netty.client;

import org.xujin.janus.core.constant.NettyConstants;
import org.xujin.janus.core.netty.client.connect.ConnectClient;
import org.xujin.janus.core.netty.client.handler.NettyClientHandler;
import org.xujin.janus.core.netty.ctx.DefaultJanusCtx;
import org.xujin.janus.core.netty.utils.IpUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * netty pooled client
 *
 * @author tbkk
 */
@Slf4j
public class NettyConnectClient extends ConnectClient {

    public  NioEventLoopGroup clientGroup = new NioEventLoopGroup(0,
            new DefaultThreadFactory("Janus-client-Worker", true));

    private Channel channel;

    @Override
    public void init(String address) throws Exception {

        Object[] array = IpUtils.parseIpPort(address);
        String host = (String) array[0];
        int port = (int) array[1];

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
//                                .addLast(new HttpClientCodec())
//                                .addLast(new HttpObjectAggregator(65536))
                                //解压
                                //.addLast(new HttpContentDecompressor())
                                .addLast(new HttpClientCodec(NettyConstants.MAX_INITIAL_LINE_LENGTH,NettyConstants.MAX_HEADER_SIZE, NettyConstants.MAX_CHUNK_SIZE),
                                new HttpObjectAggregator(NettyConstants.MAX_RESPONSE_SIZE))
                                //Netty Client把请求转发给内网源服务的Handler
                                .addLast("handler", new NettyClientHandler());
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60*1000);
        this.channel = bootstrap.connect(host, port).sync().channel();
        // valid
        if (!isValidate()) {
            close();
            return;
        }

        log.debug("netty client proxy, connect to server success at host:{}, port:{}", host, port);
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
        if (this.clientGroup != null && !this.clientGroup.isShutdown()) {
            this.clientGroup.shutdownGracefully();
        }
        log.debug("netty client close.");
    }

    @Override
    public void send(HttpRequest httpRequest, DefaultJanusCtx ctx) throws Exception {
        //发送请求通过AttributeKey标记Channel
        AttributeKey<DefaultJanusCtx> ctxAttributeKey = AttributeKey.valueOf(NettyConstants.CHANNEL_CTX_KEY);
        ctx.setInnerServerChannel(channel);
        this.channel.attr(ctxAttributeKey).set(ctx);
        this.channel.writeAndFlush(httpRequest).sync();
    }
}
