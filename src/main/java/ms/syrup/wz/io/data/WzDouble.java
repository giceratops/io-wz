package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzDouble extends WzAbstractData {

    @Getter @Setter
    private double value;

    public WzDouble(final String label, final double value) {
        super(WzDataType.DOUBLE, label);
        this.value = value;
    }
}
