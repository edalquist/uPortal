/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.dbcp;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Exposes ability to set connection properties.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class ExtendedDataSource extends BasicDataSource {
    public void setConnectionProperties(Properties properties) {
        for (final Map.Entry<Object, Object> property : properties.entrySet()) {
            this.addConnectionProperty((String) property.getKey(), (String) property.getValue());
        }
    }
}
