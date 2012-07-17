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
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import javax.sql.DataSource;
import lombok.Cleanup;
import org.h2.jdbcx.JdbcDataSource;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
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
    public void setup() throws Exception {
        server = Server.createTcpServer("-tcpDaemon", "-tcpAllowOthers");
        server.start();
        jdbcSource = new JdbcDataSource();
        jdbcSource.setURL("jdbc:h2:tcp://localhost/mem:" + this.getClass().getSimpleName() + ";DB_CLOSE_DELAY=-1");
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
            assertEquals(summary.getEventCount(level), 0);
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
            assertEquals(summary.getEventCount(level), 10);
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
            assertEquals(summary.getEventCount(LogLevel.values()[index]), 10);
        }
        for (; index < LogLevel.values().length; ++index) {
            assertEquals(summary.getEventCount(LogLevel.values()[index]), 0);
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
        PackageSummary summary = summy.summarizeByPackage();
        assertNotNull(summary);
        assertEquals(summary.getEventCount(logOne.getName()), 50);
        assertEquals(summary.getEventCount(logTwo.getName()), 100);
        assertEquals(summary.getEventCount(logEleven.getName()), 150);
        assertEquals(summary.getEventCount(testLogger.getName()), 200);
    }

    @Test
    public void summarizeByPackageSubset() {
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
        PackageSummary summary = summy.summarizeByPackage(logTwo.getName(), logEleven.getName());
        assertNotNull(summary);
        assertEquals(summary.getEventCount(logOne.getName()), 0);
        assertEquals(summary.getEventCount(logTwo.getName()), 100);
        assertEquals(summary.getEventCount(logEleven.getName()), 150);
        assertEquals(summary.getEventCount(testLogger.getName()), 0);
    }
}
