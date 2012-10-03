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

import java.util.*;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * User: mpower
 * Date: 9/11/12
 * Time: 12:41 PM
 */
@Slf4j
public class JsonEventAppenderTest {

    private JsonEventAppender appender;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private ServletOutputStream mockOutputStream;
    private FilterChain mockFilterChain;
    private JsonEventFooter mockFooter;
    private JsonEventHeader mockHeader;

    @BeforeMethod
    public void setup() {
        appender = new JsonEventAppender();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockFilterChain = mock(FilterChain.class);
        mockOutputStream = mock(ServletOutputStream.class);
        mockFooter = mock(JsonEventFooter.class);
        mockHeader = mock(JsonEventHeader.class);
    }

    @Test
    public void testAccessors() {
        String notEmpty = "notEmpty";
        JsonEventHeader diffHeader = new JsonEventHeader();
        JsonEventHeader headerDefault = new JsonEventHeader();
        diffHeader.setHeaderFormat("");
        assertEquals(appender.getHeader(), headerDefault);
        appender.setHeader(null);
        assertNull(appender.getHeader());
        appender.setHeader(diffHeader);
        assertEquals(appender.getHeader(), diffHeader);
        appender.setHeader(headerDefault);
        assertEquals(appender.getHeader(), headerDefault);

        JsonEventFooter diffFooter = new JsonEventFooter();
        JsonEventFooter footerDefault = new JsonEventFooter();
        assertEquals(appender.getFooter(), footerDefault);
        appender.setFooter(null);
        assertNull(appender.getFooter());
        appender.setFooter(diffFooter);
        assertEquals(appender.getFooter(), diffFooter);
        appender.setFooter(footerDefault);
        assertEquals(appender.getFooter(), footerDefault);

        String acceptDefault = "\\*/\\*";
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
        JsonEventFooter emptyFooter = new JsonEventFooter();
        emptyFooter.setFooter("");
        JsonEventHeader emptyHeader = new JsonEventHeader();
        emptyHeader.setHeaderFormat("");
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
        similar.setFooter(emptyFooter);
        assertFalse(appender.equals(similar));
        appender.setFooter(emptyFooter);

        similar.setHeader(null);
        assertFalse(appender.equals(similar));
        appender.setHeader(null);
        assertTrue(appender.equals(similar));
        similar.setHeader(emptyHeader);
        assertFalse(appender.equals(similar));
        appender.setHeader(emptyHeader);

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
        log.info(Integer.toHexString(appender.hashCode()));
        appender.setHeader(null);
        appender.setFooter(null);
        appender.setAcceptContent(null);
        appender.setWrappedContent(null);
        log.info(Integer.toHexString(appender.hashCode()));
    }

    @Test
    public void testToString() {
        log.info(appender.toString());
        appender.setHeader(null);
        appender.setFooter(null);
        appender.setAcceptContent(null);
        appender.setWrappedContent(null);
        log.info(appender.toString());
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
        assertTrue("*/*".matches(appender.getAcceptContent()));
        assertTrue("application/javascript".matches(appender.getAcceptContent()));
        assertTrue("application/javascript, garbage".matches(appender.getAcceptContent()));
        assertTrue("application/x-event+jsonp".matches(appender.getAcceptContent()));
        assertTrue("application/x-event+json".matches(appender.getAcceptContent()));
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
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*");
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
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*").thenReturn(readThroughHeader);
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
    public void testChangeContentTypeViaHeaders() throws Exception {
        final String readThroughHeader = "something";
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*").thenReturn(readThroughHeader);
        when(mockRequest.getHeaders(readThroughHeader)).thenReturn(new Vector<String>(Arrays.asList(readThroughHeader)).elements());
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest request = (HttpServletRequest) invocationOnMock.getArguments()[0];
                HttpServletResponse response = (HttpServletResponse) invocationOnMock.getArguments()[1];

                assertEquals(request.getHeaders(HttpHeader.ACCEPT.getSpec()).nextElement(), "application/json");
                assertEquals(request.getHeaders(readThroughHeader).nextElement(), readThroughHeader);
                return null;
            }
        }).when(mockFilterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

    }

    @Test
    public void testWrapEvent() throws Exception {
        final String expectedBody = "body";
        appender.setFooter(mockFooter);
        appender.setHeader(mockHeader);
        String expectedHeader = "jsonp(";
        String expectedFooter = ");";
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*");
        when(mockHeader.header(null, null)).thenReturn(expectedHeader);
        when(mockFooter.footer()).thenReturn(expectedFooter);
        when(mockResponse.getContentType()).thenReturn("application/json");
        when(mockResponse.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletResponse response = (HttpServletResponse) invocationOnMock.getArguments()[1];
                response.getOutputStream().write(expectedBody.getBytes("UTF-8"), 0, expectedBody.length());
                return null;
            }
        }).when(mockFilterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockFilterChain, atLeastOnce()).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        ArgumentCaptor<byte[]> headerCapture = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<byte[]> bodyCapture = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<byte[]> footerCapture = ArgumentCaptor.forClass(byte[].class);
        verify(mockOutputStream).write(headerCapture.capture(), anyInt(), eq(expectedHeader.length()));
        verify(mockOutputStream).write(bodyCapture.capture(), anyInt(), eq(expectedBody.length()));
        verify(mockOutputStream).write(footerCapture.capture(), anyInt(), eq(expectedFooter.length()));

        String actualHeader = new String(headerCapture.getValue());
        String actualBody = new String(bodyCapture.getValue()).trim();
        String actualFooter = new String(footerCapture.getValue());

        assertEquals(actualHeader, expectedHeader);
        assertEquals(actualBody, expectedBody);
        assertEquals(actualFooter, expectedFooter);
    }

    @Test
    public void testNullOutputStream() throws Exception {
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*");
        when(mockResponse.getContentType()).thenReturn("text/xml");

        appender.doFilter(mockRequest, mockResponse, mockFilterChain);

        ArgumentCaptor<ServletRequest> captor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(mockFilterChain, atLeastOnce()).doFilter(captor.capture() , any(HttpServletResponse.class));
        assertTrue(captor.getValue() instanceof HttpServletRequestWrapper);
    }

    @Test
    public void testMultiHeader() throws Exception {
        when(mockRequest.getHeader(HttpHeader.CACHE_CONTROL.getSpec())).thenReturn("non null");
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*");
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
        when(mockRequest.getHeader(HttpHeader.ACCEPT.getSpec())).thenReturn("*/*");
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
