package org.isatools.classification;

import org.apache.commons.math.MathException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class StatisticsTest {

    @Test
    public void testNormalDistribution() {
        assertEquals("The normal distribution values were different to the expected value", 0.1468996110209912, Statistics.calculateNormalDistributionScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 20}));
        assertEquals("The normal distribution values were different to the expected value", 0.2588184941263958, Statistics.calculateNormalDistributionScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals("The normal distribution values were different to the expected value", 0.2588184941263958, Statistics.calculateNormalDistributionScore(new double[]{0, 0}));
    }

    @Test
    public void testChi() {
        try {
            assertEquals("The normal distribution values were different to the expected value", 2.000427911365912E-4, Statistics.calculateChiTestScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 20}));
            assertEquals("The normal distribution values were different to the expected value", 0.018814171764781784, Statistics.calculateChiTestScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        } catch (MathException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStandardDeviation() {
        assertEquals("The standard deviation was different to the expected value", 5.400617248673217, Statistics.calculateStandardDeviation(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 20}));
        assertEquals("The standard deviation was different to the expected value", 3.0276503540974917, Statistics.calculateStandardDeviation(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
        assertEquals("The normal distribution values were different to the expected value", 18.384776310850235, Statistics.calculateStandardDeviation(new double[]{0, 26}));
    }
}
