package ms.syrup.wz.io.data;

import ms.syrup.wz.io.WzFile;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class WzAbstractExtendedData extends WzAbstractData {

    protected final Map<String, WzData> children;

    private long dataStart;
    private boolean read;

    public WzAbstractExtendedData(final WzDataType type, final String label) {
        this(type, null, label);
    }

    public WzAbstractExtendedData(final WzDataType type, final WzData parent, final String label) {
        super(type, parent, label);
        this.children = new LinkedHashMap<>();
    }

    abstract protected WzAbstractData read(final WzFile readerAtOffset) throws IOException;

    public long dataStart() {
        return this.dataStart;
    }

    public WzAbstractExtendedData dataStart(final long dataStart) {
        this.dataStart = dataStart;
        return this;
    }

    protected void parseNode() throws IOException {
        if (this.read) return;

        final var file = this.file();
        final var lock = file.readLock();
        try {
            lock.lock();
            if (!this.read) {
                this.read(file.seek(this.dataStart()));
                this.read = true;
            }
        } catch (final IOException ioe) {
            throw new IOException(String.format("Unable to parse " + this.getClass().getName() + " %s", this.fullPath()), ioe);
        } finally {
            lock.unlock();
        }
    }

    public void parseChildren(final WzFile reader) throws IOException {
        final var img = this.getImg();
        final var imgOffset = img.dataStart();
        final var entryCount = reader.readCompressedInt();
        for (var i = 0; i < entryCount; i++) {
            final var label = reader.readStringBlock(imgOffset);
            final var type = reader.readByte();
            final var child = switch (type) {
                case 0 -> new WzNull(label);
                case 2, 11 -> new WzShort(label, reader.readShort());
                case 3, 19 -> new WzInteger(label, reader.readCompressedInt());
                case 4 -> new WzFloat(label, reader.readByte() == Byte.MIN_VALUE ? reader.readFloat() : 0f);
                case 20 -> new WzLong(label, reader.readLong());
                case 5 -> new WzDouble(label, reader.readDouble());
                case 8 -> new WzString(label, reader.readStringBlock(imgOffset));
                case 9 -> { // extended
                    final var currentFP = (int) reader.getFilePointer();
                    final var blockSize = reader.readInt() + Integer.BYTES;
                    final var extendedChild = reader.readExtendedWzData(img, label);
                    reader.seek(currentFP + blockSize);
                    yield extendedChild.label(label);
                }
                default -> throw new IOException(String.format("Unknown property type at %s: %s - %s", this, type, label));
            };
            this.addChild(child);
        }
    }

    public void addChild(final WzData child) {
        Optional.ofNullable(this.children.put(child.label(), child.parent(this)))
                .ifPresent((oldChild) -> {
                    final var parent = oldChild.parent();
                    if (parent instanceof WzAbstractExtendedData) {
                        ((WzAbstractExtendedData) parent).removeChild(oldChild);
                    }
                });
    }

    public void removeChild(final WzData child) {
        if (child.parent() == this) {
            this.children.remove(child.label());
            child.parent(null);
        }
    }

    @Override
    public Map<String, WzData> children() throws IOException {
        this.parseNode();
        return Collections.unmodifiableMap(this.children);
    }
}
