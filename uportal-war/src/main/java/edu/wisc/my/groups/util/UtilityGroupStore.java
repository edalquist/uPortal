/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.groups.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.IBasicEntity;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.EntityTestingGroupImpl;
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
import org.springframework.beans.factory.InitializingBean;

import edu.wisc.my.groups.SearchType;


/**
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public class UtilityGroupStore implements IEntityGroupStore, IEntityStore, IEntitySearcher, InitializingBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private final SingletonDoubleCheckedCreator<IEntityGroup> rootGroupCreator = new SingletonDoubleCheckedCreator<IEntityGroup>() {
        /* (non-Javadoc)
         * @see org.jasig.portal.utils.threading.SingletonDoubleCheckedCreator#createSingleton(java.lang.Object[])
         */
        @Override
        protected IEntityGroup createSingleton(Object... args) {
            final IEntityGroup rootGroup = new EntityGroupImpl(UtilityGroupStore.this.rootGroupKey, UtilityGroupStore.this.rootGroupEntityType);
            rootGroup.setName(UtilityGroupStore.this.rootGroupName);
            rootGroup.setDescription(UtilityGroupStore.this.rootGroupDescription);
            return rootGroup;
        }
    };
    
    private final SingletonDoubleCheckedCreator<Map<String, IEntityGroup>> memberGroupsCreator = new SingletonDoubleCheckedCreator<Map<String, IEntityGroup>>() {
        /* (non-Javadoc)
         * @see org.jasig.portal.utils.threading.SingletonDoubleCheckedCreator#createSingleton(java.lang.Object[])
         */
        @Override
        protected Map<String, IEntityGroup> createSingleton(Object... args) {
            final Map<String, IEntityGroup> memberGroups = new LinkedHashMap<String, IEntityGroup>(UtilityGroupStore.this.utilityGroups.size());
            for (final Map.Entry<String, UtilityGroup> groupEntry : UtilityGroupStore.this.utilityGroups.entrySet()) {
                final String groupKey = groupEntry.getKey();
                final IEntityGroup group = new EntityTestingGroupImpl(groupKey, UtilityGroupStore.this.rootGroupEntityType);
                
                final UtilityGroup utilityGroup = groupEntry.getValue();
                group.setName(utilityGroup.getName());
                group.setDescription(utilityGroup.getDescription());
                
                memberGroups.put(groupKey, group);
            }
            
            return memberGroups;
        }
    };
    
    private String rootGroupKey = "util"; 
    private Class<? extends IBasicEntity> rootGroupEntityType = IPerson.class;
    private String rootGroupName = "Utility Groups";
    private String rootGroupDescription = "Groups that are general not user-specific but more service oriented";
    private Map<String, UtilityGroup> utilityGroups = Collections.emptyMap();
    
    public void setRootGroupKey(String rootGroupKey) {
        Validate.notNull(rootGroupKey);
        this.rootGroupKey = rootGroupKey;
    }
    public void setRootGroupEntityType(Class<? extends IBasicEntity> rootGroupEntityType) {
        Validate.notNull(rootGroupEntityType);
        this.rootGroupEntityType = rootGroupEntityType;
    }
    public void setRootGroupName(String rootGroupName) {
        Validate.notNull(rootGroupName);
        this.rootGroupName = rootGroupName;
    }
    public void setRootGroupDescription(String rootGroupDescription) {
        this.rootGroupDescription = rootGroupDescription;
    }
    public void setUtilityGroups(Map<String, UtilityGroup> utilityGroups) {
        Validate.notNull(utilityGroups);
        this.utilityGroups = new LinkedHashMap<String, UtilityGroup>(utilityGroups);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#contains(org.jasig.portal.groups.IEntityGroup, org.jasig.portal.groups.IGroupMember)
     */
    public boolean contains(IEntityGroup group, IGroupMember member) throws GroupsException {
        final boolean contains;
        
        if (member.isGroup()) {
            final IEntityGroup rootGroup = this.rootGroupCreator.get();
            contains = rootGroup.equals(group) && !rootGroup.equals(member); 
        }
        else if (!rootGroupEntityType.equals(member.getEntityType())) {
            contains = false;
        }
        else {
            final String groupKey = group.getLocalKey();
            
            final UtilityGroup utilityGroup = this.utilityGroups.get(groupKey);
            if (utilityGroup == null) {
                this.logger.warn("No utility group '" + groupKey + "' exists. Returning false.");
                return false;
            }
            
            final String memberKey = member.getKey();
            contains = utilityGroup.isMemberOf(memberKey);
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("contains=" + contains + " for IEntityGroup='" + group + "' and IGroupMember='" + member + "'");
        }
        
        return contains;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#find(java.lang.String)
     */
    public IEntityGroup find(String groupKey) throws GroupsException {
        final IEntityGroup rootGroup = this.rootGroupCreator.get();
        if (rootGroup.getLocalKey().equals(groupKey)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found root group '" + rootGroup + "' for key='" + groupKey + "'");
            }
            
            return rootGroup;
        }
        
        final Map<String, IEntityGroup> memberGroups = this.memberGroupsCreator.get();
        final IEntityGroup group = memberGroups.get(groupKey);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found group '" + group + "' for key='" + groupKey + "'");
        }
        
        return group;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#findContainingGroups(org.jasig.portal.groups.IGroupMember)
     */
    public Iterator<?> findContainingGroups(IGroupMember gm) throws GroupsException {
        final Set<IEntityGroup> parentGroups;
        
        if (gm.isGroup()) {
            final IEntityGroup rootGroup = this.rootGroupCreator.get();
            if (rootGroup.equals(gm)) {
                parentGroups = Collections.emptySet();
            }
            else {
                parentGroups = Collections.singleton(rootGroup);
            }
        }
        else {
            parentGroups = new LinkedHashSet<IEntityGroup>();
            
            final Map<String, IEntityGroup> memberGroups = this.memberGroupsCreator.get();
            for (final IEntityGroup group : memberGroups.values()) {
                if (this.contains(group, gm)) {
                    parentGroups.add(group);
                }
            }
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found conainting groups '" + parentGroups + "' for IGroupMember='" + gm + "'");
        }
        
        return parentGroups.iterator();
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#findEntitiesForGroup(org.jasig.portal.groups.IEntityGroup)
     */
    public Iterator<?> findEntitiesForGroup(IEntityGroup group) throws GroupsException {
        return Collections.emptyList().iterator();
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#findMemberGroupKeys(org.jasig.portal.groups.IEntityGroup)
     */
    public String[] findMemberGroupKeys(IEntityGroup group) throws GroupsException {
        final Set<String> memberGroupKeys;
        
        final IEntityGroup rootGroup = this.rootGroupCreator.get();
        if (rootGroup.equals(group)) {
            final Map<String, IEntityGroup> memberGroups = this.memberGroupsCreator.get();
            memberGroupKeys = memberGroups.keySet();
        }
        else {
            memberGroupKeys = Collections.emptySet();
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found member group keys '" + memberGroupKeys + "' for IEntityGroup='" + group + "'");
        }
        
        return memberGroupKeys.toArray(new String[memberGroupKeys.size()]);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#findMemberGroups(org.jasig.portal.groups.IEntityGroup)
     */
    public Iterator<?> findMemberGroups(IEntityGroup group) throws GroupsException {
        final Set<IEntityGroup> groups;
        
        final IEntityGroup rootGroup = this.rootGroupCreator.get();
        if (rootGroup.equals(group)) {
            final Map<String, IEntityGroup> memberGroups = this.memberGroupsCreator.get();
            groups = new LinkedHashSet<IEntityGroup>(memberGroups.values());
        }
        else {
            groups = Collections.emptySet();
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Found member groups '" + groups + "' for IEntityGroup='" + group + "'");
        }
        
        return groups.iterator();
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#searchForGroups(java.lang.String, int, java.lang.Class)
     */
    public EntityIdentifier[] searchForGroups(String query, int method, Class leaftype) throws GroupsException {
        final SearchType searchType = SearchType.getTypeForMethod(method);
        if (searchType == null) {
            throw new IllegalArgumentException("Unknonw method specified: " + method);
        }
        
        if (!rootGroupEntityType.isAssignableFrom(leaftype)) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unsupporeted leaf type '" + leaftype + "', returning empty array");
            }
            
            return new EntityIdentifier[0];
        }
        
        final Map<String, IEntityGroup> memberGroups = this.memberGroupsCreator.get();
        final List<EntityIdentifier> groupIds = new ArrayList<EntityIdentifier>(memberGroups.size() + 1);
        
        final IEntityGroup rootGroup = this.rootGroupCreator.get();
        final String rootGroupName = rootGroup.getName();
        if (this.nameMatches(query, searchType, rootGroupName)) {
            groupIds.add(rootGroup.getEntityIdentifier());
        }
        
        for (final IEntityGroup group : memberGroups.values()) {
            final String name = group.getName();
            if (this.nameMatches(query, searchType, name)) {
                groupIds.add(group.getEntityIdentifier());
            }
        }
        
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Search returned groupIds '" + groupIds + "' for query='" + query + "', method='" + searchType + "', type='" + leaftype + "'");
        }
        
        return groupIds.toArray(new EntityIdentifier[groupIds.size()]);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntitySearcher#searchForEntities(java.lang.String, int, java.lang.Class)
     */
    public EntityIdentifier[] searchForEntities(String query, int method, Class type) throws GroupsException {
        return new EntityIdentifier[0];
    }
    
    protected boolean nameMatches(String query, SearchType type, String target) {
        if (query == target) {
            return true;
        }
        
        if (target == null) {
            return false;
        }
        
        switch (type) {
            case IS: {
                return target.equals(query);
            }
            case CONTAINS: {
                return target.contains(query);
            }
            case STARTS_WITH: {
                return target.startsWith(query);
            }
            case ENDS_WITH: {
                return target.endsWith(query);
            }
        }
        
        return false;
    }
    

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#newInstance(java.lang.Class)
     */
    public IEntityGroup newInstance(Class entityType) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#update(org.jasig.portal.groups.IEntityGroup)
     */
    public void update(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#updateMembers(org.jasig.portal.groups.IEntityGroup)
     */
    public void updateMembers(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String)
     */
    public IEntity newInstance(String key) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String, java.lang.Class)
     */
    public IEntity newInstance(String key, Class type) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#findLockable(java.lang.String)
     */
    public ILockableEntityGroup findLockable(String key) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.groups.IEntityGroupStore#delete(org.jasig.portal.groups.IEntityGroup)
     */
    public void delete(IEntityGroup group) throws GroupsException {
        throw new UnsupportedOperationException(this.getClass().getName() + " is read only.");
    }

}
