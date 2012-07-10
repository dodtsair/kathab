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
import javax.sql.DataSource;
import lombok.Cleanup;
import org.h2.jdbcx.JdbcDataSource;
import static org.mockito.Mockito.*;
import org.slf4j.LoggerFactory;
import static org.testng.Assert.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 *
 * @author mpower
 */
public class JdbcLogSummarizerTest {
    
    private JdbcDataSource jdbcSource;
    private Logger testLogger = (Logger)LoggerFactory.getLogger(JdbcLogSummarizerTest.class.getName() + ".testlogs");
    private DBAppender dbAppender;
    
    
    @BeforeMethod
    public void setup() throws Exception {
        jdbcSource = new JdbcDataSource();
        jdbcSource.setURL("jdbc:h2:mem:"  + this.getClass().getSimpleName() + ";DB_CLOSE_DELAY=-1");
        @Cleanup Connection connection = jdbcSource.getConnection();
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
    }
    
    @AfterMethod
    public void teardown() throws Exception {
    }
    
    @Test
    public void summarizeByLevelEmpty() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        LevelSummary summary = summy.summarizeByLevel();
        assertNotNull(summary);
        for(LogLevel level: LogLevel.values()) {
            assertEquals(summary.getEventCount(level), 0);
        }
    }
    
    @Test
    public void summarizeByLevel() {
        JdbcLogSummarizer summy = new JdbcLogSummarizer();
        summy.setSource(jdbcSource);
        for(int count = 0; count < 10; ++count) {
            testLogger.debug("debug message String");
            testLogger.warn("warn message String");
            testLogger.error("error message String");
            testLogger.info("info message String");
            testLogger.trace("trace message String");
        }
        LevelSummary summary = summy.summarizeByLevel();
        assertNotNull(summary);
        for(LogLevel level: LogLevel.values()) {
            assertEquals(summary.getEventCount(level), 10);
        }
    }
}
