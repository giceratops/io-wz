package ms.syrup.wz.io.data;

import ms.syrup.wz.io.WzFile;

import java.io.IOException;

public class WzImg extends WzAbstractExtendedData {

    private final int checksum;

    public WzImg(final WzData parent, final String label, final int offset, final int checksum) {
        super(WzDataType.IMG, parent, label);
        super.dataStart(offset);
        this.checksum = checksum;
    }

    @Override
    protected WzImg read(final WzFile reader) throws IOException {
        if (reader.readByte() == 0x73
                && reader.readEncodedString().equalsIgnoreCase("Property")
                && reader.readShort() == 0) {
            this.parseChildren(reader);
        }
        return this;
    }

    public int checksum() {
        return this.checksum;
    }

}
