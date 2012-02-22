package org.isatools.classification;

import au.com.bytecode.opencsv.CSVReader;
import org.isatools.classification.fitness.FitnessCalculator;
import org.isatools.classification.fitness.FitnessResult;
import org.isatools.classification.visualise.ClassificationTreeViewer;
import org.isatools.classification.visualise.TreeViewXMLCreator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/01/2012
 *         Time: 12:48
 */
public class Classifier {

    private static final String PROCESSES = "processes";

    private Map<String, List<String[]>> fileContents;

    private Map<Integer, ClassificationSchema> classificationSchemas;
    private Set<ClassificationSchema> classificationSchemaPool;

    private FitnessCalculator fitnessCalculator;

    private TreeViewXMLCreator treeXMLCreator;

    public Classifier() {
        this.fileContents = new HashMap<String, List<String[]>>();
        classificationSchemas = new HashMap<Integer, ClassificationSchema>();
        classificationSchemaPool = new HashSet<ClassificationSchema>();
    }

    public void runClassification() {
        loadFiles();
        runClassificationAlgorithm();
    }

    private void loadFiles() {
        try {
            CSVReader processLoader = new CSVReader(new FileReader("CleanedData/clean-processes-for-classification.txt"), '\t');
            fileContents.put(PROCESSES, processLoader.readAll());

            createClassificationDataStructure();

            classificationSchemaPool.addAll(classificationSchemas.values());

            // this set is used in the fitness calculator
            Set<Classification> allClassifications = new HashSet<Classification>();

            // pre-populate all the statistics
            for (ClassificationSchema schema : classificationSchemaPool) {
                Statistics.addStatistics(schema);
                allClassifications.addAll(schema.getClassifications().values());
            }

            Collection<Element> elements = Statistics.getElementsInSchemas(allClassifications);

            fitnessCalculator = new FitnessCalculator();
            fitnessCalculator.calculateFitnessForAllSchemas(classificationSchemaPool, elements);

            printFitnessResults();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printFitnessResults() {
        for (FitnessResult fitnessResult : fitnessCalculator.getFitnessResults()) {
            System.out.println(fitnessResult.getSchema().getName() + " -> " + fitnessResult.getFitness()
                    + " (normalised = " + fitnessResult.getNormalizedFitness() + ")");
        }
    }

    private void createClassificationDataStructure() {
        List<String[]> rows = fileContents.get(PROCESSES);
        // Start at row 1, first row is just column headers
        createClassificationCategories(rows.get(0));

        for (int rowNumber = 1; rowNumber < rows.size(); rowNumber++) {
            String[] columns = rows.get(rowNumber);
            String elementName = columns[0];
            int occurrenceCount = Integer.valueOf(columns[1]);

            Element element = new Element(elementName, occurrenceCount);

            Statistics.numberOfElements++;
            Statistics.totalOccurrences += element.getOccurrenceCount();

            for (int columnNumber = 2; columnNumber < columns.length; columnNumber++) {
                if (columns[columnNumber].equals("1")) {
                    classificationSchemas.get(columnNumber).getClassification(columnNumber).addElement(element);
                }
            }
        }
    }

    private void createClassificationCategories(String[] topRow) {

        Map<String, ClassificationSchema> tmpClassificationSchemaMap = new HashMap<String, ClassificationSchema>();

        for (int columnNumber = 2; columnNumber < topRow.length; columnNumber++) {
            String[] classificationAndName = topRow[columnNumber].split(":");

            if (!tmpClassificationSchemaMap.containsKey(classificationAndName[0])) {
                ClassificationSchema schema = new ClassificationSchema(classificationAndName[0]);
                tmpClassificationSchemaMap.put(classificationAndName[0], schema);
            }

            tmpClassificationSchemaMap.get(classificationAndName[0]).
                    addClassification(columnNumber,
                            new Classification(classificationAndName[1]));

            classificationSchemas.put(columnNumber, tmpClassificationSchemaMap.get(classificationAndName[0]));
        }
    }


    private void runClassificationAlgorithm() {
        // starting point. We get the top level classification
        treeXMLCreator = new TreeViewXMLCreator();
        treeXMLCreator.generateStart();

        ClassificationSchema schema = fitnessCalculator.getFitnessResults().get(0).getSchema();

        classificationSchemaPool.remove(schema);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Classification");

        // we now need to look at each of the sub classifications and select a
        // classification schema which is able to sub classify. This should be based
        // Those classifications are those which contain all of the elements in one of
        // the classifications and not the other.
        treeXMLCreator.addTo(new Classification("Classification"));

        for (Classification classification : schema.getClassifications().values()) {
            Set<ClassificationSchema> observedSchemas = new HashSet<ClassificationSchema>();
            observedSchemas.add(schema);
            DefaultMutableTreeNode classificationNode = new DefaultMutableTreeNode(classification);
            rootNode.add(classificationNode);
            runSubClassifications(classification, observedSchemas, classificationNode);
        }
        treeXMLCreator.closeBranch();
        treeXMLCreator.closeTree();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                printClassificationInformation();
            }
        });
    }

    private void runSubClassifications(Classification classification,
                                       Set<ClassificationSchema> observedClassificationSchemas, DefaultMutableTreeNode node) {
        // look through classifications, find one which contains just elements
        // within the sub classification

        // locate classification scheme which can be used for the classification
        
        Set<ClassificationSchema> validClassificationSchemas = removeAlreadyObservedSchemas(Statistics.findSchemaForClassification(classificationSchemaPool, classification), observedClassificationSchemas);
        treeXMLCreator.addTo(classification);
        // If we have another classification schema available, it means we are able to sub classify
        if (validClassificationSchemas.size() > 0 && classification.getElements().size() > 0) {
            // recalculate fitness based on the restricted number of elements now to be classified
            fitnessCalculator.resetCalculator();
            System.out.println("Calculating fitness for classification of " + classification.getName() +  " with " + classification.getElements().size() + " elements");
            fitnessCalculator.calculateFitnessForAllSchemas(validClassificationSchemas, Statistics.getElementsInSchemas(Collections.singleton(classification)));
            printFitnessResults();

            // the best schema is selected from looking at the fitness and the currently available classifications
            ClassificationSchema bestSchema = Statistics.selectNextBestSchema(validClassificationSchemas, fitnessCalculator, observedClassificationSchemas);

            // we remove classification iteratively
            observedClassificationSchemas.add(bestSchema);

            for (Classification classificationCandidate : bestSchema.getClassifications().values()) {
                DefaultMutableTreeNode newClassification = new DefaultMutableTreeNode(classificationCandidate);
                node.add(newClassification);
                runSubClassifications(classificationCandidate, createCopyOfSet(observedClassificationSchemas), newClassification);
            }
        } else {
            // we cannot classify any more, so are finished
            for (Element element : classification.getElements()) {
                if (Statistics.doesElementBelongInClassification(element, node)) {
                    treeXMLCreator.addTo(element);
                    node.add(new DefaultMutableTreeNode(element.getName() + " (" + element.getOccurrenceCount() + ")"));
                }
            }
        }

        treeXMLCreator.closeBranch();

    }


    private Set<ClassificationSchema> createCopyOfSet(Set<ClassificationSchema> toCopy) {
        Set<ClassificationSchema> copied = new HashSet<ClassificationSchema>();
        copied.addAll(toCopy);

        return copied;
    }

    private void printClassificationInformation() {
        JFrame tree = new JFrame("Household Items Classification");
        ClassificationTreeViewer viewerClassification = new ClassificationTreeViewer();
        tree.add(viewerClassification.createTreeView(treeXMLCreator.getTreeFile().getAbsolutePath()));

        tree.pack();
        tree.setVisible(true);
    }


    private Set<ClassificationSchema> removeAlreadyObservedSchemas(Set<ClassificationSchema> foundClassificationSchemas, Set<ClassificationSchema> observedClassificationSchemas) {

        Set<ClassificationSchema> cleanedResult = new HashSet<ClassificationSchema>();

        for (ClassificationSchema foundClassificationSchema : foundClassificationSchemas) {
            if (!observedClassificationSchemas.contains(foundClassificationSchema)) {
                cleanedResult.add(foundClassificationSchema);
            }
        }

        return cleanedResult;
    }

    public static void main(String[] args) {
        Classifier classifier = new Classifier();
        classifier.runClassification();
    }


}
