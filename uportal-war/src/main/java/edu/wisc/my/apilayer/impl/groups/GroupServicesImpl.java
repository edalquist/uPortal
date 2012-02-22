/**
 * Copyright 2005 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.wisc.my.apilayer.impl.groups;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.services.GroupService;

import edu.wisc.my.apilayer.groups.GroupsException;
import edu.wisc.my.apilayer.groups.IBasicEntity;
import edu.wisc.my.apilayer.groups.ICompositeGroupService;
import edu.wisc.my.apilayer.groups.IEntity;
import edu.wisc.my.apilayer.groups.IEntityGroup;
import edu.wisc.my.apilayer.groups.IEntityIdentifier;
import edu.wisc.my.apilayer.groups.IGroupMember;
import edu.wisc.my.apilayer.groups.IGroupService;
import edu.wisc.my.apilayer.groups.ILockableEntityGroup;
import edu.wisc.my.apilayer.groups.SearchMethod;
import edu.wisc.my.apilayer.internal.IGroupServices;


/**
 * Provies wrapped access to the uPortal {@link org.jasig.portal.services.GroupService}
 * class for API layer clients using {@link edu.wisc.my.apilayer.groups.GroupService}.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.2 $
 * @see org.jasig.portal.services.GroupService
 * @see edu.wisc.my.apilayer.groups.GroupService
 * @since 1.2
 */
public class GroupServicesImpl implements IGroupServices {
    
    /**
     * @see GroupService#findGroup(String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#findGroup(java.lang.String)
     */
    public IEntityGroup findGroup(final String name) throws GroupsException {
        try {
            final org.jasig.portal.groups.IEntityGroup realGroup = GroupService.findGroup(name);
            
            return (IEntityGroup)GroupServiceProxyUtils.wrap(realGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getEntity(String, Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getEntity(java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IEntity getEntity(final String key, final Class<? extends IBasicEntity> type) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);

            final org.jasig.portal.groups.IEntity realEntity = GroupService.getEntity(key, realType);
            
            return (IEntity)GroupServiceProxyUtils.wrap(realEntity);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getGroupMember(String, Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getGroupMember(java.lang.String, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IGroupMember getGroupMember(final String key, final Class<? extends IBasicEntity> type) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            
            final org.jasig.portal.groups.IGroupMember realGroupMember = GroupService.getGroupMember(key, realType);
            
            return (IGroupMember)GroupServiceProxyUtils.wrap(realGroupMember);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getGroupMember(EntityIdentifier)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getGroupMember(edu.wisc.my.apilayer.groups.IEntityIdentifier)
     */
    public IGroupMember getGroupMember(final IEntityIdentifier underlyingEntityIdentifier) throws GroupsException {
        try {
            final EntityIdentifier realEntityIdentifier = (EntityIdentifier)GroupServiceProxyUtils.unwrap(underlyingEntityIdentifier);
            
            final org.jasig.portal.groups.IGroupMember realGroupMemeber = GroupService.getGroupMember(realEntityIdentifier);

            return (IGroupMember)GroupServiceProxyUtils.wrap(realGroupMemeber);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#newGroup(Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#newGroup(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IEntityGroup newGroup(final Class<? extends IBasicEntity> type) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            
            final org.jasig.portal.groups.IEntityGroup realEntityGroup = GroupService.newGroup(realType);
            
            return (IEntityGroup)GroupServiceProxyUtils.wrap(realEntityGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#searchForGroups(String, int, Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#searchForGroups(java.lang.String, edu.wisc.my.apilayer.groups.SearchMethod, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IEntityIdentifier[] searchForGroups(final String query, final SearchMethod method, final Class<? extends IBasicEntity> leaftype) throws GroupsException {
        try {
            final int methodNum = GroupServiceProxyUtils.mapSearchMethod(method);
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(leaftype);

            final EntityIdentifier[] realGroupIdentifiers = GroupService.searchForGroups(query, methodNum, realType);
            
            return (IEntityIdentifier[])GroupServiceProxyUtils.wrap(realGroupIdentifiers);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#searchForGroups(String, int, Class, IEntityGroup)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#searchForGroups(java.lang.String, edu.wisc.my.apilayer.groups.SearchMethod, java.lang.Class, edu.wisc.my.apilayer.groups.IEntityGroup)
     */
    @SuppressWarnings("unchecked")
    public IEntityIdentifier[] searchForGroups(final String query, final SearchMethod method, final Class<? extends IBasicEntity> leaftype, final IEntityGroup ancestor) throws GroupsException {
        try {
            final int methodNum = GroupServiceProxyUtils.mapSearchMethod(method);
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(leaftype);
            final org.jasig.portal.groups.IEntityGroup realAncestor = (org.jasig.portal.groups.IEntityGroup)GroupServiceProxyUtils.unwrap(ancestor);

            final EntityIdentifier[] realGroupIdentifiers = GroupService.searchForGroups(query, methodNum, realType, realAncestor);

            return (IEntityIdentifier[])GroupServiceProxyUtils.wrap(realGroupIdentifiers);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#searchForEntities(String, int, Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#searchForEntities(java.lang.String, edu.wisc.my.apilayer.groups.SearchMethod, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IEntityIdentifier[] searchForEntities(final String query, final SearchMethod method, final Class<? extends IBasicEntity> type) throws GroupsException {
        try {
            final int methodNum = GroupServiceProxyUtils.mapSearchMethod(method);
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            
            final EntityIdentifier[] realEntityIdentifiers = GroupService.searchForEntities(query, methodNum, realType);
            
            return (IEntityIdentifier[])GroupServiceProxyUtils.wrap(realEntityIdentifiers);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#searchForEntities(String, int, Class, IEntityGroup)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#searchForEntities(java.lang.String, edu.wisc.my.apilayer.groups.SearchMethod, java.lang.Class, edu.wisc.my.apilayer.groups.IEntityGroup)
     */
    @SuppressWarnings("unchecked")
    public IEntityIdentifier[] searchForEntities(final String query, final SearchMethod method, final Class<? extends IBasicEntity> type, final IEntityGroup ancestor) throws GroupsException {
        try {
            final int methodNum = GroupServiceProxyUtils.mapSearchMethod(method);
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            final org.jasig.portal.groups.IEntityGroup realAncestor = (org.jasig.portal.groups.IEntityGroup)GroupServiceProxyUtils.unwrap(ancestor);

            final EntityIdentifier[] realEntityIdentifiers = GroupService.searchForEntities(query, methodNum, realType, realAncestor);

            return (IEntityIdentifier[])GroupServiceProxyUtils.wrap(realEntityIdentifiers);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#findLockableGroup(String, String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#findLockableGroup(java.lang.String, java.lang.String)
     */
    public ILockableEntityGroup findLockableGroup(final String key, final String lockOwner) throws GroupsException {
        try {
            final org.jasig.portal.groups.ILockableEntityGroup realLockableEntityGroup = GroupService.findLockableGroup(key, lockOwner);

            return (ILockableEntityGroup)GroupServiceProxyUtils.wrap(realLockableEntityGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getCompositeGroupService()
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getCompositeGroupService()
     */
    public ICompositeGroupService getCompositeGroupService() throws GroupsException {
        try {
            final org.jasig.portal.groups.ICompositeGroupService realCompositeGroupService = GroupService.getCompositeGroupService();

            return (ICompositeGroupService)GroupServiceProxyUtils.wrap(realCompositeGroupService);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getDistinguishedGroup(String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getDistinguishedGroup(java.lang.String)
     */
    public IEntityGroup getDistinguishedGroup(final String name) throws GroupsException {
        try {
            final org.jasig.portal.groups.IEntityGroup realEntityGroup = GroupService.getDistinguishedGroup(name);

            return (IEntityGroup)GroupServiceProxyUtils.wrap(realEntityGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getEntity(String, Class, String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getEntity(java.lang.String, java.lang.Class, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public IEntity getEntity(final String key, final Class<? extends IBasicEntity> type, final String service) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);

            final org.jasig.portal.groups.IEntity realEntity = GroupService.getEntity(key, realType, service);

            return (IEntity)GroupServiceProxyUtils.wrap(realEntity);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getGroupService()
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getGroupService()
     */
    @SuppressWarnings("deprecation")
    public IGroupService getGroupService() throws GroupsException {
        try {
            final org.jasig.portal.groups.IGroupService realGroupService = GroupService.getGroupService();

            return (IGroupService)GroupServiceProxyUtils.wrap(realGroupService);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getRootGroup(Class)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getRootGroup(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public IEntityGroup getRootGroup(final Class<? extends IBasicEntity> type) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            
            final org.jasig.portal.groups.IEntityGroup realEntityGroup = GroupService.getRootGroup(realType);

            return (IEntityGroup)GroupServiceProxyUtils.wrap(realEntityGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#isComposite()
     * @see edu.wisc.my.apilayer.internal.IGroupServices#isComposite()
     */
    public boolean isComposite() {
        return GroupService.isComposite();
    }

    /**
     * @see GroupService#newGroup(Class, String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#newGroup(java.lang.Class, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public IEntityGroup newGroup(final Class<? extends IBasicEntity> type, final String serviceName) throws GroupsException {
        try {
            final Class<? extends org.jasig.portal.IBasicEntity> realType = (Class<? extends org.jasig.portal.IBasicEntity>)GroupServiceProxyUtils.unwrap(type);
            
            final org.jasig.portal.groups.IEntityGroup realEntityGroup = GroupService.newGroup(realType, serviceName);

            return (IEntityGroup)GroupServiceProxyUtils.wrap(realEntityGroup);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#parseLocalKey(String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#parseLocalKey(java.lang.String)
     */
    public String parseLocalKey(final String compositeKey) throws InvalidNameException, GroupsException {
        try {
            return GroupService.parseLocalKey(compositeKey);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#parseServiceName(String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#parseServiceName(java.lang.String)
     */
    public Name parseServiceName(final String serviceName) throws InvalidNameException, GroupsException{
        try {
            return GroupService.parseServiceName(serviceName);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }

    /**
     * @see GroupService#getDistinguishedGroupKey(String)
     * @see edu.wisc.my.apilayer.internal.IGroupServices#getDistinguishedGroupKey(java.lang.String)
     */
    public String getDistinguishedGroupKey(final String name) throws GroupsException {
        try {
            final GroupService srvc = GroupService.instance();
            return srvc.getDistinguishedGroupKey(name);
        }
        catch (final org.jasig.portal.groups.GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
    }
}
