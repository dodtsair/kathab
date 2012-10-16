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

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * A simple implementation using direct JDBC calls.
 *
 * @author mpower
 */
@Data
@Slf4j
public class JdbcLogSummarizer implements LogSummarizer {

    private DataSource source;
    private static final String summarizePackageQuery =
            "select logger_name as packageName, count(timestmp) as eventCount " +
            "from TABLE(prefix VARCHAR(256)=?) as prefix " +
            "left join (select logger_name, timestmp from logging_event join TABLE(level VARCHAR(256)=?) as level where logging_event.level_string = level.level) " +
            "on locate(prefix.prefix, logger_name) != 0 " +
            "GROUP BY logger_name order by eventCount";
    private static final String summarizeLevelQuery =
            "select level.level_string as logLevel, count(timestmp) as eventCount " +
            "from TABLE(level_string VARCHAR(256)=?) as level " +
            "left join (select level_string, timestmp from logging_event " +
            "join TABLE(prefix VARCHAR(256)=?) as prefix where locate(prefix.prefix, logging_event.logger_name) != 0) as logging_event " +
            "on level.level_string = logging_event.level_string GROUP BY level.level_string order by eventCount";
    private static final String summarizeTimeQuery =
            "SELECT range.slice as slice, count(TIMESTMP) as eventCount " +
            "from (select CONVERT(?, BIGINT) / ? * ? - X * ? as slice FROM SYSTEM_RANGE(0,99)) as range " +
            "left join (select TIMESTMP / ? * ? as slice, TIMESTMP from logging_event) as data " +
            "on data.slice = range.slice group by range.slice order by range.slice";

    @Override
    public LevelSummary summarizeLevel() {
        return summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[]{""}, LogLevel.values());
    }

    @Override
    public LevelSummary summarizeLevel(TimePeriod period) {
        return summarizeLevel(System.currentTimeMillis(), period, new String[] {""}, LogLevel.values());
    }

    @Override
    public LevelSummary summarizeLevel(LogLevel... levels) {
        return summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {""}, levels);
    }

    @Override
    public PrefixSummary summarizePrefix() {
        return summarizePrefix(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[]{""}, LogLevel.values());
    }

    @Override
    public PrefixSummary summarizePrefix(TimePeriod period) {
        return summarizePrefix(System.currentTimeMillis(), period, new String[] {""}, LogLevel.values());
    }

    @Override
    public PrefixSummary summarizePrefix(String... includePrefixes) {
        return summarizePrefix(System.currentTimeMillis(), TimePeriod.DEFAULT, includePrefixes, LogLevel.values());
    }

    @Override
    public PrefixSummary summarizePrefix(long endMillis, TimePeriod period, String[] includePrefixes, LogLevel... levels) {
        PrefixSummary summary = null;
        try {
            @Cleanup
            Connection connection = source.getConnection();
            @Cleanup
            PreparedStatement query = connection.prepareStatement(summarizePackageQuery);
            query.setObject(1, includePrefixes);
            query.setObject(2, EnumMixin.names(levels));
            ResultSet request = query.executeQuery();
            LinkedHashMap<String, Integer> values = new LinkedHashMap<String, Integer>();
            Collection<AbstractMap.SimpleImmutableEntry<String, Integer>> set = new HashSet<AbstractMap.SimpleImmutableEntry<String, Integer>>();
            while (request.next()) {
                String packageName = request.getString("PackageName");
                int eventCount = request.getInt("eventCount");
                values.put(packageName, eventCount);
                set.add(new AbstractMap.SimpleImmutableEntry<String, Integer>(packageName, eventCount));
            }
            summary = new PrefixSummary(values);
        } catch (SQLException ex) {
            String error = "Failed to read package summary";
            log.debug(error, ex);
            if (log.isInfoEnabled()) {
                log.info("{}, prefixes: {}", error, Arrays.deepToString(includePrefixes));
            }
            log.error(error);
        }
        return summary;
    }

    @Override
    public PrefixSummary summarizePrefix(LogLevel... levels) {
        return summarizePrefix(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {""}, levels);
    }

    @Override
    public LevelSummary summarizeLevel(long endMillis, TimePeriod period, String[] includePrefix, LogLevel... levels) {
        LevelSummary summary = null;
        try {
            @Cleanup
            Connection connection = source.getConnection();
            @Cleanup
            PreparedStatement query = connection.prepareStatement(summarizeLevelQuery);
            query.setObject(1, EnumMixin.names(levels));
            query.setObject(2, includePrefix);
            @Cleanup
            ResultSet request = query.executeQuery();
            LogLevel level = null;
            summary = new LevelSummary();
            while (request.next()) {
                try {
                    level = LogLevel.valueOf(request.getString("logLevel"));
                    int eventCount = request.getInt("eventCount");
                    summary.setCount(level, eventCount);
                } catch (IllegalArgumentException ex) {
                    String error = "Failed to parse logLevel";
                    log.debug(error, ex);
                    if (log.isInfoEnabled()) {
                        log.info("{}, level: {}", error, level);
                    }
                    log.error(error);
                }
            }
        } catch (SQLException ex) {
            String error = "Failed to read log level summary";
            log.debug(error, ex);
            if (log.isInfoEnabled()) {
                log.info("{}, levels: {}", error, Arrays.deepToString(levels));
            }
            log.error(error);
        }
        return summary;
    }

    @Override
    public LevelSummary summarizeLevel(String... includePrefix) {
        return summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, includePrefix, LogLevel.values());
    }

    @Override
    public TimeSummary summarizeTime() {
        return this.summarizeTime(TimePeriod.DEFAULT);
    }

    @Override
    public TimeSummary summarizeTime(TimePeriod period) {
        return summarizeTime(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {""}, LogLevel.values());
    }

    @Override
    public TimeSummary summarizeTime(LogLevel... levels) {
        return summarizeTime(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {""}, levels);
    }

    @Override
    public TimeSummary summarizeTime(long endMillis, TimePeriod period, String[] includePrefix, LogLevel... levels) {
        TimeSummary summary = null;
        try {
            @Cleanup
            Connection connection = source.getConnection();
            @Cleanup
            PreparedStatement query = connection.prepareStatement(summarizeTimeQuery);
            query.setLong(1, endMillis);
            query.setLong(2, period.getMillis());
            query.setLong(3, period.getMillis());
            query.setLong(4, period.getMillis());
            query.setLong(5, period.getMillis());
            query.setLong(6, period.getMillis());
            ResultSet request = query.executeQuery();
            summary = new TimeSummary();
            while (request.next()) {
                int eventCount = request.getInt("eventCount");
                long slice = request.getLong("slice");
                summary.setCount(slice, eventCount);
            }
        } catch (SQLException ex) {
            String error = "Failed to read time summary";
            log.debug(error, ex);
            if (log.isInfoEnabled()) {
                log.info("{}, period: {}", error, period);
            }
            log.error(error);
        }
        return summary;
    }

    @Override
    public TimeSummary summarizeTime(String... includePrefix) {
        return summarizeTime(System.currentTimeMillis(), TimePeriod.DEFAULT, includePrefix, LogLevel.values());
    }
}