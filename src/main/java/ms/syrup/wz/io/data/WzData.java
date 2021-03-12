package ms.syrup.wz.io.data;

import lombok.SneakyThrows;
import ms.syrup.wz.io.WzFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public interface WzData {

    WzDataType type();

    WzFile file();

    WzData parent();

    WzData parent(final WzData parent);

    String label();

    WzData label(final String label);

    String fullPath();

    Map<String, WzData> children() throws IOException;

    default Optional<WzData> find(final String path) {
        try {
            return Optional.ofNullable(this.get(path).unlink());
        } catch (final IOException ioe) {
            return Optional.empty();
        }
    }

    // TODO
    default WzData get(final String path) throws IOException {
        if (path == null || path.isEmpty()) {
            return this;
        }

        final String lbl, sub;
        final int i = path.indexOf("/");
        if (i < 0) {
            lbl = path;
            sub = null;
        } else {
            lbl = path.substring(0, i);
            sub = path.substring(i + 1);
        }

        if ("..".equals(lbl)) {
            final WzData parent_ = this.parent();
            if (parent_ == null) {
                return null;
            } else if (sub != null && !sub.startsWith("..")) {
                final WzData parent__ = parent_.parent();
                if (parent__ != null) {
                    return parent__.get(sub);
                }
            } else {
                return parent_.get(sub);
            }
        } else {
            final Map<String, WzData> children_ = this.children();
            if (children_.isEmpty()) {
                return null;
            } else {
                final WzData child = children_.get(lbl);
                if (child != null) {
                    return child.get(sub);
                }
            }
        }
        return null;
    }

    @SneakyThrows
    default WzData getUnchecked(final String path) {
        return this.get(path);
    }

    @SneakyThrows
    default WzData unlink() {
        var data = this;
        while (data instanceof WzUOL) {
            data = this.get(((WzUOL) data).value());
        }
        return data;
    }
}
