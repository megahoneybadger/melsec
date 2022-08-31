package melsec.utils;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Simple class to add endian support to DataInputStream.
 * User: michael
 * Date: 9/12/13
 * Time: 4:39 PM
 */
public class EndianDataInputStream extends InputStream implements DataInput {
  DataInputStream dataIn;
  private ByteBuffer buffer = ByteBuffer.allocate(8);
  ByteOrder order = ByteOrder.LITTLE_ENDIAN;

  public EndianDataInputStream( InputStream stream ){
    dataIn = new DataInputStream( stream );
  }

  public EndianDataInputStream order(ByteOrder o){
    order = o;
    return this;
  }

  @Override
  public int read(byte[] b) throws IOException {
    return dataIn.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return dataIn.read(b, off, len);
  }

  @Deprecated
  @Override
  public String readLine() throws IOException {
    return dataIn.readLine();
  }

  @Override
  public boolean readBoolean() throws IOException {
    return dataIn.readBoolean();
  }

  @Override
  public byte readByte() throws IOException {
    return dataIn.readByte();
  }

  @Override
  public int read() throws IOException {
    return readByte();
  }

  @Override
  public boolean markSupported(){
    return dataIn.markSupported();
  }

  @Override
  public void mark(int readlimit) {
    dataIn.mark(readlimit);
  }

  @Override
  public void reset() throws IOException {
    dataIn.reset();
  }

  @Override
  public char readChar() throws IOException {
    return dataIn.readChar();
  }

  @Override
  public void readFully(byte[] b) throws IOException {
    dataIn.readFully(b);
  }

  @Override
  public void readFully(byte[] b, int off, int len) throws IOException {
    dataIn.readFully(b, off, len);
  }

  @Override
  public String readUTF() throws IOException {
    return dataIn.readUTF();
  }

  @Override
  public int skipBytes(int n) throws IOException {
    return dataIn.skipBytes(n);
  }

  @Override
  public double readDouble() throws IOException {
    long tmp = readLong();
    return Double.longBitsToDouble( tmp );
  }

  @Override
  public float readFloat() throws IOException {
    int tmp = readInt();
    return Float.intBitsToFloat( tmp );
  }

  @Override
  public int readInt() throws IOException {
    buffer.clear();
    buffer.order( ByteOrder.BIG_ENDIAN )
      .putInt( dataIn.readInt() )
      .flip();
    return buffer.order( order ).getInt();
  }

  @Override
  public long readLong() throws IOException {
    buffer.clear();
    buffer.order( ByteOrder.BIG_ENDIAN )
      .putLong( dataIn.readLong() )
      .flip();
    return buffer.order( order ).getLong();
  }

  @Override
  public short readShort() throws IOException {
    buffer.clear();
    buffer.order( ByteOrder.BIG_ENDIAN )
      .putShort( dataIn.readShort () )
      .flip();
    return buffer.order( order ).getShort();
  }

  @Override
  public int readUnsignedByte() throws IOException {
    return (int)readByte() & 0xFF;
  }

  @Override
  public int readUnsignedShort() throws IOException {
    return readShort() & 0xFFFF;
  }
}