package protocol.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;
import protocol.MySQLMessage;
import protocol.MySQLPacket;

@Data
public class OkPacket extends MySQLPacket {

  private static final byte HEADER = 0x00;

  private byte header;
  private int affectedRows;
  private int lastInsertId;
  private int statusFlag;
  private int warnings;
  private String info;
  private String sessionStateChanges;

  public OkPacket(int affectedRows, int lastInsertId, int statusFlag, int warnings, String info) {
    this.affectedRows = affectedRows;
    this.lastInsertId = lastInsertId;
    this.statusFlag = statusFlag;
    this.warnings = warnings;
    this.info = info;
  }

  public void writeBytes() {

    ByteBuf b = MySQLPacket.initByteBuf();
    b.writeByte(HEADER);
    MySQLMessage.writeLength(b, this.affectedRows);
    MySQLMessage.writeLength(b, this.lastInsertId);
    b.writeByte(this.statusFlag & 0xff);
    b.writeByte(this.statusFlag >>> 8);
    b.writeByte(this.warnings & 0xff);
    b.writeByte(this.warnings >>> 8);
    b.writeBytes(info.getBytes());

    this.writePacketHeader(b);
    this.setData(b);
  }
}
