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
 *         Time: 18:41
 */
public class PotentialUsageMetric extends FitnessMetric {
    @Override
    /**
     * Will return a value between 0 and 1 reflecting the percent coverage of the current schema on the total number
     * of occurrences in the classifcation vs the overall number of element occurrences.
     */
    public double calculate(ClassificationSchema schema, Collection<Element> elements) {
        // calculate coverage as proportion of occurrences
        return Statistics.getOccurrencesWithinClassificationSchema(schema) / Statistics.calculateNumberOfOccurrences(elements);
    }

    @Override
    public String getName() {
        return "Potential Use Metric";
    }
}
