/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.snow;

import edu.wisc.my.snow.om.DeclaredSnowEmergency;

/**
 * DAO to access snow emergency XML feed data.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public interface SnowEmergencyDao {

    /**
     * @return The current declared snow emergency data
     */
    public DeclaredSnowEmergency getSnowEmergencyStatus();

}