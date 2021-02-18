package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzNull extends WzAbstractData {

    public WzNull() {
        this(null);
    }

    public WzNull(final String label) {
        super(WzDataType.NULL, label);
    }

    public Void value() {
        return null;
    }
}
