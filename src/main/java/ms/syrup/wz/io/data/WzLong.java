package ms.syrup.wz.io.data;

public class WzLong extends WzAbstractData {

    private long value;

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
