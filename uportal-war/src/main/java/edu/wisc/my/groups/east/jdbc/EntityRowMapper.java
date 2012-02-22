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
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.services.GroupService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class EntityRowMapper implements ParameterizedRowMapper<IEntity> {
    public static final EntityRowMapper INSTANCE = new EntityRowMapper();
    
    protected final Log logger = LogFactory.getLog(this.getClass());

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
     */
    public IEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        final String key = rs.getString("MEMBER_ID");
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating IEntity for key='" + key + "'");
        }

        final IEntity entity;
        try {
            entity = GroupService.getEntity(key, IPerson.class);
        }
        catch (GroupsException ge) {
            throw new DataRetrievalFailureException("Failed to create IEntity for memberId " + key + " and entity type " + IPerson.class, ge);
        }
        
        return entity;
    }

}
