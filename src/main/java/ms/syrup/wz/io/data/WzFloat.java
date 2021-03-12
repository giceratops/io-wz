package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzFloat extends WzAbstractData {

    @Getter @Setter
    private float value;

    public WzFloat(final String label, final float value) {
        super(WzDataType.FLOAT, label);
        this.value = value;
    }
}
