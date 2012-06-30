/**
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

import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Slf4j
@Data
public class ServletPathConverter implements Converter<URI, HttpServletRequest> {

    @Override
    public URI convert(HttpServletRequest original) {
        URI uri = null;
        log.debug("Converting request to uri, request at {}", original.getRequestURI());
        String path = original.getRequestURI().substring(Math.max(original.getContextPath().length(), original.getContextPath().lastIndexOf('/')));
        if (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.length() != 0) {
            try {
                uri = new URI(path);
            } catch (URISyntaxException ex) {
                String error = "Failed to convert path to uri";
                log.error(error);
                if (log.isInfoEnabled()) {
                    log.info("{}, path: {}", error, path);
                    log.info(error, ex);
                }
            }
        }
        return uri;
    }
}
