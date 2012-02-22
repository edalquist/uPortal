/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups.east;

import java.util.Collection;
import java.util.Set;

import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;

import edu.wisc.my.groups.SearchType;


/**
 * Defines operations on a data store needed by the FlatGroupStore.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
public interface FlatGroupStoreDao {
    /**
     * Returns true if the memberId is a member of the group specified by the groupId
     * 
     * @param groupId ID of the group to check against
     * @param memberId ID of the group member to check for, can not be null.
     * @return true if the member is the in group, false if not.
     */
    public boolean isMemberInGroup(long groupId, String memberId);
    
    /**
     * Returns the IEntityGroup for the groupId, null if no group exists for the id.
     * 
     * @param groupId ID of the group to find.
     * @return The group for the ID or null if no group exists for the ID.
     */
    public IEntityGroup getGroup(long groupId);
    
    /**
     * Gets all the parent IEntityGroups for a specific member id.
     * 
     * @param memberId The id of the member to get parent groups for.
     * @return Set of the parent groups, empty set if no parent groups or no member for the member id are found. Will not be null.
     */
    public Set<IEntityGroup> getParentGroups(String memberId);
    
    /**
     * Get all the IEntityGroups in the store.
     * 
     * @return Set of all groups, empty set if there are no groups in the store. Will not be null.
     */
    public Set<IEntityGroup> getGroups();
    
    /**
     * Gets the set of members of group specified by the id. 
     * 
     * @param groupId The id of the group to get members of.
     * @return Set of the members of the group, empty set if no mebmers exists or if no group exsits for the id. Will not be null.
     */
    public Set<IEntity> getGroupMembers(long groupId);
    
    
    /**
     * Finds all groups with names that match the specified query and search type.
     * 
     * @param nameQuery The name or partial name to search against groups name with.
     * @param searchType The type of search to be done.
     * @return A Collection of group identifiers that match the query, empty collection if no matching groups are found. Will not be null.
     */
    public Collection<EntityIdentifier> searchForGroups(String nameQuery, SearchType searchType);
    
    /**
     * Finds all members with names that match the specified query and search type.
     * 
     * @param nameQuery The name or partial name to search against members id with.
     * @param searchType The type of search to be done.
     * @return A Collection of member identifiers that match the query, empty collection if no matching members are found. Will not be null.
     */
    public Collection<EntityIdentifier> searchForMembers(String idQuery, SearchType searchType);
}
