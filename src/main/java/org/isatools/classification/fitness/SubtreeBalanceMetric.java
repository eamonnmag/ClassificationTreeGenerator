package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;
import org.isatools.classification.Statistics;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/02/2012
 *         Time: 18:42
 */
public class SubtreeBalanceMetric extends FitnessMetric {

    @Override
    /**
     * Will return a value between 0 and 1 reflecting the balance of the elements within the subtree
     */
    public double calculate(ClassificationSchema schema) {

        // maybe we have to get all standard deviations, then the largest is our reference point for the 1 value.
        // Everything else is a function of the top value.
        double standardDeviationOfInterest = Statistics.getStdDeviationVariable(schema);
        double maxStandardDeviation = Statistics.getLargestStdDeviation();
        // now map to value between 0 and 1;
        return 1 - (standardDeviationOfInterest / maxStandardDeviation);
    }

    @Override
    public String getName() {
        return "Subtree Balance Metric";
    }
}
