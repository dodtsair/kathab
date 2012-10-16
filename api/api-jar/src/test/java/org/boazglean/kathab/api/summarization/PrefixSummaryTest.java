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
import org.testng.Assert;
import org.testng.annotations.Test;

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
        Assert.assertEquals(summary.getCount("a"), 0);
        Assert.assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testPrefixSummaryTestCollection() {
        PrefixSummary summary = new PrefixSummary(Arrays.asList(new AbstractMap.SimpleImmutableEntry<String, Integer>("a", 0), new AbstractMap.SimpleImmutableEntry<String, Integer>("b", 2)));
        Assert.assertEquals(summary.getCount("a"), 0);
        Assert.assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testPrefixSummaryTestMap() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        Assert.assertEquals(summary.getCount("a"), 0);
        Assert.assertEquals(summary.getCount("b"), 2);
    }

    @Test
    public void testGetCount() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        Assert.assertEquals(summary.getCount("a"), 0);
        Assert.assertEquals(summary.getCount("b"), 2);
        Assert.assertEquals(summary.getCount("NOTFOUND"), 0);
    }

    @Test
    public void testCalcMean() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("a", 0);
        data.put("b", 2);
        PrefixSummary summary = new PrefixSummary(data);
        Assert.assertEquals(summary.getMean(), 1.0);

    }

    @Test
    public void testCalcStdDeviation() {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("b", 2);
        data.put("c", -2);
        PrefixSummary summary = new PrefixSummary(data);
        Assert.assertEquals(summary.getStdDeviation(), 2.0);
    }
}
