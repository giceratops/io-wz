package ms.syrup.wz.io.data;

import ms.syrup.wz.io.ResourceFile;
import ms.syrup.wz.io.WzFile;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class WzDataTest {

    @Rule
    private static final ResourceFile res = new ResourceFile("/Test.wz");

    @Test
    @SuppressWarnings("UnnecessaryDefault")
    void type() throws IOException {
        for (final var t : WzDataType.values()) {
            final var data = switch (t) {
                case FILE -> new WzFile(res.getFile(), null);
                case NULL -> new WzNull(null);
                case IMG -> new WzImg(null, null, 0, 0);
                case SHORT -> new WzShort(null, (short) 0);
                case INTEGER -> new WzInteger(null, 0);
                case FLOAT -> new WzFloat(null, 0f);
                case DOUBLE -> new WzDouble(null, 0d);
                case LONG -> new WzLong(null, 0L);
                case STRING -> new WzString(null, null);
                case DIRECTORY -> new WzDirectory(null, null, 0, 0);
                case PROPERTY -> new WzProperty(null);
                case CANVAS -> new WzCanvas(null);
                case VECTOR -> new WzVector(null);
                case CONVEX -> new WzConvex(null);
                case SOUND -> new WzSound(null);
                case UOL -> new WzUOL(null, null);
                default -> throw new IllegalStateException("Unexpected value: " + t);
            };

            Assertions.assertEquals(t, data.type());
        }
    }
}
