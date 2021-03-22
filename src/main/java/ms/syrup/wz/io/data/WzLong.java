package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzLong extends WzAbstractData {

    @Getter @Setter
    private long value;

    public WzLong(final String label, final long value) {
        super(WzDataType.LONG, label);
        this.value = value;
    }
}
