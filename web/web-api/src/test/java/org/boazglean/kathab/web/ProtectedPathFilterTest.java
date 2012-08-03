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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author mpower
 */
@Slf4j
@Test
public class ProtectedPathFilterTest {
    public void testConvertRejected() throws Exception {
        ProtectedPathFilter instance = new ProtectedPathFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("a-META-INF/..."));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("/WEB-INF/..."));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("jar:file:/WEB-INF/a/b/c!/d/e/f"));
        assertFalse(filtered);
        filtered = instance.isFiltered(new URI("jar:file:/META-INF/a/b/c!/d/e/f"));
        assertFalse(filtered);
    }

    public void testConvert() throws Exception {
        ProtectedPathFilter instance = new ProtectedPathFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("jar:file:/a/b/c!/WEB-INF/d/e/f"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("jar:file:/a/b/c!/META-INF/d/e/f"));
        assertTrue(filtered);
    }

    public void testConvertNull() throws Exception {
        ProtectedPathFilter instance = new ProtectedPathFilter();
        boolean filtered;
        filtered = instance.isFiltered(null);
        assertTrue(filtered);
    }

    public void testConvertEmptyUri() throws Exception {
        ProtectedPathFilter instance = new ProtectedPathFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("http:", null, null, null));
        assertFalse(filtered);
    }

    public void testConvertEmptyPath() throws Exception {
        ProtectedPathFilter instance = new ProtectedPathFilter();
        boolean filtered = true;
        filtered = instance.isFiltered(new URI("http:", "", "", ""));
        assertFalse(filtered);
    }
    
    public void testAccessers() throws Exception {

        ProtectedPathFilter instance = new ProtectedPathFilter();
        List<Pattern> patterns = instance.getProtectedPaths();
        assertNotNull(patterns);

        boolean filtered = false;
        instance.setProtectedPaths(Arrays.asList(Pattern.compile(".*a"),Pattern.compile(".*b")));
        filtered = instance.isFiltered(new URI("something.a"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.b"));
        assertTrue(filtered);
        filtered = instance.isFiltered(new URI("something.gif"));
        assertFalse(filtered);

        instance.setProtectedPaths(patterns);
        filtered = instance.isFiltered(new URI("jar:file:/a/b/c!/WEB-INF/d/e/f"));
        assertTrue(filtered);
    }

    public void testToString() throws Exception {

        ProtectedPathFilter instance = new ProtectedPathFilter();
        instance.toString();
        instance.setProtectedPaths(new ArrayList<Pattern>());
        instance.toString();
        instance.setProtectedPaths(null);
        instance.toString();
    }
}
