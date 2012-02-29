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
 *         Time: 18:42
 */
public class SubtreeBalanceMetric extends FitnessMetric {

    @Override
    /**
     * Will return a value between 0 and 1 reflecting the balance of the elements within the subtree
     */
    public double calculate(ClassificationSchema schema, Collection<Element> elements) {

        int classificationsWithElements = 0;
         
        for (Classification classification : schema.getClassifications().values()) {
            Set<Element> elementsIntersect = new HashSet<Element>();
            for (Element element : classification.getElements()) {
                if (elements.contains(element)) {
                    elementsIntersect.add(element);
                }
            }

            if(elementsIntersect.size() > 0) classificationsWithElements++;
        }
        
        if(classificationsWithElements <= 1) return 0;

        return Statistics.calculateNormalDistributionScore(schema, elements);
    }

    @Override
    public MetricType getMetricType() {
        return MetricType.SUBTREE_BALANCE;
    }
}
