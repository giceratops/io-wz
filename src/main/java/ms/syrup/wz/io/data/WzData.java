package ms.syrup.wz.io.data;

import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.WzUtils;

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

    default WzData get(final String path) throws IOException {
        if (path == null || path.isEmpty()) {
            return this;
        }

        final var split = WzUtils.split(path, '/');
        return (switch (split.left) {
            case "." -> this;
            case ".." -> this.parent();
            default -> this.children().get(split.left);
        }).get(split.right);
    }

    default WzData getUnchecked(final String path) {
        try {
            return this.get(path);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    default WzData unlink() {
        var data = this;
        while (data instanceof WzUOL) {
            data = data.parent().getUnchecked(((WzUOL) data).value());
        }
        return data;
    }
}
