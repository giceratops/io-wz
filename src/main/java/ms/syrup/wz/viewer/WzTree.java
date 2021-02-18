package ms.syrup.wz.viewer;

import ms.syrup.wz.io.data.WzData;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class WzTree extends JTree implements MouseListener {

    private final WzTreeModel model;

    public WzTree() {
        super();
        super.setRootVisible(false);

        this.model = new WzTreeModel();
        super.setModel(this.model);

        super.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
                if (value instanceof WzData) {
                    return super.getTreeCellRendererComponent(tree, ((WzData) value).label(), sel, expanded, leaf, row, hasFocus);
                } else {
                    return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                }
            }
        });
        super.addMouseListener(WzTree.this);
    }

    public WzTreeModel getModel() {
        return this.model;
    }

    public void sort(final boolean sort) {
        this.model.sort(sort);
        super.updateUI();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
