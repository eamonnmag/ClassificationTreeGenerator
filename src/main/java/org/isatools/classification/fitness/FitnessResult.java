package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/02/2012
 *         Time: 12:19
 */
public class FitnessResult implements Comparable<FitnessResult> {

    private ClassificationSchema schema;
    private double fitness;
    private double normalizedFitness;

    private Map<MetricType, Double> metricToValue;

    public FitnessResult(ClassificationSchema schema) {
        this(schema, 0.0);
    }

    public FitnessResult(ClassificationSchema schema, double fitness) {
        this.schema = schema;
        this.fitness = fitness;
        metricToValue = new HashMap<MetricType, Double>();
    }

    public ClassificationSchema getSchema() {
        return schema;
    }

    public double getFitness() {
        return fitness;
    }

    public double getNormalizedFitness() {
        return normalizedFitness;
    }

    public void setNormalizedFitness(double normalizedFitness) {
        this.normalizedFitness = normalizedFitness;
    }

    public int compareTo(FitnessResult fitnessResult) {
        if (getFitness() < fitnessResult.getFitness()) {
            return 1;
        } else if (getFitness() > fitnessResult.getFitness()) {
            return -1;
        }
        return 0;
    }

    public double getMetricValue(MetricType metric) {
        if (metricToValue.containsKey(metric)) {
            return metricToValue.get(metric);
        } else {
            return 0;
        }
    }

    public void addMetricValue(MetricType metric, double value) {
        metricToValue.put(metric, value);
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
