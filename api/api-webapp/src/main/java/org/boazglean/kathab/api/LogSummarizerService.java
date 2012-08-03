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
package org.boazglean.kathab.api;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.boazglean.kathab.api.summarization.*;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author mpower
 */
@Path(value="/summary")
@Data
@Slf4j
public class LogSummarizerService extends JdbcLogSummarizer implements LogSummarizer {

    public LogSummarizerService() {
        JdbcDataSource jdbcSource;
        jdbcSource = new JdbcDataSource();
        jdbcSource.setURL("jdbc:h2:mem:api-webapp;DB_CLOSE_DELAY=-1");
        this.setSource(jdbcSource);
    }
   
    @Override
    public DataSource getSource() {
        return super.getSource();
    }

    @Override
    public void setSource(DataSource source) {
        super.setSource(source);
    }

    @Override
    @GET
    @Path(value="/level")
    @Produces(value="application/json")
    public LevelSummary summarizeByLevel() {
        log.info("Call to summary");
        return super.summarizeByLevel();
    }

    @Override
    @GET
    @Path(value="/level/{level}")
    @Produces(value="application/json")
    public LevelSummary summarizeByLevel(@PathParam(value="level") LogLevel... levels) {
        log.info("Call to summary");
        log.debug("Call to summary, levels: {}", levels);
        return super.summarizeByLevel(levels);
    }

    @Override
    @GET
    @Path(value="/prefix")
    @Produces(value="application/json")
    public PrefixSummary summarizeByPrefix() {
        log.info("Call to summary");
        return super.summarizeByPrefix();
    }

    @Override
    @GET
    @Path(value="/prefix/{prefix}")
    @Produces(value="application/json")
    public PrefixSummary summarizeByPrefix(@PathParam(value="prefix") String... includePrefixes) {
        log.info("Call to summary");
        log.debug("Call to summary, prefixes: {}", includePrefixes);
        return super.summarizeByPrefix(includePrefixes);
    }

    @Override
    @GET
    @Path(value="/prefix/{prefix}/level/{level}")
    @Produces(value="application/json")
    public LevelSummary summarizeByPrefixAndLevel(@PathParam(value="prefix") String includePrefix, @PathParam(value="level") LogLevel... levels) {
        log.info("Call to summary");
        log.debug("Call to summary, prefix: {}, levels: {}", includePrefix, levels);
        return super.summarizeByPrefixAndLevel(includePrefix, levels);
    }

    @Override
    @GET
    @Path(value="/period")
    @Produces(value="application/json")
    public TimeSummary summarizeByTime() {
        return super.summarizeByTime();
    }

    @Override
    @GET
    @Path(value="/period/{period}")
    @Produces(value="application/json")
    public TimeSummary summarizeByTime(@PathParam(value="period") TimePeriod period) {
        return super.summarizeByTime(period);
    }
}
