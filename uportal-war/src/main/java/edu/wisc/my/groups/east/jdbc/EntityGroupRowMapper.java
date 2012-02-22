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
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.security.IPerson;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import edu.wisc.my.groups.east.DynamicEntityGroupImpl;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class EntityGroupRowMapper implements ParameterizedRowMapper<IEntityGroup> {
    public static final EntityGroupRowMapper INSTANCE = new EntityGroupRowMapper();
    
    protected final Log logger = LogFactory.getLog(this.getClass());

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.simple.ParameterizedRowMapper#mapRow(java.sql.ResultSet, int)
     */
    public IEntityGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
        final long groupId = rs.getLong("GROUP_ID");
        final String name = rs.getString("NAME");
        final String description = rs.getString("DESCR");
        final String creator = rs.getString("CREATED_BY");
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating DynamicEntityGroupImpl for groupId=" + groupId + ", name='" + name + "', desc='" + description + "', creator='" + creator + "'");
        }
        
        final DynamicEntityGroupImpl egi;
        try {
            egi = new DynamicEntityGroupImpl(Long.toString(groupId), IPerson.class);
        }
        catch (GroupsException ge) {
            throw new DataRetrievalFailureException("Failed to create DynamicEntityGroupImpl for groupId " + groupId + " and entity type " + IPerson.class, ge);
        }
        
        egi.primSetName(name);
        egi.setDescription(description);
        egi.setCreatorID(creator);
        
        return egi;
    }

}
