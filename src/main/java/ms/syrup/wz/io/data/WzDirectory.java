package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ms.syrup.wz.io.WzFile;

import java.io.IOException;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzDirectory extends WzAbstractExtendedData {

    private final int checksum;

    public WzDirectory(final WzFile parent, final String label) {
        super(WzDataType.DIRECTORY, parent, label, parent.header().fileStart(), (parent.header().indexStart() - parent.header().fileStart()));
        this.checksum = 0;
    }

    public WzDirectory(final WzData parent, final String label, final int offset, final int checksum) {
        super(WzDataType.DIRECTORY, parent, label, offset, 0);
        this.checksum = checksum;
    }

    @Override
    public final void parseNode() throws IOException {
        try {
            super.parseNode();
        } catch (final IOException ioe) {
            final var file = super.file();
            if (this.parent() == file && file.header().nextVersion()) {
                this.parseNode();
            } else {
                throw new IOException(String.format("Unable to parse " + this.getClass().getName() + " %s", this.fullPath()), ioe);
            }
        }
    }

    @Override
    protected WzDirectory read(final WzFile reader) throws IOException {
        final var entryCount = reader.readCompressedInt();

        for (int i = 0; i < entryCount; i++) {
            byte type = reader.readByte();
            int blockSize, checksum_;
            long offset_, rememberPos;
            String fname;
            switch (type) {
                case 0x01 -> {
                    reader.readInt();
                    reader.readShort();
                    reader.readOffset();
                    continue;
                }
                case 0x02 -> {
                    int stringOffset = reader.readInt();
                    rememberPos = reader.getFilePointer();
                    reader.seek(reader.header().fileStart() + stringOffset);
                    type = reader.readByte();
                    fname = reader.readEncodedString();
                }
                case 0x03, 0x04 -> {
                    fname = reader.readEncodedString();
                    rememberPos = reader.getFilePointer();
                }
                default -> throw new IOException(String.format("Unknown type=%s", type));
            }

            switch (type) {
                case 0x03, 0x04 -> { // .img
                    reader.seek(rememberPos);
                    blockSize = reader.readCompressedInt();
                    checksum_ = reader.readCompressedInt();
                    offset_ = reader.readOffset();
                    if (type == 0x04) {
                        this.addChild(new WzImg(this, fname, (int) offset_, checksum_));
                    } else {
                        this.addChild(new WzDirectory(this, fname, (int) offset_, checksum_));
                    }
                }
                default -> throw new IOException(String.format("Unknown type=%s", type));
            }
        }
        return this;
    }
}
