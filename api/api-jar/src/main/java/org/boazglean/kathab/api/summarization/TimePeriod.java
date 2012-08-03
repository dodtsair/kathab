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
package org.boazglean.kathab.api.summarization;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author mpower
 */
public enum TimePeriod {
    SECOND(TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)),
    MINUTE(TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)),
    QUARTER(TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES)),
    HALF(TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)),
    HOUR(TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)),
    DAY(TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

    private final long millis;

    private TimePeriod(long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }
}
