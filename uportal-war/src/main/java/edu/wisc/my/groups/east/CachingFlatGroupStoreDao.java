/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups.east;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;

import edu.wisc.my.groups.SearchType;

/**
 * Wraps a FlatGroupStoreDao to provide intelligent caching of its methods. The following named caches are
 * expected to exist in the configured CacheManager:<br/>
 * <ul>
 *  <li>groupCache</li>
 *  <li>groupMembershipCache</li>
 *  <li>groupsCache</li>
 *  <li>parentGroupsCache</li>
 *  <li>isMemberInGroupCache</li>
 *  <li>groupSearchCache</li>
 *  <li>memberSearchCache</li>
 * </ol>
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
public class CachingFlatGroupStoreDao implements FlatGroupStoreDao {
    private static final String GROUPS_SET_KEY = "GROUPS_SET_KEY";
    
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private FlatGroupStoreDao flatGroupStoreDao;
    private CacheManager cacheManager;
    
    private Ehcache groupCache;
    private Ehcache groupMembershipCache;
    private Ehcache groupsCache;
    private Ehcache parentGroupsCache;
    private Ehcache isMemberInGroupCache;
    private Ehcache groupSearchCache;
    private Ehcache memberSearchCache;
    
    
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
     * @return the cacheManager
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
    /**
     * @param cacheManager the cacheManager to set
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        
        this.groupCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.groupCache");
        this.groupMembershipCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.groupMembershipCache");
        this.groupsCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.groupsCache");
        this.parentGroupsCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.parentGroupsCache");
        this.isMemberInGroupCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.isMemberInGroupCache");
        this.groupSearchCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.groupSearchCache");
        this.memberSearchCache = this.cacheManager.getEhcache("edu.wisc.my.groups.east.FlatGroupStoreDao.memberSearchCache");
    }
    
    
    public synchronized IEntityGroup getGroup(long groupId) {
        final Element cacheElement = this.groupCache.get(groupId);
        
        final IEntityGroup group;
        if (cacheElement != null) {
            group = (IEntityGroup)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found IEntityGroup '" + group + "' in cache for key " + groupId);
            }
        }
        else {
            group = this.flatGroupStoreDao.getGroup(groupId);
            
            final Element newElement = new Element(groupId, group);
            this.groupCache.put(newElement);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached IEntityGroup '" + group + "' in cache for key " + groupId);
            }
        }
        
        return group;
    }

    @SuppressWarnings("unchecked")
    public synchronized Set<IEntity> getGroupMembers(long groupId) {
        final Element cacheElement = this.groupMembershipCache.get(groupId);
        
        final Set<IEntity> groupMembers;
        if (cacheElement != null) {
            groupMembers = (Set<IEntity>)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found IEntity members '" + groupMembers + "' in cache for key " + groupId);
            }
        }
        else {
            groupMembers = this.flatGroupStoreDao.getGroupMembers(groupId);

            final Element newElement = new Element(groupId, groupMembers);
            this.groupMembershipCache.put(newElement);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached IEntity members '" + groupMembers + "' in cache for key " + groupId);
            }
        }
        
        return groupMembers;
    }

    @SuppressWarnings("unchecked")
    public synchronized Set<IEntityGroup> getGroups() {
        final Element cacheElement = this.groupsCache.get(GROUPS_SET_KEY);
        
        final Set<IEntityGroup> groups;
        if (cacheElement != null) {
            groups = (Set<IEntityGroup>)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found IEntityGroups '" + groups + "' in cache");
            }
        }
        else {
            groups = this.flatGroupStoreDao.getGroups();

            final Element newElement = new Element(GROUPS_SET_KEY, groups);
            this.groupsCache.put(newElement);
            
            //Make sure the groupCache has the correct content
            this.groupCache.removeAll();
            this.cacheAllGroups(groups);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached IEntityGroups '" + groups + "'");
            }
        }
        
        return groups;
    }

    @SuppressWarnings("unchecked")
    public synchronized Set<IEntityGroup> getParentGroups(String memberId) {
        final Element cacheElement = this.parentGroupsCache.get(memberId);
        
        final Set<IEntityGroup> parentGroups;
        if (cacheElement != null) {
            parentGroups = (Set<IEntityGroup>)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found parent IEntityGroups '" + parentGroups + "' in cache for member '" + memberId + "'");
            }
        }
        else {
            parentGroups = this.flatGroupStoreDao.getParentGroups(memberId);

            final Element newElement = new Element(memberId, parentGroups);
            this.parentGroupsCache.put(newElement);
            
            //Make sure the groupCache has the correct content
            this.cacheAllGroups(parentGroups);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached parent IEntityGroups '" + parentGroups + "' for member '" + memberId + "'");
            }
        }
        
        return parentGroups;
    }

    public synchronized boolean isMemberInGroup(long groupId, String memberId) {
        final Map<String, Object> key = new HashMap<String, Object>(2);
        key.put("groupId", groupId);
        key.put("memberId", memberId);
        
        final Element cacheElement = this.isMemberInGroupCache.get(key);
        
        final boolean memberInGroup;
        if (cacheElement != null) {
            memberInGroup = (Boolean)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found isMemberInGroup '" + memberInGroup + "' in cache for group '" + groupId + "' and member '" + memberId + "'");
            }
        }
        else {
            memberInGroup = this.flatGroupStoreDao.isMemberInGroup(groupId, memberId);
            
            final Element newElement = new Element(key, memberInGroup);
            this.isMemberInGroupCache.put(newElement);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached isMemberInGroup '" + memberInGroup + "' in cache for group '" + groupId + "' and member '" + memberId + "'");
            }
        }
        
        return memberInGroup;
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection<EntityIdentifier> searchForGroups(String nameQuery, SearchType searchType) {
        final Map<String, Object> key = new HashMap<String, Object>(2);
        key.put("nameQuery", nameQuery);
        key.put("searchType", searchType);
        
        
        final Element cacheElement = this.groupSearchCache.get(key);
        
        final Collection<EntityIdentifier> groups;
        if (cacheElement != null) {
            groups = (Collection<EntityIdentifier>)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found groups '" + groups + "' in cache for query '" + nameQuery + "' and search type '" + searchType + "'");
            }
        }
        else {
            groups = this.flatGroupStoreDao.searchForGroups(nameQuery, searchType);
            
            final Element newElement = new Element(key, groups);
            this.groupSearchCache.put(newElement);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached groups '" + groups + "' in cache for query '" + nameQuery + "' and search type '" + searchType + "'");
            }
        }
        
        return groups;
    }

    @SuppressWarnings("unchecked")
    public synchronized Collection<EntityIdentifier> searchForMembers(String idQuery, SearchType searchType) {
        final Map<String, Object> key = new HashMap<String, Object>(2);
        key.put("nameQuery", idQuery);
        key.put("searchType", searchType);
        
        
        final Element cacheElement = this.memberSearchCache.get(key);
        
        final Collection<EntityIdentifier> members;
        if (cacheElement != null) {
            members = (Collection<EntityIdentifier>)cacheElement.getObjectValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Found members '" + members + "' in cache for query '" + idQuery + "' and search type '" + searchType + "'");
            }
        }
        else {
            members = this.flatGroupStoreDao.searchForMembers(idQuery, searchType);
            
            final Element newElement = new Element(key, members);
            this.memberSearchCache.put(newElement);
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved and cached members '" + members + "' in cache for query '" + idQuery + "' and search type '" + searchType + "'");
            }
        }
        
        return members;
    }

    /**
     * Adds all IEntityGroups in the Collection to the groupCache.
     */
    private void cacheAllGroups(Collection<IEntityGroup> groups) {
        for (final IEntityGroup group : groups) {
            final String groupIdStr = group.getLocalKey();
            final Long groupId = Long.valueOf(groupIdStr);
            final Element newGroupElement = new Element(groupId, group);
            this.groupCache.put(newGroupElement);
        }
    }
}
