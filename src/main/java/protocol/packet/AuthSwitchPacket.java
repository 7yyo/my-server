package protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import protocol.MySQLMessage;
import protocol.MySQLPacket;
import util.ByteUtil;
import util.RandomUtil;

@Data
public class AuthSwitchPacket extends MySQLPacket {

    private int statusFlag;
    private String pluginName;
    private String pluginProvidedData;

    public AuthSwitchPacket() {
        this.statusFlag = 0xfe;
        this.pluginName = "mysql_native_password";
        this.pluginProvidedData = new String(RandomUtil.randomBytes(20));
    }

    public void writeBytes() {
        ByteBuf b = MySQLPacket.initByteBuf();
        b.writeByte(this.statusFlag);
        b.writeBytes(this.pluginName.getBytes());
        b.writeBytes(this.pluginProvidedData.getBytes());
        b.writeByte(0);

        this.writePacketHeader(b);
    }
}
