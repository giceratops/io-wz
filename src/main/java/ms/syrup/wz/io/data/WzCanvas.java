package ms.syrup.wz.io.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import ms.syrup.wz.io.WzFile;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

// TODO split up reading data[], inflating and converting to img
// TODO pre-parse height/weight without image.
public class WzCanvas extends WzAbstractExtendedData {

    private static final int[] ZAHLEN = new int[]{0x02, 0x01, 0x00, 0x03};

    @JsonProperty private int height, width;
    private int format;
    private byte scale;
    private BufferedImage image;

    public WzCanvas(final String label) {
        super(WzDataType.CANVAS, label);
    }

    public int height() {
        return this.height;
    }

    public int width() {
        return this.width;
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

        final var len = reader.readInt();
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

        final var header = reader.readShort() & 0xFFFF;
        reader.skip(-2);
        final var requiresDecryption = header != 0x9C78 && header != 0xDA78 && header != 0x0178 && header != 0x5E78;

        final byte[] data;
        if (requiresDecryption) {
            //final var blockSize = reader.readInt();
            //reader.skip(blockSize);
            data = reader.readEncodedBytes(len - 1);
        } else {
            data = reader.readFully(len - 1);
        }

        final var dec = new Inflater();
        dec.setInput(data);
        int declen;

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
