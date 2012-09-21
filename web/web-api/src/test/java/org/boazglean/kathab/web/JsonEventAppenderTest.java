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

import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: mpower
 * Date: 9/11/12
 * Time: 12:41 PM
 */
@Slf4j
public class JsonEventAppenderTest {

    public static class Arrays {
        public static boolean equals(byte[] first, int firstOffset, byte[] second, int secondOffset, int length) {
            for(int offset = 0; offset < length; ++offset) {
                if(first[offset + firstOffset] != second[offset + secondOffset]) {
                    return false;
                }
            }
            return true;
        }
    }

    private JsonEventAppender appender;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private ServletOutputStream mockOutputStream;
    private FilterChain mockFilterChain;

    @BeforeMethod
    public void setup() {
        appender = new JsonEventAppender();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockFilterChain = mock(FilterChain.class);
        mockOutputStream = mock(ServletOutputStream.class);
    }

    @Test
    public void testAccessors() {
        String notEmpty = "a";
        String headerDefault = "$(document).ready(function() {";
        assertEquals(appender.getHeader(), headerDefault);
        appender.setHeader(null);
        assertNull(appender.getHeader());
        appender.setHeader(notEmpty);
        assertEquals(appender.getHeader(), notEmpty);
        appender.setHeader(headerDefault);
        assertEquals(appender.getHeader(), headerDefault);

        String footerDefault = "});";
        assertEquals(appender.getFooter(), footerDefault);
        appender.setFooter(null);
        assertNull(appender.getFooter());
        appender.setFooter(notEmpty);
        assertEquals(appender.getFooter(), notEmpty);
        appender.setFooter(footerDefault);
        assertEquals(appender.getFooter(), footerDefault);

        String acceptDefault = "application/x-event\\+json";
        assertEquals(appender.getAcceptContent(), acceptDefault);
        appender.setAcceptContent(null);
        assertNull(appender.getAcceptContent());
        appender.setAcceptContent(notEmpty);
        assertEquals(appender.getAcceptContent(), notEmpty);
        appender.setAcceptContent(acceptDefault);
        assertEquals(appender.getAcceptContent(), acceptDefault);

        String wrappedDefault = "application/(.+\\+)?json";
        assertEquals(appender.getWrappedContent(), wrappedDefault);
        appender.setWrappedContent(null);
        assertNull(appender.getWrappedContent());
        appender.setWrappedContent(notEmpty);
        assertEquals(appender.getWrappedContent(), notEmpty);
        appender.setWrappedContent(wrappedDefault);
        assertEquals(appender.getWrappedContent(), wrappedDefault);

    }

    @Test
    public void testCanEqual() {
        assertTrue(appender.canEqual(new JsonEventAppender()));
        assertFalse(appender.canEqual(null));
        assertFalse(appender.canEqual(new Integer(1)));
    }

    @Test
    public void testEquals() {
        String empty = "";
        JsonEventAppender other = mock(JsonEventAppender.class);
        when(other.canEqual(appender)).thenReturn(true);
        JsonEventAppender similar = new JsonEventAppender();
        assertTrue(appender.equals(appender));
        assertTrue(appender.equals(similar));
        assertFalse(appender.equals(new Integer(1)));
        assertFalse(appender.equals(mock(JsonEventAppender.class)));
        assertFalse(appender.equals(other));

        similar.setAcceptContent(null);
        assertFalse(appender.equals(similar));
        appender.setAcceptContent(null);
        assertTrue(appender.equals(similar));
        similar.setAcceptContent(empty);
        assertFalse(appender.equals(similar));
        appender.setAcceptContent(empty);

        similar.setFooter(null);
        assertFalse(appender.equals(similar));
        appender.setFooter(null);
        assertTrue(appender.equals(similar));
        similar.setFooter(empty);
        assertFalse(appender.equals(similar));
        appender.setFooter(empty);

        similar.setHeader(null);
        assertFalse(appender.equals(similar));
        appender.setHeader(null);
        assertTrue(appender.equals(similar));
        similar.setHeader(empty);
        assertFalse(appender.equals(similar));
        appender.setHeader(empty);

        similar.setWrappedContent(null);
        assertFalse(appender.equals(similar));
        appender.setWrappedContent(null);
        assertTrue(appender.equals(similar));
        similar.setWrappedContent(empty);
        assertFalse(appender.equals(similar));
        appender.setWrappedContent(empty);
    }

    @Test
    public void testHashCode() {
        JsonEventAppenderTest.log.info(Integer.toHexString(appender.hashCode()));
        appender.setHeader(null);
        appender.setFooter(null);
        appender.setAcceptContent(null);
        appender.setWrappedContent(null);
        JsonEventAppenderTest.log.info(Integer.toHexString(appender.hashCode()));
    }

    @Test
    public void testToString() {
        JsonEventAppenderTest.log.info(appender.toString());
        appender.setHeader(null);
        appender.setFooter(null);
        appender.setAcceptContent(null);
        appender.setWrappedContent(null);
        JsonEventAppenderTest.log.info(appender.toString());
    }

    @Test
    public void testInitMethod() throws Exception {
        FilterConfig mockConfig = mock(FilterConfig.class);
        appender.init(mockConfig);
        verifyZeroInteractions(mockConfig);
    }

    @Test
    public void testDestroyMethod() throws Exception {
        appender.destroy();
    }

    @Test
    public void testContentTypeMatch() {
        assertTrue("application/json".matches(appender.getWrappedContent()));
        assertTrue("application/blah+json".matches(appender.getWrappedContent()));
        assertFalse("application/+json".matches(appender.getWrappedContent()));
        assertFalse("application/jsonp".matches(appender.getWrappedContent()));
    }


    @Test
    public void testAcceptMatch() {
        assertTrue("application/x-event+json".matches(appender.getAcceptContent()));
        assertFalse("application/x-event+jsonp".matches(appender.getAcceptContent()));
        assertFalse("application/event+json".matches(appender.getAcceptContent()));
        assertFalse("application/x-padding+json".matches(appender.getAcceptContent()));
    }

    @Test
    public void testBypass() throws Exception {
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("text/html");
        appender.doFilter(mockRequest, mockResponse, mockFilterChain);
        verify(mockFilterChain).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testSkipWhenNotJSONResponse() throws Exception {
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json");
        when(mockResponse.getContentType()).thenReturn("text/xml");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(mockFilterChain, atLeastOnce()).doFilter(captor.capture(), any(HttpServletResponse.class));
        verify(mockOutputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
        assertTrue(captor.getValue() instanceof HttpServletRequestWrapper);
    }

    @Test
    public void testChangeContentType() throws Exception {
        final String readThroughHeader = "something";
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json").thenReturn(readThroughHeader);
        when(mockRequest.getHeader(readThroughHeader)).thenReturn(readThroughHeader);
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest request = (HttpServletRequest) invocationOnMock.getArguments()[0];
                HttpServletResponse response = (HttpServletResponse) invocationOnMock.getArguments()[1];

                assertEquals(request.getHeader(HttpHeader.ACCEPT.getSpec()), "application/json");
                assertEquals(request.getHeader(readThroughHeader), readThroughHeader);
                return null;
            }
        }).when(mockFilterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

    }

    @Test
    public void testWrapEvent() throws Exception {
        final byte[] bodyData = "body".getBytes("UTF-8");
        byte[] headerData = appender.getHeader().getBytes("UTF-8");
        byte[] footerData = appender.getFooter().getBytes("UTF-8");
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json");
        when(mockResponse.getContentType()).thenReturn("application/json");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletResponse response = (HttpServletResponse) invocationOnMock.getArguments()[1];
                response.getOutputStream().write(bodyData, 0, bodyData.length);
                return null;
            }
        }).when(mockFilterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, atLeastOnce()).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        ArgumentCaptor<byte[]> headerCapture = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<byte[]> bodyCapture = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<byte[]> footerCapture = ArgumentCaptor.forClass(byte[].class);
        verify(mockOutputStream).write(headerCapture.capture(), anyInt(), eq(headerData.length));
        verify(mockOutputStream).write(bodyCapture.capture(), anyInt(), eq(bodyData.length));
        verify(mockOutputStream).write(footerCapture.capture(), anyInt(), eq(footerData.length));
        Arrays.equals(headerCapture.getValue(), 0, headerData, 0, headerData.length);
        Arrays.equals(bodyCapture.getValue(), 0, headerData, 0, bodyData.length);
        Arrays.equals(footerCapture.getValue(), 0, headerData, 0, footerData.length);
    }

    @Test
    public void testNullOutputStream() throws Exception {
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json");
        when(mockResponse.getContentType()).thenReturn("text/xml");

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(mockFilterChain, atLeastOnce()).doFilter(captor.capture() , any(HttpServletResponse.class));
        assertTrue(captor.getValue() instanceof HttpServletRequestWrapper);
    }

    @Test
    public void testMultiHeader() throws Exception {
        when(mockRequest.getHeader(HttpHeader.CACHE_CONTROL.getSpec())).thenReturn("non null");
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json");
        when(mockResponse.getContentType()).thenReturn("text/xml");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(mockFilterChain, atLeastOnce()).doFilter(captor.capture(), any(HttpServletResponse.class));
        verify(mockOutputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
        assertTrue(captor.getValue() instanceof HttpServletRequestWrapper);
    }

    @Test
    public void testHeaderList() throws Exception {
        Collection<String> headerNames = mock(Collection.class);
        Iterator<String> mockIterator = mock(Iterator.class);
        when(mockRequest.getHeader(HttpHeader.CACHE_CONTROL.getSpec())).thenReturn("non null");
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("application/x-event+json");
        when(mockResponse.getContentType()).thenReturn("text/xml");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        when(mockResponse.getHeaderNames()).thenReturn(headerNames);
        when(headerNames.isEmpty()).thenReturn(false);
        when(headerNames.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockIterator.next()).thenReturn("first");
        when(mockIterator.next()).thenReturn("second");

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(mockFilterChain, atLeastOnce()).doFilter(captor.capture(), any(HttpServletResponse.class));
        verify(mockOutputStream, atLeastOnce()).write(any(byte[].class), anyInt(), anyInt());
        verify(headerNames).iterator();
        verify(mockIterator, times(3)).hasNext();
//        verifyNoMoreInteractions(headerNames);
        assertTrue(captor.getValue() instanceof HttpServletRequestWrapper);
    }

    @Test
    public void testSkipWhenNotHttp() throws Exception {
        ServletRequest request = mock(ServletRequest.class);
        ServletResponse response = mock(ServletResponse.class);

        appender.doFilter(request, response, mockFilterChain);

        verify(mockFilterChain).doFilter(request,response);
    }

}
