package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzInteger extends WzAbstractData {

    @Getter @Setter
    private int value;

    public WzInteger(final String label, final int value) {
        super(WzDataType.INTEGER, label);
        this.value = value;
    }
}
