/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.groups.east.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.security.IPerson;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class MemberEntityIdentifierRowMapper implements ParameterizedRowMapper<EntityIdentifier> {
    public static final MemberEntityIdentifierRowMapper INSTANCE = new MemberEntityIdentifierRowMapper();
    
    protected final Log logger = LogFactory.getLog(this.getClass());

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
     */
    public EntityIdentifier mapRow(ResultSet rs, int rowNum) throws SQLException {
        final String memberId = rs.getString("MEMBER_ID");
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating EntityIdentifier for memberId='" + memberId + "'");
        }
        
        final EntityIdentifier entityIdentifier = new EntityIdentifier(memberId, IPerson.class);
        
        return entityIdentifier;
    }

}
