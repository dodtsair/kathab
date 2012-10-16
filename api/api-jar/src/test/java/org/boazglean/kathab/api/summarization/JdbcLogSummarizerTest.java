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

import org.testng.annotations.*;
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

    @BeforeClass
    public void setupDebugServer() throws Exception {
        server = Server.createTcpServer("-tcpDaemon", "-tcpAllowOthers");
        server.start();
    }

    @AfterClass
    public void shutdownDebugServer() throws Exception {
        server.stop();
    }

    @BeforeMethod
    public void setup(Method method) throws Exception {
        String dbName = method.getDeclaringClass().getSimpleName();

        jdbcSource = new JdbcDataSource();
        // DB_CLOSE_DELAY during the test we are not going to always have a connection open, so make sure the db stays
        // even when there is no connection
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
        log.debug("Setup for method complete for {}", dbName);
    }

    @AfterMethod
    public void teardown() throws Exception {
        testLogger.detachAppender(dbAppender);
        dbAppender.stop();
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement statement = connection.createStatement();
        //Now actually shutdown the database, as it will not shutdown by itself.
        statement.execute("SHUTDOWN");
    }
    
    @Test
    public void summarizeByLevelEmpty() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            assertEquals(summary.getCount(level), 0);
        }
    }

    @Test
    public void summarizeByLevelDefaults() throws Exception {
        //Just make sure it doesn't blow up.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        summy.summarizeLevel();
        summy.summarizeLevel(LogLevel.values());
        summy.summarizeLevel(TimePeriod.DEFAULT);
        summy.summarizeLevel("");

    }


    @Test
    public void summarizeByLevel() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        testLogger.debug("debug message String");
        testLogger.warn("warn message String");
        testLogger.error("error message String");
        testLogger.info("info message String");
        testLogger.trace("trace message String");
        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            assertEquals(summary.getCount(level), 1);
        }
    }

    @Test
    public void summarizeByLevelOmitTrace() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        testLogger.debug("debug message String");
        testLogger.warn("warn message String");
        testLogger.error("error message String");
        testLogger.info("info message String");
        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);
        for (LogLevel level : LogLevel.values()) {
            if(level.equals(LogLevel.TRACE)) {
                assertTrue(summary.containsKey(LogLevel.TRACE));
            }
            else {
                assertEquals(summary.getCount(level), 1);
            }
        }
    }

    @Test
    public void summarizeByLevelSubset() {
        int subsetSize = 2;
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        testLogger.debug("debug message String");
        testLogger.warn("warn message String");
        testLogger.error("error message String");
        testLogger.info("info message String");
        testLogger.trace("trace message String");
        LogLevel[] levels = Arrays.copyOf(LogLevel.values(), subsetSize);
        LevelSummary summary = summy.summarizeLevel(levels);
        assertNotNull(summary);
        int index;
        for (index = 0; index < subsetSize; ++index) {
            assertEquals(summary.getCount(LogLevel.values()[index]), 1);
        }
        for (; index < LogLevel.values().length; ++index) {
            assertEquals(summary.getCount(LogLevel.values()[index]), 0);
        }
    }

    @Test
    public void summarizeByPrefixDefaults() throws Exception {
        //Just make sure it doesn't blow up.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        summy.summarizePrefix();
        summy.summarizePrefix(LogLevel.values());
        summy.summarizePrefix(TimePeriod.DEFAULT);
        summy.summarizePrefix("");

    }

    @Test
    public void summarizeByPrefix() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 1;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            for (int batch = 0; batch < batchSize * count; ++batch) {
                logger.info("info message String");
            }
            ++count;
        }
        PrefixSummary summary = summy.summarizePrefix();
        assertNotNull(summary);
        assertEquals(summary.getCount(logOne.getName()), 1);
        assertEquals(summary.getCount(logTwo.getName()), 2);
        assertEquals(summary.getCount(logEleven.getName()), 3);
        assertEquals(summary.getCount(testLogger.getName()), 4);
    }

    @Test
    public void summarizeByPrefixFilterLevel() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 1;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");

        //Log once to make sure we do not get any in the result
        logOne.trace("logging for tests");
        logOne.info("logging for tests");
        logOne.debug("logging for tests");

        //We should get these in the result
        logOne.error("logging for tests");
        logOne.warn("logging for tests");

        //Just log to error as the entire logger should not even be present
        logTwo.error("logging for tests");

        //Logging to eleven does not affect one's totals
        //Log once to make sure we do not get any in the result
        logEleven.trace("logging for tests");
        logEleven.info("logging for tests");
        logEleven.debug("logging for tests");

        //We should get these in the result
        logEleven.error("logging for tests");
        logEleven.warn("logging for tests");

        PrefixSummary summary = summy.summarizePrefix(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {logOne.getName()}, LogLevel.ERROR, LogLevel.WARN);
        assertNotNull(summary);
        assertEquals(summary.getCount(logOne.getName()), 2);
        assertFalse(summary.getPoints().containsKey(logTwo.getName()));
        assertEquals(summary.getCount(logEleven.getName()), 2);
    }

    @Test
    public void summarizeByPrefixSubset() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        String baseLoggerName = testLogger.getName();
        int count = 1;
        int batchSize = 1;
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");
        for (Logger logger : new Logger[]{logOne, logTwo, logEleven, testLogger}) {
            //use count to log a different number of messages per logger
            for (int batch = 0; batch < batchSize * count; ++batch) {
                logger.info("info message String");
            }
            ++count;
        }
        PrefixSummary summary = summy.summarizePrefix(logTwo.getName(), logEleven.getName());
        assertNotNull(summary);
        assertEquals(summary.getCount(logOne.getName()), 0);
        assertEquals(summary.getCount(logTwo.getName()), 2);
        assertEquals(summary.getCount(logEleven.getName()), 3);
        assertEquals(summary.getCount(testLogger.getName()), 0);
    }
    
    @Test
    public void summarizeByLevelFilterPackage() {
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
        LevelSummary summary = summy.summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {logTwo.getName()}, LogLevel.values());
        assertNotNull(summary);
        assertEquals(summary.getCount(LogLevel.ERROR), 4);
        assertEquals(summary.getCount(LogLevel.WARN), 5);
        assertEquals(summary.getCount(LogLevel.INFO), 7);
        assertEquals(summary.getCount(LogLevel.DEBUG), 10);
        assertEquals(summary.getCount(LogLevel.TRACE), 20);
    }

    @Test
    public void summarizeByLevelAndLevelSubset() {
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
        LevelSummary summary = summy.summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[] {logTwo.getName()}, LogLevel.ERROR, LogLevel.WARN);
        assertNotNull(summary);
        assertEquals(summary.getCount(LogLevel.ERROR), 4);
        assertEquals(summary.getCount(LogLevel.WARN), 5);

        //We'll only return the counts for the level asked for.  Anything else is omitted
        assertFalse(summary.containsKey(LogLevel.INFO));
        assertFalse(summary.containsKey(LogLevel.DEBUG));
        assertFalse(summary.containsKey(LogLevel.TRACE));
    }

    @Test
    public void summarizeByTimePeriod() throws Exception {
        //Just make sure it doesn't blow up.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        summy.summarizeTime();
        summy.summarizeTime(LogLevel.values());
        summy.summarizeTime(TimePeriod.DEFAULT);
        summy.summarizeTime("");

    }

    @Test(dataProvider="timePeriods")
    public void summarizeBySpecificTimePeriods(TimePeriod period) throws Exception {
        long slice = period.getMillis();
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        long beginning = System.currentTimeMillis() / slice * slice;
        for (int count = 0; count < 10; ++count) {
            long sliceStart = (beginning - slice * count);
            for(int repeat = 0; repeat <= count; ++repeat) {
                testLogger.info("info message String with data, count: {}, duration: {}, beginning: {}", new Object[] {count, slice, sliceStart});
            }
        }
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement query = connection.createStatement();
        //The appender sets the timestamp to when the entry was logged.  Difficult to test against.  Instead change
        //all the timestamps to a predefined number so that we can test against it.
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG2, BIGINT);");
        TimeSummary summary = summy.summarizeTime(beginning, period, new String[] {""}, LogLevel.values());

        assertNotNull(summary);
        assertEquals(summary.size(), 100);
        long total = 0;
        for (int count = 0; count < 99; ++count) {
            assertTrue(summary.containsKey(beginning - slice * count), "Contains key: beginning: " + beginning + ", slice: " + slice + ", count: " + count);
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
                //Check to make sure there are not gaps
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
        //Thus we'll be able to verify the response we get back does not have a gap but several entries with 0 in them.
        for (int count : slices) {
            //only select those which when divided by four are even, 0, 1, 2, 3, 8, 9,
            if(count / 4 % 2 == 0) {
                long sliceStart = (beginning - slice * count);
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
        //The appender sets the timestamp to when the entry was logged.  Difficult to test against.  Instead change
        //all the timestamps to a predefined number so that we can test against it.
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG2, BIGINT);");
        TimeSummary summary = summy.summarizeTime(beginning, period, new String[] {""}, LogLevel.values());

        assertNotNull(summary);
        assertEquals(summary.size(), 100, "Seems we did not get the right number of slices: " + summary.toString());
        long total = 0;
        for (int count : slices) {
            assertTrue(summary.containsKey(beginning - slice * count), "Result does not contain key for count:" + count);
        }
        for (int count : slices) {
            long sliceTimestamp = beginning - slice * count;
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
