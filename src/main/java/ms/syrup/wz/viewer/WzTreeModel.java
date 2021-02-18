package ms.syrup.wz.viewer;

import ms.syrup.wz.io.WzFile;
import ms.syrup.wz.io.data.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.util.*;

public class WzTreeModel implements TreeModel {

    private final WzTreeRoot root;

    private boolean sort = true;

    public WzTreeModel() {
        this.root = new WzTreeRoot();
    }

    public boolean isSorted() {
        return this.sort;
    }

    public void sort(final boolean sort) {
        this.sort = sort;
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

    private List<WzData> getChildren(final Object o) {
        try {
            if (o instanceof WzData) {
                final Map<String, WzData> children = ((WzData) o).children();
                if (children != null) {
                    final List<WzData> c = new ArrayList<>(children.values());

                    if (this.sort && !(((WzData) o).label().startsWith("zmap") || ((WzData) o).label().startsWith("smap"))) {
                        //Collections.sort(c, (WzData t, WzData t1) -> String.CASE_INSENSITIVE_ORDER.compare(t.label(), t1.label()));
                        Collections.sort(c, new NaturalOrderComparator());
                    }
                    return c;
                }
            }
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getChild(final Object o, final int i) {
        if (o instanceof WzData) {
            final List<WzData> children = this.getChildren(o);
            if (children != null) {
                return children.get(i);
            }
        }
        return null;
    }

    @Override
    public int getChildCount(final Object o) {
        if (o instanceof WzData) {
            final List<WzData> children = this.getChildren(o);
            if (children != null) {
                return children.size();
            }
        }
        return 0;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
        if (parent instanceof WzData && child instanceof WzData) {
            final List<WzData> children = this.getChildren(parent);
            if (children != null) {
                return children.indexOf(child);
            }
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
