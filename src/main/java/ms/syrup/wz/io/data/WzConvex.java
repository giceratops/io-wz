package ms.syrup.wz.io.data;

import ms.syrup.wz.io.WzFile;

import java.io.IOException;

public class WzConvex extends WzAbstractExtendedData {

    public WzConvex() {
        super(WzDataType.CONVEX);
    }

    @Override
    protected WzConvex read(final WzFile reader) throws IOException {
        final var img = super.getImg();
        final int entryCount = reader.readCompressedInt();
        for (int i = 0; i < entryCount; i++) {
            final var child = reader.readExtendedWzData(img);
            this.addChild(child);
        }
        return this;
    }
}
