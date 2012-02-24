package org.isatools.classification;

import org.apache.commons.math.MathException;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 24/02/2012
 *         Time: 13:23
 */
public class StatisticsTest {
    private static StatisticsTest ourInstance = new StatisticsTest();

    public static StatisticsTest getInstance() {
        return ourInstance;
    }

    @Test
    public void testNormalDistribution() {
        assertEquals("The normal distribution values were different to the expected value", 0.1468996110209912, Statistics.calculateNormalDistributionScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 20}));
        assertEquals("The normal distribution values were different to the expected value", 0.2588184941263958, Statistics.calculateNormalDistributionScore(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));
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
    }
}
