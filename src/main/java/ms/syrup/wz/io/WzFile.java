package ms.syrup.wz.io;

import ms.syrup.wz.io.data.*;
import ms.syrup.wz.io.util.RandomLittleEndianAccessFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WzFile extends RandomLittleEndianAccessFile implements WzData {

    private final WzDecoder decoder;
    private final WzHeader header;
    private final WzDirectory root;
    private final Lock readLock;

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

    public WzDirectory root() {
        return this.root;
    }

    public WzHeader header() {
        return this.header;
    }

    public Lock readLock() {
        return this.readLock;
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
    public String fullPath(){
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
            case 0, 0x73 -> this.readEncodedString();
            case 1, 0x1B -> this.readEncodedStringAtAndReset(offset + super.readInt());
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

    public WzAbstractData readExtendedWzData(final WzImg img) throws IOException {
        final var currentFP = (int) this.getFilePointer();
        final var extendedType = this.readByte();
        final var dataType = switch (extendedType) {
            case 0x00, 0x73 -> this.readEncodedString();
            case 0x01, 0x1B -> this.readEncodedStringAtAndReset(img.offset() + this.readInt());
            default -> throw new IOException("Unknown dataType byte: " + extendedType);
        };
        final var dataOffset = (int) (this.getFilePointer() - currentFP);
        return switch (dataType) {
            case "Property" -> new WzProperty(currentFP, dataOffset);
            case "Canvas" -> new WzCanvas(currentFP, dataOffset);
            case "Shape2D#Vector2D" -> new WzVector(currentFP, dataOffset);
            case "Shape2D#Convex2D" -> new WzConvex(currentFP, dataOffset);
            case "Sound_DX8" -> new WzSound(currentFP, dataOffset);
            case "UOL" -> {
                this.readByte();
                final byte type = this.readByte();
                final var uol = switch (type) {
                    case 0 -> this.readEncodedString();
                    case 1 -> this.readEncodedStringAtAndReset(img.offset() + this.readInt());
                    default -> throw new IOException("Invalid byte for UOL: " + type);
                };
                yield new WzUOL(uol);
            }
            default -> throw new IOException("Unknown dataType String: " + dataType);
        };
    }

    @Override
    public String toString() {
        return "WzFile(root="+this.root.toString()+")";
    }
}
