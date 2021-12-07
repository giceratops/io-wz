package ms.syrup.wz.io.data;

public class WzFloat extends WzAbstractData {

    private float value;

    public WzFloat(final String label, final float value) {
        super(WzDataType.FLOAT, label);
        this.value = value;
    }

    public float value() {
        return this.value;
    }

    public WzFloat value(final float value) {
        this.value = value;
        return this;
    }
}
