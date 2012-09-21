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

import lombok.Getter;
import lombok.Setter;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: mpower
 * Date: 9/11/12
 * Time: 1:41 PM
 */
@Setter
@Getter
public class BufferedServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override
    public void write(byte[] b) throws IOException {
        buffer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buffer.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.write(b);
    }

    public void writeTo(OutputStream dest) throws IOException{
        buffer.writeTo(dest);
    }

    public int size() {
        return buffer.size();
    }

}
