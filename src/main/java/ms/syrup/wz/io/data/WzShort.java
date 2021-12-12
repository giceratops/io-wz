package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WzShort extends WzAbstractData {

    @JsonProperty private short value;

    public WzShort(final String label, final short value) {
        super(WzDataType.SHORT, label);
        this.value = value;
    }

}
