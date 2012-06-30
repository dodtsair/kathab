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
package org.boazglean.kathab.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author mpower
 */
@Test
public class ServletPathConverterTest {
    public void testConvert() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getRequestURI()).thenReturn("/webapp/a/b/c");
        when(request.getContextPath()).thenReturn("/webapp");
        URI uri = converter.convert(request);
        assertEquals("a/b/c", uri.getPath());
    }
    
    public void testConvertExtraSlash() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getRequestURI()).thenReturn("/webapp//a/b/c");
        when(request.getContextPath()).thenReturn("/webapp/");
        URI uri = converter.convert(request);
        assertEquals("a/b/c", uri.getPath());
    }

    public void testConvertNoSlash() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getRequestURI()).thenReturn("/webapp/a/b/c");
        when(request.getContextPath()).thenReturn("/webapp/");
        URI uri = converter.convert(request);
        assertEquals("a/b/c", uri.getPath());
    }

    public void testConvertEmpty() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        
        when(request.getRequestURI()).thenReturn("/webapp/");
        when(request.getContextPath()).thenReturn("/webapp");
        URI uri = converter.convert(request);
        assertNull(uri);
    }

    public void testConvertInvalidDebug() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        Logger logger = (Logger)LoggerFactory.getLogger(converter.getClass());
        logger.setLevel(Level.INFO);
        when(request.getRequestURI()).thenReturn("/webapp/a/\"");
        when(request.getContextPath()).thenReturn("/webapp");
        URI uri = converter.convert(request);
        assertNull(uri);
    }
    
    public void testConvertInvalidNoDebug() {
        ServletPathConverter converter = new ServletPathConverter();
        HttpServletRequest request = mock(HttpServletRequest.class);
        Logger logger = (Logger)LoggerFactory.getLogger(converter.getClass());
        logger.setLevel(Level.OFF);
        when(request.getRequestURI()).thenReturn("/webapp/a/\"");
        when(request.getContextPath()).thenReturn("/webapp");
        URI uri = converter.convert(request);
        assertNull(uri);
    }
    public void testCanEqual() throws Exception {

        ServletPathConverter instance = new ServletPathConverter();
        Object other = new ServletPathConverter();
        Object derived = new ServletPathConverter() {};
        Object non = new Object();
        
        assertTrue(instance.canEqual(other));
        assertTrue(instance.canEqual(derived));
        assertFalse(instance.canEqual(non));
    }
    
    public void testToString() throws Exception {

        ServletPathConverter instance = new ServletPathConverter();
        instance.toString();
    }

    public void testHashcode() throws Exception {

        ServletPathConverter instance = new ServletPathConverter();
        ServletPathConverter other = new ServletPathConverter();
        assertEquals(instance.hashCode(), other.hashCode());
    }
    
    public void testEquals() throws Exception {
        ServletPathConverter instance = new ServletPathConverter();
        ServletPathConverter other = new ServletPathConverter();
        ServletPathConverter cantEqual = mock(ServletPathConverter.class);
        when(cantEqual.canEqual(instance)).thenReturn(false);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(other));
        assertTrue(instance.equals(new ServletPathConverter() {}));
        assertFalse(instance.equals(new Object()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(cantEqual));
    }
}

