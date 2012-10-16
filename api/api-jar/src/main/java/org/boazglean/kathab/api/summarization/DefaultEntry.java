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

/**
 * User: mpower
 * Date: 8/3/12
 * Time: 3:46 PM
 */
@Data
public class DefaultEntry<K extends Comparable<K>, V> implements ImmutableEntry<K, V> {
    private final K key;
    private final V value;

    public DefaultEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(ImmutableEntry<K, V> entry) {
        return this.getKey().compareTo(entry.getKey());
    }


//    @Override
//    public ImmutableEntry<K, V> withValue(V value) {
//        return new DefaultEntry<K, V>(this.getKey(), value);
//    }
//
//    @Override
//    public ImmutableEntry<K, V> withKey(K key) {
//        return new DefaultEntry<K, V>(key, this.value);
//    }
}
