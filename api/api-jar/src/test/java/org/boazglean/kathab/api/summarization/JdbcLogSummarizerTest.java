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
import java.sql.*;
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
import static org.mockito.Mockito.*;

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
    public void summarizePrefixCoverSqlException() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenThrow(new SQLException("throw for tests"));
        summy.setSource(jdbcSource);

        PrefixSummary summary = summy.summarizePrefix();
        assertNull(summary);
    }

    @Test
    public void summarizePrefixCoverEmptyResult() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenReturn(false);
        summy.setSource(jdbcSource);

        PrefixSummary summary = summy.summarizePrefix();
        assertNotNull(summary);
        assertEquals(summary.getPoints().size(), 0);
    }

    @Test
    public void summarizePrefixCoverCloseConnection() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizePrefix();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);

    }

    @Test
    public void summarizePrefixCoverCloseStatement() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizePrefix();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(connection).close();
    }

    @Test
    public void summarizePrefixCoverCloseResult() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizePrefix();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(query).close();
    }

    @Test
    public void summarizePrefixCoverExceptionalCloseResult() throws Exception {
        //The cleanup annotation creates a finally block, in java there
        //are two finally blocks one is executed in the normal case
        //the other is executed during an exception
        //Execute the finally exception block with a non null result
        //so that the code calls close on it.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenThrow(new SQLException("Generated for tests"));
        summy.setSource(jdbcSource);

        PrefixSummary summary = summy.summarizePrefix();
        assertNotNull(summary);

        verify(request).close();
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
        LevelSummary summary = summy.summarizeLevel(System.currentTimeMillis(), TimePeriod.DEFAULT, new String[]{logTwo.getName()}, LogLevel.values());
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
    public void summarizeByTimePeriodDefaults() throws Exception {
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

    @Test
    public void summarizeByPeriodFilterLevel() throws Exception {
        TimePeriod period = TimePeriod.DAY;
        long slice = period.getMillis();
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        long beginning = System.currentTimeMillis() / slice * slice;
        testLogger.trace("Test log, at timestamp: {}", new Object[] {beginning});
        testLogger.info("Test log, at timestamp: {}", new Object[] {beginning});
        testLogger.debug("Test log, at timestamp: {}", new Object[] {beginning});
        testLogger.warn("Test log, at timestamp: {}", new Object[] {beginning});
        testLogger.error("Test log, at timestamp: {}", new Object[] {beginning});
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement query = connection.createStatement();
        //The appender sets the timestamp to when the entry was logged.  Difficult to test against.  Instead change
        //all the timestamps to a predefined number so that we can test against it.
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG0, BIGINT);");
        TimeSummary summary = summy.summarizeTime(beginning, period, new String[] {""}, LogLevel.WARN, LogLevel.ERROR);

        assertNotNull(summary);
        assertEquals(summary.size(), 100);
        assertEquals(summary.getCount(beginning), 2);
    }

    @Test
    public void summarizePeriodFilterPrefix() throws Exception {
        TimePeriod period = TimePeriod.DAY;
        long slice = period.getMillis();
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        long beginning = System.currentTimeMillis() / slice * slice;

        String baseLoggerName = testLogger.getName();
        Logger logOne = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1");
        Logger logTwo = (Logger) LoggerFactory.getLogger(baseLoggerName + ".2");
        Logger logEleven = (Logger) LoggerFactory.getLogger(baseLoggerName + ".1.1");

        logOne.info("Test log, at timestamp: {}", new Object[]{beginning});
        logTwo.info("Test log, at timestamp: {}", new Object[] {beginning});
        logEleven.info("Test log, at timestamp: {}", new Object[] {beginning});
        testLogger.info("Test log, at timestamp: {}", new Object[] {beginning});
        @Cleanup
        Connection connection = jdbcSource.getConnection();
        @Cleanup
        Statement query = connection.createStatement();
        //The appender sets the timestamp to when the entry was logged.  Difficult to test against.  Instead change
        //all the timestamps to a predefined number so that we can test against it.
        query.execute("update logging_event set TIMESTMP = CONVERT(ARG0, BIGINT);");

        TimeSummary summary = summy.summarizeTime(beginning, period, new String[] {logEleven.getName(), logTwo.getName()}, LogLevel.values());
        assertNotNull(summary);
        assertEquals(summary.size(), 100);
        assertEquals(summary.getCount(beginning), 2);
    }

    @Test
    public void summarizeLevelCoverEmptyResult() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenReturn(false);
        summy.setSource(jdbcSource);

        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);
        assertEquals(summary.size(), 0);
    }

    @Test
    public void summarizeLevelCoverSevereLevel() throws Exception {
        //Return some number for the eventCount, we'll pick 3.
        final int someNumber = 3;
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        //Precede a legit LogLevel with an illegitimate LogLevel
        //the code should swallow SEVERE and return ERROR
        when(request.getString(anyString())).thenReturn("SEVERE").thenReturn("ERROR");
        when(request.getInt(anyString())).thenReturn(someNumber).thenReturn(someNumber);
        summy.setSource(jdbcSource);

        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);
        assertEquals(summary.size(), 1);
        assertEquals(summary.getCount(LogLevel.ERROR), someNumber);
    }

    @Test
    public void summarizeLevelCoverSqlException() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenThrow(new SQLException("throw for tests"));
        summy.setSource(jdbcSource);

        LevelSummary summary = summy.summarizeLevel();
        assertNull(summary);
    }

    @Test
    public void summarizeLevelCoverCloseConnection() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeLevel();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);

    }

    @Test
    public void summarizeLevelCoverCloseStatement() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeLevel();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(connection).close();
    }

    @Test
    public void summarizeLevelCoverCloseResult() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeLevel();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(query).close();
    }

    @Test
    public void summarizeLevelCoverExceptionalCloseResult() throws Exception {
        //The cleanup annotation creates a finally block, in java there
        //are two finally blocks one is executed in the normal case
        //the other is executed during an exception
        //Execute the finally exception block with a non null result
        //so that the code calls close on it.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenThrow(new SQLException("Generated for tests"));
        summy.setSource(jdbcSource);

        LevelSummary summary = summy.summarizeLevel();
        assertNotNull(summary);

        verify(request).close();
    }

    @Test
    public void summarizePeriodCoverSqlException() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenThrow(new SQLException("throw for tests"));
        summy.setSource(jdbcSource);

        TimeSummary summary = summy.summarizeTime();
        assertNull(summary);
    }

    @Test
    public void summarizePeriodCoverEmptyResult() throws Exception {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenReturn(false);
        summy.setSource(jdbcSource);

        TimeSummary summary = summy.summarizeTime();
        assertNotNull(summary);
        assertEquals(summary.size(), 0);
    }

    @Test
    public void summarizePeriodCoverCloseConnection() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeTime();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);

    }

    @Test
    public void summarizePeriodCoverCloseStatement() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeTime();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(connection).close();
    }

    @Test
    public void summarizePeriodCoverCloseResult() throws Exception {
        //The cleanup annotation will check to see if the object being
        //cleaned up is not null.  We need to make the object null
        //to exercise that branch.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(null);
        summy.setSource(jdbcSource);

        try {
            summy.summarizeTime();
        }
        catch (NullPointerException e) {
            exception = true;
            //Expected exception
        }
        assertTrue(exception);
        verify(query).close();
    }

    @Test
    public void summarizePeriodCoverExceptionalCloseResult() throws Exception {
        //The cleanup annotation creates a finally block, in java there
        //are two finally blocks one is executed in the normal case
        //the other is executed during an exception
        //Execute the finally exception block with a non null result
        //so that the code calls close on it.
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        Connection connection = mock(Connection.class);
        PreparedStatement query = mock(PreparedStatement.class);
        ResultSet request = mock(ResultSet.class);
        boolean exception = false;

        when(jdbcSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(query);
        when(query.executeQuery()).thenReturn(request);
        when(request.next()).thenThrow(new SQLException("Generated for tests"));
        summy.setSource(jdbcSource);

        TimeSummary summary = summy.summarizeTime();
        assertNotNull(summary);

        verify(request).close();
    }

    @Test
    public void canEqual() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();

        assertTrue(summy.canEqual(summy));
        assertFalse(summy.canEqual(new Object()));

    }

    @Test
    public void testEquals() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcLogSummarizer other = new JdbcLogSummarizer();
        JdbcLogSummarizer mocked = mock(JdbcLogSummarizer.class);
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);
        JdbcDataSource otherSource = mock(JdbcDataSource.class);

        assertTrue(summy.equals(summy));
        assertTrue(summy.equals(other));
        other.setSource(jdbcSource);
        assertFalse(summy.equals(other));
        summy.setSource(otherSource);
        assertFalse(summy.equals(other));
        summy.setSource(jdbcSource);
        assertTrue(summy.equals(other));

        when(mocked.canEqual(summy)).thenReturn(false);

        assertFalse(summy.equals(mocked));
        assertFalse(summy.equals(new Object()));
    }

    @Test
    public void testToString() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);

        assertNotNull(summy.toString());
        summy.setSource(jdbcSource);
        assertNotNull(summy.toString());

    }

    @Test
    public void testHashcode() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);

        summy.hashCode();
        summy.setSource(jdbcSource);
        summy.hashCode();

    }

    @Test void testAccessors() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        JdbcDataSource jdbcSource = mock(JdbcDataSource.class);

        assertNull(summy.getSource());
        summy.setSource(jdbcSource);
        assertEquals(summy.getSource(), jdbcSource);
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
