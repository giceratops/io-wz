package ms.syrup.wz.io.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ms.syrup.wz.io.WzFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@ToString
@EqualsAndHashCode
public abstract class WzAbstractData implements WzData {

    @Getter
    protected final WzDataType type;
    @ToString.Exclude
    @Getter @Setter
    protected WzData parent;
    @Getter @Setter
    protected String label;

    public WzAbstractData(final WzDataType type, final String label) {
        this(type, null, label);
    }

    public WzAbstractData(final WzDataType type, final WzData parent, final String label) {
        this.type = type;
        this.parent = parent;
        this.label = label;
    }

    @Override
    public WzFile file() {
        return this.parent.file();
    }

    @Override
    @ToString.Include
    public String fullPath() {
        if (this.parent != null && !(this.parent instanceof WzFile)) {
            return String.format("%s/%s", this.parent.fullPath(), this.label());
        } else {
            return this.label();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, WzData> children() throws IOException {
        return Collections.EMPTY_MAP;
    }

    public WzImg getImg() {
        WzData node = this;
        do {
            if (node instanceof WzImg) {
                return (WzImg) node;
            }
            node = node.parent();
        } while (node != null);
        return null;
    }
}
