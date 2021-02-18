package ms.syrup.wz.io.util;

import java.io.*;

public class RandomLittleEndianAccessFile implements DataInput, DataOutput, Closeable {

    private final RandomAccessFile raf;

    public RandomLittleEndianAccessFile(final File file, final String mode) throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, mode);
    }

    public final FileDescriptor getFD() throws IOException {
        return this.raf.getFD(); 
    }

    public final long getFilePointer() throws IOException {
        return this.raf.getFilePointer();
    }

    public final long length() throws IOException {
        return this.raf.length();
    }

    public RandomLittleEndianAccessFile seek(final long pos) throws IOException {
        this.raf.seek(pos);
        return this;
    }

    @Override
    public final void close() throws IOException {
        this.raf.close();
    }

    public final int read() throws IOException {
        return this.raf.read();
    }

    private byte readAndCheckByte() throws IOException {
        final int b1 = this.read();
        if (b1 == -1) {
            throw new EOFException();
        }
        return (byte) b1;
    }

    public final int read(final byte[] b) throws IOException {
        return this.raf.read(b);
    }

    public final int read(final byte[] b, final int off, final int len) throws IOException {
        return this.raf.read(b, off, len);
    }

    @Override
    public final void readFully(final byte[] b) throws IOException {
        this.raf.readFully(b);
    }

    public final byte[] readFully(final int length) throws IOException {
        final byte[] buf = new byte[length];
        this.readFully(buf);
        return buf;
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        this.raf.readFully(b, off, len);
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        return this.raf.skipBytes(n);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.raf.readUnsignedByte();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.raf.readBoolean();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return (this.readAndCheckByte() & 0xFF)
                | (this.readAndCheckByte() & 0xFF) << 8;
    }

    @Override
    public int readInt() throws IOException {
        return (this.readAndCheckByte() & 0xFF)
                | (this.readAndCheckByte() & 0xFF) << 8
                | (this.readAndCheckByte() & 0xFF) << 16
                | (this.readAndCheckByte() & 0xFF) << 24;
    }

    @Override
    public long readLong() throws IOException {
        return (this.readAndCheckByte() & 0xFFL)
                | (this.readAndCheckByte() & 0xFFL) << 8
                | (this.readAndCheckByte() & 0xFFL) << 16
                | (this.readAndCheckByte() & 0xFFL) << 24
                | (this.readAndCheckByte() & 0xFFL) << 32
                | (this.readAndCheckByte() & 0xFFL) << 40
                | (this.readAndCheckByte() & 0xFFL) << 48
                | (this.readAndCheckByte() & 0xFFL) << 56;
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public String readUTF() throws IOException {
        return this.raf.readUTF();
    }

    @Override
    public String readLine() throws IOException {
        return this.raf.readLine();
    }

    @Override
    public short readShort() throws IOException {
        return (short) this.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return (char) this.readUnsignedShort();
    }

    @Override
    public byte readByte() throws IOException {
        return this.raf.readByte();
    }

    @Override
    public final void write(final int b) throws IOException {
        this.raf.write(b);
    }

    @Override
    public final void write(final byte[] b) throws IOException {
        this.raf.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.raf.write(b, off, len);
    }

    @Override
    public void writeBoolean(final boolean bln) throws IOException {
        this.raf.writeBoolean(bln);
    }

    @Override
    public void writeByte(final int b) throws IOException {
        this.raf.writeByte(b);
    }

    @Deprecated
    @Override
    public void writeBytes(final String s) throws IOException {
        this.raf.writeBytes(s);
    }

    @Override
    public void writeChar(final int c) throws IOException {
        this.writeShort(c);
    }

    @Override
    public void writeChars(final String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            this.writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeShort(final int s) throws IOException {
        this.write(0xFF & s);
        this.write(0xFF & (s >> 8));
    }

    @Override
    public void writeInt(final int i) throws IOException {
        this.write(0xFF & i);
        this.write(0xFF & (i >> 8));
        this.write(0xFF & (i >> 16));
        this.write(0xFF & (i >> 24));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        this.writeInt(Float.floatToIntBits(v));
    }

    @Override
    public final void writeLong(final long l) throws IOException {
        this.write((int) (0xFF & l));
        this.write((int) (0xFF & (l >> 8)));
        this.write((int) (0xFF & (l >> 16)));
        this.write((int) (0xFF & (l >> 24)));
        this.write((int) (0xFF & (l >> 32)));
        this.write((int) (0xFF & (l >> 40)));
        this.write((int) (0xFF & (l >> 48)));
        this.write((int) (0xFF & (l >> 56)));
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        this.writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        this.raf.writeUTF(s);
    }
}
