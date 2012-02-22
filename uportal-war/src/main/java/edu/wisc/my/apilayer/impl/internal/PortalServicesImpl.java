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
package edu.wisc.my.apilayer.impl.internal;


import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import edu.wisc.my.apilayer.impl.groups.GroupServicesImpl;
import edu.wisc.my.apilayer.internal.IDatabaseServices;
import edu.wisc.my.apilayer.internal.IGroupServices;
import edu.wisc.my.apilayer.internal.ILdapServices;
import edu.wisc.my.apilayer.internal.IPersonServices;
import edu.wisc.my.apilayer.internal.IPortalServices;
import edu.wisc.my.apilayer.internal.PortalServicesLocator;


/**
 * uPortal impl of the IPortalServices interface. Provides access to the
 * uPortal implementations of the different service interfaces.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision $
 */
public class PortalServicesImpl implements IPortalServices, InitializingBean, DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IDatabaseServices databaseServices;
    private ILdapServices ldapServices;
    private IPersonServices personServices;
    private IGroupServices groupServices = new GroupServicesImpl();

    /**
     * @see edu.wisc.my.apilayer.internal.IPortalServices#getDatabaseServices()
     */
    public IDatabaseServices getDatabaseServices() {
        return this.databaseServices;
    }

    /**
     * @see edu.wisc.my.apilayer.internal.IPortalServices#getLdapServices()
     */
    public ILdapServices getLdapServices() {
        return this.ldapServices;
    }

    /**
     * @see edu.wisc.my.apilayer.internal.IPortalServices#getPersonServices()
     */
    public IPersonServices getPersonServices() {
        return this.personServices;
    }

    /**
     * @see edu.wisc.my.apilayer.internal.IPortalServices#getGroupServices()
     */
    public IGroupServices getGroupServices() {
        return this.groupServices;
    }
    

    /**
     * @param databaseServices the databaseServices to set
     */
    @Required
    public void setDatabaseServices(IDatabaseServices databaseServices) {
        Validate.notNull(databaseServices);
        this.databaseServices = databaseServices;
    }

    /**
     * @param ldapServices the ldapServices to set
     */
    @Required
    public void setLdapServices(ILdapServices ldapServices) {
        Validate.notNull(ldapServices);
        this.ldapServices = ldapServices;
    }

    /**
     * @param personServices the personServices to set
     */
    @Required
    public void setPersonServices(IPersonServices personServices) {
        Validate.notNull(personServices);
        this.personServices = personServices;
    }

    /**
     * @param groupServices the groupServices to set
     */
    public void setGroupServices(IGroupServices groupServices) {
        Validate.notNull(groupServices);
        this.groupServices = groupServices;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        this.logger.debug("Initializing PortalServicesLocator");
        PortalServicesLocator.setPortalServices(this);
        this.logger.info("Initialized PortalServicesLocator");
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.logger.debug("UnInitializing PortalServicesLocator");
        PortalServicesLocator.setPortalServices(null);
        this.logger.info("UnInitialized PortalServicesLocator");
    }
}
