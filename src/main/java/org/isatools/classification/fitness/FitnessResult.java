package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;

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

    public FitnessResult(ClassificationSchema schema, double fitness) {
        this.schema = schema;
        this.fitness = fitness;
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
}
