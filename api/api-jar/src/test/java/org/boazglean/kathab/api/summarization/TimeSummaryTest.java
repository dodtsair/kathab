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
 * Date: 10/24/12
 * Time: 6:56 PM
 */
public class TimeSummaryTest {

    @Test
    public void testCon() {
        new TimeSummary();
    }

    @Test
    public void testAccessors() {
        TimeSummary summy = new TimeSummary();

        assertEquals(summy.getCount(0), 0);
        summy.setCount(0, 0);
        assertEquals(summy.getCount(0), 0);
        summy.setCount(0, 10);
        assertEquals(summy.getCount(0), 10);
    }

    @Test
    public void testCanEquals() {
        TimeSummary summy = new TimeSummary();
        TimeSummary same = new TimeSummary();

        assertTrue(summy.canEqual(summy));
        assertTrue(summy.canEqual(same));
        assertFalse(summy.canEqual(new Object()));
    }

    @Test
    public void testHashcode() {
        TimeSummary summy = new TimeSummary();

        summy.hashCode();
        summy.setCount(0, 0);
        summy.hashCode();
        summy.setCount(0, 10);
        summy.hashCode();
    }

    @Test
    public void testEquals() {
        TimeSummary summy = new TimeSummary();
        TimeSummary other = new TimeSummary();
        TimeSummary mock = mock(TimeSummary.class);
        Object neither = new Object();

        assertTrue(summy.equals(summy));
        assertTrue(summy.equals(other));
        other.setCount(10, 10);
        assertFalse(summy.equals(other));
        summy.setCount(10, 10);
        assertTrue(summy.equals(other));
        summy.setCount(20, 20);
        assertFalse(summy.equals(other));

        summy.remove(20);
        summy.remove(10);
        when(mock.canEqual(summy)).thenReturn(false);
        assertFalse(summy.equals(mock));
        assertFalse(summy.equals(neither));
    }
}