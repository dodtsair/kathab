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
import javax.ws.rs.DefaultValue;
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
        jdbcSource.setUser("apiwebapp");
        jdbcSource.setPassword("password");
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
    @Path(value="/level/all")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel() {
        log.info("Call to summary");
        return super.summarizeLevel();
    }

    @Override
    @GET
    @Path(value="/level/level={level}")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel(@PathParam(value="level") LogLevel... levels) {
        log.info("Call to summary");
        log.debug("Call to summary, levels: {}", levels);
        return super.summarizeLevel(levels);
    }

    @GET
    @Path(value="/prefix/level={level}&prefix={prefix}")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel(@PathParam(value="prefix") String includePrefix, @PathParam(value="level") LogLevel... levels) {
        log.info("Call to summary");
        log.debug("Call to summary, prefix: {}, levels: {}", includePrefix, levels);
        return super.summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {includePrefix}, levels);
    }

    @Override
    @GET
    @Path(value="/level/level={level}&period={period}&prefix={prefix}")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel(
            @DefaultValue("0")long endMillis, /* not used */
            @PathParam(value="period") TimePeriod period,
            @PathParam(value="prefix") String[] includePrefix,
            @PathParam(value="level") LogLevel... levels) {
        return super.summarizeLevel(System.currentTimeMillis(), period, includePrefix, levels);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/level/prefix={prefix}")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel(@PathParam(value="prefix") String... includePrefix) {
        return super.summarizeLevel(includePrefix);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/level/period={period}")
    @Produces(value="application/json")
    public LevelSummary summarizeLevel(@PathParam(value="period") TimePeriod period) {
        return super.summarizeLevel(period);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/prefix/all")
    @Produces(value="application/json")
    public PrefixSummary summarizePrefix() {
        log.info("Call to summary");
        return super.summarizePrefix();
    }

    @Override
    @GET
    @Path(value="/prefix/prefix={prefix}")
    @Produces(value="application/json")
    public PrefixSummary summarizePrefix(@PathParam(value="prefix") String... includePrefixes) {
        log.info("Call to summary");
        log.debug("Call to summary, prefixes: {}", includePrefixes);
        return super.summarizePrefix(includePrefixes);
    }

    @Override
    @GET
    @Path(value="/prefix/period={period}")
    @Produces(value="application/json")
    public PrefixSummary summarizePrefix(@PathParam(value="period") TimePeriod period) {
        return super.summarizePrefix(period);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/prefix/level={level}&period={period}&prefix={prefix}")
    @Produces(value="application/json")
    public PrefixSummary summarizePrefix(
            @DefaultValue("0")long endMillis, /* not used */
            @PathParam(value="period") TimePeriod period,
            @PathParam(value="prefix") String[] includePrefixes,
            @PathParam(value="level") LogLevel... levels) {
        return super.summarizePrefix(System.currentTimeMillis(), period, includePrefixes, levels);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/prefix/level={level}")
    @Produces(value="application/json")
    public PrefixSummary summarizePrefix(@PathParam(value="level") LogLevel... levels) {
        return super.summarizePrefix(levels);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/period/level={level}")
    @Produces(value="application/json")
    public TimeSummary summarizeTime(@PathParam(value="level") LogLevel... levels) {
        return super.summarizeTime(levels);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/period/level={level}&period={period}&prefix={prefix}")
    @Produces(value="application/json")
    public TimeSummary summarizeTime(
            @DefaultValue("0")long endMillis, /* not used */
            @PathParam(value="period") TimePeriod period,
            @PathParam(value="prefix") String[] includePrefix,
            @PathParam(value="level") LogLevel... levels) {
        return super.summarizeTime(System.currentTimeMillis(), period, includePrefix, levels);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/period/prefix={prefix}")
    @Produces(value="application/json")
    public TimeSummary summarizeTime(String... includePrefix) {
        return super.summarizeTime(includePrefix);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    @GET
    @Path(value="/period/all")
    @Produces(value="application/json")
    public TimeSummary summarizeTime() {
        return super.summarizeTime();
    }

    @Override
    @GET
    @Path(value="/period/period={period}")
    @Produces(value="application/json")
    public TimeSummary summarizeTime(@PathParam(value="period") TimePeriod period) {
        return super.summarizeTime(period);
    }
}
