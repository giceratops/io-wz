package ms.syrup.wz.io;

import ms.syrup.wz.io.data.WzString;
import ms.syrup.wz.io.data.WzUOL;
import ms.syrup.wz.io.data.WzVector;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class DecoderTest {
    @Rule
    private final static ResourceFile res = new ResourceFile("/Test.wz");
    private final WzDecoder decoder;
    private final WzFile wzFile;

    public DecoderTest() throws Throwable {
        // Hardcoded key and iv from v83 ZLZ...
        this.decoder = new WzDecoder(
                new byte[]{77, 35, -57, 43},
                new byte[]{19, 0, 0, 0, 8, 0, 0, 0, 6, 0, 0, 0, -76, 0, 0, 0, 27, 0, 0, 0, 15, 0, 0, 0, 51, 0, 0, 0, 82, 0, 0, 0}
        );
        this.wzFile = new WzFile(res.getFile(), this.decoder);
    }


    @AfterEach
    public void cleanUp() throws IOException {
        this.wzFile.close();
    }

    @AfterAll
    public static void cleanFile() throws IOException {
        Files.delete(res.getFile().toPath());
    }

    @Test
    public void decoderTest() {
        final var expected = new byte[]{-106, -82, 63, -92, 72, -6, -35, -112, 70, 118, 5, 97, -105, -50, 120, 104, 43, -96, 68, -113, -63, 86, 126, 50, -4, -31, -11, -77, 20, 20, -59, 34, -11, -61, 104, 46, -99, -61, 74, 11, -6, -2, 104, 69, 83, -118, -5, 93, 9, 79, 89, -4, -23, 17, 18, -101, -39, 15, -14, -24, 98, 105, 59, 118, 71, -120, 16, 117, -84, -29, -106, -41, -37, 18, 121, -51, 89, -28, -32, 12, -20, -95, -4, -46, -78, 60, -68, 74, 91, -99, 107, -87, -122, 41, 40, -126, 11, 67, 89, 64};
        for (var i = 0; i < 100; i++) {
            assertEquals(expected[i], this.decoder.get(i), "Not equal for index " + i);
        }
    }

    @Test
    public void testHeader() {
        final var header = this.wzFile.header();
        assertEquals(4438, header.fileSize());
        assertEquals("Copyleft Syrup ms", header.copyright());
        assertEquals(172, header.encVersion());
        assertEquals(34, header.fileStart());
        assertEquals("PKG1", header.ident());
        assertEquals(83, header.realVersion());
    }

    @Test
    public void testRoot() throws IOException {
        final var expected = new String[]{
                "00002000.img", "00002001.img", "Cap"
        };
        final var read = wzFile.children().keySet().toArray(new String[0]);
        assertArrayEquals(expected, read);
    }

    @Test
    public void testEmptyRoots() throws IOException {
        final var img2000 = this.wzFile.get("00002000.img");
        final var img2001 = this.wzFile.get("00002001.img");
        assertEquals(0, img2000.children().size());
        assertEquals(0, img2001.children().size());
    }

    @Test
    public void testDeepEquals() throws IOException {
        final var c1 = this.wzFile.get("Cap").get("01000000.img");
        final var c2 = this.wzFile.get("Cap/01000000.img");
        assertSame(c1, c2);
    }

    @Test
    public void testCap() throws IOException {
        final var cap = this.wzFile.get("Cap");
        final var img100000 = cap.get("01000000.img");
        assertEquals(1, cap.children().size());
        assertEquals(4, img100000.children().size());
    }

    @Test
    public void testBackDefault() throws IOException {
        final var backDefault = this.wzFile.get("Cap/01000000.img/backDefault/default");

        assertEquals(new Point(15, 25), ((WzVector) backDefault.get("origin")).point());
        assertEquals("backCap", ((WzString) backDefault.get("z")).value());
        assertEquals(new Point(1, -8), ((WzVector) backDefault.get("map/brow")).point());
    }

    @Test
    public void testUnlink() throws IOException {
        final var fly = this.wzFile.get("Cap/01000000.img/fly/0/default");
        assertEquals("../../default/default", ((WzUOL) fly).value());

        final var def = this.wzFile.get("Cap/01000000.img/default/default");
        assertSame(fly.unlink(), def);

    }
}
