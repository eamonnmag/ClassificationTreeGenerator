package org.isatools.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Classification {

    private Collection<Element> elements;

    private String name;
    // need to calculate occurrences, proportions of totals, etc.

    public Classification(String name) {
        this.name = name;
        elements = new ArrayList<Element>();
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public Collection<Element> getElements() {
        return elements;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
