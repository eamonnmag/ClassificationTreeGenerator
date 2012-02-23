package org.isatools.classification;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.isatools.classification.fitness.FitnessCalculator;
import org.isatools.classification.fitness.FitnessResult;

import java.util.*;

public class Statistics {

    public static int numberOfElements = 0;
    public static int totalOccurrences = 0;

    public static double getOccurrencesWithinClassificationSchema(ClassificationSchema schema, Collection<Element> toBeClassified) {
        int totalClassificationCoverage = 0;
        for (Classification classification : schema.getClassifications().values()) {
            for (Element element : classification.getElements()) {
                if (toBeClassified.contains(element)) {
                    totalClassificationCoverage += element.getOccurrenceCount();
                }
            }
        }
        return totalClassificationCoverage;
    }

    public static double getStdDeviationInNumberOfElements(ClassificationSchema classificationSchema, Collection<Element> elements) {
        StandardDeviation standardDeviation = new StandardDeviation();

        for (Classification classification : classificationSchema.getClassifications().values()) {
            int occurrenceForClassification = 0;
            for (Element classificationElement : classification.getElements()) {
                if (elements.contains(classificationElement)) {
                    occurrenceForClassification++;
                }
            }

            standardDeviation.increment(occurrenceForClassification);
        }
        return standardDeviation.getResult();
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
     * @return Collection<Element> - a unique collection of Element objects.
     */
    public static Collection<Element> getElementsInSchemas(Collection<Classification> classifications) {
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


    public static double getMeanElements(ClassificationSchema schema, Collection<Element> elements) {

        Mean mean = new Mean();

        for (Classification classification : schema.getClassifications().values()) {
            int numberOfElements = 0;
            for (Element classificationElement : classification.getElements()) {
                if (elements.contains(classificationElement)) {
                    numberOfElements++;
                }
            }
            mean.increment(numberOfElements);
        }

        return mean.getResult();
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
