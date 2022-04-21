package protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;
import util.ByteUtil;

import java.util.Arrays;

@Data
public class MySQLPacket {

    private int size;
    private ByteBuf data;

    public static ByteBuf initByteBuf() {
        ByteBuf b = ByteBufAllocator.DEFAULT.buffer();
        b.writeByte(0);
        b.writeByte(0);
        b.writeByte(0);
        b.writeByte(0);
        return b;
    }

    public void writePacketHeader(ByteBuf b) {
        int size = ByteUtil.toBytes(b).length;
        this.setSize(size);

        b.writerIndex(0);
        b.writeByte(size);
        b.writeByte(size >>> 8);
        b.writeByte(size >>> 16);
        b.writeByte(0);
        b.writerIndex(4 + size);

        this.setData(b);
    }

    public static byte[] cleanPacketHeader(byte[] bytes) {
        return Arrays.copyOfRange(bytes, 4, bytes.length);
    }
}
