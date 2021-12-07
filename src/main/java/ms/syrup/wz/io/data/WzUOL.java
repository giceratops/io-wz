package ms.syrup.wz.io.data;

public class WzUOL extends WzAbstractData {

    private String value;

    public WzUOL(final String label, final byte b, final String value) {
        this(label, value);
    }

    public WzUOL(final String label, final String value) {
        super(WzDataType.UOL, label);
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public WzUOL value(final String value) {
        this.value = value;
        return this;
    }
}
