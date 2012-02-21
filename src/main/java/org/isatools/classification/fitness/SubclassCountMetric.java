package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/02/2012
 *         Time: 18:43
 */
public class SubclassCountMetric extends FitnessMetric {
    private static final int UPPER_LIMIT = 10;

    @Override
    public double calculate(ClassificationSchema schema) {
        int classificationCount = schema.getClassifications().size();
        if (classificationCount < 2) {
            return 0;
        } else if (classificationCount > UPPER_LIMIT) {
            return (double) (UPPER_LIMIT - 1) / UPPER_LIMIT;
        } else {
            return (double) (UPPER_LIMIT - classificationCount + 2) / UPPER_LIMIT;
        }
    }

    @Override
    public String getName() {
        return "Subclass count metric";
    }
}
