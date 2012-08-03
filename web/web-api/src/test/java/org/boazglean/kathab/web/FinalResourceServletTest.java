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
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 *
 * @author mpower
 */
@Test
public class FinalResourceServletTest {
    
    public void testFinalResourceServlet() {
    }

    public void testAccessors() {
        FinalResourceServlet servlet = new FinalResourceServlet();
        Converter<URI, URI> classpathFactory = servlet.getClasspathFactory();
        Converter<URI, HttpServletRequest> requestFactory = servlet.getRequestFactory();
        Filter<URI> blacklist = servlet.getClasspathBlacklistFilter();
        Filter<URI> whitelist = servlet.getRequestWhitelistFilter();
        Converter<Converter<Boolean, HttpServletResponse>, URI> resolver = servlet.getResolver();

        servlet.setClasspathFactory(mock(Converter.class));
        servlet.setClasspathBlacklistFilter(mock(Filter.class));
        servlet.setRequestFactory(mock(Converter.class));
        servlet.setRequestWhitelistFilter(mock(Filter.class));
        servlet.setResolver(mock(Converter.class));

        assertNotEquals(servlet.getClasspathFactory(), classpathFactory);
        assertNotEquals(servlet.getClasspathBlacklistFilter(), blacklist);
        assertNotEquals(servlet.getRequestFactory(), requestFactory);
        assertNotEquals(servlet.getRequestWhitelistFilter(), whitelist);
        assertNotEquals(servlet.getResolver(), resolver);

        servlet.setClasspathFactory(classpathFactory);
        servlet.setClasspathBlacklistFilter(blacklist);
        servlet.setRequestFactory(requestFactory);
        servlet.setRequestWhitelistFilter(whitelist);
        servlet.setResolver(resolver);

        assertEquals(servlet.getClasspathFactory(), classpathFactory);
        assertEquals(servlet.getClasspathBlacklistFilter(), blacklist);
        assertEquals(servlet.getRequestFactory(), requestFactory);
        assertEquals(servlet.getRequestWhitelistFilter(), whitelist);
        assertEquals(servlet.getResolver(), resolver);
    }

    public void testInit() throws Exception {
        FinalResourceServlet servlet = new FinalResourceServlet();
        servlet.init(mock(ServletConfig.class));
    }
    
    public void testToString() throws Exception {
        FinalResourceServlet servlet = new FinalResourceServlet();
        servlet.toString();
    }
    
    public void testDoGet() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/folder/imagefile.svg");
        when(request.getContextPath()).thenReturn("/webapp");
        servlet.doGet(request, response);
        
        ArgumentCaptor<byte[]> imageCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(out).write(imageCaptor.capture(), anyInt(), anyInt());
        assertEquals(new String(imageCaptor.getValue()).trim(), "mark");
        
    }
    
    public void testDoGetWhitelist() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/MANIFEST.MF");
        when(request.getContextPath()).thenReturn("/webapp");
        servlet.doGet(request, response);
        
        verify(response).sendError(404);
        
    }
    
    public void testDoGetWhitelistException() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/MANIFEST.MF");
        when(request.getContextPath()).thenReturn("/webapp");
        doThrow(new IOException("Generated for testing")).when(response).sendError(404);
        servlet.doGet(request, response);
        
        verify(response).sendError(404);
    }
    
    public void testDoGetBlacklist() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/META-INF/imagefile.svg");
        when(request.getContextPath()).thenReturn("/webapp");
        servlet.doGet(request, response);
        
        verify(response).sendError(404);
        
    }
    
    public void testDoGetFailStream() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/folder/imagefile.svg");
        when(request.getContextPath()).thenReturn("/webapp");
        doThrow(new IOException("Generated for testing")).when(out).write((byte[])any(), anyInt(), anyInt());
        servlet.doGet(request, response);
        verify(response).sendError(500);
    }
    
    public void testDoGetFailStreamException() throws Exception{
        FinalResourceServlet servlet = new FinalResourceServlet();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);
        when(request.getRequestURI()).thenReturn("/webapp/folder/imagefile.svg");
        when(request.getContextPath()).thenReturn("/webapp");
        doThrow(new IOException("Generated for testing")).when(out).write((byte[])any(), anyInt(), anyInt());
        doThrow(new IOException("Generated for testing")).when(response).sendError(500);
        servlet.doGet(request, response);
        verify(response).sendError(500);
    }
    
}
