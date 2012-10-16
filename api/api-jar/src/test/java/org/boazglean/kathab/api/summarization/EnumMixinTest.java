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

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;


/**
 * User: mpower
 * Date: 10/15/12
 * Time: 10:13 AM
 */
public class EnumMixinTest {

    private enum Count {
        ZERO,
        ONE,
        TWO,
        THREE;
    }

    @Test
    public void testValues() {
        String[] names = EnumMixin.names(Count.values());
        assertEquals("ZERO", names[0]);
        assertEquals("ONE", names[1]);
        assertEquals("TWO", names[2]);
        assertEquals("THREE", names[3]);
    }

    @Test
    public void testEmptyValues() {
        Count[] values = new Count[] {};
        String[] names = EnumMixin.names(values);
        assertEquals(0, names.length);
    }

    @Test
    public void testSubsetValues() {
        Count[] values = new Count[] {Count.ONE, Count.TWO};
        String[] names = EnumMixin.names(values);
        assertEquals(values.length, names.length);
    }
}
