package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WzLong extends WzAbstractData {

    @JsonProperty private long value;

    public WzLong(final String label, final long value) {
        super(WzDataType.LONG, label);
        this.value = value;
    }

    public long value() {
        return this.value;
    }

    public WzLong value(final long value) {
        this.value = value;
        return this;
    }
}
