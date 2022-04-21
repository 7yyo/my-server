package error;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.packet.ErrorPacket;

import java.util.Arrays;

public class Error {

    private static final Logger logger = LoggerFactory.getLogger("logger");

    public static void writeCrashErrorPacketAndSend(ChannelHandlerContext ctx, int errCode, Exception e) {
        ErrorPacket err = new ErrorPacket(
                (byte) 0xff,
                errCode,
                (byte) 1,
                new byte[]{1},
                Error.NORMAL_ERROR(e.getMessage()).getBytes());
        err.writePacket();
        ctx.channel().writeAndFlush(err.getData());
        logger.error(e + " -> from server: " + Arrays.toString(e.getStackTrace()));
    }

    public static String ERROR_1045(String userName, String pwdFlag) {
        return String.format("ERROR 1045 (28000): Access denied for user '%s'@'localhost' (using password: %s)", userName, pwdFlag);
    }

    public static String NORMAL_ERROR(String msg) {
        return String.format(" %s", msg);
    }
}
