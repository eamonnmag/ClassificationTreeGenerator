package org.isatools.classification;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math.stat.inference.ChiSquareTest;
import org.apache.commons.math.stat.inference.ChiSquareTestImpl;
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

    public static double calculateChiTestScore(ClassificationSchema classificationSchema, Collection<Element> elements) throws MathException {


        List<Double> observedValues = new ArrayList<Double>();

        for (Classification classification : classificationSchema.getClassifications().values()) {
            double occurrenceForClassification = 0;
            for (Element classificationElement : classification.getElements()) {
                if (elements.contains(classificationElement)) {
                    occurrenceForClassification++;
                }
            }
            // we don't add zero occurrences since they are not allowed in the ChiTest
            if (occurrenceForClassification > 0) {
                observedValues.add(occurrenceForClassification);
            }
        }

        double[] values = ArrayUtils.toPrimitive(observedValues.toArray(new Double[observedValues.size()]));

        return calculateChiTestScore(values);
    }

    public static double calculateChiTestScore(double[] values) throws MathException {
        Mean mean = new Mean();
        long meanResult = (long) mean.evaluate(values);

        long[] expectedValues = new long[values.length];
        for (int observedIndex = 0; observedIndex < values.length; observedIndex++) {
            expectedValues[observedIndex] = meanResult;
        }

        if (values.length == 1) return 1;

        ChiSquareTest chiSquareTest = new ChiSquareTestImpl();
        return Math.sqrt(chiSquareTest.chiSquareTest(values, expectedValues));
    }

    public static double getOccurrencesForElements(Collection<Element> elements) {
        double occurrences = 0;
        for (Element element : elements) {
            occurrences += element.getOccurrenceCount();
        }
        return occurrences;
    }

    /**
     * Returns the P value, larger the better indicating how much variance there is in the data
     *
     * @param classificationSchema - Schema to be classified
     * @param elements             - elements to be further classified
     * @return double value between 0 and 1 indicating how well the data stays around the mean.
     */
    public static double calculateNormalDistributionScore(ClassificationSchema classificationSchema, Collection<Element> elements) {
        List<Double> observedValues = new ArrayList<Double>();
        for (Classification classification : classificationSchema.getClassifications().values()) {
            double occurrenceForClassification = 0;
            for (Element classificationElement : classification.getElements()) {
                if (elements.contains(classificationElement)) {
                    occurrenceForClassification++;
                }
            }
            // we don't add zero occurrences since they are not allowed in the ChiTest
            observedValues.add(occurrenceForClassification);
        }

        double[] values = ArrayUtils.toPrimitive(observedValues.toArray(new Double[observedValues.size()]));
        return calculateNormalDistributionScore(values);
    }

    public static double calculateNormalDistributionScore(double[] values) {
        Mean mean = new Mean();
        double meanValue = mean.evaluate(values);
        double stdDev = calculateStandardDeviation(values);
        if (stdDev < 0.000001) {
            return 1;
        } else {
            NormalDistribution distribution = new NormalDistributionImpl(meanValue, stdDev);
            try {
                // we are calculating the probability that the value falls within +-1 of the mean value.
                // This is normally calculated on the std deviation, however, since that changes per test, calculating on a
                // constant value 1 is better for getting a normalised value.
                return distribution.cumulativeProbability(meanValue - 1, meanValue + 1);
            } catch (MathException e) {
                return 0;
            }
        }
    }


    public static double calculateStandardDeviation(double[] values) {
        StandardDeviation dev = new StandardDeviation();
        return dev.evaluate(values);
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
                                                                        Collection<Element> elementsToBeClassified) {
        Set<ClassificationSchema> validClassificationSchemas = new HashSet<ClassificationSchema>();

        // we need to find classification schemas which contain just the elements within this classification
        for (ClassificationSchema classificationSchema : classificationSchemas) {
            Set<Element> elementsInSchema = new HashSet<Element>();
            for (Classification classificationToInspect : classificationSchema.getClassifications().values()) {
                for (Element element : classificationToInspect.getElements()) {
                    if (elementsInSchema.contains(element)) {
                        System.out.println("WARNING: The element " + element.getName() +
                                " is classified twice in the same ClassificationSchema (duplicate at "
                                + classificationToInspect.getName() + ")");
                    } else {
                        elementsInSchema.add(element);
                    }
                }
            }
            // now check if this set of elements contains only those elements in the classification we are checking
            if (checkIfSubset(elementsToBeClassified, elementsInSchema)) {
                validClassificationSchemas.add(classificationSchema);
            }
        }
        return validClassificationSchemas;
    }

    private static boolean checkIfSubset(
            Collection<Element> elementsInSchema, Collection<Element> elementsToBeClassified) {
        // Ensuring that child class contains all parent class elements
        for (Element element : elementsToBeClassified) {
            // this would obviously break down if our sub classification
            // did not classify everything properly
            if (!elementsInSchema.contains(element)) {
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
