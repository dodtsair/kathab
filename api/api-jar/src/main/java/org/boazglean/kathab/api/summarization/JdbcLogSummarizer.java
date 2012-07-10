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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private String summarizeByLevelQuery = "select level_string as LogLevel, count(*) as eventCount from logging_event GROUP BY level_string;";
    private String summarizeByPackageQuery;
    private String summarizeByPackageAndLevelQuery;

    @Override
    public LevelSummary summarizeByLevel() {
        return summarizeByLevel(LogLevel.values());
    }

    @Override
    public LevelSummary summarizeByLevel(LogLevel... levels) {
        LevelSummary summary = null;
        try {
            @Cleanup Connection connection = source.getConnection();
            @Cleanup Statement query = connection.createStatement();
            ResultSet request = query.executeQuery(summarizeByLevelQuery);
            LogLevel level = null;
            summary = new LevelSummary();
            while(request.next()) {
                try {
                    level = LogLevel.valueOf(request.getString("LogLevel"));
                    int eventCount = request.getInt("eventCount");
                    summary.setEventCount(level, eventCount);
                }
                catch(IllegalArgumentException ex) {
                    String error = "Failed to parse LogLevel";
                    log.error(error);
                    if(log.isInfoEnabled()) {
                        log.info("{}, level: {}", error, level);
                        log.info(error, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            String error = "Failed to read log summary";
            log.error(error);
            if(log.isInfoEnabled()) {
                log.info("{}, levels: {}", error, Arrays.deepToString(levels));
                log.info(error, ex);
            }
        }
        return summary;
    }

    @Override
    public Object summarizeByPackage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object summarizeByPackage(String... includePrefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object summarizeByPackageAndLevel(String includePrefix, LogLevel... levels) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
