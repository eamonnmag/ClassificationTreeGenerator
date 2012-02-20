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
        TreeView tview = new TreeView(t, new Dimension(800, 600));

        tview.setBackground(background);
        tview.setForeground(foreground);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);
        panel.setForeground(foreground);
        panel.add(tview, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        final String dataFile = args[0];

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame tree = new JFrame("TreeView");
                ClassificationTreeViewer viewerClassification = new ClassificationTreeViewer();
                tree.add(viewerClassification.createTreeView(dataFile));

                tree.pack();
                tree.setVisible(true);
            }
        });
    }
}
