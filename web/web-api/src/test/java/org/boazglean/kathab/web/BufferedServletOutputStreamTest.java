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

package org.boazglean.kathab.web;

import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: mpower
 * Date: 9/11/12
 * Time: 1:41 PM
 */
public class BufferedServletOutputStreamTest {

    private BufferedServletOutputStream stream;

    @BeforeMethod
    public void setup() {
        stream = new BufferedServletOutputStream();
    }

    @Test
    public void testAccessors() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        assertNotNull(stream.getBuffer());
        stream.setBuffer(null);
        assertNull(stream.getBuffer());
        stream.setBuffer(buffer);
        assertEquals(stream.getBuffer(), buffer);
    }

    @Test
    public void testWriteThrough() throws Exception {
        byte[] byteArray = new byte[]{};
        ByteArrayOutputStream mockBuffer = mock(ByteArrayOutputStream.class);
        OutputStream dump = mock(OutputStream.class);
        stream.setBuffer(mockBuffer);
        stream.write((byte)0);
        stream.write(byteArray);
        stream.write(byteArray, 0, 0);
        stream.writeTo(dump);
        verify(mockBuffer).write(0);
        verify(mockBuffer).write(byteArray);
        verify(mockBuffer).write(byteArray, 0, 0);
        verify(mockBuffer).writeTo(dump);
    }
}
