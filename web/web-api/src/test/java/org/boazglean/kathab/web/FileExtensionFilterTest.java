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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class FileExtensionFilterTest {

    public void testConvert() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("something.gif"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.png"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.svg"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.ico"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.jpeg"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.jpg"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.css"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.js"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.jss"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.html"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.xhtml"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.htm"));
        assertTrue(filtered);
    }

    public void testConvertRejected() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("somethinggif"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("somethin.sgif"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("something.class"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("something.java"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("something.properties"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("something.xml"));
        assertFalse(filtered);
    }

    public void testConvertNull() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(null);
        assertFalse(filtered);
    }

    public void testConvertNullPath() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("http:", "localhost", null, "frag"));
        assertFalse(filtered);
    }

    public void testConvertEmptyPath() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("http:", "localhost", "", "frag"));
        assertFalse(filtered);
    }

    public void testAccessers() throws Exception {

        FileExtensionFilter instance = new FileExtensionFilter();
        List<String> extensions = instance.getExtensions();
        assertNotNull(extensions);

        boolean filtered = false;
        instance.setExtensions(Arrays.asList("a", "b"));
        filtered = instance.isFiltered(new URI("something.a"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.b"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.gif"));
        assertFalse(filtered);

        instance.setExtensions(extensions);
        filtered = instance.isFiltered(new URI("something.gif"));
        assertTrue(filtered);
    }

    public void testCanEqual() throws Exception {

        FileExtensionFilter instance = new FileExtensionFilter();
        Object other = new FileExtensionFilter();
        Object derived = new FileExtensionFilter() {};
        Object non = new Object();
        
        assertTrue(instance.canEqual(other));
        assertTrue(instance.canEqual(derived));
        assertFalse(instance.canEqual(non));
    }
    
    public void testToString() throws Exception {

        FileExtensionFilter instance = new FileExtensionFilter();
        instance.toString();
        instance.setExtensions(new ArrayList<String>());
        instance.toString();
        instance.setExtensions(null);
        instance.toString();
    }

    public void testHashcode() throws Exception {

        FileExtensionFilter instance = new FileExtensionFilter();
        FileExtensionFilter other = new FileExtensionFilter();
        assertEquals(instance.hashCode(), other.hashCode());
        instance.setExtensions(new ArrayList<String>());
        other.setExtensions(new ArrayList<String>());
        assertEquals(instance.hashCode(), other.hashCode());
        instance.setExtensions(null);
        other.setExtensions(null);
        assertEquals(instance.hashCode(), other.hashCode());
    }
    
    public void testEquals() throws Exception {
        FileExtensionFilter instance = new FileExtensionFilter();
        List<String> extensions = instance.getExtensions();
        FileExtensionFilter other = new FileExtensionFilter();
        FileExtensionFilter cantEqual = mock(FileExtensionFilter.class);
        when(cantEqual.canEqual(instance)).thenReturn(false);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(other));
        assertTrue(instance.equals(new FileExtensionFilter() {}));
        assertFalse(instance.equals(new Object()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(cantEqual));
        
        instance.setExtensions(new ArrayList<String>());
        assertFalse(instance.equals(other));
        other.setExtensions(new ArrayList<String>());
        assertTrue(instance.equals(other));
        instance.setExtensions(null);
        assertFalse(instance.equals(other));
        other.setExtensions(null);
        assertTrue(instance.equals(other));
        instance.setExtensions(extensions);
        assertFalse(instance.equals(other));
    }
}
