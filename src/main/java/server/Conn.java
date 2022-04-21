package server;

import error.Error;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import lombok.SneakyThrows;
import mysql.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.MySQLPacket;
import protocol.con.CapabilitiesFlags;
import protocol.con.Charset;
import protocol.con.ServerStatus;
import protocol.packet.AuthPacket;
import protocol.packet.AuthSwitchPacket;
import protocol.packet.HandshakeV10Packet;
import protocol.packet.OkPacket;
import util.ByteUtil;
import util.CtxUtil;
import util.RandomUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static protocol.con.Command.*;

@Data
public class Conn extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger("logger");

    private static final int HANDSHAKE_STATUS = 0;
    private static final int SWITCH_AUTH_STATUS = 1;
    private static final int IN_CONNECTION_STATUS = 2;

    private String remoteAddress;
    private int connStatus;
    private int capabilityFlags;
    private AtomicInteger connId;
    private AuthPacket authPacket;
    private byte[] authPluginData;

    public Conn(AtomicInteger connId) {
        this.connId = connId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.remoteAddress = ctx.pipeline().channel().remoteAddress().toString().replaceAll("/", "");
        writeHandshakePacket(ctx, this.connId);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {

        ByteBuf byteBuf = (ByteBuf) msg;

        try {
            switch (this.connStatus) {
                // handshake and auth phase
                case HANDSHAKE_STATUS -> {
                    AuthPacket a = parseAuthPacketRequest(byteBuf);
                    // if plugin name is not mysql_native_password, send switch auth request,
                    // set connStatus = switch_auth_status in same time. receive response,
                    // case switch_auth_status to verify it.
                    if (!writeAuthSwitchRequest(ctx, a) && !verifyAuth(ctx)) {
                        return;
                    }
                    logger.info("create connection: {}, user:'{}', remote address: {}", this.connId, this.authPacket.getUserName(), this.remoteAddress);
                }
                // todo if switch auth request, should process next response, so now, MySQL 8.0 client dont be supported
                case SWITCH_AUTH_STATUS -> {
                    Error.writeCrashErrorPacketAndSend(ctx, 110, new Exception("Authentication methods other than mysql_native_password are currently not supported."));
                }
                // connection phase, receive request from client.
                case IN_CONNECTION_STATUS -> {
                    byte[] bytes = ByteUtil.toBytes(byteBuf);
                    bytes = MySQLPacket.cleanPacketHeader(bytes);
                    byte header = bytes[0];
                    // todo Gradually support for all request types
                    switch (header) {
                        case COM_QUIT -> {
                            logger.info("receive com_quit request from {}, close connection, id: {}", this.remoteAddress, this.connId);
                            ctx.channel().close();
                        }
                        // todo Now response OK packet for every query
                        case COM_INIT_DB -> {
                            OkPacket.writeOKPacket(ctx, 0, 0, 2, 0, "OK");
                        }
                        case COM_QUERY -> {
                            logger.info("COM_QUERY -> {}", new String(bytes));
                        }
                        case COM_FIELD_LIST -> {
                            logger.info("COM_FIELD_LIST -> {}", new String(bytes));
                        }
                        case COM_REFRESH -> {
                            logger.info("COM_REFRESH -> {}", new String(bytes));
                        }
                        case COM_STATISTICS -> {
                            logger.info("COM_STATISTICS -> {}", new String(bytes));
                        }
                        case COM_PROCESS_INFO -> {
                            logger.info("COM_PROCESS_INFO -> {}", new String(bytes));
                        }
                        case COM_PROCESS_KILL -> {
                            logger.info("COM_PROCESS_KILL -> {}", new String(bytes));
                        }
                        case COM_DEBUG -> {
                            logger.info("COM_DEBUG -> {}", new String(bytes));
                        }
                        case COM_PING -> {
                            logger.info("COM_PING -> {}", new String(bytes));
                        }
                        case COM_CHANGE_USER -> {
                            logger.info("COM_CHANGE_USER -> {}", new String(bytes));
                        }
                        case COM_RESET_CONNECTION -> {
                            logger.info("COM_RESET_CONNECTION -> {}", new String(bytes));
                        }
                        case COM_SET_OPTION -> {
                            logger.info("COM_SET_OPTION -> {}", new String(bytes));
                        }
                        default -> {
                            logger.error("unknown request type. command: {}", new String(bytes));
                            Error.writeCrashErrorPacketAndSend(ctx, 110, new Exception("unknown request type. command: " + new String(bytes)));
                        }
                    }
                    OkPacket.writeOKPacket(ctx, 0, 0, 2, 0, "OK");
                }
            }
        } catch (Exception e) {
            Error.writeCrashErrorPacketAndSend(ctx, 110, e);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.alloc().buffer().clear();
    }

    public void writeHandshakePacket(ChannelHandlerContext ctx, AtomicInteger connId) {

        byte[] authPluginData = RandomUtil.randomBytes(20);
        byte[] header8 = Arrays.copyOfRange(authPluginData, 0, 8);
        byte[] end8 = Arrays.copyOfRange(authPluginData, 12, 20);
        this.authPluginData = authPluginData;

        HandshakeV10Packet v10 = new HandshakeV10Packet((byte) HandshakeV10Packet.PROTOCOL_VERSION, HandshakeV10Packet.SERVER_VERSION, connId.addAndGet(1), header8, (byte) 0, CapabilitiesFlags.getDefaultCapabilitiesFlags(), (byte) Charset.UTF8, (byte) ServerStatus.SERVER_STATUS_AUTOCOMMIT, end8, "mysql_native_password".getBytes());

        v10.writePacket(0);
        CtxUtil.writeAndFlush(ctx, v10.getData());

        this.capabilityFlags = v10.getCapabilityFlags();
        this.connStatus = HANDSHAKE_STATUS;
    }

    @SneakyThrows
    public AuthPacket parseAuthPacketRequest(ByteBuf byteBuf) {
        AuthPacket a = new AuthPacket(byteBuf);
        a.parse();
        this.authPacket = a;
        return a;
    }

    public boolean writeAuthSwitchRequest(ChannelHandlerContext ctx, AuthPacket a) {
        String authPluginName = a.getAuthPluginName();
        if (!"mysql_native_password".equals(authPluginName)) {
            logger.info("Auth request auth plugin name is {}, send response switch to mysql_native_password", authPluginName);
            AuthSwitchPacket as = new AuthSwitchPacket();
            as.writeBytes();
            CtxUtil.writeAndFlush(ctx, as.getData());
            this.connStatus = SWITCH_AUTH_STATUS;
            return true;
        }
        return false;
    }

    public boolean verifyAuth(ChannelHandlerContext ctx) {
        String hasPassword = "NO";
        if (this.authPacket.getAuthResponse() != null) {
            hasPassword = "YES";
        }
        User user = User.mockUser();
        // todo Now only check userName for mock user, don't check password.
        if (!user.getUser().equals(this.authPacket.getUserName())) {
            Error.writeCrashErrorPacketAndSend(ctx, 1045, new Exception(Error.ERROR_1045(this.authPacket.getUserName(), hasPassword)));
            logger.error(Error.ERROR_1045(this.authPacket.getUserName(), hasPassword));
            return false;
        } else {
            OkPacket.writeOKPacket(ctx, 0, 0, 2, 0, "OK");
            this.connStatus = IN_CONNECTION_STATUS;
            return true;
        }
    }
}
