package protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import protocol.MySQLPacket;

// https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake
@Data
public class HandshakeV10Packet extends MySQLPacket {

  public static final int PROTOCOL_VERSION = 10;
  public static final byte[] SERVER_VERSION = "5.7.25-MySQL-v6.1.0".getBytes();

  private byte protocolVersion; // protocol_version
  private byte[] serverVersion; // human-readable server version
  private long connectionId; // connection id
  private byte[] authPluginDataPart1; //  [len=8] first 8 bytes of the auth-plugin data
  private byte filler; // 0x00
  private int capabilityFlags; // lower 2 bytes of the Protocol::CapabilityFlags (optional)
  private byte
      characterSet; // default server character-set, only the lower 8-bits Protocol::CharacterSet
  // (optional) This “character set” value is really a collation ID but implies
  // the character set; see the Protocol::CharacterSet description.
  private byte statusFlag; // Protocol::StatusFlags (optional)
  private int
      authPluginDataLen; // length of the combined auth_plugin_data, if auth_plugin_data_len is > 0
  private byte[] authPluginDataPart2; //  [len=8] first 8 bytes of the auth-plugin data
  private byte[] authPluginName; //  name of the auth_method that the auth_plugin_data belongs to

  public HandshakeV10Packet(
      byte protocolVersion,
      byte[] serverVersion,
      long connectionId,
      byte[] authPluginDataPart1,
      byte filler,
      int capabilityFlags,
      byte characterSet,
      byte statusFlag,
      byte[] authPluginDataPart2,
      byte[] authPluginName) {
    this.protocolVersion = protocolVersion;
    this.serverVersion = serverVersion;
    this.connectionId = connectionId;
    this.authPluginDataPart1 = authPluginDataPart1;
    this.filler = filler;
    this.capabilityFlags = capabilityFlags;
    this.characterSet = characterSet;
    this.statusFlag = statusFlag;
    this.authPluginDataPart2 = authPluginDataPart2;
    this.authPluginName = authPluginName;
  }

  public void writePacket(long sequenceId) {

    ByteBuf b = MySQLPacket.initByteBuf();

    b.writeByte(this.protocolVersion);
    b.writeBytes(this.serverVersion);
    b.writeByte((int) sequenceId);
    b.writeByte((int) (this.connectionId & 0xff))
        .writeByte((int) (this.connectionId >>> 8))
        .writeByte((int) (this.connectionId >>> 16))
        .writeByte((int) (this.connectionId >>> 24));
    b.writeBytes(this.authPluginDataPart1);
    b.writeByte(0);
    b.writeByte(this.capabilityFlags & 0xff).writeByte(this.capabilityFlags >>> 8);
    b.writeByte(this.characterSet);
    b.writeByte(this.statusFlag & 0xff).writeByte(this.statusFlag >>> 8);
    b.writeByte(this.capabilityFlags >>> 16).writeByte(this.capabilityFlags >>> 24);
    b.writeByte(this.authPluginDataPart1.length + this.authPluginDataPart2.length);
    b.writeBytes(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    b.writeBytes(this.authPluginDataPart2);
    b.writeByte(0);
    b.writeBytes(this.authPluginName);
    b.writeByte(0);

    this.writePacketHeader(b);
  }
}
