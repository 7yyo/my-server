package protocol;

import lombok.Data;

@Data
public class MySQLMessage {

  private byte[] data;
  private int length;
  private int position;

  public MySQLMessage(byte[] data, int length, int position) {
    this.data = data;
    this.length = length;
    this.position = position;
  }

  public void skip(long n) {
    for (int i = 0; i < n; i++) {
      this.read1();
    }
  }

  public int read1() {
    return this.data[this.position++] & 0xff;
  }

  public int read2() {
    int i = this.data[this.position++] & 0xff;
    i |= this.data[this.position++] << 8;
    return i;
  }

  public int read3() {
    int i = this.data[this.position++] & 0xff;
    i |= this.data[this.position++] << 8;
    i |= this.data[this.position++] << 16;
    return i;
  }

  public int read4() {
    int i = this.data[this.position++] & 0xff;
    i |= this.data[this.position++] << 8;
    i |= this.data[this.position++] << 16;
    i |= this.data[this.position++] << 24;
    return i;
  }

  public int read8() {
    int i = this.data[this.position++] & 0xff;
    i |= this.data[this.position++] << 8;
    i |= this.data[this.position++] << 16;
    i |= this.data[this.position++] << 24;
    i |= this.data[this.position++] << 32;
    i |= this.data[this.position++] << 40;
    i |= this.data[this.position++] << 48;
    i |= this.data[this.position++] << 56;
    return i;
  }

  public String readStringNull() {
    byte[] bytes = this.data;
    int offset = -1;
    for (int i = this.position; i < this.length; i++) {
      if (bytes[i] == 0) {
        offset = i;
        break;
      }
    }
    if (offset == -1) {
      String s = new String(bytes, this.position, this.length - this.position);
      this.position = this.length;
      return s;
    }
    if (offset > this.position) {
      String s = new String(bytes, this.position, offset - this.position);
      this.position = offset + 1;
      return s;
    } else {
      this.position++;
      return null;
    }
  }

  public long readLength() {
    int length = data[position++] & 0xff;
    switch (length) {
      case 251:
        return -1;
      case 252:
        return read2();
      case 253:
        return read3();
      case 254:
        return read4();
      default:
        return length;
    }
  }

  public String readN(long n) {
    byte[] bytes = new byte[(int) n];
    for (int i = 0; i < n; i++) {
      bytes[i] = (byte) this.read1();
    }
    return new String(bytes);
  }
}
