package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.WzUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public abstract class WzAbstractData implements WzData {

    @JsonProperty protected final WzDataType type;
    @JsonIgnore protected WzData parent;
    @JsonProperty protected String label;

    public WzAbstractData(final WzDataType type, final String label) {
        this(type, null, label);
    }

    public WzAbstractData(final WzDataType type, final WzData parent, final String label) {
        this.type = type;
        this.parent = parent;
        this.label = label;
    }

    public WzDataType type() {
        return this.type;
    }

    public WzData parent() {
        return this.parent;
    }

    public WzAbstractData parent(final WzData parent) {
        this.parent = parent;
        return this;
    }

    public String label() {
        return this.label;
    }

    public WzAbstractData label(final String label) {
        this.label = label;
        return this;
    }

    @Override
    public WzFile file() {
        return this.parent.file();
    }

    @Override
    public String fullPath() {
        if (this.parent != null && !(this.parent instanceof WzFile)) {
            return String.format("%s/%s", this.parent.fullPath(), this.label());
        } else {
            return this.label();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
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

    @Override
    public String toString() {
        return WzUtils.toString(this);
    }
}
