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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Slf4j
@Data
public class UriClasspathConverter implements Converter<URI, URI> {

    private ClassLoader loader = this.getClass().getClassLoader();

    @Override
    public URI convert(URI original) {
        String search = null;
        URL found = null;
        URI result = null;
        if (original != null) {
            search = original.getPath();
            try {
                if (!search.isEmpty()) {
                    log.debug("Searching for resource, resource: {}", search);
                    found = loader.getResource(search);
                    if (found != null) {
                        log.debug("Found resource at: {}", found);
                        result = found.toURI();
                    }
                    else {
                        log.debug("Did not find resource, resource: {}", search);
                    }
                }
            } catch (URISyntaxException ex) {
                String error = "Failed to convert URL to URI";
                log.error(error);
                if (log.isInfoEnabled()) {
                    log.info("{}, url: {}", error, found);
                    log.info(error, ex);
                }
            }
        }
        return result;
    }
}
