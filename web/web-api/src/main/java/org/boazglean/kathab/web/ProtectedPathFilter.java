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
import java.util.regex.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Slf4j
@Getter
@Setter
@ToString
public class ProtectedPathFilter implements Filter<URI> {
    
    List<Pattern> protectedPaths = Arrays.asList(
            Pattern.compile(".*!/WEB-INF/[^!]*$", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*/META-INF/[^!]*$", Pattern.CASE_INSENSITIVE));

    @Override
    public boolean isFiltered(URI filter) {
        if(filter != null) {
            String rawUri = filter.toASCIIString();
            for(Pattern protectedPath: protectedPaths) {
                if(protectedPath.matcher(rawUri).matches()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
}
