package protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import util.ByteUtil;

@Data
public class MySQLPacket {

  private int size;
  private ByteBuf data;

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
}
