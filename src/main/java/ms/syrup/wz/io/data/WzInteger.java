package ms.syrup.wz.io.data;

public class WzInteger extends WzAbstractData {

    private int value;

    public WzInteger(final String label, final int value) {
        super(WzDataType.INTEGER, label);
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public WzInteger value(final int value) {
        this.value = value;
        return this;
    }
}
