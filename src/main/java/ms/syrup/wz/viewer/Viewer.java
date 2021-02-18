package ms.syrup.wz.viewer;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import ms.syrup.wz.io.WzDecoder;
import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.data.WzCanvas;
import ms.syrup.wz.io.data.WzData;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Viewer extends JFrame {

    private final JSplitPane split;
    private final WzTree tree;
    private final JLabel lblValue;

    public Viewer() {
        super("Wz Directory Viewer");
        super.setLayout(new BorderLayout());
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setForeground(Color.BLACK);
        super.setBackground(Color.LIGHT_GRAY);

        //super.setIconImage(new ImageIcon(Resources.class.getResource("icon.png")).getImage());

        this.lblValue = new JLabel();
        this.lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblValue.setHorizontalTextPosition(SwingConstants.CENTER);
        this.lblValue.setVerticalTextPosition(SwingConstants.BOTTOM);

        this.tree = new WzTree();
        this.tree.addTreeSelectionListener((TreeSelectionEvent e) -> {
            final WzData node = (WzData) e.getPath().getLastPathComponent();
//
            try {
                final BufferedImage img;
                if (node instanceof WzCanvas) {
                    img = ((WzCanvas) node).get();
                } else {
                    img = null;
                }
                this.lblValue.setIcon(img == null ? null : new ImageIcon(img));
                this.lblValue.setText(node.toString());
            } catch (final Exception ioe) {
                ioe.printStackTrace(System.err);
            }
            System.out.println(node);
        });

        this.lblValue.addMouseListener(this.tree);

        this.split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(this.tree), this.lblValue);
        this.split.setResizeWeight(0.18d);

        super.add(this.split, BorderLayout.CENTER);
    }

    public void addFile(final WzFile... files) {
        for (final var file : files) {
            this.tree.getModel().getRoot().addChild(file);
        }
        this.tree.updateUI();
    }

    public void open(final String folder) throws Exception {
        final var dir = new File(folder);
        final var decoder = WzDecoder.fromZLZ(new File(dir, "ZLZ.dll"));
        for (var file : dir.listFiles((dir1, name) -> name.endsWith(".wz") && !name.contains("List"))) {
            this.tree.getModel().getRoot().addChild(new WzFile(file, decoder));
        }
        this.tree.updateUI();

        EventQueue.invokeLater(() -> {
            this.setSize(800, 600);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        });
    }
}
