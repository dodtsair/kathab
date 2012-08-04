/*
 * The MIT License
 *
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

import java.util.*;

/**
 *
 * @author mpower
 */
@Data
public class PrefixSummary {

    double mean;
    double stdDeviation;
    NavigableSet<ImmutableEntry<String, Integer>> points = new TreeSet<ImmutableEntry<String, Integer>>();

    public PrefixSummary(ImmutableEntry<String, Integer>... points) {
        for(ImmutableEntry<String, Integer> point: points) {
            this.points.add(point);
        }
        mean = calcMean();
        stdDeviation = calcStdDeviation();
    }

    public PrefixSummary(Collection<? extends ImmutableEntry<String, Integer>> points) {
        this.points.addAll(points);
        mean = calcMean();
        stdDeviation = calcStdDeviation();
    }

    public PrefixSummary(Map<String, Integer> points) {

        for(String key: points.keySet()) {
            this.points.add(new DefaultEntry<String, Integer>(key, points.get(key)));
        }
        mean = calcMean();
        stdDeviation = calcStdDeviation();
    }

    public int getCount(String prefix) {
        ImmutableEntry<String, Integer> search = new DefaultEntry<String, Integer>(prefix, null);
        if(points.contains(search)) {
            return points.floor(search).getValue();
        }
        return 0;
    }

    public double calcMean() {
        int sum = 0;
        for(ImmutableEntry<String, Integer> point: points) {
            sum += point.getValue();
        }
        return (double)sum / (double)points.size();

    }

    public double calcStdDeviation() {
        double sumDevSq = 0;
        double deviation = 0;
        for(ImmutableEntry<String, Integer> point: points) {
            deviation = mean - (double)point.getValue();
            sumDevSq += deviation * deviation;
        }
        return Math.sqrt(sumDevSq / (double)points.size());

    }
}
