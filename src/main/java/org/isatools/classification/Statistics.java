package org.isatools.classification;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.isatools.classification.fitness.FitnessCalculator;
import org.isatools.classification.fitness.FitnessResult;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/01/2012
 *         Time: 11:59
 */
public class Statistics {

    public static int numberOfElements = 0;
    public static int totalOccurrences = 0;

    private static Map<Classification, Integer> occurrencesWithinClassification = new HashMap<Classification, Integer>();
    private static Map<ClassificationSchema, Double> stdDeviationAcrossSchema = new HashMap<ClassificationSchema, Double>();
    private static Map<ClassificationSchema, Double> meanAcrossSchema = new HashMap<ClassificationSchema, Double>();

    public static void addOccurrenceVariable(Classification classification, int occurrence) {
        occurrencesWithinClassification.put(classification, occurrence);
    }

    public static double getOccurrencesWithinClassificationSchema(ClassificationSchema schema) {
        int totalClassificationCoverage = 0;
        for (Classification classification : schema.getClassifications().values()) {
            totalClassificationCoverage += occurrencesWithinClassification.get(classification);
        }
        return totalClassificationCoverage;
    }

    public static double getStdDeviationInNumberOfElements(ClassificationSchema classificationSchema) {
        if (stdDeviationAcrossSchema.containsKey(classificationSchema)) {
            return stdDeviationAcrossSchema.get(classificationSchema);
        }
        return 0.0;
    }

    public static int calculateNumberOfOccurrences(Collection<Element> elements) {
        int elementOccurrenceCount = 0;
        for (Element element : elements) {
            elementOccurrenceCount += element.getOccurrenceCount();
        }

        return elementOccurrenceCount;
    }

    /**
     * Gets the unique collection of elements within a classification
     *
     * @param classifications - Set of ClassificationSchema objects to query
     * @return Colletion<Element> - a unique collection of Element objects.
     */
    public static Collection<Element> getElementsInSchemas(Set<Classification> classifications) {
        Map<String, Element> elements = new HashMap<String, Element>();

        for (Classification classification : classifications) {
            for (Element element : classification.getElements()) {
                if (!elements.containsKey(element.getName())) {
                    elements.put(element.getName(), element);
                }
            }
        }

        return elements.values();
    }

    public static void addStatistics(ClassificationSchema classificationSchema) {

        for (Classification classification : classificationSchema.getClassifications().values()) {
            int classificationOccurrenceTotal = 0;
            for (Element element : classification.getElements()) {
                classificationOccurrenceTotal += element.getOccurrenceCount();
            }
            addOccurrenceVariable(classification, classificationOccurrenceTotal);
        }

        doStatisticalAnalysis(classificationSchema);
    }

    private static void doStatisticalAnalysis(ClassificationSchema classificationSchema) {
        StandardDeviation standardDeviation = new StandardDeviation();
        Mean mean = new Mean();

        for (Classification classification : classificationSchema.getClassifications().values()) {
            int numberOfElements = classification.getElements().size();

            standardDeviation.increment(numberOfElements);
            mean.increment(numberOfElements);
        }

        double stdDeviationVar = standardDeviation.getResult();
        double meanVar = mean.getResult();

        stdDeviationAcrossSchema.put(classificationSchema, stdDeviationVar);
        meanAcrossSchema.put(classificationSchema, meanVar);
    }


    public static double getMeanElements(ClassificationSchema schema) {
        return meanAcrossSchema.get(schema);
    }

    public static Set<ClassificationSchema> findSchemaForClassification(Collection<ClassificationSchema> classificationSchemas,
                                                                        Classification classification) {
        Set<ClassificationSchema> selectedSchemas = new HashSet<ClassificationSchema>();

        Set<Element> parentElements = new HashSet<Element>();
        parentElements.addAll(classification.getElements());

        // we need to find classification schemas which contain just the elements within this classification
        for (ClassificationSchema classificationSchema : classificationSchemas) {
            Set<Element> elements = new HashSet<Element>();
            for (Classification classificationToInspect : classificationSchema.getClassifications().values()) {
                for (Element element : classificationToInspect.getElements()) {
                    if (elements.contains(element)) {
                        System.out.println("WARNING: The element " + element.getName() +
                                " is classified twice in the same ClassificationSchema (duplicate at "
                                + classificationToInspect.getName() + ")");
                    } else {
                        elements.add(element);
                    }
                }
            }
            // now check if this set of elements contains only those elements in the classification we are checking
            if (checkIfSubset(parentElements, elements)) {
                selectedSchemas.add(classificationSchema);
            }
        }
        return selectedSchemas;
    }

    private static boolean checkIfSubset(
            Set<Element> parentClassificationElements, Set<Element> childClassificationElements) {
        // Ensuring that child class contains all parent class elements
        for (Element element : parentClassificationElements) {
            // this would obviously break down if our sub classification
            // did not classify everything properly
            if (!childClassificationElements.contains(element)) {
                return false;
            }
        }

        return true;
    }

    public static double calculatePercentageOfTotal(Classification classification) {
        return ((double) occurrencesWithinClassification.get(classification) / (double) totalOccurrences) * 100;
    }

    public static boolean doesElementBelongInClassification(Element element, DefaultMutableTreeNode node) {

        Classification classification = (Classification) node.getUserObject();
        return classification.getElements().contains(element) && (!(node.getParent() != null
                && !(((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof String))
                || doesElementBelongInClassification(element, (DefaultMutableTreeNode) node.getParent()));
    }

    public static ClassificationSchema selectNextBestSchema(Set<ClassificationSchema> validClassificationSchemas, FitnessCalculator fitnessCalculator, Set<ClassificationSchema> observedClassificationSchemas) {
        ClassificationSchema selectedSchema = null;
        for (FitnessResult fitnessResult : fitnessCalculator.getFitnessResults()) {
            // we will ALWAYS process in order. So we just take the first one which becomes
            // available and which hasn't already been recorded
            if (!observedClassificationSchemas.contains(fitnessResult.getSchema()) && validClassificationSchemas.contains(fitnessResult.getSchema())) {
                return fitnessResult.getSchema();
            }
        }
        return selectedSchema;
    }
}
