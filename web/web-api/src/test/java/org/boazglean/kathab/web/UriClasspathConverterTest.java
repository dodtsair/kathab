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
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;

/**
 *
 * @author mpower
 */
@Test
public class UriClasspathConverterTest {

    public void testConvert() throws Exception {
        ClassLoader loader = mock(ClassLoader.class);
        URI resourceRequest = new URI("a/b/c");
        UriClasspathConverter instance = new UriClasspathConverter();
        instance.setLoader(loader);

        when(loader.getResource("a/b/c")).thenReturn(new URL("file:///a/b/c"));
        URI result = instance.convert(resourceRequest);
        assertTrue(result.getPath().endsWith("a/b/c"));
    }
    
    public void testConvertNull() throws Exception {
        URI resourceRequest = null;
        UriClasspathConverter instance = new UriClasspathConverter();

        URI result = instance.convert(resourceRequest);
        assertEquals(null, result);
    }    
    
    public void testConvertEmpty() throws Exception {
        URI resourceRequest = new URI("");
        UriClasspathConverter instance = new UriClasspathConverter();

        URI result = instance.convert(resourceRequest);
        assertEquals(null, result);
    }    
        
    public void testConvertException() throws Exception {
        ClassLoader loader = mock(ClassLoader.class);
        URI resourceRequest = new URI("a/b/c");
        UriClasspathConverter instance = new UriClasspathConverter();
        instance.setLoader(loader);
        //Get a URI Syntax exception by putting a space in the URL
        when(loader.getResource("a/b/c")).thenReturn(new URL("file:///a/b with space/c"));
        Logger logger = (Logger)LoggerFactory.getLogger(instance.getClass());
        logger.setLevel(Level.INFO);

        URI result = instance.convert(resourceRequest);
        assertEquals(null, result);
    }    
    
    public void testConvertExceptionNoLog() throws Exception {
        ClassLoader loader = mock(ClassLoader.class);
        URI resourceRequest = new URI("a/b/c");
        UriClasspathConverter instance = new UriClasspathConverter();
        instance.setLoader(loader);
        when(loader.getResource("a/b/c")).thenReturn(new URL("file:///a/b with space/c"));
        Logger logger = (Logger)LoggerFactory.getLogger(instance.getClass());
        logger.setLevel(Level.OFF);

        URI result = instance.convert(resourceRequest);
        assertEquals(null, result);
    }    
    
    public void testConvertNotFound() throws Exception {
        ClassLoader loader = mock(ClassLoader.class);
        URI resourceRequest = new URI("a/b/c");
        UriClasspathConverter instance = new UriClasspathConverter();
        instance.setLoader(loader);

        when(loader.getResource("a/b/c")).thenReturn(null);

        URI result = instance.convert(resourceRequest);
        assertEquals(null, result);
    }  
    
    public void testAccessers() {
        UriClasspathConverter instance = new UriClasspathConverter();
        ClassLoader loader = mock(ClassLoader.class);
        ClassLoader orig = instance.getLoader();
        instance.setLoader(loader);
        assertEquals(loader, instance.getLoader());
        instance.setLoader(orig);
        assertEquals(orig, instance.getLoader());
        
    }

    public void testCanEqual() throws Exception {

        UriClasspathConverter instance = new UriClasspathConverter();
        Object other = new UriClasspathConverter();
        Object derived = new UriClasspathConverter() {};
        Object non = new Object();
        
        assertTrue(instance.canEqual(other));
        assertTrue(instance.canEqual(derived));
        assertFalse(instance.canEqual(non));
    }
    
    public void testToString() throws Exception {

        UriClasspathConverter instance = new UriClasspathConverter();
        instance.toString();
        instance.setLoader(null);
        instance.toString();
    }

    public void testHashcode() throws Exception {

        UriClasspathConverter instance = new UriClasspathConverter();
        UriClasspathConverter other = new UriClasspathConverter();
        assertEquals(instance.hashCode(), other.hashCode());
        instance.setLoader(null);
        other.setLoader(null);
        assertEquals(instance.hashCode(), other.hashCode());
    }
    
    public void testEquals() throws Exception {
        UriClasspathConverter instance = new UriClasspathConverter();
        ClassLoader loader = instance.getLoader();
        UriClasspathConverter other = new UriClasspathConverter();
        UriClasspathConverter cantEqual = mock(UriClasspathConverter.class);
        when(cantEqual.canEqual(instance)).thenReturn(false);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(other));
        assertTrue(instance.equals(new UriClasspathConverter() {}));
        assertFalse(instance.equals(new Object()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(cantEqual));
        
        instance.setLoader(null);
        assertFalse(instance.equals(other));
        other.setLoader(null);
        assertTrue(instance.equals(other));
        instance.setLoader(loader);
        assertFalse(instance.equals(other));
    }
}
