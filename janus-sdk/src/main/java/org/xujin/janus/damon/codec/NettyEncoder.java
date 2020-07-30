package org.xujin.janus.damon.codec;

import org.xujin.janus.damon.serializer.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private AbstractSerializer serializer;

    public NettyEncoder(Class<?> genericClass, final AbstractSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}