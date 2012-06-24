/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.boazglean.kathab.dhtml;

import org.springframework.js.resource.ResourceServlet;

/**
 *
 * @author mpower
 */
public class StaticContentServlet extends ResourceServlet{
    
    public StaticContentServlet() {
        this.setAllowedResourcePaths(
                "/**/*.gif," +
                "/**/*.ico," +
                "/**/*.jpeg," +
                "/**/*.jpg," +
                "/**/*.js," +
                "/**/*.css," +
                "/**/*.jss," +
                "/**/*.png," +
                "/**/*.html," +
                "/**/*.xhtml," +
                "/**/*.htm," +
                "META-INF/**/*.gif," +
                "META-INF/**/*.ico," +
                "META-INF/**/*.jpeg," +
                "META-INF/**/*.jpg," +
                "META-INF/**/*.js," +
                "META-INF/**/*.css," +
                "META-INF/**/*.jss," +
                "META-INF/**/*.png," +
                "META-INF/**/*.html," +
                "META-INF/**/*.xhtml," +
                "META-INF/**/*.htm," +
                ""
                );
    }
    
}