package ms.syrup.wz.io.data;

public class WzNull extends WzAbstractData {

    private Void value;

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
