/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.groups.util;

/**
 * Represents a simple utility group that can simply determine if a username is a member or not.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public interface UtilityGroup {
    /**
     * @return Name of the group
     */
    public String getName();
    
    /**
     * @return Description of the group
     */
    public String getDescription();
    
    /**
     * @param memberKey username of the member
     * @return true if the user is a member, false if not
     */
    public boolean isMemberOf(String memberKey);
}
