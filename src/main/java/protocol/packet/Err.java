package protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import protocol.MySQLPacket;
import util.ByteUtil;

@Data
public class Err extends MySQLPacket {

  private int header;
  private int errCode;
  private byte sqlStateMarker;
  private byte[] sqlState;
  private byte[] errorMessage;

  public Err(int header, int errCode, byte sqlStateMarker, byte[] sqlState, byte[] errorMessage) {
    this.header = header;
    this.errCode = errCode;
    this.sqlStateMarker = sqlStateMarker;
    this.sqlState = sqlState;
    this.errorMessage = errorMessage;
  }

  public void writeByteBuf() {

    ByteBuf b = ByteBufAllocator.DEFAULT.buffer();
    b.writeByte(0);
    b.writeByte(0);
    b.writeByte(0);
    b.writeByte(0);
    b.writerIndex(4);
    b.writeByte(this.header);
    b.writeByte(this.errCode & 0xff);
    b.writeByte(this.errCode >>> 8);
    b.writeByte(this.sqlStateMarker);
    b.writeBytes(this.sqlState);
    b.writeBytes(this.errorMessage);

    this.writePacketHeader(b);
  }
}
