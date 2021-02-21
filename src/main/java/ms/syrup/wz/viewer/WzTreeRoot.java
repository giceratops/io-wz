package ms.syrup.wz.viewer;

import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.data.WzAbstractData;
import ms.syrup.wz.io.data.WzData;
import ms.syrup.wz.io.data.WzDataType;

import java.util.HashMap;
import java.util.Map;

public class WzTreeRoot extends WzAbstractData {

    private final Map<String, WzData> children;

    public WzTreeRoot() {
        super(WzDataType.DIRECTORY, "WzTreeRoot");
        this.children = new HashMap<>();
    }

    public void addChild(final WzFile f) {
        this.children.put(f.label(), f);
    }

    @Override
    public Map<String, WzData> children() {
        return this.children;
    }
}
