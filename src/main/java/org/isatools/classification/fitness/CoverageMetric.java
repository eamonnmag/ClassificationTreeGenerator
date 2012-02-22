package org.isatools.classification.fitness;

import org.isatools.classification.Classification;
import org.isatools.classification.ClassificationSchema;
import org.isatools.classification.Element;
import org.isatools.classification.Statistics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/02/2012
 *         Time: 18:41
 */
public class CoverageMetric extends FitnessMetric {

    @Override
    /**
     * Will return a value between 0 and 1 reflecting the percent coverage of the current schema on the total number
     * of elements
     */
    public double calculate(ClassificationSchema schema, Collection<Element> elements) {
        Set<Element> elementCoverage = new HashSet<Element>();

        for (Classification classification : schema.getClassifications().values()) {
            elementCoverage.addAll(classification.getElements());
        }

        System.out.println("Element coverage = " + elementCoverage.size());
        System.out.println("Number of elements in classification = " + elements.size());
        return (double) elementCoverage.size() / elements.size();
    }

    @Override
    public String getName() {
        return "Coverage Metric";
    }
}
