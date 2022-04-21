package util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class CtxUtil {

    public static void writeAndFlush(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        ctx.channel().writeAndFlush(byteBuf);
        ctx.alloc().buffer().clear();
    }
}
