package org.isatools.classification;

public class Element {

    private String name;
    private int occurrenceCount;

    public Element(String name, int occurrenceCount) {
        this.name = name;
        this.occurrenceCount = occurrenceCount;
    }

    public String getName() {
        return name;
    }

    public int getOccurrenceCount() {
        return occurrenceCount;
    }
}
