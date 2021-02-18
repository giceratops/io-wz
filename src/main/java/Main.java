import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import ms.syrup.wz.viewer.Viewer;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) {
        showViewer(args[0]);
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
