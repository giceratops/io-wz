package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzString extends WzAbstractData {

    @Getter @Setter
    private String value;

    public WzString(final String value) {
        this(null, value);
    }

    public WzString(final String label, final String value) {
        super(WzDataType.STRING, label);
        this.value = value;
    }
}
