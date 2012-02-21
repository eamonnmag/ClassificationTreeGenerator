package org.isatools.classification.fitness;

import org.isatools.classification.ClassificationSchema;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/02/2012
 *         Time: 18:39
 */
public abstract class FitnessMetric {
    public abstract String getName();

    public abstract double calculate(ClassificationSchema schema);
}