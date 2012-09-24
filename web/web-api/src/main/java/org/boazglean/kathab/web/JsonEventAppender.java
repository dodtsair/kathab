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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * User: mpower
 * Date: 9/11/12
 * Time: 12:07 PM
 */
@Data
@Slf4j
public class JsonEventAppender implements Filter {

    private String acceptContent = "\\*/\\*";
    private String wrappedContent = "application/(.+\\+)?json";
    private JsonEventHeader header = new JsonEventHeader();
    private JsonEventFooter footer = new JsonEventFooter();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletResponse instanceof HttpServletResponse) {
            HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
            HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
            if(httpServletRequest.getHeader(HttpHeader.ACCEPT.getSpec()).matches(acceptContent)) {
                JsonEventAppender.log.info("Requested content type matches {}, filtering request for event wrapped json", acceptContent);
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest) {

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if(name.equals(HttpHeader.ACCEPT.getSpec())) {
                            Vector<String> acceptHeader = new Vector<String>(Arrays.asList("application/json"));
                            return acceptHeader.elements();
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public String getHeader(String name) {
                        if(name.equals(HttpHeader.ACCEPT.getSpec())) {
                            return "application/json";
                        }
                        return super.getHeader(name);
                    }
                };
                HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpServletResponse) {
                    BufferedServletOutputStream buffer = new BufferedServletOutputStream();
                    @Override
                    public ServletOutputStream getOutputStream() throws IOException {
                        return buffer;
                    }
                };




                filterChain.doFilter(requestWrapper, responseWrapper);



                OutputStream out = httpServletResponse.getOutputStream();
                BufferedServletOutputStream buffer = (BufferedServletOutputStream) responseWrapper.getOutputStream();
                Collection<String> headernames = responseWrapper.getHeaderNames();
                for(String headerName: responseWrapper.getHeaderNames()) {
                    responseWrapper.setHeader(headerName, httpServletResponse.getHeader(headerName));
                }
                if(responseWrapper.getContentType() != null && responseWrapper.getContentType().matches(wrappedContent)) {
                    byte[] headerData = header.header(httpServletRequest.getContextPath(), httpServletRequest.getRequestURI()).getBytes("UTF-8");
                    byte[] footerData = footer.footer().getBytes("UTF-8");
                    log.info("Provided content type is json, wrapping the body in the event json header and footer");
                    httpServletResponse.setHeader(HttpHeader.CONTENT_TYPE.getSpec(), "application/javascript");
                    httpServletResponse.setHeader(HttpHeader.CONTENT_LENGTH.getSpec(), Integer.toString(headerData.length + footerData.length + buffer.size()));
                    out.write(headerData, 0, headerData.length);
                    buffer.writeTo(out);
                    out.write(footerData, 0, footerData.length);
                }
                else {
                    log.info("Provided content type is not json doing no wrapping, content type: {}", responseWrapper.getContentType());
                    if(out != null) {
                        httpServletResponse.setHeader(HttpHeader.CONTENT_LENGTH.getSpec(), Integer.toString(buffer.size()));
                        buffer.writeTo(out);
                    }
                    log.info("Completed wrapping json in an event");
                }
            }
            else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
