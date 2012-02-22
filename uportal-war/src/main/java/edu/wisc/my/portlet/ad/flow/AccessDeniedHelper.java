/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.flow;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.services.GroupService;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;

import edu.wisc.my.portlet.ad.dao.AccessRecordDao;
import edu.wisc.my.portlet.ad.om.AccessGroup;
import edu.wisc.my.portlet.ad.om.AccessRecord;
import edu.wisc.my.portlet.ad.om.AccessRecordCreatedComparator;

/**
 * Controller that handles requests from users with no portal access rights 
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class AccessDeniedHelper {
    private static final String ACCESS_RECORD_KEY = "accessRecordKey";

    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IPersonAttributeDao personAttributeDao;
    private AccessRecordDao accessRecordDao;
    
    public void setPersonAttributeDao(IPersonAttributeDao personAttributeDao) {
        this.personAttributeDao = personAttributeDao;
    }
    
    public void setAccessRecordDao(AccessRecordDao accessRecordDao) {
        this.accessRecordDao = accessRecordDao;
    }
    
    public AccessRecord getAccessRecordByKey(String referenceKey) {
        return this.accessRecordDao.getAccessRecordByKey(referenceKey);
    }
    
    public Set<AccessRecord> getMostRecentAccessRecords(int startPosition, int maxResults) {
        final Collection<AccessRecord> accessRecords = this.accessRecordDao.getAccessRecordsRange(startPosition, maxResults);

        //Build sorted list of records
        final Set<AccessRecord> sortedAccessRecords = new TreeSet<AccessRecord>(AccessRecordCreatedComparator.DESCENDING);
        sortedAccessRecords.addAll(accessRecords);
        
        return sortedAccessRecords;
    }
    
    public Set<AccessRecord> getAccessRecordsForPerson(String username) {
        if (username == null) {
            return Collections.emptySet();
        }
        
        final Collection<AccessRecord> accessRecords = this.accessRecordDao.getAccessRecordsForUser(username);

        //Build sorted list of records
        final Set<AccessRecord> sortedAccessRecords = new TreeSet<AccessRecord>(AccessRecordCreatedComparator.DESCENDING);
        sortedAccessRecords.addAll(accessRecords);
        
        return sortedAccessRecords;
    }

    public AccessRecord getOrCreateAccessRecord(RenderRequest request) {
        final PortletSession session = request.getPortletSession();
        
        //Check if an AccessRecord has already been created for this session, simply return it if it has
        final String accessRecordKey = (String)session.getAttribute(ACCESS_RECORD_KEY);
        if (accessRecordKey != null) {
            final AccessRecord accessRecord = this.accessRecordDao.getAccessRecordByKey(accessRecordKey);
            if (accessRecord != null) {
                if (this.logger.isDebugEnabled()) {
                    logger.debug("Found existing AccessRecord for session key " + accessRecordKey + ": " + accessRecord);
                }
                
                return accessRecord;
            }
        }
        
        //No AccessRecord yet, create and populate
        final String username = request.getRemoteUser();
        final AccessRecord accessRecord = this.accessRecordDao.createAccessRecord(username);
        
        this.setAttributes(username, accessRecord);
        
        this.setGroups(username, accessRecord);

        //Store the populated record in the DB and a key reference in the session
        this.accessRecordDao.storeAccessRecord(accessRecord);
        session.setAttribute(ACCESS_RECORD_KEY, accessRecord.getReferenceKey());

        return accessRecord;
    }

    private void setAttributes(final String username, final AccessRecord accessRecord) {
        final Map<String, String> accessAttributes = accessRecord.getAttributes();
        
        final IPersonAttributes personAttributes = this.personAttributeDao.getPerson(username);
        final Map<String, List<Object>> attributes = personAttributes.getAttributes();
        for (final Map.Entry<String, List<Object>> attributeEntry : attributes.entrySet()) {
            final String name = attributeEntry.getKey();
            final List<Object> values = attributeEntry.getValue();
            
            final String value;
            if (values == null || values.size() == 0) {
                value = null;
            }
            else if (values.size() == 1) {
                value = String.valueOf(values.get(0));
            }
            else {
                value = values.toString();
            }
            
            accessAttributes.put(name, value);
        }
    }

    @SuppressWarnings("unchecked")
    private void setGroups(final String username, final AccessRecord accessRecord) {
        final Collection<AccessGroup> groups = accessRecord.getGroups();
        
        final IEntity personEntity = GroupService.getEntity(username, IPerson.class);
        for (final Iterator<IEntityGroup> allContainingGroups = personEntity.getAllContainingGroups(); allContainingGroups.hasNext(); ) {
            final IEntityGroup containingGroup = allContainingGroups.next();
            
            final AccessGroup accessGroup = new AccessGroup();
            accessGroup.setKey(containingGroup.getKey());
            accessGroup.setName(containingGroup.getName());
            
            groups.add(accessGroup);
        }
    }
}
