package org.isatools.classification.visualise;

import org.isatools.classification.Classification;
import org.isatools.classification.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 27/01/2012
 *         Time: 20:36
 */
public class TreeViewXMLCreator {

    private PrintStream printStream = null;
    private File treeFile;
    // todo create XML for tree

    public File generateStart() {
        treeFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "view.xml");
        System.out.println(treeFile.getAbsolutePath());

        try {
            printStream = new PrintStream(new FileOutputStream(treeFile));
            printStream.println("<tree>");
            printStream.println(getDeclaration());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return treeFile;
    }

    public void closeTree() {
        printStream.println("</tree>");
        if (printStream != null) {
            printStream.close();
        }
    }

    private String getDeclaration() {
        return "<declarations>\n" +
                "   <attributeDecl name=\"type\" type=\"String\"/>\n" +
                "   <attributeDecl name=\"name\" type=\"String\"/>\n" +
                " </declarations>";
    }

    public void addTo(Classification classification) {
        String classificationInfo = "<branch>" +
                "<attribute name = \"type\" value = \"Classification\"/>" +
                "<attribute name=\"name\" value= \"" + classification.getName() + "\"/>\n";
        printStream.println(classificationInfo);
    }

    public void closeBranch() {
        printStream.println("</branch>");
    }

    public void addTo(Element element) {
        String elementInfo = "<leaf>\n" +
                "<attribute name = \"type\" value = \"Element\"/>" +
                "<attribute name=\"name\" value= \"" + element.getName() + " #" + element.getOccurrenceCount() + "\"/>" +
                "\n</leaf>";
        printStream.println(elementInfo);
    }

    public File getTreeFile() {
        return treeFile;
    }

}
