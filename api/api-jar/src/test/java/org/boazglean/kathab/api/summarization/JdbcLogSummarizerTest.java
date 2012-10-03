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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.core.db.DataSourceConnectionSource;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import lombok.Cleanup;
import org.h2.jdbcx.JdbcDataSource;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;

/**
 *
 * @author mpower
 */
@Slf4j
public class JdbcLogSummarizerTest {

    private JdbcDataSource jdbcSource;
    private Logger testLogger = (Logger) LoggerFactory.getLogger(JdbcLogSummarizerTest.class.getName() + ".testlogs");
    private DBAppender dbAppender;
    private Server server;

    @BeforeMethod
    public void setup(Method method) throws Exception {
        String dbName = method.getDeclaringClass().getSimpleName();

        server = Server.createTcpServer("-tcpDaemon", "-tcpAllowOthers");
        server.start();
        jdbcSource = new JdbcDataSource();
        jdbcSource.setURL("jdbc:h2:tcp://localhost/mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        jdbcSource.setUser(dbName);
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        Schema schema = new Schema();
        schema.initSchema(connection);


        dbAppender = new DBAppender();
        dbAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        DataSourceConnectionSource dbConnectionSource = new DataSourceConnectionSource();
        dbConnectionSource.setDataSource(jdbcSource);
        dbConnectionSource.discoverConnectionProperties();
        dbAppender.setConnectionSource(dbConnectionSource);

        testLogger.setLevel(Level.ALL);
        testLogger.addAppender(dbAppender);
        dbAppender.start();
        log.debug("" + connection.createStatement().executeQuery("select * from logging_event;"));
        log.debug("Setup for method complete for {}", dbName);
    }

    @AfterMethod
    public void teardown() throws Exception {
        log.debug("" + jdbcSource.getConnection().createStatement().executeQuery("select * from logging_event;"));
        testLogger.detachAppender(dbAppender);

        dbAppender.stop();
        server.stop();
    }
    
    @Test
    public void summarizeByLevelEmpty() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        LevelSummary summary = summy.summarizeByLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            assertEquals(summary.getCount(level), 0);
        }
    }

    @Test
    public void summarizeByLevel() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        for (int count = 0; count < 10; ++count) {
            testLogger.debug("debug message String");
            testLogger.warn("warn message String");
            testLogger.error("error message String");
            testLogger.info("info message String");
            testLogger.trace("trace message String");
        }
        LevelSummary summary = summy.summarizeByLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            assertEquals(summary.getCount(level), 10);
        }
    }

    @Test
    public void summarizeByLevelOmitTrace() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        for (int count = 0; count < 10; ++count) {
            testLogger.debug("debug message String");
            testLogger.warn("warn message String");
            testLogger.error("error message String");
            testLogger.info("info message String");
        }
        LevelSummary summary = summy.summarizeByLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            if(level.equals(LogLevel.TRACE)) {
                assertTrue(summary.containsKey(LogLevel.TRACE));
            }
            else {
                assertEquals(summary.getCount(level), 10);
            }
        }
    }

    @Test
    public void summarizeByLevelSubset() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        for (int count = 0; count < 10; ++count) {
            testLogger.debug("debug message String");
            testLogger.warn("warn message String");
            testLogger.error("error message String");
            testLogger.info("info message String");
            testLogger.trace("trace message String");
        }
        LogLevel[] levels = Arrays.copyOf(LogLevel.values(), 2);
        LevelSummary summary = summy.summarizeByLevel(levels);
        assertNotNull(summary);
        int index;
        for (index = 0; index < 2; ++index) {
            assertEquals(summary.getCount(LogLevel.values()[index]), 10);
        }
        for (; index < LogLevel.values().length; ++index) {
            assertEquals(summary.getCount(LogLevel.values()[index]), 0);
        }
    }

    @Test
    public void summarizeByPackage() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 10;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            for (int batch = 0; batch < batchSize * count; ++batch) {
                logger.error("error message String");
                logger.warn("warn message String");
                logger.info("info message String");
                logger.debug("debug message String");
                logger.trace("trace message String");
            }
            ++count;
        }
        PrefixSummary summary = summy.summarizeByPrefix();
        assertNotNull(summary);
        assertEquals(summary.getCount(logOne.getName()), 50);
        assertEquals(summary.getCount(logTwo.getName()), 100);
        assertEquals(summary.getCount(logEleven.getName()), 150);
        assertEquals(summary.getCount(testLogger.getName()), 200);
    }

    @Test
    public void summarizeByPrefixSubset() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 10;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            for (int batch = 0; batch < batchSize * count; ++batch) {
                logger.error("error message String");
                logger.warn("warn message String");
                logger.info("info message String");
                logger.debug("debug message String");
                logger.trace("trace message String");
            }
            ++count;
        }
        PrefixSummary summary = summy.summarizeByPrefix(logTwo.getName(), logEleven.getName());
        assertNotNull(summary);
        assertEquals(summary.getCount(logOne.getName()), 0);
        assertEquals(summary.getCount(logTwo.getName()), 100);
        assertEquals(summary.getCount(logEleven.getName()), 150);
        assertEquals(summary.getCount(testLogger.getName()), 0);
    }
    
    @Test
    public void summarizeByPackageAndLevel() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 10;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            for (int batch = 0; batch < batchSize * count; ++batch) {
                if(batch % 5 == 0) logger.error("error message String");
                if(batch % 4 == 0) logger.warn("warn message String");
                if(batch % 3 == 0) logger.info("info message String");
                if(batch % 2 == 0) logger.debug("debug message String");
                logger.trace("trace message String");
            }
            ++count;
        }
        LevelSummary summary = summy.summarizeByPrefixAndLevel(logTwo.getName(), LogLevel.values());
        assertNotNull(summary);
        assertEquals(summary.getCount(LogLevel.ERROR), 4);
        assertEquals(summary.getCount(LogLevel.WARN), 5);
        assertEquals(summary.getCount(LogLevel.INFO), 7);
        assertEquals(summary.getCount(LogLevel.DEBUG), 10);
        assertEquals(summary.getCount(LogLevel.TRACE), 20);
    }

    @Test
    public void summarizeByPackageAndLevelSubset() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 10;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            for (int batch = 0; batch < batchSize * count; ++batch) {
                if(batch % 5 == 0) logger.error("error message String");
                if(batch % 4 == 0) logger.warn("warn message String");
                if(batch % 3 == 0) logger.info("info message String");
                if(batch % 2 == 0) logger.debug("debug message String");
                logger.trace("trace message String");
            }
            ++count;
        }
        LevelSummary summary = summy.summarizeByPrefixAndLevel(logTwo.getName(), LogLevel.ERROR, LogLevel.WARN);
        assertNotNull(summary);
        assertEquals(summary.getCount(LogLevel.ERROR), 4);
        assertEquals(summary.getCount(LogLevel.WARN), 5);
        assertEquals(summary.getCount(LogLevel.INFO), 0);
        assertEquals(summary.getCount(LogLevel.DEBUG), 0);
        assertEquals(summary.getCount(LogLevel.TRACE), 0);
    }
    
    @Test(dataProvider="timePeriods")
    public void summarizeByTimePeriod(TimePeriod period) throws Exception {
        long slice = period.getMillis();
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        long beginning = System.currentTimeMillis() / slice * slice;
        for (int count = 0; count < 10; ++count) {
            long sliceStart = (beginning + slice * count);
            for(int repeat = 0; repeat <= count; ++repeat) {
                testLogger.info("info message String with data, count: {}, duration: {}, beginning: {}", new Object[] {count, slice, sliceStart});
            }
        }
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement query = connection.createStatement();
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG2, BIGINT);");
        TimeSummary summary = summy.summarizeByTime(period);

        assertNotNull(summary);
        assertEquals(summary.size(), 10, "Only split them up into 9 groups: " + summary.toString());
        long total = 0;
        for (int count = 0; count < 10; ++count) {
            assertTrue(summary.containsKey(beginning + slice * count));
        }
            for (Long key: summary.keySet()) {
            total += summary.get(key);
        }
        assertEquals(total, 55);
        SortedSet<Long> timeslices = new TreeSet<Long>();
        timeslices.addAll(summary.keySet());
        Integer prev = 0;
        for(Long tail: timeslices) {
            if(prev != 0) {
                assertEquals(tail - prev, slice);
            }
        }
    }

    @Test(dataProvider="timePeriods")
    public void summarizeByTimePeriodGap(TimePeriod period) throws Exception {
        long slice = period.getMillis();
        int[] slices = new int[] {0,1,2,3,4,5,6,7,8,9};
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        //Set up the data in the data base.
        long beginning = System.currentTimeMillis() / slice * slice;
        //Of a possible 10 slices we'll roll through the first four and the last three creating a gap in the middle.
        for (int count : slices) {
            //only select those which when divided by four are even, 0, 1, 2, 3, 8, 9,
            if(count / 4 % 2 == 0) {
                long sliceStart = (beginning + slice * count);
                int addALine = 0;
                for(int repeat = 0; repeat <= count; ++repeat) {
                    testLogger.info("info message String with data, count: {}, duration: {}, beginning: {}", new Object[] {count, slice, sliceStart});
                }
            }
        }
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement query = connection.createStatement();
        //THe appender sets the timestamp to when the entry was logged.  Difficult to test against.  Instead change
        //all the timestamps to a predefined number so that we can test against it.
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG2, BIGINT);");
        TimeSummary summary = summy.summarizeByTime(period);

        assertNotNull(summary);
        assertEquals(summary.size(), slices.length, "Seems we did not get the right number of slices: " + summary.toString());
        long total = 0;
        for (int count : slices) {
            assertTrue(summary.containsKey(beginning + slice * count), "Result does not contain key for count:" + count);
        }
        for (int count : slices) {
            long sliceTimestamp = beginning + slice * count;
            if(count /4 %2 == 0) {
                assertEquals(count + 1, summary.getCount(sliceTimestamp));
            }
            else {
                assertEquals(0, summary.getCount(sliceTimestamp));
            }
        }
    }

    @DataProvider(parallel = false, name = "timePeriods")
    public Object[][] createTimePeriods() {
        return new Object[][] {
                { TimePeriod.SECOND},
                { TimePeriod.MINUTE},
                { TimePeriod.QUARTER},
                { TimePeriod.HALF},
                { TimePeriod.HOUR},
                { TimePeriod.DAY},
        };
    }
}
