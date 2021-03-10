package ms.syrup.wz.io.data;

import lombok.Getter;
import ms.syrup.wz.io.WzFile;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class WzCanvas extends WzAbstractExtendedData {

    private static final int[] ZAHLEN = new int[]{0x02, 0x01, 0x00, 0x03};

    @Getter
    private int height, width;
    private int format;
    private byte scale;
    private BufferedImage image;

    public WzCanvas(final String label) {
        super(WzDataType.CANVAS, label);
    }

    @Override
    protected WzCanvas read(final WzFile reader) throws IOException {
        if (reader.readShort() == 0x0100) {
            reader.readShort();
            this.parseChildren(reader);
        }

        this.width = reader.readCompressedInt();
        this.height = reader.readCompressedInt();
        this.format = reader.readCompressedInt();
        this.scale = reader.readByte();
        reader.readInt();

        final var len = reader.readInt() - 1;
        reader.readByte();


        int sizeUncompressed = 0;
        int size8888;
        int maxWriteBuf = 2;
        byte[] writeBuf = new byte[maxWriteBuf];
        switch (this.format) {
            case 0x1, 0x201 -> sizeUncompressed = this.height * this.width * 4;
            case 0x2 -> sizeUncompressed = this.height * this.width * 8;
            case 0x205 -> sizeUncompressed = this.height * this.width / 128;
        }
        size8888 = this.height * this.width * 8;
        if (size8888 > maxWriteBuf) {
            maxWriteBuf = size8888;
            writeBuf = new byte[maxWriteBuf];
        }

        var data = reader.readFully(len);
        data = new byte[]{
                120, -100, -76, -43, 75, 110, -38, 96, 16, 0, 96, 88, 119, -27, 28, -63, -36, 0, -85, -71, 0, -12, 113, 0, -69, 82, -42, 45, -82, -44, 109, 36, -88, -102, -20, 113, -107, 100, -35, 84, 77, -70, 15, 18, 28, 32, 105, -110, 3, -108, 38, 23, 40, 112, -125, -16, -1, 126, 63, -95, 51, -13, -37, 56, 16, -13, 72, -108, 122, 100, 97, -13, -8, 60, -98, 127, 6, -105, 74, -21, -74, 50, 91, -5, -107, 39, 108, -1, 67, 45, -77, -25, 81, -17, 43, -67, 23, 21, -13, -71, -44, -36, -87, -104, -73, 73, 126, -74, -71, -113, -58, -4, -73, -17, -97, -105, -39, 117, -108, -99, 61, -82, 22, 101, 54, -99, -50, 75, 87, -87, -124, -97, 100, -57, 18, -1, -30, 61, -82, 22, -8, 107, -108, -105, -85, -113, 55, -17, -69, -8, 75, 84, -9, 60, 113, 52, -99, 98, 5, 36, -34, 11, -97, -94, 102, 46, 101, -100, -86, -30, -99, 81, 114, 29, -115, -110, 67, 95, 92, 101, 86, 41, -74, 16, 107, 93, -111, -13, 22, 31, 37, 120, 60, -98, -32, -114, 106, -103, -119, 59, 64, 69, 51, 85, -34, -30, 109, -82, 51, -99, -75, -72, -10, -48, -99, 93, 43, 87, 23, -73, -101, 24, -59, -52, 84, -71, 110, 25, -26, 119, -69, -29, -100, 57, -57, 112, -92, 115, -67, -48, -67, -119, -123, -67, -52, 61, 9, -13, 60, 85, 110, -40, 23, 94, 63, -24, -121, -3, -32, -52, 49, 64, 109, -78, 6, -45, 88, -79, -117, 114, -90, -2, -119, 13, 95, 28, -75, 60, 82, 3, 81, 81, -116, 11, -73, -17, 15, -62, -65, -31, -79, -43, -30, 77, -72, 127, 13, 76, -95, -42, 11, -35, 110, -40, 13, -123, 59, -98, 124, -11, 13, 127, -112, -44, -19, -117, 8, -49, -121, -119, -72, 127, 48, -67, 65, 116, -105, -4, 14, 80, 20, 86, 22, 106, -111, -70, -32, 78, -89, -3, 88, 54, 69, -90, 121, -74, 109, -114, -26, -71, 39, 114, 84, 105, -49, -93, -66, -62, 45, -91, -22, 71, -9, -99, -93, -69, -65, -96, -85, 46, -29, -15, 100, 60, -111, 77, -51, -20, 7, 119, 49, -102, 42, -85, -111, 81, -93, 87, -107, -4, -91, -39, -90, 46, 102, -5, -54, -42, 93, -84, -17, 107, -5, 71, -80, -25, -99, 4, -61, 4, -5, -32, 46, -18, -5, 45, -14, -22, 76, 97, 85, 8, 25, -94, 74, -14, 106, 85, 84, -96, 19, -118, 9, -24, -123, 7, 62, -42, -12, -64, -65, -116, 42, -26, 32, 50, 76, -115, 50, 69, 79, -95, 44, -85, 76, -126, 93, 77, -41, -85, -40, -51, 123, 107, -104, 116, -61, 67, -1, 16, 76, -119, -117, 9, -64, -11, 106, -110, -92, -112, -45, 100, 45, -120, 38, 72, 50, -20, -53, 107, -112, 117, -64, 77, 60, 76, -16, -98, -81, -30, 61, -81, 36, 86, -126, 103, -77, 37, 76, 13, -68, 54, -84, 89, 29, 103, 11, -34, -111, -105, -44, 32, -17, -56, 45, 46, -101, 91, 28, 39, -75, 27, -94, -86, -79, -114, 3, 115, 4, -67, -113, -97, 106, 112, -57, 10, -27, 89, -89, 90, 40, -48, 97, -104, 109, 109, 85, 101, 103, 27, -82, -42, 109, -68, -29, -68, -79, 75, -84, 99, 55, 96, 63, 119, 53, 82, -15, 58, 77, 8, 25, 94, -37, -28, 53, -88, -54, -101, -88, 88, -115, 81, -78, -17, -19, -72, -37, 22, -72, 48, -101, -57, -106, -24, 79, 9, -14, -60, 121, -110, -88, 10, -62, -109, 104, -19, -22, 69, 115, -69, -80, 25, -2, 120, 114, -28, -17, 123, -104, -83, 102, 117, -100, 6, 23, -35, 94, -93, 10, -96, -82, 83, -107, -79, 111, -91, -76, -33, -44, -75, 42, -50, 67, 47, -36, -9, 62, -71, 47, 45, -119, 127, -74, -37, -26, -121, 116, -122, 112, -75, 116, -22, -41, 42, 77, 66, -107, -70, 64, -52, -60, 122, 117, -108, -36, 38, -104, -19, -114, -125, -82, 110, 125, 51, 119, -87, 43, 69, 23, -88, 52, 3, 50, -39, 27, -101, -92, -62, 28, 68, -32, -6, 111, 109, 116, 91, -26, 123, -74, 75, -39, -54, -44, 75, 42, 93, -95, -106, -42, 5, 43, -77, 90, 85, 44, -59, 66, 115, 60, 17, -18, 17, 76, -61, -74, 85, -127, -114, -45, -88, 79, 27, -28, 41, -77, -1, 3, 101, 35, 19, 69, 49, 99, -24, 94, 69, 24, -94, -62, -13, -49, -85, -38, 44, 86, 63, -67, -78, 44, -25, -97, 6, -71, -116, 121, -25, -111, -5, -89, -63, 105, 80, 44, -49, -18, 59, 125, 10, -30, -106, -3, 10, -97, -25, -53, 2, 69, -116, -97, 16, -117, 50, -102, 15, 35, -1, 60, -97, -23, -12, 9, -50, -14, 43, 46, -66, -1, 15, 0, 0, -1, -1
        };
        System.out.println(Arrays.toString(data));

        final var dec = new Inflater();
        dec.setInput(data);
        int declen;


        System.out.printf("widght=%d height=%d format=%d children=%s %n", this.width, this.height, this.format, this.children);

        final var uc = new byte[sizeUncompressed];
        try {
            declen = dec.inflate(uc);
        } catch (DataFormatException ex) {
            throw new RuntimeException("zlib fucks", ex);
        }
        dec.end();
        if (this.format == 1) {
            for (int i = 0; i < sizeUncompressed; i++) {
                byte low = (byte) (uc[i] & 0x0F);
                byte high = (byte) (uc[i] & 0xF0);
                writeBuf[(i << 1)] = (byte) (((low << 4) | low) & 0xFF);
                writeBuf[(i << 1) + 1] = (byte) (high | ((high >>> 4) & 0xF));
            }
        } else if (this.format == 2) {
            writeBuf = uc;
        } else if (this.format == 513) {
            for (int i = 0; i < declen; i += 2) {
                byte bBits = (byte) ((uc[i] & 0x1F) << 3);
                byte gBits = (byte) (((uc[i + 1] & 0x07) << 5) | ((uc[i] & 0xE0) >> 3));
                byte rBits = (byte) (uc[i + 1] & 0xF8);
                writeBuf[(i << 1)] = (byte) (bBits | (bBits >> 5));
                writeBuf[(i << 1) + 1] = (byte) (gBits | (gBits >> 6));
                writeBuf[(i << 1) + 2] = (byte) (rBits | (rBits >> 5));
                writeBuf[(i << 1) + 3] = (byte) 0xFF;
            }
        } else if (this.format == 517) {
            byte b;
            int pixelIndex;
            for (int i = 0; i < declen; i++) {
                for (int j = 0; j < 8; j++) {
                    b = (byte) (((uc[i] & (0x01 << (7 - j))) >> (7 - j)) * 255);
                    for (int k = 0; k < 16; k++) {
                        pixelIndex = (i << 9) + (j << 6) + k * 2;
                        writeBuf[pixelIndex] = b;
                        writeBuf[pixelIndex + 1] = b;
                        writeBuf[pixelIndex + 2] = b;
                        writeBuf[pixelIndex + 3] = (byte) 0xFF;
                    }
                }
            }
        }
        final var imgData = new DataBufferByte(writeBuf, sizeUncompressed);
        final var sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, this.width, this.height, 4, this.width * 4, ZAHLEN);
        final var imgRaster = Raster.createWritableRaster(sm, imgData, new Point(0, 0));
        final var aa = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        aa.setData(imgRaster);
        this.image = aa;

        return this;
    }

    public BufferedImage get() {
        try {
            this.parseNode();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return this.image;
    }
}
