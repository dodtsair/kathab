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
import java.util.Arrays;
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
    private String summarizeByLevelQuery = "select level_string as logLevel, count(*) as eventCount from logging_event where level_string in (%s) GROUP BY level_string";
    private String summarizeByPackageQuery = "select logger_name as packageName, count(*) as eventCount from logging_event where %s GROUP BY logger_name";
    private String summarizeByPackageWhereFragment = "locate(?, logger_name) != 0 or ";
    private String summarizeByPackageAndLevelQuery;

    @Override
    public LevelSummary summarizeByLevel() {
        return summarizeByLevel(LogLevel.values());
    }

    @Override
    public LevelSummary summarizeByLevel(LogLevel... levels) {
        LevelSummary summary = null;
        try {
            @Cleanup
            Connection connection = source.getConnection();
            StringBuilder levelSet = new StringBuilder();
            for (LogLevel level : levels) {
                levelSet.append("?,");
            }
            if (levels.length != 0) {
                levelSet.deleteCharAt(levelSet.length() - 1);
            }
            @Cleanup
            PreparedStatement query = connection.prepareStatement(String.format(summarizeByLevelQuery, levelSet));
            for (int index = 1; index <= levels.length; ++index) {
                query.setString(index, levels[index - 1].name());
            }
            ResultSet request = query.executeQuery();
            LogLevel level = null;
            summary = new LevelSummary();
            while (request.next()) {
                try {
                    level = LogLevel.valueOf(request.getString("logLevel"));
                    int eventCount = request.getInt("eventCount");
                    summary.setEventCount(level, eventCount);
                } catch (IllegalArgumentException ex) {
                    String error = "Failed to parse logLevel";
                    log.error(error);
                    if (log.isInfoEnabled()) {
                        log.info("{}, level: {}", error, level);
                        log.info(error, ex);
                    }
                }
            }
        } catch (SQLException ex) {
            String error = "Failed to read log level summary";
            log.error(error);
            if (log.isInfoEnabled()) {
                log.info("{}, levels: {}", error, Arrays.deepToString(levels));
                log.info(error, ex);
            }
        }
        return summary;
    }

    @Override
    public PackageSummary summarizeByPackage() {
        return summarizeByPackage("");
    }

    @Override
    public PackageSummary summarizeByPackage(String... includePrefixes) {
        PackageSummary summary = null;
        try {
            @Cleanup
            Connection connection = source.getConnection();
            StringBuilder prefixMatch = new StringBuilder();
            for (String prefix: includePrefixes) {
                prefixMatch.append(summarizeByPackageWhereFragment);
            }
            if (includePrefixes.length != 0) {
                prefixMatch.delete(prefixMatch.length() - "or ".length(), prefixMatch.length());
            }
            @Cleanup
            PreparedStatement query = connection.prepareStatement(String.format(summarizeByPackageQuery, prefixMatch.toString()));
            for (int index = 1; index <= includePrefixes.length; ++index) {
                query.setString(index, includePrefixes[index - 1]);
            }
            ResultSet request = query.executeQuery();
            summary = new PackageSummary();
            while (request.next()) {
                String packageName = request.getString("PackageName");
                int eventCount = request.getInt("eventCount");
                summary.setEventCount(packageName, eventCount);
            }
        } catch (SQLException ex) {
            String error = "Failed to read package summary";
            log.error(error);
            if (log.isInfoEnabled()) {
                log.info("{}, levels: {}", error, Arrays.deepToString(includePrefixes));
                log.info(error, ex);
            }
        }
        return summary;
    }

    @Override
    public Object summarizeByPackageAndLevel(String includePrefix, LogLevel... levels) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
