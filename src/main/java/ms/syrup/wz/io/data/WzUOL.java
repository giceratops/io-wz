package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WzUOL extends WzAbstractData {

    @Getter @Setter
    private String value;

    public WzUOL(final String value) {
        this(null, value);
    }

    public WzUOL(final String label, final String value) {
        super(WzDataType.UOL, label);
        this.value = value;
    }
}
