package server;

import error.Error;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import mysql.User;
import protocol.con.CapabilitiesFlags;
import protocol.con.Charset;
import protocol.con.ServerStatus;
import protocol.packet.AuthPacket;
import protocol.packet.ErrorPacket;
import protocol.packet.HandshakeV10Packet;
import protocol.packet.OkPacket;
import util.ByteUtil;
import util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Conn extends SimpleChannelInboundHandler {

  private static final int HANDSHAKE_STATUS = 0;
  private static final int AUTH_STATUS = 1;

  private int connStatus;
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

    switch (this.connStatus) {
      case HANDSHAKE_STATUS:
        parseAuthPacketAndResponse(ctx, byteBuf);
        break;
      case AUTH_STATUS:
        ByteUtil.printByteBufString(byteBuf);
        OkPacket ok = new OkPacket(1, 0, 2, 0, "OK!");
        ok.writeBytes();
        ctx.channel().writeAndFlush(ok.getData());
        break;
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
    ctx.alloc().buffer().clear();
  }

  public void writeHandshakePacket(ChannelHandlerContext ctx, AtomicInteger connId) {
    this.connStatus = HANDSHAKE_STATUS;

    HandshakeV10Packet v10 =
        new HandshakeV10Packet(
            (byte) HandshakeV10Packet.PROTOCOL_VERSION,
            HandshakeV10Packet.SERVER_VERSION,
            connId.addAndGet(1),
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

  public void parseAuthPacketAndResponse(ChannelHandlerContext ctx, ByteBuf byteBuf) {
    this.connStatus = AUTH_STATUS;

    AuthPacket a = new AuthPacket(byteBuf);
    a.parse();

    ctx.alloc().buffer().clear();

    String hasPassword = "NO";
    if (a.getAuthResponse() != null) {
      hasPassword = "YES";
    }
    User user = User.mockUser();
    if (!user.getUser().equals(a.getUserName())) {
      ErrorPacket err =
          new ErrorPacket(
              (byte) 0xff,
              1045,
              (byte) 1,
              new byte[] {1},
              Error.ERROR_1045(a.getUserName(), hasPassword).getBytes());
      err.writePacket();
      ctx.channel().writeAndFlush(err.getData());
    } else {
      OkPacket ok = new OkPacket(0, 0, 2, 0, "OK!");
      ok.writeBytes();
      ctx.channel().writeAndFlush(ok.getData());
    }
  }
}
