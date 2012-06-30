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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author mpower
 */
public class ResponseStreamerTest {
    
    @Test
    public void testAccessors() throws Exception {
        URI source = new URI("a");
        URI newSource = new URI("b");
        ResponseStreamer streamer = new ResponseStreamer(source);
        assertEquals(source, streamer.getSource());
        streamer.setSource(newSource);
        assertEquals(newSource, streamer.getSource());
        streamer.setSource(source);
        assertEquals(source, streamer.getSource());
    }
    
    @Test(expectedExceptions={NullPointerException.class})
    public void testIllegalArgumentAccessor() throws Exception {
        URI source = new URI("a");
        ResponseStreamer streamer = new ResponseStreamer(source);
        streamer.setSource(null);
    }    
    
    @Test(expectedExceptions={NullPointerException.class})
    public void testIllegalArgumentConstructor() throws Exception {
        ResponseStreamer streamer = new ResponseStreamer(null);
    }    
    
    @Test
    public void testConstructor() throws Exception {
        URI source = new URI("a");
        //Ignore compiler warning by adding toString
        new ResponseStreamer(source).toString();
    }
    
    @Test
    public void testConverter() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(out);
        Boolean result = streamer.convert(resp);
        assertTrue(result);
        verify(resp).setDateHeader(eq(HttpHeader.LAST_MODIFIED.getSpec()), anyLong());
        verify(resp).setHeader(eq(HttpHeader.CONTENT_LENGTH.getSpec()), anyString());
        verify(out).close();
    }
    
    @Test
    public void testConverterMalformedUrl() throws Exception {
        //We are going to cheat to get this code exercised.  Normally the
        //uri method call would throw the MalformedUrlException
        //we are going to throw it out the getOutputStream
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenThrow(new MalformedURLException("Exception generated for negative testing"));
        Boolean result = streamer.convert(resp);
        assertFalse(result);
    }

    @Test
    public void testConverterUrlConnectionException() throws Exception {
        //We are going to cheat to get this code exercised.  Normally the
        //openConnection method call would throw the IOException
        //we are going to throw it out the getOutputStream
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenThrow(new IOException("Exception generated for negative testing"));
        Boolean result = streamer.convert(resp);
        assertFalse(result);
    }

    @Test
    public void testConverterWriteException() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(out);
        doThrow(new IOException("Exception generated for negative testing")).when(out).write((byte[])any(), anyInt(), anyInt());
        Boolean result = streamer.convert(resp);
        assertFalse(result);
        verify(out).close();
    }
    
    @Test
    public void testConverterCloseException() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(resp.getOutputStream()).thenReturn(out);
        doThrow(new IOException("Exception generated for negative testing")).when(out).close();
        Boolean result = streamer.convert(resp);
        //We fail after we succeed at writing everything, so we assume the write worked.
        assertTrue(result);
    }
    
    @Test
    public void testCanEqual() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        ResponseStreamer streamer = new ResponseStreamer(source);
        ResponseStreamer other = new ResponseStreamer(source);
        ResponseStreamer derived = new ResponseStreamer(source) {};
        assertTrue(streamer.canEqual(streamer));
        assertTrue(streamer.canEqual(other));
        assertTrue(streamer.canEqual(derived));
        assertFalse(streamer.canEqual(new Object()));
    }
    
    @Test
    public void testEquals() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        URI sameSource = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        URI otherSource = new URI("A");
        ResponseStreamer streamer = new ResponseStreamer(source);
        ResponseStreamer same = new ResponseStreamer(sameSource);
        ResponseStreamer other = new ResponseStreamer(source);
        ResponseStreamer diff = new ResponseStreamer(otherSource);
        ResponseStreamer derived = new ResponseStreamer(source) {};
        ResponseStreamer derivedDiff = new ResponseStreamer(otherSource) {};
        ResponseStreamer nullMember = new ResponseStreamer(source);
        Object not = new Object();
        ResponseStreamer cant = new ResponseStreamer(source) {

            @Override
            public boolean canEqual(Object other) {
                return false;
            }
            
        };
        setSource(nullMember, null);

        assertTrue(streamer.equals(streamer));
        assertTrue(streamer.equals(same));
        assertTrue(streamer.equals(other));
        assertTrue(streamer.equals(derived));
        assertFalse(streamer.equals(diff));
        assertFalse(streamer.equals(derivedDiff));
        assertFalse(streamer.equals(nullMember));
        assertFalse(nullMember.equals(streamer));
        assertFalse(streamer.equals(cant));
        assertFalse(streamer.equals(not));
        assertFalse(streamer.equals(null));
    }
    
    @Test
    public void testHashCode() throws Exception {
        URI source = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF").toURI();
        URI otherSource = new URI("A");
        ResponseStreamer streamer = new ResponseStreamer(source);
        ResponseStreamer other = new ResponseStreamer(source);
        ResponseStreamer diff = new ResponseStreamer(otherSource);
        ResponseStreamer derived = new ResponseStreamer(source) {};
        ResponseStreamer nullMember = new ResponseStreamer(source);
        Object not = new Object();
        ResponseStreamer cant = new ResponseStreamer(source) {

            @Override
            public boolean canEqual(Object other) {
                return false;
            }
            
        };
        
        setSource(nullMember, null);
        assertEquals(streamer.hashCode(), streamer.hashCode());
        assertEquals(streamer.hashCode(), derived.hashCode());
        assertEquals(streamer.hashCode(), other.hashCode());
        assertEquals(streamer.hashCode(), cant.hashCode());
        assertNotEquals(streamer.hashCode(), diff.hashCode());
        assertNotEquals(streamer.hashCode(), not.hashCode());
        assertNotEquals(nullMember.hashCode(), streamer.hashCode());
        assertNotEquals(streamer.hashCode(), nullMember.hashCode());
    }
    
    private void setSource(ResponseStreamer streamer, URI source) throws Exception {
        //Need to cut past the null checks to exercise some code branches
        Field sourceField = streamer.getClass().getDeclaredField("source");
        sourceField.setAccessible(true);
        sourceField.set(streamer, source);
    }
}
