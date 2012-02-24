/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups.east.jdbc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.googlecode.ehcache.annotations.Cacheable;

import edu.wisc.my.groups.SearchType;
import edu.wisc.my.groups.east.FlatGroupStoreDao;
import edu.wisc.my.util.StringLoader;


/**
 * Flat group store DAO implementation backed by a JDBC database
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public class JdbcFlatGroupStoreDao extends SimpleJdbcDaoSupport implements FlatGroupStoreDao {
    private final String groupIdParam;
    private final String groupNameParam;
    private final String memberIdParam;
    
    private final String isMemberInGroupQuery;
    private final String singleEntityGroupQuery;
    private final String parentEntityGroupsQuery;
    private final String groupsQuery;
    private final String groupMembersQuery;
    private final String searchForGroupName;
    private final String searchForGroupNameLike;
    private final String searchForMemberName;
    private final String searchForMemberNameLike;
    
    public JdbcFlatGroupStoreDao() {
        final StringLoader loader = new StringLoader("edu/wisc/my/groups/east/JdbcFlatGroupStoreDaoSql.xml");
        
        this.groupIdParam = loader.getString("groupIdParam");
        this.groupNameParam = loader.getString("groupNameParam");
        this.memberIdParam = loader.getString("memberIdParam");
        
        this.isMemberInGroupQuery = loader.getString("isMemberInGroupQuery");
        this.singleEntityGroupQuery = loader.getString("singleEntityGroupQuery");
        this.parentEntityGroupsQuery = loader.getString("parentEntityGroupsQuery");
        this.groupsQuery = loader.getString("groupsQuery");
        this.groupMembersQuery = loader.getString("groupMembersQuery");
        this.searchForGroupName = loader.getString("searchForGroupName");
        this.searchForGroupNameLike = loader.getString("searchForGroupNameLike");
        this.searchForMemberName = loader.getString("searchForMemberName");
        this.searchForMemberNameLike = loader.getString("searchForMemberNameLike");
    }

    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.isMemberInGroupCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public boolean isMemberInGroup(long groupId, String memberId) {
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.groupIdParam, groupId);
        args.put(this.groupIdParam, memberId);
        
        try {
            final int memberCount = simpleJdbcTemplate.queryForInt(this.isMemberInGroupQuery, args);
            return memberCount == 1;
        }
        catch (DataAccessException dae) {
            this.logger.warn("isMemberInGroup query failed for groupId=" + groupId + " and memberId=" + memberId + ", failing safe and returning false", dae);
            return false;
        }
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.groupCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public IEntityGroup getGroup(long groupId) {
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.groupIdParam, groupId);
        
        try {
            final List<IEntityGroup> results = simpleJdbcTemplate.query(this.singleEntityGroupQuery, EntityGroupRowMapper.INSTANCE, args);
        
            if (CollectionUtils.isEmpty(results)) {
                return null;
            }
            return results.get(0);
        }
        catch (DataAccessException dae) {
            this.logger.warn("getGroup query failed for groupId=" + groupId + ", failing safe and returning null", dae);
            return null;
        } 
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.parentGroupsCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public Set<IEntityGroup> getParentGroups(String memberId) {
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.memberIdParam, memberId);
        
        try {
            final List<IEntityGroup> parentGroups = simpleJdbcTemplate.query(this.parentEntityGroupsQuery, EntityGroupRowMapper.INSTANCE, args);
            return new LinkedHashSet<IEntityGroup>(parentGroups);
        }
        catch (DataAccessException dae) {
            this.logger.warn("getParentGroups query failed for memberId=" + memberId + ", failing safe and returning an empty Set", dae);
            return Collections.emptySet();
        }
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.groupsCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public Set<IEntityGroup> getGroups() {
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        try {
            final List<IEntityGroup> groups = simpleJdbcTemplate.query(this.groupsQuery, EntityGroupRowMapper.INSTANCE);
            return new LinkedHashSet<IEntityGroup>(groups);
        }
        catch (DataAccessException dae) {
            this.logger.warn("getGroups query failed, failing safe and returning an empty Set", dae);
            return Collections.emptySet();
        }
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.groupMembershipCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public Set<IEntity> getGroupMembers(long groupId) {
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.groupIdParam, groupId);
        
        try {
            final List<IEntity> groupMembers = simpleJdbcTemplate.query(this.groupMembersQuery, EntityRowMapper.INSTANCE, args);
            return new LinkedHashSet<IEntity>(groupMembers);
        }
        catch (DataAccessException dae) {
            this.logger.warn("getGroupMembers query failed for groupId=" + groupId + ", failing safe and returning an empty Set", dae);
            return Collections.emptySet();
        }
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.groupSearchCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public Collection<EntityIdentifier> searchForGroups(String nameQuery, SearchType searchType) {
        final String searchForGroupQuery;
        
        switch (searchType) {
            case IS: {
                searchForGroupQuery = this.searchForGroupName;
            } break;
            
            case  STARTS_WITH: {
                searchForGroupQuery = this.searchForGroupNameLike;
                nameQuery = nameQuery + "%";
            } break;
            
            case  CONTAINS: {
                searchForGroupQuery = this.searchForGroupNameLike;
                nameQuery = "%" + nameQuery + "%";
            } break;
            
            case  ENDS_WITH: {
                searchForGroupQuery = this.searchForGroupNameLike;
                nameQuery = "%" + nameQuery;
            } break;
            
            default: {
                throw new IllegalArgumentException("Unknown SearchType specified: " + searchType);
            }
        }
        
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.groupNameParam, nameQuery);
        
        try {
            final List<EntityIdentifier> groupsIdentifiers = simpleJdbcTemplate.query(searchForGroupQuery, GroupEntityIdentifierRowMapper.INSTANCE, args);
            return groupsIdentifiers;
        }
        catch (DataAccessException dae) {
            this.logger.warn("searchForGroups query failed for nameQuery=" + nameQuery +", failing safe and returning an empty Collection", dae);
            return Collections.emptyList();
        }
    }
    
    @Cacheable(cacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.memberSearchCache", 
            exceptionCacheName="edu.wisc.my.groups.east.FlatGroupStoreDao.EXCEPTION")
    public Collection<EntityIdentifier> searchForMembers(String idQuery, SearchType searchType) {
        final String searchForMemberQuery;
        
        switch (searchType) {
            case IS: {
                searchForMemberQuery = this.searchForMemberName;
            } break;
            
            case  STARTS_WITH: {
                searchForMemberQuery = this.searchForMemberNameLike;
                idQuery = idQuery + "%";
            } break;
            
            case  CONTAINS: {
                searchForMemberQuery = this.searchForMemberNameLike;
                idQuery = "%" + idQuery + "%";
            } break;
            
            case  ENDS_WITH: {
                searchForMemberQuery = this.searchForMemberNameLike;
                idQuery = "%" + idQuery;
            } break;
            
            default: {
                throw new IllegalArgumentException("Unknown SearchType specified: " + searchType);
            }
        }
        
        final SimpleJdbcTemplate simpleJdbcTemplate = this.getSimpleJdbcTemplate();
        
        final Map<String, Object> args = new HashMap<String, Object>();
        args.put(this.memberIdParam, idQuery);
        
        try {
            final List<EntityIdentifier> memberIdentifiers = simpleJdbcTemplate.query(searchForMemberQuery, MemberEntityIdentifierRowMapper.INSTANCE, args);
            return memberIdentifiers;
        }
        catch (DataAccessException dae) {
            this.logger.warn("searchForMembers query failed for idQuery=" + idQuery +", failing safe and returning an empty Collection", dae);
            return Collections.emptyList();
        }
    }
}
