package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WzNull extends WzAbstractData {

    @JsonProperty private Void value;

    public WzNull(final String label) {
        super(WzDataType.NULL, label);
    }

    public Object value() {
        return this.value;
    }

    public WzNull value(final Void value) {
        this.value = value;
        return this;
    }
}
