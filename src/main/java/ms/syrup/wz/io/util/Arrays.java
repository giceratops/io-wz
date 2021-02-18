package ms.syrup.wz.io.util;

public class Arrays {

    private Arrays() {
    }

    public static byte[] repeat(final byte[] arr, final int times) {
        if (arr == null || arr.length == 0) {
            return new byte[0];
        }

        final byte[] mul = new byte[arr.length * times];
        for (int i = 0; i < times; i++) {
            System.arraycopy(arr, 0, mul, i * times, arr.length);
        }
        return mul;
    }
}
