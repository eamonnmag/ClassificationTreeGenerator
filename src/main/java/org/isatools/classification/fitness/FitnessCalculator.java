package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;
import org.isatools.classification.Element;

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

    private List<FitnessMetric> metricsToPerform = new ArrayList<FitnessMetric>();
    private List<FitnessResult> fitnessResults = new ArrayList<FitnessResult>();

    private Map<MetricType, Double> metricWeights;
    private FitnessResult maxFitness;

    public FitnessCalculator() {
        this(new HashMap<MetricType, Double>());
    }

    /**
     * FitnessCalculator can take a Map of weights for each of the metrics.
     *
     * @param metricWeights
     */
    public FitnessCalculator(Map<MetricType, Double> metricWeights) {
        this.metricWeights = metricWeights;
        this.maxFitness = null;

        instantiateFitnessMetrics();
    }

    public List<FitnessResult> calculateFitnessForAllSchemas(Collection<ClassificationSchema> schemas, Collection<Element> elements) {
        resetCalculator();

        for (ClassificationSchema schema : schemas) {
            FitnessResult fitness = calculateFitness(schema, elements);
            if(maxFitness == null) {
                maxFitness = fitness;
            } else if (fitness.getFitness() > maxFitness.getFitness()) {
                maxFitness = fitness;
            }
            
            fitnessResults.add(fitness);
        }

        for(FitnessResult fitnessResult : fitnessResults) {
            if(fitnessResult.getMetricValue(MetricType.COVERAGE) == 0) {
                fitnessResult.setFitness(0);
            } else if(fitnessResult.getMetricValue(MetricType.SUBTREE_BALANCE) == 0) {
                fitnessResult.setFitness(0);
            }
        }

        Collections.sort(fitnessResults);
        normaliseFitnessMetrics();
        return fitnessResults;
    }

    public FitnessResult calculateFitness(ClassificationSchema schema, Collection<Element> elements) {
        double overallValue = 0.0;
        
        System.out.println("Calculating fitness for " + schema.getName());
        
        FitnessResult result = new FitnessResult(schema);
        
        for (FitnessMetric metric : metricsToPerform) {
            double weight = metricWeights.get(metric.getMetricType()) == null ? 1 : metricWeights.get(metric.getMetricType());
            double value = weight * metric.calculate(schema, elements);

            result.addMetricValue(metric.getMetricType(), value);
            
            System.out.println(metric.getMetricType() + " yielded " + value);
            overallValue += value;
            
        }
        result.setFitness(overallValue);
        return result;
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
        metricsToPerform.add(coverageMetric);

        FitnessMetric potentialUsageMetric = new PotentialUsageMetric();
        metricsToPerform.add(potentialUsageMetric);

        FitnessMetric subClassCountMetric = new SubclassCountMetric();
        metricsToPerform.add(subClassCountMetric);

        FitnessMetric subtreeBalanceMetric = new SubtreeBalanceMetric();
        metricsToPerform.add(subtreeBalanceMetric);
    }

    public void resetCalculator() {
        fitnessResults.clear();
        maxFitness = null;
    }
}
