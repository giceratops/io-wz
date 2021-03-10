package ms.syrup.wz.io;

import lombok.Getter;
import ms.syrup.wz.io.data.*;
import ms.syrup.wz.io.util.RandomLittleEndianAccessFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WzFile extends RandomLittleEndianAccessFile implements WzData {

    private final WzDecoder decoder;
    private final WzDirectory root;
    @Getter private final WzHeader header;
    @Getter private final Lock readLock;

    public WzFile(final String filePath, final WzDecoder decoder) throws IOException {
        this(new File(filePath), decoder);
    }

    public WzFile(final File file, final WzDecoder decoder) throws IOException {
        super(file, "r");
        this.decoder = decoder;
        this.header = new WzHeader().read(WzFile.this);
        this.root = new WzDirectory(WzFile.this, file.getName());
        this.readLock = new ReentrantLock();
    }

    @Override
    public WzFile file() {
        return this;
    }

    @Override
    public WzData parent() {
        return null;
    }

    @Override
    public WzData parent(final WzData parent) {
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " cannot have a parent");
    }

    @Override
    public WzDataType type() {
        return WzDataType.FILE;
    }

    @Override
    public String label() {
        return this.root.label();
    }

    @Override
    public String fullPath() {
        return this.root.fullPath();
    }

    @Override
    public WzData label(final String label) {
        return this.root.label(label);
    }

    @Override
    public Map<String, WzData> children() throws IOException {
        return this.root.children();
    }

    @Override
    public WzFile seek(final long position) throws IOException {
        super.seek(position);
        return this;
    }

    public final String readString(final int length) throws IOException {
        final byte[] bytes = new byte[length];
        super.read(bytes);
        return new String(bytes);
    }

    public final String readNullTerminatedString() throws IOException {
        final StringBuilder ret = new StringBuilder();
        byte b;
        while ((b = this.readByte()) != 0) {
            ret.append((char) b);
        }
        return ret.toString();
    }

    public int readCompressedInt() throws IOException {
        int i = this.readByte();
        if (i == Byte.MIN_VALUE) {
            i = this.readInt();
        }
        return i;
    }

    public float readCompressedFloat() throws IOException {
        float f = this.readByte();
        if (f == Byte.MIN_VALUE) {
            f = this.readFloat();
        }
        return f;
    }

    public long readCompressedLong() throws IOException {
        long l = this.readByte();
        if (l == Byte.MIN_VALUE) {
            l = this.readLong();
        }
        return l;
    }

    public byte[] readEncodedBytes(final int length) throws IOException {
        System.out.println(Arrays.toString(this.readFully(length)));
        this.skip(-length);

        System.out.println(" length = " + length);
        try (final var baos = new ByteArrayOutputStream()) {
            var read = 0;
            while (read < length) {
                var blockSize = this.readInt();
                System.out.println(blockSize);
                read += Integer.BYTES;
                if (blockSize > length - read || blockSize < 0) {
                    throw new IOException("Block size for reading buffer is wrong: " + blockSize);
                }

                for (var i = 0; i < blockSize; i++) {
                    baos.write(this.readByte() ^ decoder.get(i));
                    read++;
                }
            }
            return baos.toByteArray();
        }
    }

    public String readEncodedString() throws IOException {
        int length = this.readByte();
        final boolean unicode = length > 0;

        if (length == (unicode ? Byte.MAX_VALUE : Byte.MIN_VALUE)) {
            length = this.readInt();
        } else if (length < 0) {
            length *= -1;
        }

        if (length <= 0) {
            return "";
        } else if (unicode) {
            length *= 2;
        }
        final byte[] buffer = this.readFully(length);
        return unicode ? this.toUnicode(buffer) : this.toAscii(buffer);
    }

    private String toUnicode(final byte[] buffer) {
        int xorByte = 0xAAAA;
        final char[] charRet = new char[buffer.length / 2];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (buffer[i] ^ this.decoder.get(i));
        }
        for (int i = 0; i < (buffer.length / 2); i++) {
            final char toXor = (char) ((buffer[i] << 8) | buffer[i + 1]);
            charRet[i] = (char) (toXor ^ xorByte);
            xorByte++;
        }
        return String.valueOf(charRet);
    }

    private String toAscii(final byte[] buffer) {
        byte xorByte = (byte) 0xAA;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (buffer[i] ^ xorByte ^ this.decoder.get(i));
            xorByte++;
        }
        return new String(buffer);
    }

    public String readEncodedStringAt(final long offset) throws IOException {
        return this.seek(offset).readEncodedString();
    }

    public String readEncodedStringAtAndReset(final long offset) throws IOException {
        final long tmp = this.getFilePointer();
        try {
            return this.readEncodedStringAt(offset);
        } finally {
            this.seek(tmp);
        }
    }

    public String readStringBlock(final long offset) throws IOException {
        return switch (super.readByte()) {
            case 0x00, 0x73 -> this.readEncodedString();
            case 0x01, 0x1B -> this.readEncodedStringAtAndReset(offset + super.readInt());
            default -> "";
        };
    }

    public long readOffset() throws IOException {
        var offset = (int) super.getFilePointer();
        offset -= this.header.fileStart();
        offset ^= 0xFFFFFFFFL;
        offset *= this.header.hash();
        offset -= 0x581C3F6DL;
        offset = Integer.rotateLeft(offset, offset);
        offset ^= this.readInt();
        offset += this.header.fileStart() * 2;
        return Integer.toUnsignedLong(offset);
    }

    public WzAbstractData readExtendedWzData(final WzImg img, final String label) throws IOException {
        final var dataType = readStringBlock(img.dataStart());
        final var currentFP = this.getFilePointer();
        return switch (dataType) {
            case "Property" -> new WzProperty(label).dataStart(currentFP);
            case "Canvas" -> new WzCanvas(label).dataStart(currentFP);
            case "Shape2D#Vector2D" -> new WzVector(label).dataStart(currentFP);
            case "Shape2D#Convex2D" -> new WzConvex(label).dataStart(currentFP);
            case "Sound_DX8" -> new WzSound(label).dataStart(currentFP);
            case "UOL" -> new WzUOL(label, this.readByte(), this.readStringBlock(img.dataStart()));
            default -> throw new IOException("Unknown dataType String: " + dataType);
        };
    }

    @Override
    public String toString() {
        return String.format("WzFile(root=%s)", this.root);
    }
}
