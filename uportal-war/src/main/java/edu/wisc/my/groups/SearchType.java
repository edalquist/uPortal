/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups;

public enum SearchType {
    IS(1),
    STARTS_WITH(2),
    ENDS_WITH(3),
    CONTAINS(4);
    
    private final int method;
    
    private SearchType(int method) {
        this.method = method;
    }
    
    public static SearchType getTypeForMethod(int method) {
        for (final SearchType type : SearchType.values()) {
            if (type.method == method) {
                return type;
            }
        }
        
        return null;
    }
}