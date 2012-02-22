/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.apilayer.impl.groups;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.security.IPerson;

import edu.wisc.my.apilayer.groups.IEntityIdentifier;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
public class GroupServiceProxyUtilsTest extends TestCase {
    public void testEntityImplWrapping() throws Exception {
        final MyEntityImpl adminEntityImpl = new MyEntityImpl();

        final edu.wisc.my.apilayer.groups.IGroupMember wrappedAdminEntityImpl = (edu.wisc.my.apilayer.groups.IGroupMember)GroupServiceProxyUtils.wrap(adminEntityImpl);
        
        assertEquals(5, wrappedAdminEntityImpl.getClass().getInterfaces().length);
        assertTrue(wrappedAdminEntityImpl instanceof Wrapper);
        assertTrue(wrappedAdminEntityImpl instanceof Comparable<?>);
        assertTrue(wrappedAdminEntityImpl instanceof edu.wisc.my.apilayer.groups.IEntity);
        
        assertEquals(adminEntityImpl.toString(), wrappedAdminEntityImpl.toString());
        assertEquals(adminEntityImpl.hashCode(), wrappedAdminEntityImpl.hashCode());
        assertFalse(adminEntityImpl.equals(wrappedAdminEntityImpl));
        assertFalse(wrappedAdminEntityImpl.equals(adminEntityImpl));
        
        assertFalse(wrappedAdminEntityImpl.contains(null));
        assertEquals(edu.wisc.my.apilayer.person.IPerson.class, wrappedAdminEntityImpl.getEntityType());
        
        
        final EntityIdentifierWrapper expectedEntityIdentifier = new EntityIdentifierWrapper(new EntityIdentifier("admin", IPerson.class));
        final IEntityIdentifier actualEntityIdentifier = wrappedAdminEntityImpl.getEntityIdentifier();
        
        assertEquals("admin", actualEntityIdentifier.getKey());
        assertEquals(expectedEntityIdentifier.getKey(), actualEntityIdentifier.getKey());
        assertEquals(edu.wisc.my.apilayer.person.IPerson.class, actualEntityIdentifier.getType());
        assertEquals(expectedEntityIdentifier.getType(), actualEntityIdentifier.getType());
        assertEquals(expectedEntityIdentifier.hashCode(), actualEntityIdentifier.hashCode());
        assertEquals(expectedEntityIdentifier.toString(), actualEntityIdentifier.toString());
        
        
        final Iterator<edu.wisc.my.apilayer.groups.IEntityGroup> actualContainingGroups = wrappedAdminEntityImpl.getAllContainingGroups();
        
        assertTrue(actualContainingGroups.hasNext());
        final edu.wisc.my.apilayer.groups.IEntityGroup group1 = actualContainingGroups.next();
        assertEquals("entityGroup1", group1.getKey());
        
        assertTrue(actualContainingGroups.hasNext());
        final edu.wisc.my.apilayer.groups.IEntityGroup group2 = actualContainingGroups.next();
        assertEquals("entityGroup2", group2.getKey());
        
        assertFalse(actualContainingGroups.hasNext());
        
        
        assertEquals(1337, ((Comparable<?>)wrappedAdminEntityImpl).compareTo(null));
        
        
        
        final IGroupMember realGroupMemeber = EasyMock.createMock(IGroupMember.class);
        EasyMock.expect(realGroupMemeber.getKey()).andReturn("parent");
        EasyMock.replay(realGroupMemeber);
        
        final edu.wisc.my.apilayer.groups.IGroupMember wrappedGroupMemeber = (edu.wisc.my.apilayer.groups.IGroupMember)GroupServiceProxyUtils.wrap(realGroupMemeber);
        assertTrue(wrappedAdminEntityImpl.isMemberOf(wrappedGroupMemeber));
    }
    
    public void testArrayWrapping() throws Exception {
        final EntityIdentifier[] realIds = new EntityIdentifier[0];

        final Object[] wrappedIds = GroupServiceProxyUtils.wrap(realIds);
        assertEquals(IEntityIdentifier.class, wrappedIds.getClass().getComponentType());
        
        final Object[] originalIds = GroupServiceProxyUtils.unwrap(wrappedIds);
        assertEquals(EntityIdentifier.class, originalIds.getClass().getComponentType());
    }
    
    private class MyEntityImpl implements IEntity, IGroupMember, Comparable<Object> {
        
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            return 1337;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#contains(org.jasig.portal.groups.IGroupMember)
         */
        public boolean contains(IGroupMember gm) throws GroupsException {
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#deepContains(org.jasig.portal.groups.IGroupMember)
         */
        public boolean deepContains(IGroupMember gm) throws GroupsException {
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getAllContainingGroups()
         */
        public Iterator getAllContainingGroups() throws GroupsException {
            final List<IEntityGroup> groups = new ArrayList<IEntityGroup>();
            
            final IEntityGroup entityGroup1 = EasyMock.createMock(IEntityGroup.class);
            EasyMock.expect(entityGroup1.getKey()).andReturn("entityGroup1");
            groups.add(entityGroup1);
            
            final IEntityGroup entityGroup2 = EasyMock.createMock(IEntityGroup.class);
            EasyMock.expect(entityGroup2.getKey()).andReturn("entityGroup2");
            groups.add(entityGroup2);

            EasyMock.replay(groups.toArray());
            return groups.iterator();
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getAllEntities()
         */
        public Iterator getAllEntities() throws GroupsException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getAllMembers()
         */
        public Iterator getAllMembers() throws GroupsException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getContainingGroups()
         */
        public Iterator getContainingGroups() throws GroupsException {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getEntities()
         */
        public Iterator getEntities() throws GroupsException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getEntityType()
         */
        public Class getEntityType() {
            return IPerson.class;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getKey()
         */
        public String getKey() {
            return "admin";
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getLeafType()
         */
        public Class getLeafType() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getMemberGroupNamed(java.lang.String)
         */
        public IEntityGroup getMemberGroupNamed(String name) throws GroupsException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getMembers()
         */
        public Iterator getMembers() throws GroupsException {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getType()
         */
        public Class getType() {
            return IPerson.class;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#getUnderlyingEntityIdentifier()
         */
        public EntityIdentifier getUnderlyingEntityIdentifier() {
            return null;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#hasMembers()
         */
        public boolean hasMembers() throws GroupsException {
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#isDeepMemberOf(org.jasig.portal.groups.IGroupMember)
         */
        public boolean isDeepMemberOf(IGroupMember gm) throws GroupsException {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#isEntity()
         */
        public boolean isEntity() {
            return true;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#isGroup()
         */
        public boolean isGroup() {
            return false;
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.groups.IGroupMember#isMemberOf(org.jasig.portal.groups.IGroupMember)
         */
        public boolean isMemberOf(IGroupMember gm) throws GroupsException {
            return gm != null && gm.getKey().equals("parent");
        }

        /* (non-Javadoc)
         * @see org.jasig.portal.IBasicEntity#getEntityIdentifier()
         */
        public EntityIdentifier getEntityIdentifier() {
            return new EntityIdentifier("admin", IPerson.class);
        }
    }
}
