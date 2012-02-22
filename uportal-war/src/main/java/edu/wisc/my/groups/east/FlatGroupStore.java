/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups.east;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.EntityTypes;
import org.jasig.portal.RDBMServices;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IEntitySearcher;
import org.jasig.portal.groups.IEntityStore;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.ILockableEntityGroup;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.utils.threading.SingletonDoubleCheckedCreator;

import edu.wisc.my.groups.SearchType;
import edu.wisc.my.groups.east.jdbc.JdbcFlatGroupStoreDao;


/**
 * Provides a view of a 'flat' group store where there is a single root group that is parent to all
 * other groups the store knows about. The child groups only contains IPersons.
 * <br>
 * More generic DAO type operations are encapsulated by the {@link FlatGroupStoreDao} interface. The
 * default implementation used is {@link JdbcFlatGroupStoreDao} which uses the default {@link DataSource}
 * from {@link RDBMServices}
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.4 $
 */
public class FlatGroupStore implements IEntityGroupStore, IEntityStore, IEntitySearcher {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private final SingletonDoubleCheckedCreator<DynamicEntityGroupImpl> rootGroupCreator = new SingletonDoubleCheckedCreator<DynamicEntityGroupImpl>() {
        /* (non-Javadoc)
         * @see org.jasig.portal.utils.threading.SingletonDoubleCheckedCreator#createSingleton(java.lang.Object[])
         */
        @Override
        protected DynamicEntityGroupImpl createSingleton(Object... args) {
            final DynamicEntityGroupImpl rootGroup = new DynamicEntityGroupImpl(FlatGroupStore.this.rootGroupKey, FlatGroupStore.this.rootGroupEntityType);
            rootGroup.setName(FlatGroupStore.this.rootGroupName);
            rootGroup.setDescription(FlatGroupStore.this.rootGroupDescription);
            return rootGroup;
        }
    };
    
    private FlatGroupStoreDao flatGroupStoreDao;
    
    private String rootGroupKey; 
    private Class<? extends IEntity> rootGroupEntityType;
    private String rootGroupName;
    private String rootGroupDescription;
    
    /**
     * @return the flatGroupStoreDao
     */
    public FlatGroupStoreDao getFlatGroupStoreDao() {
        return flatGroupStoreDao;
    }
    /**
     * @param flatGroupStoreDao the flatGroupStoreDao to set
     */
    public void setFlatGroupStoreDao(FlatGroupStoreDao flatGroupStoreDao) {
        this.flatGroupStoreDao = flatGroupStoreDao;
    }
    
    /**
     * @return the rootGroupKey
     */
    public String getRootGroupKey() {
        return rootGroupKey;
    }
    /**
     * @param rootGroupKey the rootGroupKey to set
     */
    public void setRootGroupKey(String rootGroupKey) {
        this.rootGroupKey = rootGroupKey;
    }
    
    /**
     * @return the rootGroupEntityType
     */
    public Class<? extends IEntity> getRootGroupEntityType() {
        return rootGroupEntityType;
    }
    /**
     * @param rootGroupEntityType the rootGroupEntityType to set
     */
    public void setRootGroupEntityType(Class<? extends IEntity> rootGroupEntityType) {
        this.rootGroupEntityType = rootGroupEntityType;
    }
    
    /**
     * @return the rootGroupName
     */
    public String getRootGroupName() {
        return rootGroupName;
    }
    /**
     * @param rootGroupName the rootGroupName to set
     */
    public void setRootGroupName(String rootGroupName) {
        this.rootGroupName = rootGroupName;
    }
    
    /**
     * @return the rootGroupDescription
     */
    public String getRootGroupDescription() {
        return rootGroupDescription;
    }
    /**
     * @param rootGroupDescription the rootGroupDescription to set
     */
    public void setRootGroupDescription(String rootGroupDescription) {
        this.rootGroupDescription = rootGroupDescription;
    }
    
    
    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#contains(org.jasig.portal.groups.IEntityGroup, org.jasig.portal.groups.IGroupMember)
     */
    public boolean contains(IEntityGroup group, IGroupMember member) throws GroupsException {
        final boolean contains;
        
        if (member.isGroup()) {
            final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
            contains = rootGroup.equals(group) && !rootGroup.equals(member); 
        }
        else if (!rootGroupEntityType.equals(member.getEntityType())) {
            contains = false;
        }
        else {
            final String groupKey = group.getLocalKey();
            final String memberKey = member.getKey();
            
            final long groupId;
            try {
                groupId = Long.parseLong(groupKey);
            }
            catch (NumberFormatException nfe) {
                this.logger.warn("Could not convert group key '" + groupKey + "' from group " + group + " to a long, returning false for 'contains'", nfe);
                return false;
            }
            
            contains = this.flatGroupStoreDao.isMemberInGroup(groupId, memberKey);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("contains=" + contains + " for IEntityGroup='" + group + "' and IGroupMember='" + member + "'");
        }
        
        return contains;
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#delete(org.jasig.portal.groups.IEntityGroup)
     */
    public void delete(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException("FlatGroupStore is read only.");
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#find(java.lang.String)
     */
    public IEntityGroup find(String groupKey) throws GroupsException {
        final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
        if (rootGroup.getLocalKey().equals(groupKey)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found root group '" + rootGroup + "' for key='" + groupKey + "'");
            }
            
            return rootGroup;
        }

        //Not the root group, do a lookup
        final long groupId;
        try {
            groupId = Long.parseLong(groupKey);
        }
        catch (NumberFormatException nfe) {
            this.logger.warn("Could not convert group key '" + groupKey + "' from to a long, returning false for 'contains'", nfe);
            return null;
        }
        
        final IEntityGroup group = this.flatGroupStoreDao.getGroup(groupId);
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found group '" + group + "' for key='" + groupKey + "'");
        }
        
        return group;
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#findContainingGroups(org.jasig.portal.groups.IGroupMember)
     */
    public Iterator<IEntityGroup> findContainingGroups(IGroupMember gm) throws GroupsException {
        final Set<IEntityGroup> parentGroups;
        
        if (gm.isGroup()) {
            final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
            if (rootGroup.equals(gm)) {
                parentGroups = Collections.emptySet();
            }
            else {
                parentGroups = Collections.singleton((IEntityGroup)rootGroup);
            }
        }
        else {
            final String memberKey = gm.getKey();
            parentGroups = this.flatGroupStoreDao.getParentGroups(memberKey);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found conainting groups '" + parentGroups + "' for IGroupMember='" + gm + "'");
        }
        
        return parentGroups.iterator();
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#findEntitiesForGroup(org.jasig.portal.groups.IEntityGroup)
     */
    public Iterator<IEntity> findEntitiesForGroup(IEntityGroup group) throws GroupsException {
        final Set<IEntity> entities;
        
        final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
        if (rootGroup.equals(group)) {
            entities = Collections.emptySet();
        }
        else {
            final String groupKey = group.getLocalKey();
            
            final long groupId;
            try {
                groupId = Long.parseLong(groupKey);
            }
            catch (NumberFormatException nfe) {
                this.logger.warn("Could not convert group key '" + groupKey + "' from to a long, returning false for 'contains'", nfe);
                return null;
            }
            
            entities = this.flatGroupStoreDao.getGroupMembers(groupId);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found entities '" + entities + "' for IEntityGroup='" + group + "'");
        }
        
        return entities.iterator();
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#findLockable(java.lang.String)
     */
    public ILockableEntityGroup findLockable(String key) throws GroupsException {
        throw new UnsupportedOperationException("FlatGroupStore does not support lockable groups");
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#findMemberGroupKeys(org.jasig.portal.groups.IEntityGroup)
     */
    public String[] findMemberGroupKeys(IEntityGroup group) throws GroupsException {
        final Set<String> memberGroupKeys;
        
        final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
        if (rootGroup.equals(group)) {
            final Set<IEntityGroup> groups = this.flatGroupStoreDao.getGroups();

            memberGroupKeys = new HashSet<String>(groups.size());
            for (final IEntityGroup memberGroup : groups) {
                final String localKey = memberGroup.getLocalKey();
                memberGroupKeys.add(localKey);
            }
        }
        else {
            memberGroupKeys = Collections.emptySet();
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found member group keys '" + memberGroupKeys + "' for IEntityGroup='" + group + "'");
        }
        
        return memberGroupKeys.toArray(new String[memberGroupKeys.size()]);
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#findMemberGroups(org.jasig.portal.groups.IEntityGroup)
     */
    public Iterator<IEntityGroup> findMemberGroups(IEntityGroup group) throws GroupsException {
        final Set<IEntityGroup> groups;
        
        final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
        if (rootGroup.equals(group)) {
            groups = this.flatGroupStoreDao.getGroups();
        }
        else {
            groups = Collections.emptySet();
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found member groups '" + groups + "' for IEntityGroup='" + group + "'");
        }
        
        return groups.iterator();
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#newInstance(java.lang.Class)
     */
    public IEntityGroup newInstance(Class entityType) throws GroupsException {
        throw new UnsupportedOperationException("FlatGroupStore is read only.");
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#searchForGroups(java.lang.String, int, java.lang.Class)
     */
    public EntityIdentifier[] searchForGroups(String query, int method, Class leaftype) throws GroupsException {
        final SearchType searchType = SearchType.getTypeForMethod(method);
        if (searchType == null) {
            throw new IllegalArgumentException("Unknonw method specified: " + method);
        }
        
        if (!IPerson.class.isAssignableFrom(leaftype)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unsupporeted leaf type '" + leaftype + "', returning empty array");
            }
            
            return new EntityIdentifier[0];
        }
        
        Collection<EntityIdentifier> groups = this.flatGroupStoreDao.searchForGroups(query, searchType);
        
        //Add the root group to the collection if it matches.
        final DynamicEntityGroupImpl rootGroup = this.rootGroupCreator.get();
        final String rootGroupName = rootGroup.getName();
        if ((SearchType.IS == searchType && rootGroupName.equals(query)) ||
                (SearchType.STARTS_WITH == searchType && rootGroupName.startsWith(query)) ||
                (SearchType.CONTAINS == searchType && rootGroupName.contains(query)) ||
                (SearchType.ENDS_WITH == searchType && rootGroupName.endsWith(query))) {
            
            final ArrayList<EntityIdentifier> updatedGroups = new ArrayList<EntityIdentifier>(groups.size() + 1);
            updatedGroups.addAll(groups);
            updatedGroups.add(rootGroup.getEntityIdentifier());
            
            groups = updatedGroups;
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Search returned groups '" + groups + "' for query='" + query + "', method='" + searchType + "', type='" + leaftype + "'");
        }
        
        return groups.toArray(new EntityIdentifier[groups.size()]);
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#update(org.jasig.portal.groups.IEntityGroup)
     */
    public void update(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException("FlatGroupStore is read only.");
    }

    /**
     * @see org.jasig.portal.groups.IEntityGroupStore#updateMembers(org.jasig.portal.groups.IEntityGroup)
     */
    public void updateMembers(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException("FlatGroupStore is read only.");
    }

    /**
     * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String, java.lang.Class)
     */
    public IEntity newInstance(String key, Class type) throws GroupsException {
        if (type != null && EntityTypes.getEntityTypeID(type) == null) {
            throw new GroupsException("Invalid entity type: " + type.getName());
        }
        
        final EntityImpl entity = new EntityImpl(key, type);
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Created IEntity='" + entity + "' for key='" + key + "', type='" + type + "'");
        }
        
        return entity;
    }

    /**
     * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String)
     */
    public IEntity newInstance(String key) throws GroupsException {
        return this.newInstance(key, null);
    }

    /**
     * @see org.jasig.portal.groups.IEntitySearcher#searchForEntities(java.lang.String, int, java.lang.Class)
     */
    public EntityIdentifier[] searchForEntities(String query, int method, Class type) throws GroupsException {
        final SearchType searchType = SearchType.getTypeForMethod(method);
        if (searchType == null) {
            throw new IllegalArgumentException("Unknonw method specified: " + method);
        }
        
        if (!IPerson.class.isAssignableFrom(type)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unsupporeted entity type '" + type + "', returning empty array");
            }
            
            return new EntityIdentifier[0];
        }
        
        final Collection<EntityIdentifier> members = this.flatGroupStoreDao.searchForMembers(query, searchType);
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Search returned members '" + members + "' for query='" + query + "', method='" + searchType + "', type='" + type + "'");
        }
        
        return members.toArray(new EntityIdentifier[members.size()]);
    }
}
