package ms.syrup.wz.io.data;

import lombok.*;
import ms.syrup.wz.io.WzFile;

import java.awt.*;
import java.io.IOException;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzVector extends WzAbstractExtendedData {

    @Setter
    private Point point;

    public WzVector(final String label) {
        super(WzDataType.VECTOR, label);
    }

    @Override
    protected WzVector read(final WzFile reader) throws IOException {
        this.point = new Point(reader.readCompressedInt(), reader.readCompressedInt());
        return this;
    }

    @SneakyThrows
    public Point point() {
        this.parseNode();
        return this.point;
    }
}
