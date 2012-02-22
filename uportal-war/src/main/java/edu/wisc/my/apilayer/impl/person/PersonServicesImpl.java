/**
 * Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
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
package edu.wisc.my.apilayer.impl.person;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.IUserIdentityStore;
import org.jasig.portal.security.PersonFactory;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import edu.wisc.my.apilayer.internal.IPersonServices;
import edu.wisc.my.apilayer.person.IPerson;


/**
 * Provides the implementation of the user searching abilities specified
 * by {@link edu.wisc.my.apilayer.internal.IPersonServices}. Most of these
 * functions map to the {@link org.jasig.portal.services.PersonDirectory}
 * service.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.1 $
 */
public class PersonServicesImpl implements IPersonServices {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IPersonAttributeDao personAttributeDao;
    private IUserIdentityStore userIdentityStore;
    
    @Autowired
    public void setUserIdentityStore(IUserIdentityStore userIdentityStore) {
        this.userIdentityStore = userIdentityStore;
    }
    /**
     * @return the personAttributeDao
     */
    public IPersonAttributeDao getPersonAttributeDao() {
        return personAttributeDao;
    }
    
    /**
     * @param personAttributeDao the personAttributeDao to set
     */
    @Required
    public void setPersonAttributeDao(IPersonAttributeDao personAttributeDao) {
        Validate.notNull(personAttributeDao);
        this.personAttributeDao = personAttributeDao;
    }


    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IPersonServices#getPersonByUserName(java.lang.String)
     */
    public IPerson getPersonByUserName(final String userName) {
        final Map<String, List<Object>> userAttributes = this.personAttributeDao.getMultivaluedUserAttributes(userName);
        if (userAttributes == null) {
            //No person was found according to the IPersonAttributeDao contract
            return null;
        }
        
        return this.createPerson(userName, userAttributes);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IPersonServices#getPersonByKey(java.lang.String)
     */
    public IPerson getPersonByKey(final String key) {
        final int personId;
        try {
            personId = Integer.parseInt(key);
        }
        catch (final NumberFormatException nfe) {
            this.logger.warn("Invalid user key specified: " + key, nfe);
            return null;
        }
        
        final String userName = this.userIdentityStore.getPortalUserName(personId);
        if (userName == null) {
            return null;
        }
        
        return this.getPersonByUserName(userName);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IPersonServices#getPerson(java.util.Map)
     */
    public IPerson getPerson(final Map<String, List<Object>> queryMap) {
        final Map<String, List<Object>> userAttributes = this.personAttributeDao.getMultivaluedUserAttributes(queryMap);
        if (userAttributes == null) {
            //No person was found according to the IPersonAttributeDao contract
            return null;
        }
        
        return this.createPerson(null, userAttributes);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IPersonServices#getPerson(java.lang.String, java.lang.String)
     */
    public IPerson getPerson(final String queryAttr, final String queryVal) {
        final Map<String, List<Object>> queryMap = new HashMap<String, List<Object>>();
        queryMap.put(queryAttr, Collections.singletonList((Object)queryVal));

        return this.getPerson(queryMap);
    }

    /**
     * Creates and populates an IPerson for the userName and attributes
     */
    protected IPerson createPerson(final String userName, final Map<String, List<Object>> userAttributes) {
        final org.jasig.portal.security.IPerson person = PersonFactory.createRestrictedPerson();
        
        //Add the attributes to the person
        person.setAttributes(userAttributes);
        
        if (userName != null) {
            //Set the user name for the person
            person.setAttribute(org.jasig.portal.security.IPerson.USERNAME, userName);
        }

        //Try to get the portal user id
        this.populatePortalId(person);

        return new PersonImpl(person);
    }
    
    /**
     * Attempts to populate the user id for the person
     */
    protected void populatePortalId(final org.jasig.portal.security.IPerson person) {
        try {
            final int pid = this.userIdentityStore.getPortalUID(person, false);
            person.setID(pid);
        }
        catch (Exception e) {
            this.logger.warn("Error getting portal user ID for IPerson: " + person.getAttribute(org.jasig.portal.security.IPerson.USERNAME));
        }
    }
}