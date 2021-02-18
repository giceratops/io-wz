package ms.syrup.wz.io;

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

}
