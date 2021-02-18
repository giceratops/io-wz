package ms.syrup.wz.io.util;

import java.io.OutputStream;

public class CopyStream extends OutputStream {

    private final byte[] dst;
    private int i;

    public CopyStream(final byte[] dst, final int start) {
        this.dst = dst;
        this.i = start;
    }

    @Override
    public void write(final int b) {
        if (this.i < this.dst.length) {
            this.dst[this.i++] = (byte) b;
        }
    }

    @Override
    public void write(final byte[] src, final int offset, final int length) {
        if (this.dst.length - this.i >= length) {
            System.arraycopy(src, offset, this.dst, this.i, length);
            this.i += length;
        }
    }

    @Override
    public void close() {
    }
}
