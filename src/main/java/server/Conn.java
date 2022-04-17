package server;

import error.Error;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mysql.User;
import protocol.con.CapabilitiesFlags;
import protocol.con.Charset;
import protocol.con.ServerStatus;
import protocol.packet.Auth;
import protocol.packet.Err;
import protocol.packet.HandshakeV10;
import util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class Conn extends SimpleChannelInboundHandler {

  private final AtomicInteger connId;

  public Conn(AtomicInteger connId) {
    this.connId = connId;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    writeHandshakePacket(ctx, this.connId);
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, Object msg) {
    ByteBuf byteBuf = (ByteBuf) msg;
    Auth a = new Auth(byteBuf);
    a.parse();
    System.out.println(a);

    User user = User.mockUser();
    if (!user.getUser().equals(a.getUserName())) {
      Err err = new Err((byte) 0xff, 1045, (byte) 1, new byte[] {1}, Error.ERROR_1045.getBytes());
      err.writeByteBuf();
      ctx.channel().writeAndFlush(err.getData());
    }
  }

  public void writeHandshakePacket(ChannelHandlerContext ctx, AtomicInteger connectionId) {

    HandshakeV10 v10 =
        new HandshakeV10(
            (byte) HandshakeV10.PROTOCOL_VERSION,
            HandshakeV10.SERVER_VERSION,
            connectionId.addAndGet(1),
            RandomUtil.randomBytes(8),
            (byte) 0,
            CapabilitiesFlags.getDefaultCapabilitiesFlags(),
            (byte) Charset.UTF8,
            (byte) ServerStatus.SERVER_STATUS_AUTOCOMMIT,
            RandomUtil.randomBytes(8),
            "mysql_native_password".getBytes());

    v10.writePacket(0);
    ctx.channel().writeAndFlush(v10.getData());

    ctx.alloc().buffer().clear();
  }
}
