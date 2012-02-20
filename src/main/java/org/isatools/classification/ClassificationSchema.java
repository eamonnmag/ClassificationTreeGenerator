package org.isatools.classification;

import java.util.HashMap;
import java.util.Map;

public class ClassificationSchema {

    private String name;

    private Map<Integer, Classification> classifications;

    public ClassificationSchema(String name) {
        this.name = name;
        classifications = new HashMap<Integer, Classification>();
    }

    public void addClassification(int index, Classification classification) {
        classifications.put(index, classification);
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Classification> getClassifications() {
        return classifications;
    }

    public Classification getClassification(int index) {
        return classifications.get(index);
    }

    public double getStandardDeviationAcrossClassifications() {
        if (Statistics.getStdDeviationVariable(this) == 0.0) {
            Statistics.addStatistics(this);
        }

        return Statistics.getStdDeviationVariable(this);
    }
}
