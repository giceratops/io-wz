package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzNull extends WzAbstractData {

    @Getter @Setter
    private Void value;

    public WzNull(final String label) {
        super(WzDataType.NULL, label);
    }
}
