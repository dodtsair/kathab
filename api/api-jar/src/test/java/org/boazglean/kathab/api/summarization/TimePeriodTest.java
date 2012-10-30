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

import java.util.Arrays;

import static org.testng.Assert.*;

/**
 * User: mpower
 * Date: 10/24/12
 * Time: 6:37 PM
 */
public class TimePeriodTest {


    @Test
    public void testValues() {
        assertEquals(TimePeriod.valueOf("DAY"), TimePeriod.DAY);
        assertEquals(TimePeriod.valueOf("MINUTE"), TimePeriod.MINUTE);
        assertEquals(TimePeriod.valueOf("HALF"), TimePeriod.HALF);
        assertEquals(TimePeriod.valueOf("HOUR"), TimePeriod.HOUR);
        assertEquals(TimePeriod.valueOf("QUARTER"), TimePeriod.QUARTER);
        assertEquals(TimePeriod.valueOf("SECOND"), TimePeriod.SECOND);
        assertEquals(TimePeriod.valueOf("MINUTE"), TimePeriod.DEFAULT);

        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.DAY));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.MINUTE));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.HALF));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.HOUR));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.QUARTER));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.SECOND));
        assertTrue(Arrays.asList(TimePeriod.values()).contains(TimePeriod.DEFAULT));
    }

    @Test
    public void testMillis() {
        assertEquals(TimePeriod.SECOND.getMillis(), 1000);
        assertEquals(TimePeriod.MINUTE.getMillis(), 60000);
        assertEquals(TimePeriod.DEFAULT.getMillis(), 60000);
        assertEquals(TimePeriod.QUARTER.getMillis(), 900000);
        assertEquals(TimePeriod.HALF.getMillis(), 1800000);
        assertEquals(TimePeriod.HOUR.getMillis(), 3600000);
        assertEquals(TimePeriod.DAY.getMillis(), 86400000);
    }
}
