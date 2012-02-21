package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/02/2012
 *         Time: 18:50
 */
public class FitnessCalculator {

    private List<FitnessMetric> fitnessMetrics = new ArrayList<FitnessMetric>();
    private List<FitnessResult> fitnessResults = new ArrayList<FitnessResult>();
    private Map<String, Double> metricWeights;
    private double maxFitness = Double.MIN_VALUE;

    public FitnessCalculator() {
        this(new HashMap<String, Double>());
    }

    /**
     * FitnessCalculator can take a Map of weights for each of the metrics.
     *
     * @param metricWeights
     */
    public FitnessCalculator(Map<String, Double> metricWeights) {
        this.metricWeights = metricWeights;
        instantiateFitnessMetrics();
    }

    public List<FitnessResult> calculateFitnessForAllSchemas(Collection<ClassificationSchema> schemas) {
        for (ClassificationSchema schema : schemas) {

            double fitness = calculateFitness(schema);
            if (fitness > maxFitness) {
                maxFitness = fitness;
            }
            fitnessResults.add(new FitnessResult(schema, fitness));
        }

        Collections.sort(fitnessResults);
        normaliseFitnessMetrics();
        return fitnessResults;
    }

    public double calculateFitness(ClassificationSchema schema) {
        double overallValue = 0.0;
        System.out.println("Calculating fitness for " + schema.getName());
        for (FitnessMetric metric : fitnessMetrics) {
            double weight = metricWeights.get(metric.getName()) == null ? 1 : metricWeights.get(metric.getName());
            double value = weight * metric.calculate(schema);
            System.out.println(metric.getName() + " yielded " + value);
            overallValue += value;
        }
        return overallValue;
    }

    private void normaliseFitnessMetrics() {

        // We store the last value to check if two numbers actually have the same rank, but are in different positions
        double lastValue = Double.MIN_VALUE;
        int lastRank = 0;

        int count = 0;
        for (FitnessResult result : fitnessResults) {
            if (lastValue == result.getFitness()) {

                result.setNormalizedFitness(lastRank / fitnessResults.size());
            } else {
                System.out.println(count + "/" + fitnessResults.size());
                result.setNormalizedFitness((double) count / (fitnessResults.size() - 1));
                lastRank = count;
                lastValue = result.getFitness();
            }
            count++;
        }

    }

    public List<FitnessResult> getFitnessResults() {
        return fitnessResults;
    }

    private void instantiateFitnessMetrics() {
        FitnessMetric coverageMetric = new CoverageMetric();
        fitnessMetrics.add(coverageMetric);

        FitnessMetric potentialUsageMetric = new PotentialUsageMetric();
        fitnessMetrics.add(potentialUsageMetric);

        FitnessMetric subClassCountMetric = new SubclassCountMetric();
        fitnessMetrics.add(subClassCountMetric);

        FitnessMetric subtreeBalanceMetric = new SubtreeBalanceMetric();
        fitnessMetrics.add(subtreeBalanceMetric);
    }


}
