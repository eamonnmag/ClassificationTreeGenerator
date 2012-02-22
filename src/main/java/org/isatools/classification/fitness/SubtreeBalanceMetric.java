package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;
import org.isatools.classification.Element;
import org.isatools.classification.Statistics;

import java.util.Collection;
import java.util.Set;

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
    public double calculate(ClassificationSchema schema, Collection<Element> elements) {

        // maybe we have to get all standard deviations, then the largest is our reference point for the 1 value.
        // Everything else is a function of the top value.
        return 1 - (Statistics.getStdDeviationInNumberOfElements(schema) / Statistics.getMeanElements(schema));
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.SUBTREE_BALANCE;
    }
}
