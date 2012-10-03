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
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: mpower
 * Date: 9/21/12
 * Time: 9:21 AM
 */
@Slf4j
public class JsonEventHeaderTest {

    JsonEventHeader header;
    Pattern pattern = Pattern.compile("\\$\\(document\\)\\.ready\\(function\\(\\)\\{\\$\\(this\\)\\.trigger\\(\"([^\"]+)\",");
    String headerString = "$(document).ready(function(){$(this).trigger(\"/level\",";

    @BeforeMethod
    public void setup() {
        header = new JsonEventHeader();
    }

    @Test
    public void testPattern() {
        Matcher match = pattern.matcher(headerString);
        assertTrue(match.matches());
        assertEquals(match.group(1), "/level");
    }

    @Test
    public void testFilter() throws Exception {

        String levelPath = "/level";
        String levelFile = "/all";
        String webAppPath = "/1.0/dhtml";
        String eventHeader = "";

        eventHeader = header.header(webAppPath, webAppPath + levelPath + levelFile);

        assertEquals(eventHeader, headerString);
        Matcher match = pattern.matcher(eventHeader);
        assertTrue(match.matches());
        assertEquals(match.group(1), levelPath);
    }

    @Test
    public void testFilterNegative() throws Exception {

        String levelPath = "level";
        String levelFile = "all";
        String webAppPath = "/1.0/dhtml";
        String eventHeader = "";

        eventHeader = header.header(webAppPath, webAppPath + "/");

        Matcher match = pattern.matcher(eventHeader);
        assertTrue(match.matches());
        assertEquals(match.group(1), "/");
    }

    @Test
    public void testAccessors() {
        assertEquals("$(document).ready(function(){$(this).trigger(\"%s\",",header.getHeaderFormat());
        header.setHeaderFormat(null);
        assertNull(header.getHeaderFormat());
        header.setHeaderFormat("%s");
        assertEquals(header.getHeaderFormat(), "%s");
    }

    @Test
    public void testCanEqual() {
        assertTrue(header.canEqual(new JsonEventHeader()));
        assertFalse(header.canEqual(null));
        assertFalse(header.canEqual(new Integer(1)));
    }

    @Test
    public void testHashCode() {
        log.info(Integer.toHexString(header.hashCode()));
        header.setHeaderFormat(null);
        log.info(Integer.toHexString(header.hashCode()));
    }

    @Test
    public void testEquals() {
        String empty = "";
        JsonEventHeader other = mock(JsonEventHeader.class);
        when(other.canEqual(header)).thenReturn(true);
        JsonEventHeader similar = new JsonEventHeader();
        assertTrue(header.equals(header));
        assertTrue(header.equals(similar));
        assertFalse(header.equals(new Integer(1)));
        assertFalse(header.equals(mock(JsonEventHeader.class)));
        assertFalse(header.equals(other));

        similar.setHeaderFormat(null);
        assertFalse(header.equals(similar));
        header.setHeaderFormat(null);
        assertTrue(header.equals(similar));
        similar.setHeaderFormat(empty);
        assertFalse(header.equals(similar));
        header.setHeaderFormat(empty);
        assertTrue(header.equals(similar));


    }
}
