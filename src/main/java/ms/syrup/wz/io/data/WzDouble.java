package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WzDouble extends WzAbstractData {

    @JsonProperty private double value;

    public WzDouble(final String label, final double value) {
        super(WzDataType.DOUBLE, label);
        this.value = value;
    }

    public double value() {
        return this.value;
    }

    public WzDouble value(final double value) {
        this.value = value;
        return this;
    }
}
