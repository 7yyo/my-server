package util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.nio.charset.StandardCharsets;

public class ByteUtil {

  public static void printByteBuf(ByteBuf byteBuf) {
    for (int i = 0; i < ByteBufUtil.getBytes(byteBuf).clone().length; i++) {
      System.out.println(ByteBufUtil.getBytes(byteBuf).clone()[i]);
    }
  }

  public static void printByteBufString(ByteBuf byteBuf) {
    System.out.println(new String(ByteBufUtil.getBytes(byteBuf).clone(), StandardCharsets.UTF_8));
  }

  public static byte[] toBytes(ByteBuf byteBuf) {
    return ByteBufUtil.getBytes(byteBuf).clone();
  }
}
