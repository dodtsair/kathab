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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.Cleanup;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates the schema needed for the db logging stuff to write to.
 *
 * @author mpower
 */
@Data
@Slf4j
public class Schema {

    public static final String LOGBACK_DB_DDL = "ch/qos/logback/classic/db/script/h2.sql";
    
    private String ddl;

    public Schema() {
        this(LOGBACK_DB_DDL, null);
    }

    public Schema(String schemaPath) {
        this(schemaPath, null);
    }

    public Schema(String schemaPath, ClassLoader loader) {
        ClassLoader resourceloader = loader == null ? this.getClass().getClassLoader(): loader;
        ddl = "";
        URL ddlUrl = null;
        try {
            ddlUrl = resourceloader.getResource(schemaPath);
            if(ddlUrl != null) {
                URLConnection connectionUrl = ddlUrl.openConnection();
                int length = connectionUrl.getContentLength();
                byte[] buffer = new byte[length];
                InputStream ddlStream = connectionUrl.getInputStream();
                ddlStream.read(buffer);
                ddl = new String(buffer);
            }
        } catch (IOException ex) {
            String error = "Failed to load schema from classpath";
            log.debug(error, ex);
            log.info("{}, ddlPath: {}, ddlUrl: {}", new String[] {error, schemaPath, "" + ddlUrl});
            log.error(error);
        }
    }

    public void initSchema(Connection connection) {
        try {
            @Cleanup Statement ddlStatement = connection.createStatement();
            ddlStatement.execute(ddl);
        } catch (SQLException ex) {
            String error = "Failed to create schema on connection";
            log.debug(error, ex);
            log.info("{}, ddl: {}", error, ddl);
            log.error(error);
        }
        
    }
}
