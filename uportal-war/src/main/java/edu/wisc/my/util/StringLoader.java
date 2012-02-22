/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

/**
 * Utility for loading a properties XML file from the classpath and pulling out strings
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class StringLoader {
    private final String path;
    private final Properties sql;
    
    /**
     * @param path Classpath location of the Properties XML file to load from
     * @throws IllegalStateException if the properties file could not be loaded
     */
    public StringLoader(String path) {
        this(path, null);
    }
    
    /**
     * @param path Classpath location of the Properties XML file to load from
     * @param loader Classloader to use when loading the properties file
     * @throws IllegalStateException if the properties file could not be loaded
     */
    public StringLoader(String path, ClassLoader loader) {
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }
        
        this.path = path;
        this.sql = new Properties();

        final InputStream sqlXmlStream = loader.getResourceAsStream(this.path);
        try {
            this.sql.loadFromXML(sqlXmlStream);
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Failed loading Properties XML '" + this.path + "' containing strings, check project complition.", ioe);
        }
    }
    
    /**
     * @param key Name of the property to retrieve
     * @return The property value, will never be null
     * @throws MissingResourceException If the property is not found
     */
    public String getString(String key) {
        final String sql = this.sql.getProperty(key);
        
        if (sql == null) {
            throw new MissingResourceException("Could not find string '" + key + "' in properties file '" + this.path + "'", this.path, key);
        }
        
        return sql;
    }
}
