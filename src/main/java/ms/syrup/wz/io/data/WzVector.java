package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ms.syrup.wz.io.WzFile;

import java.awt.*;
import java.io.IOException;

@JsonIgnoreProperties("children")
public class WzVector extends WzAbstractExtendedData {

    @JsonProperty private Point point;

    public WzVector(final String label) {
        super(WzDataType.VECTOR, label);
    }

    @Override
    protected WzVector read(final WzFile reader) throws IOException {
        this.point = new Point(reader.readCompressedInt(), reader.readCompressedInt());
        return this;
    }

    public Point point() throws IOException {
        this.parseNode();
        return this.point;
    }

    public WzVector point(final Point point) {
        this.point = point;
        return this;
    }
}
