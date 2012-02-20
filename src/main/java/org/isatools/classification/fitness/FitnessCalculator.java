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

        harmoniseFitnessMetrics();
        Collections.sort(fitnessResults);
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

    private void harmoniseFitnessMetrics() {
        for (FitnessResult result : fitnessResults) {
            double normalisedMetric = 1 - result.getFitness() / maxFitness;
            result.setNormalizedFitness(normalisedMetric);
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
