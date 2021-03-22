package ms.syrup.wz.io.data;

import ms.syrup.wz.io.WzFile;

import java.io.IOException;

public class WzConvex extends WzAbstractExtendedData {

    public WzConvex(final String label) {
        super(WzDataType.CONVEX, label);
    }

    @Override
    protected WzConvex read(final WzFile reader) throws IOException {
        final var img = super.getImg();
        final var entryCount = reader.readCompressedInt();
        for (var i = 0; i < entryCount; i++) {
            final var child = reader.readExtendedWzData(img, null);
            this.addChild(child);
        }
        return this;
    }
}
