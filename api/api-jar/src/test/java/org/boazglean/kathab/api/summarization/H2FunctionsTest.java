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

import java.sql.Timestamp;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: mpower
 * Date: 10/24/12
 * Time: 6:30 PM
 */
public class H2FunctionsTest {

    @Test
    public void testDefaultCon() {
        new H2Fuctions();
    }

    @Test
    public void testGetMillis() {
        Timestamp mockStamp = mock(Timestamp.class);

        when(mockStamp.getTime()).thenReturn(0l);

        long stamp = H2Fuctions.getMillis(mockStamp);

        assertEquals(stamp, 0l);
    }
}
