/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.groups.util;

/**
 * Base for {@link UtilityGroup} that allows setting of the name and description
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public abstract class BaseUtilityGroup implements UtilityGroup {
    private String name;
    private String description;
    
    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
