package ms.syrup.wz.io.data;

public class WzShort extends WzAbstractData {

    private short value;

    public WzShort(final String label, final short value) {
        super(WzDataType.SHORT, label);
        this.value = value;
    }

}
