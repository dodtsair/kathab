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

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.boazglean.kathab.api.summarization.Schema;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author mpower
 */
@Slf4j
public class SchemaInit implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JdbcDataSource jdbcSource = new JdbcDataSource();
        jdbcSource.setURL("jdbc:h2:mem:api-webapp;DB_CLOSE_DELAY=-1");
        try {
            @Cleanup
            Connection connection = jdbcSource.getConnection();
            Schema schema = new Schema();
            schema.initSchema(connection);
        } catch (SQLException e) {
            log.info("Failed to initialize schema", e);
            log.error("Failed to initialize schema");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
