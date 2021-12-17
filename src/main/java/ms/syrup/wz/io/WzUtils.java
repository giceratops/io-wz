package ms.syrup.wz.io;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ms.syrup.wz.io.data.WzData;

import java.io.IOException;

public class WzUtils {

    private WzUtils() {
    }

    static long encVersion(final int real) {
        final var versionHash = encHash(real);
        return 0xFF ^ ((versionHash >> 8) & 0xFF) ^ (versionHash & 0xFF);
    }

    static long encHash(final int real) {
        final var str = Integer.toString(real);
        var versionHash = 0;
        for (var i = 0; i < str.length(); i++) {
            versionHash = (32 * versionHash) + (int) str.charAt(i) + 1;
        }
        return versionHash;
    }

    public record Tuple2<L, R>(L left, R right) {
    }

    public static Tuple2<String, String> split(final String path, final char split) {
        final int i = path.indexOf(split);
        return i < 0
                ? new Tuple2<>(path, null)
                : new Tuple2<>(path.substring(0, i), path.substring(i + 1));
    }

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    public static String toJson(final WzData data) {
        try {
            data.children(); // read children
            return JSON_MAPPER.writeValueAsString(data);
        } catch (final IOException jsonException) {
            return data.toString();
        }
    }
}
