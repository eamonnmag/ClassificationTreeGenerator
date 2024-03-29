package org.isatools.classification;

import au.com.bytecode.opencsv.CSVReader;
import org.isatools.classification.fitness.FitnessCalculator;
import org.isatools.classification.fitness.FitnessResult;
import org.isatools.classification.fitness.MetricType;
import org.isatools.classification.visualise.ClassificationTreeViewer;
import org.isatools.classification.visualise.TreeViewXMLCreator;

import javax.swing.*;
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
    private TreeViewXMLCreator schemaSelectionXMLCreator;

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

    /**
     * Top point for running the classification. This is the point where the top classification is selected.
     */
    private void runClassificationAlgorithm() {
        // starting point. We get the top level classification
        treeXMLCreator = new TreeViewXMLCreator();
        treeXMLCreator.generateStart("tree");

        schemaSelectionXMLCreator = new TreeViewXMLCreator();
        schemaSelectionXMLCreator.generateStart("schema");

        ClassificationSchema schema = fitnessCalculator.getFitnessResults().get(0).getSchema();

        classificationSchemaPool.remove(schema);

        // we now need to look at each of the sub classifications and select a
        // classification schema which is able to sub classify. This should be based
        // Those classifications are those which contain all of the elements in one of
        // the classifications and not the other.
        treeXMLCreator.addTo(new Classification("Classification"));

        schemaSelectionXMLCreator.addTo(schema.getName());

        for (Classification classification : schema.getClassifications().values()) {
            Set<ClassificationSchema> observedSchemas = new HashSet<ClassificationSchema>();
            observedSchemas.add(schema);
            runSubClassifications(classification, observedSchemas, classification.getElements());
        }
        treeXMLCreator.closeBranch();
        treeXMLCreator.closeTree();

        schemaSelectionXMLCreator.closeBranch();
        schemaSelectionXMLCreator.closeTree();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                printClassificationInformation();
            }
        });
    }

    /**
     * Performs a recursive operation to further classify each level of the hierarchy
     *
     * @param classification                - Classification to be further classified
     * @param observedClassificationSchemas - ClassificationSchema objects already observed thus far in the classification
     * @param elementsToClassify            - Elements to be further classified by the algorithm.
     */
    private void runSubClassifications(Classification classification, Set<ClassificationSchema> observedClassificationSchemas,
                                       Collection<Element> elementsToClassify) {

        // locate classification scheme which can be used for the classification
        treeXMLCreator.addTo(classification, elementsToClassify);

        Set<ClassificationSchema> validClassificationSchemas = removeAlreadyObservedSchemas(classificationSchemaPool, observedClassificationSchemas);

        fitnessCalculator.calculateFitnessForAllSchemas(validClassificationSchemas, elementsToClassify);
        printFitnessResults();

        // the best schema is selected from looking at the fitness and the currently available classifications
        ClassificationSchema bestSchema = Statistics.selectNextBestSchema(validClassificationSchemas, fitnessCalculator, observedClassificationSchemas);

        // If we have another classification schema available, it means we are able to sub classify
        if (validClassificationSchemas.size() > 0 && elementsToClassify.size() > 0 && bestSchema != null) {
            // recalculate fitness based on the restricted number of elements now to be classified

            // add elements which don't fall into next classification into the tree.
            addElementsToTree(getSetDifference(elementsToClassify, Statistics.getElementsInSchemas(bestSchema.getClassifications().values())));
            // we remove classification iteratively
            observedClassificationSchemas.add(bestSchema);

            for (Classification classificationCandidate : bestSchema.getClassifications().values()) {
                Collection<Element> elementsToFurtherClassify = getIntersectionOfSet(classificationCandidate.getElements(), elementsToClassify);
                // we only further classify when there are elements to be classified
                if (elementsToFurtherClassify.size() > 0) {
                    schemaSelectionXMLCreator.addTo(bestSchema.getName() + " e=" + elementsToClassify.size() + " u=" + Statistics.getOccurrencesForElements(elementsToClassify));
                    System.out.println("******************************");
                    System.out.println("Next best schema is: " + bestSchema.getName());
                    System.out.println("******************************\n");
                    runSubClassifications(classificationCandidate, createCopyOfSet(observedClassificationSchemas), getIntersectionOfSet(classificationCandidate.getElements(), elementsToClassify));
                    schemaSelectionXMLCreator.closeBranch();

                }
            }

        } else {
            // we cannot classify any more, so are finished
            addElementsToTree(elementsToClassify);
        }

        treeXMLCreator.closeBranch();
    }

    private void addElementsToTree(Collection<Element> elementsToClassify) {
        for (Element element : elementsToClassify) {
            treeXMLCreator.addTo(element);
        }
    }


    private Set<ClassificationSchema> createCopyOfSet(Set<ClassificationSchema> toCopy) {
        Set<ClassificationSchema> copied = new HashSet<ClassificationSchema>();
        copied.addAll(toCopy);
        return copied;
    }

    private Collection<Element> getIntersectionOfSet(Collection<Element> set1, Collection<Element> set2) {
        Collection<Element> intersection = new HashSet<Element>();
        for (Element element : set1) {
            if (set2.contains(element)) {
                intersection.add(element);
            }
        }
        return intersection;
    }

    private Collection<Element> getSetDifference(Collection<Element> set1, Collection<Element> set2) {
        Collection<Element> difference = new HashSet<Element>();
        for (Element element : set1) {
            if (!set2.contains(element)) {
                difference.add(element);
            }
        }
        return difference;
    }

    private void printClassificationInformation() {
        JFrame tree = new JFrame("Taxonomy Hierarchy");
        ClassificationTreeViewer viewerClassification = new ClassificationTreeViewer();
        tree.add(viewerClassification.createTreeView(treeXMLCreator.getTreeFile().getAbsolutePath()));
        tree.pack();
        tree.setVisible(true);

        JFrame schemaSelection = new JFrame("Classification Schemas");
        ClassificationTreeViewer schemaSelectionViewer = new ClassificationTreeViewer(2);
        schemaSelection.add(schemaSelectionViewer.createTreeView(schemaSelectionXMLCreator.getTreeFile().getAbsolutePath()));
        schemaSelection.pack();
        schemaSelection.setVisible(true);
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
