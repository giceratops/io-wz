package ms.syrup.wz.io.data;

public class WzString extends WzAbstractData {

    private String value;

    public WzString(final String label, final String value) {
        super(WzDataType.STRING, label);
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public WzString value(final String value) {
        this.value = value;
        return this;
    }
}
