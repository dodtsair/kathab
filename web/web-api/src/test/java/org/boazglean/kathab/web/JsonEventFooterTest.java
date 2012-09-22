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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * User: mpower
 * Date: 9/21/12
 * Time: 11:19 AM
 */
@Slf4j
public class JsonEventFooterTest {

    JsonEventFooter footer;

    @BeforeMethod
    public void setup() {
        footer = new JsonEventFooter();
    }

    @Test
    public void testFilter() throws Exception {

        String levelPath = "level";
        String webAppPath = "/1.0/dhtml";
        String eventFooter = "";

        eventFooter = footer.footer();

        assertEquals(eventFooter, String.format(footer.getFooter()));
    }

    @Test
    public void testAccessors() {
        assertEquals(");});",footer.getFooter());
        footer.setFooter(null);
        assertNull(footer.getFooter());
        footer.setFooter("%s");
        assertEquals(footer.getFooter(), "%s");
    }
    @Test
    public void testCanEqual() {
        assertTrue(footer.canEqual(new JsonEventFooter()));
        assertFalse(footer.canEqual(null));
        assertFalse(footer.canEqual(new Integer(1)));
    }

    @Test
    public void testHashCode() {
        log.info(Integer.toHexString(footer.hashCode()));
        footer.setFooter(null);
        log.info(Integer.toHexString(footer.hashCode()));
    }

    @Test
    public void testEquals() {
        String empty = "";
        JsonEventFooter other = mock(JsonEventFooter.class);
        when(other.canEqual(footer)).thenReturn(true);
        JsonEventFooter similar = new JsonEventFooter();
        assertTrue(footer.equals(footer));
        assertTrue(footer.equals(similar));
        assertFalse(footer.equals(new Integer(1)));
        assertFalse(footer.equals(mock(JsonEventFooter.class)));
        assertFalse(footer.equals(other));

        similar.setFooter(null);
        assertFalse(footer.equals(similar));
        footer.setFooter(null);
        assertTrue(footer.equals(similar));
        similar.setFooter(empty);
        assertFalse(footer.equals(similar));
        footer.setFooter(empty);
        assertTrue(footer.equals(similar));


    }
}
