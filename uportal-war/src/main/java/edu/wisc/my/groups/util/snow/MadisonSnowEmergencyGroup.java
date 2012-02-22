/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.groups.util.snow;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import edu.wisc.my.groups.util.BaseUtilityGroup;
import edu.wisc.my.snow.SnowEmergencyDao;
import edu.wisc.my.snow.om.DeclaredSnowEmergency;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class MadisonSnowEmergencyGroup extends BaseUtilityGroup {
    private SnowEmergencyDao snowEmergencyDao;
    
    public SnowEmergencyDao getSnowEmergencyDao() {
        return this.snowEmergencyDao;
    }
    @Required
    public void setSnowEmergencyDao(SnowEmergencyDao snowEmergencyDao) {
        Validate.notNull(snowEmergencyDao);
        this.snowEmergencyDao = snowEmergencyDao;
    }



    /* (non-Javadoc)
     * @see edu.wisc.my.groups.util.UtilityGroup#isMemberOf(java.lang.String)
     */
    public boolean isMemberOf(String memberKey) {
        final DeclaredSnowEmergency snowEmergencyStatus = this.snowEmergencyDao.getSnowEmergencyStatus();
        return snowEmergencyStatus.isStatus();
    }
}
