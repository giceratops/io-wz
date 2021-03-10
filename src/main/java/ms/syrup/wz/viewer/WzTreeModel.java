package ms.syrup.wz.viewer;

import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.data.WzAbstractExtendedData;
import ms.syrup.wz.io.data.WzData;
import ms.syrup.wz.io.data.WzVector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WzTreeModel implements TreeModel {

    private final WzTreeRoot root;

    public WzTreeModel() {
        this.root = new WzTreeRoot();
    }

    @Override
    public WzTreeRoot getRoot() {
        return this.root;
    }

    @Override
    public boolean isLeaf(final Object o) {
        if (o == this.root) {
            return false;
        } else if (o instanceof WzFile) {
            return false;
        } else if (o instanceof WzVector) {
            return true;
        } else return !(o instanceof WzAbstractExtendedData);
    }

    private List<WzData> getChildren(final WzData o) {
        try {
            final var children = o.children();
            final var c = new ArrayList<>(children.values());
            final var lbl = o.label();
            if (!lbl.startsWith("zmap") && !lbl.startsWith("smap")) {
                //c.sort(new WzComparator());
            }
            return c;
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        // noinspection unchecked
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object getChild(final Object o, final int i) {
        if (o instanceof WzData) {
            return this.getChildren((WzData) o).get(i);
        }
        return null;
    }

    @Override
    public int getChildCount(final Object o) {
        if (o instanceof WzData) {
            return this.getChildren((WzData) o).size();
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        if (parent instanceof WzData && child instanceof WzData) {
            return this.getChildren((WzData) parent).indexOf(child);
        }
        return -1;
    }

    @Override
    public void valueForPathChanged(final TreePath tp, final Object o) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener tl) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener tl) {
    }
}
