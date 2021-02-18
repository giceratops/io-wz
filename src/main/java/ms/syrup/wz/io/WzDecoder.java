package ms.syrup.wz.io;

import ms.syrup.wz.io.util.Arrays;
import ms.syrup.wz.io.util.CopyStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class WzDecoder {

    private static final byte BLOCK_SIZE = 16;
    private static final int BATCH_SIZE = BLOCK_SIZE * 16;

    public static WzDecoder fromZLZ(final String filePath) throws IOException, GeneralSecurityException {
        return fromZLZ(new File(filePath));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static WzDecoder fromZLZ(final File zlz) throws IOException, GeneralSecurityException {
        try (final FileInputStream fis = new FileInputStream(zlz)) {
            final var iv = new byte[4];
            fis.skip(0x10040);
            fis.read(iv);

            final var block = 4;
            final var blocks = 8;
            final var key = new byte[block * blocks];
            fis.skip(0x20 - iv.length);
            for (byte b = 0; b < blocks; b++) {
                fis.read(key, b * block, block);
                fis.skip(0x10 - block);
            }
            return new WzDecoder(iv, key);
        }
    }

    private final Cipher cipher;
    private byte[] keys;

    WzDecoder(final byte[] iv, final byte[] key) throws GeneralSecurityException {
        if (iv.length != 4 || (iv[0] & iv[1] & iv[2] & iv[3]) == 0) {
            throw new IllegalArgumentException("iv must be length 4 and non [0,0,0,0]");
        }

        this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        this.keys = this.cipher.update(Arrays.repeat(iv, 4));
    }

    public byte get(int index) {
        if (this.keys.length <= index) {
            synchronized (this.cipher) {
                // check again in synchronized context
                if (index < this.keys.length) {
                    return this.keys[index];
                }

                this.expandTo(((index / BATCH_SIZE) + 1) * BATCH_SIZE);
            }
        }
        return keys[index];
    }

    private void expandTo(final int newSize) {
        final var startIndex = this.keys.length;
        final var newKeys = new byte[newSize];
        System.arraycopy(this.keys, 0, newKeys, 0, this.keys.length);

        try (final var cos = new CipherOutputStream(new CopyStream(newKeys, startIndex), this.cipher)) {
            for (var i = startIndex; i < newKeys.length; i += BLOCK_SIZE) {
                cos.write(newKeys, i - BLOCK_SIZE, BLOCK_SIZE);
            }
        } catch (IOException ignore) {
        }
        this.keys = newKeys;
    }
}
