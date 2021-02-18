package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzShort extends WzAbstractData {

    @Getter @Setter
    private short value;

    public WzShort(short value) {
        this(null, value);
    }

    public WzShort(final String label, final short value) {
        super(WzDataType.SHORT, label);
        this.value = value;
    }

}
