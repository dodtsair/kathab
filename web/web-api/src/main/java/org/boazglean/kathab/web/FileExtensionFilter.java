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
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Data
@Slf4j
public class FileExtensionFilter implements Filter<URI> {

    private List<String> extensions = Arrays.asList(
            "gif",
            "ico",
            "jpeg",
            "jpg",
            "js",
            "css",
            "jss",
            "png",
            "svg",
            "xhtml",
            "html",
            "htm");

    @Override
    public boolean isFiltered(URI filter) {
        log.debug("Filter request for uri: {}", filter);
        boolean filtered = false;
        if (filter != null && filter.getPath() != null) {
            for (String extension : extensions) {
                // this is the same as filter.getPath().endsWith("." + extension)
                // without the string concatenation
                if (filter.getPath().endsWith(extension)
                        && filter.getPath().charAt(filter.getPath().length() - extension.length() - 1) == '.') {
                    log.debug("URI matched extension, uri: {} extension: {}", filter, extension);
                    filtered = true;
                    break;                    
                }
            }
        }
        return filtered;
    }
}
