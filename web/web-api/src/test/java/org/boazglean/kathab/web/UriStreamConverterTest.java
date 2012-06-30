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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.servlet.http.HttpServletResponse;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author mpower
 */
@Test
public class UriStreamConverterTest {
    
    
    public void testConvert() throws Exception {
        UriStreamConverter converter = new UriStreamConverter();
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        Converter<Boolean, HttpServletResponse> processor = converter.convert(source);
        assertNotNull(processor);
    }

    public void testCanEqual() throws Exception {

        UriStreamConverter instance = new UriStreamConverter();
        Object other = new UriStreamConverter();
        Object derived = new UriStreamConverter() {};
        Object non = new Object();
        
        assertTrue(instance.canEqual(other));
        assertTrue(instance.canEqual(derived));
        assertFalse(instance.canEqual(non));
    }
    
    public void testToString() throws Exception {

        UriStreamConverter instance = new UriStreamConverter();
        instance.toString();
    }

    public void testHashcode() throws Exception {

        UriStreamConverter instance = new UriStreamConverter();
        UriStreamConverter other = new UriStreamConverter();
        assertEquals(instance.hashCode(), other.hashCode());
    }
    
    public void testEquals() throws Exception {
        UriStreamConverter instance = new UriStreamConverter();
        UriStreamConverter other = new UriStreamConverter();
        UriStreamConverter cantEqual = mock(UriStreamConverter.class);
        when(cantEqual.canEqual(instance)).thenReturn(false);
        assertTrue(instance.equals(instance));
        assertTrue(instance.equals(other));
        assertTrue(instance.equals(new UriStreamConverter() {}));
        assertFalse(instance.equals(new Object()));
        assertFalse(instance.equals(null));
        assertFalse(instance.equals(cantEqual));
    }    
}
