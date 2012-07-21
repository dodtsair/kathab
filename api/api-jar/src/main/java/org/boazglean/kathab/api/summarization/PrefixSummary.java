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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mpower
 */
public class PrefixSummary extends HashMap<String, Integer> {

    public PrefixSummary(Map<? extends String, ? extends Integer> m) {
        super(m);
    }

    public PrefixSummary() {
    }

    public PrefixSummary(int initialCapacity) {
        super(initialCapacity);
    }

    public PrefixSummary(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public int getCount(String prefix) {
        if(containsKey(prefix)) {
            return get(prefix);
        }
        return 0;
    }

    public void setCount(String prefix, int count) {
        put(prefix, count);
    }
}
