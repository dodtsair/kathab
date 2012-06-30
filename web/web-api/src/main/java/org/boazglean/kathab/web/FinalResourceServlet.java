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
import java.net.URI;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Slf4j
@Getter
@Setter
@ToString
public class FinalResourceServlet extends HttpServlet {

    private Converter<URI, HttpServletRequest> requestFactory = new ServletPathConverter();
    private Converter<URI, URI> classpathFactory = new UriClasspathConverter();
    private Filter<URI> requestWhitelistFilter = new FileExtensionFilter();
    private Filter<URI> classpathBlacklistFilter = new ProtectedPathFilter();
    private Converter<Converter<Boolean, HttpServletResponse>, URI> resolver = new UriStreamConverter();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.debug("Get request to servlet: {}", this.getClass().getName());
        URI resourceRequest = requestFactory.convert(req);
        if(requestWhitelistFilter.isFiltered(resourceRequest)) {
            URI classpathResource = classpathFactory.convert(resourceRequest);
            if(!classpathBlacklistFilter.isFiltered(classpathResource)) {
                Converter<Boolean, HttpServletResponse> processor = resolver.convert(classpathResource);
                if(!processor.convert(resp)) {
                    String error = "Failed to respond to request";
                    log.error(error);
                    log.info("{}, request: {}", error, resourceRequest);
                    try {
                        //Ensure that a 500 gets sent
                        //processor may have already set this.  
                        //If so we'll throw, ignore it and move on
                        resp.sendError(500);
                    }
                    catch(IOException ex) {
                        String respError = "Failed to respond with 500";
                        log.info(respError);
                        log.info(respError, ex);
                        log.info("{}, request: {}", respError, resourceRequest);
                    }
                }
                return;
            }
        }
        try {
            resp.sendError(404);
        }
        catch(IOException ex) {
            String error = "Failed to respond with 404";
            log.error(error);
            log.info(error, ex);
            log.info("{}, request: {}", error, resourceRequest);
        }
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.debug("Init servlet: {}", this.getClass().getName());
    }
    
}
