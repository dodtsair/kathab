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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author mpower
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class ResponseStreamer implements Converter<Boolean, HttpServletResponse> {
    @NonNull
    private URI source;

    @Override
    public Boolean convert(HttpServletResponse resp) {
        Boolean result = false;
        try {
            URLConnection connection = source.toURL().openConnection();
            int contentLength = connection.getContentLength();
            long lastModified = connection.getLastModified();
            resp.setHeader(HttpHeader.CONTENT_LENGTH.getSpec(), Long.toString(contentLength));
            resp.setDateHeader(HttpHeader.LAST_MODIFIED.getSpec(), lastModified);
            InputStream in = connection.getInputStream();
            try {
                OutputStream out = resp.getOutputStream();
                try {
                    stream(in, out);
                    result = true;
                } catch(IOException ex) {
                    String error = "Failed to stream uri";
                    log.error(error);
                    log.info(error, ex);
                    log.info("{}, uri: {}", error, source);
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (MalformedURLException ex) {
            String error = "Failed form a url from the uri";
            log.error(error);
            log.info(error, ex);
            log.info("{}, uri: {}", error, source);
        } catch (IOException ex) {
            String error = "Failed io operations";
            log.error(error);
            log.info(error, ex);
            log.info("{}, uri: {}", error, source);
        }
        return result;
    }

    public static void stream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read = in.read(buffer);
        while (read != -1) {
            out.write(buffer, 0, read);
            read = in.read(buffer);
        }
    }
    
}
