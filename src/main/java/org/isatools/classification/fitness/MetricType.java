package org.isatools.classification.fitness;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 22/02/2012
 *         Time: 17:26
 */
public enum MetricType {
    COVERAGE("Coverage"), POTENTIAL_USAGE("Potential Usage"),
    SUBCLASS_COUNT("Subclass Count"), SUBTREE_BALANCE("Subtree Balance");

    private String name;

    private MetricType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
