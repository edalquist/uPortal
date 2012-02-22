/* Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package edu.wisc.my.groups.util;

import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IEntityGroupStoreFactory;
import org.jasig.portal.groups.IEntitySearcher;
import org.jasig.portal.groups.IEntitySearcherFactory;
import org.jasig.portal.groups.IEntityStore;
import org.jasig.portal.groups.IEntityStoreFactory;

/**
 * Simply creates a single utilityGroupStore instance and returns it for all factory methods.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class UtilityGroupStoreFactory implements IEntityStoreFactory, IEntityGroupStoreFactory, IEntitySearcherFactory {
    public IEntityStore newEntityStore() throws GroupsException {
        return UtilityGroupStoreLocator.getUtilityGroupStore();
    }

    public IEntityGroupStore newGroupStore() throws GroupsException {
        return UtilityGroupStoreLocator.getUtilityGroupStore();
    }

    public IEntityGroupStore newGroupStore(ComponentGroupServiceDescriptor svcDescriptor) throws GroupsException {
        return UtilityGroupStoreLocator.getUtilityGroupStore();
    }

    public IEntitySearcher newEntitySearcher() throws GroupsException {
        return UtilityGroupStoreLocator.getUtilityGroupStore();
    }
}
