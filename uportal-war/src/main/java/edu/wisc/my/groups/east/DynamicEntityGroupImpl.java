/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license distributed with this
 * file and available online at http://www.uportal.org/license.html
 */

package edu.wisc.my.groups.east;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.IIndividualGroupService;

/**
 * Always consults the underlying local group service for queries about group membership
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class DynamicEntityGroupImpl extends EntityGroupImpl {

    public DynamicEntityGroupImpl(String groupKey, Class entityType) throws GroupsException {
        super(groupKey, entityType);
    }

    /**
     * Checks if <code>GroupMember</code> gm is a member of this.
     * 
     * @return boolean
     * @param gm org.jasig.portal.groups.IGroupMember
     */
    public boolean contains(IGroupMember gm) throws GroupsException {
        final IIndividualGroupService localGroupService = this.getLocalGroupService();
        return localGroupService.contains(this, gm);
    }

    /**
     * Returns an <code>Iterator</code> over the entities in our member <code>Collection</code>.
     * 
     * @return java.util.Iterator
     */
    protected java.util.Iterator getMemberEntities() throws GroupsException {
        final Set<IGroupMember> memberEntities = new HashSet<IGroupMember>();
        
        
        @SuppressWarnings("unchecked")
        final Iterator<IGroupMember> members = getMembers();
        
        for (final Iterator<IGroupMember> memberItr = members; memberItr.hasNext();) {
            final IGroupMember member = memberItr.next();
            
            if (member.isEntity()) {
                memberEntities.add(member);
            }
        }

        return memberEntities.iterator();
    }

    /**
     * Returns an <code>Iterator</code> over the entities in our member <code>Collection</code>.
     * 
     * @return java.util.Iterator
     */
    protected java.util.Iterator getMemberGroups() throws GroupsException {
        final Set<IGroupMember> memberGroups = new HashSet<IGroupMember>();
        
        @SuppressWarnings("unchecked")
        final Iterator<IGroupMember> members = getMembers();
        
        for (final Iterator<IGroupMember> memberItr = members; memberItr.hasNext();) {
            final IGroupMember member = memberItr.next();

            if (member.isGroup()) {
                memberGroups.add(member);
            }
        }
        
        return memberGroups.iterator();
    }

    /**
     * Returns an <code>Iterator</code> over the <code>GroupMembers</code> in our member
     * <code>Collection</code>.
     * 
     * @return java.util.Iterator
     */
    public java.util.Iterator getMembers() throws GroupsException {
        final IIndividualGroupService localGroupService = this.getLocalGroupService();
        return localGroupService.findMembers(this);
    }
}
