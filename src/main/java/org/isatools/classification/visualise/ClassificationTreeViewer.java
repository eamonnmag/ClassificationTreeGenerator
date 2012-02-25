package org.isatools.classification.visualise;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.visualization.TreeView;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/01/2012
 *         Time: 10:41
 */
public class ClassificationTreeViewer {

    private int orientation;

    public ClassificationTreeViewer() {
        this(0);
    }

    public ClassificationTreeViewer(int orientation) {
        this.orientation = orientation;
    }

    public JPanel createTreeView(String datafile) {

        Color background = UIHelper.BG_COLOR;
        Color foreground = Color.BLACK;

        Tree t = null;

        try {
            t = (Tree) new TreeMLReader().readGraph(datafile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create a new treemap
        TreeView tview = new TreeView(t, new Dimension(800, 600), orientation);

        tview.setBackground(background);
        tview.setForeground(foreground);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);
        panel.setForeground(foreground);
        panel.add(tview, BorderLayout.CENTER);

        return panel;
    }

}
