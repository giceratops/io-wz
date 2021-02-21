import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import ms.syrup.wz.io.WzDecoder;
import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.viewer.Viewer;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) throws Throwable {
        showViewer(args[0]);

        final var zlz = WzDecoder.fromZLZ(args[0] + "/ZLZ.dll");
        final var map = new WzFile(args[0] + "/Map.wz", zlz);

        //map.get("Map/Map0/")
    }

    public static void showViewer(final String folder) {
        try {
            LafManager.install(new DarculaTheme());
            final Viewer viewer = new Viewer();
            viewer.open(folder);
        } catch (final Exception io) {
            JOptionPane.showMessageDialog(null, io.getMessage());
            io.printStackTrace(System.err);
        }
    }
}
