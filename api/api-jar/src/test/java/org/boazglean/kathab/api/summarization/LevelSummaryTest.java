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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: mpower
 * Date: 10/24/12
 * Time: 8:15 PM
 */
public class LevelSummaryTest {

    @Test
    public void testCon() {
        LevelSummary summy = new LevelSummary();
        assertEquals(summy.size(), 0);
        Map<LogLevel, Integer> internalMap = new HashMap<LogLevel, Integer>();
        internalMap.put(LogLevel.ERROR, 0);

        summy = new LevelSummary(internalMap);
        assertEquals(summy.size(), 1);

        EnumMap<LogLevel, Integer> internalEnum = new EnumMap<LogLevel, Integer>(LogLevel.class);
        internalEnum.put(LogLevel.WARN, 1);
        internalEnum.put(LogLevel.DEBUG, 2);
        summy = new LevelSummary(internalEnum);
        assertEquals(summy.size(), 2);
    }

    @Test
    public void testAccessors() {
        LevelSummary summy = new LevelSummary();

        assertEquals(summy.getCount(LogLevel.INFO), 0);

        summy.setCount(LogLevel.INFO, 10);

        assertEquals(summy.getCount(LogLevel.INFO), 10);
    }

    @Test
    public void testCanEquals() {
        LevelSummary summy = new LevelSummary();

        assertTrue(summy.canEqual(summy));
        assertTrue(summy.canEqual(new LevelSummary()));
        assertFalse(summy.canEqual(new Object()));
    }

    @Test
    public void testHashcode() {
        LevelSummary summy = new LevelSummary();

        summy.hashCode();
        summy.setCount(LogLevel.INFO, 0);
        summy.hashCode();
        summy.setCount(LogLevel.INFO, 10);
        summy.hashCode();
    }

    @Test
    public void testToString() {
        LevelSummary summy = new LevelSummary();

        assertNotNull(summy.toString());
        summy.setCount(LogLevel.INFO, 0);
        assertNotNull(summy.toString());
        summy.setCount(LogLevel.INFO, 10);
        assertNotNull(summy.toString());
    }

    @Test
    public void testEquals() {
        LevelSummary summy = new LevelSummary();
        LevelSummary other = new LevelSummary();
        LevelSummary mock = mock(LevelSummary.class);
        Object neither = new Object();

        assertTrue(summy.equals(summy));
        assertTrue(summy.equals(other));
        other.setCount(LogLevel.INFO, 10);
        assertFalse(summy.equals(other));
        summy.setCount(LogLevel.INFO, 10);
        assertTrue(summy.equals(other));
        summy.setCount(LogLevel.ERROR, 20);
        assertFalse(summy.equals(other));

        summy.remove(LogLevel.INFO);
        summy.remove(LogLevel.ERROR);
        when(mock.canEqual(summy)).thenReturn(false);
        assertFalse(summy.equals(mock));
        assertFalse(summy.equals(neither));
    }
}
