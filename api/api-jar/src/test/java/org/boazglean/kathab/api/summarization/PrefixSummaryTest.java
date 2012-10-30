/*
 * The MIT License
 * Copyright 2012 mpower.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.boazglean.kathab.api.summarization;

import lombok.Data;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

import java.util.*;

/**
 *
 * @author mpower
 */
@Data
public class PrefixSummaryTest {

    @Test
    public void testPrefixSummaryTestArray() {
        PrefixSummary summary = new PrefixSummary(new AbstractMap.SimpleImmutableEntry<String, Integer>("a", 0), new AbstractMap.SimpleImmutableEntry<String, Integer>("b", 2));
        assertEquals(summary.getCount("a"), 0);
        assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testPrefixSummaryTestCollection() {
        PrefixSummary summary = new PrefixSummary(Arrays.asList(new AbstractMap.SimpleImmutableEntry<String, Integer>("a", 0), new AbstractMap.SimpleImmutableEntry<String, Integer>("b", 2)));
        assertEquals(summary.getCount("a"), 0);
        assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testPrefixSummaryTestMap() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        assertEquals(summary.getCount("a"), 0);
        assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testGetCount() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        assertEquals(summary.getCount("a"), 0);
        assertEquals(summary.getCount("b"), 2);
        assertEquals(summary.getCount("NOTFOUND"), 0);
    }

    @Test
    public void testCalcMean() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        assertEquals(summary.getMean(), 1.0);

    }

    @Test
    public void testCalcStdDeviation() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("b", 2);
        data.put("c", -2);
        PrefixSummary summary = new PrefixSummary(data);
        assertEquals(summary.getStdDeviation(), 2.0);
    }

    @Test
    public void testAccessors() {
        PrefixSummary summy = new PrefixSummary();
        Map<String, Integer> points = new HashMap<String, Integer>();

        points.put("one", 1);
        assertEquals(summy.getMean(), Double.NaN);
        summy.setMean(0.0);
        assertEquals(summy.getMean(), 0.0);

        assertEquals(summy.getStdDeviation(), Double.NaN);
        summy.setStdDeviation(0.0);
        assertEquals(summy.getStdDeviation(), 0.0);

        assertEquals(summy.getPoints().size(), 0);
        summy.setPoints(points);
        assertEquals(summy.getPoints().size(), 1);
    }

    @Test
    public void testCanEqual() {
        PrefixSummary summy = new PrefixSummary();

        assertTrue(summy.canEqual(summy));
        assertFalse(summy.canEqual(new Object()));
    }

    @Test
    public void testToString() {
        PrefixSummary summary = new PrefixSummary();

        assertNotNull(summary.toString());

        summary.setPoints(null);

        assertNotNull(summary.toString());

    }

    @Test
    public void testHashcode() {
        PrefixSummary summary = new PrefixSummary();

        summary.hashCode();

        summary.setPoints(null);

        summary.hashCode();

    }

    @Test
    public void testEquals() {
        PrefixSummary summary = new PrefixSummary();
        PrefixSummary other = new PrefixSummary();
        PrefixSummary mocked = mock(PrefixSummary.class);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("one", 1);

        assertTrue(summary.equals(summary));
        assertTrue(summary.equals(other));
        other.setPoints(map);
        assertFalse(summary.equals(other));
        summary.setPoints(null);
        assertFalse(summary.equals(other));
        summary.setPoints(map);
        other.setPoints(null);
        assertFalse(summary.equals(other));
        summary.setPoints(null);
        assertTrue(summary.equals(other));

        other.setMean(1.0);
        assertFalse(summary.equals(other));
        summary.setMean(1.0);
        assertTrue(summary.equals(other));

        other.setStdDeviation(1.0);
        assertFalse(summary.equals(other));
        summary.setStdDeviation(1.0);
        assertTrue(summary.equals(other));

        when(mocked.canEqual(summary)).thenReturn(false);
        assertFalse(summary.equals(mocked));
        assertFalse(summary.equals(new Object()));
    }
}
