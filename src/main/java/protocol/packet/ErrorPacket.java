package protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import protocol.MySQLPacket;

@Data
public class ErrorPacket extends MySQLPacket {

  private int header;
  private int errCode;
  private byte sqlStateMarker;
  private byte[] sqlState;
  private byte[] errorMessage;

  public ErrorPacket(
      int header, int errCode, byte sqlStateMarker, byte[] sqlState, byte[] errorMessage) {
    this.header = header;
    this.errCode = errCode;
    this.sqlStateMarker = sqlStateMarker;
    this.sqlState = sqlState;
    this.errorMessage = errorMessage;
  }

  public void writePacket() {

    ByteBuf b = MySQLPacket.initByteBuf();

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
