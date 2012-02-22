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
package edu.wisc.my.apilayer.impl.ldap;

import org.apache.commons.lang.Validate;
import org.jasig.portal.ldap.ILdapServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import edu.wisc.my.apilayer.internal.ILdapServices;


/**
 * Provides wrapped access to uPortal's {@link org.jasig.portal.ldap.LdapServices}
 * class.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision $
 * @since 1.0
 * @see edu.wisc.my.apilayer.internal.ILdapServices
 */
@SuppressWarnings("deprecation")
public class LdapServicesImpl implements ILdapServices, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private ILdapServer defaultLdapServer;
    private LdapServerImpl defaultLdapServerImpl;
    
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /**
     * @return the defaultLdapServer
     */
    public ILdapServer getDefaultLdapServer() {
        return defaultLdapServer;
    }
    /**
     * @param defaultLdapServer the defaultLdapServer to set
     */
//    @Required
    public void setDefaultLdapServer(ILdapServer defaultLdapServer) {
        Validate.notNull(defaultLdapServer);
        this.defaultLdapServer = defaultLdapServer;
        this.defaultLdapServerImpl = new LdapServerImpl(this.defaultLdapServer);
    }

    /**
     * @see edu.wisc.my.apilayer.internal.ILdapServices#getDefaultServer()
     */
    public edu.wisc.my.apilayer.ldap.ILdapServer getDefaultServer() {
        return this.defaultLdapServerImpl;
    }

    /**
     * @see edu.wisc.my.apilayer.internal.ILdapServices#getServer(java.lang.String)
     */
    public edu.wisc.my.apilayer.ldap.ILdapServer getServer(final String name) {
        ILdapServer ldapServer = null;
        try {
            ldapServer = (ILdapServer)this.applicationContext.getBean(name, ILdapServer.class);
        }
        catch (NoSuchBeanDefinitionException nsbde) {
            //Ignore the exception for not finding the named bean.
        }
        
        if (ldapServer == null) {
            return null;
        }

        return new LdapServerImpl(ldapServer);
    }

    /**
     * @see edu.wisc.my.apilayer.internal.ILdapServices#getServerNames()
     */
    public String[] getServerNames() {
        final String[] serverNames = this.applicationContext.getBeanNamesForType(ILdapServer.class);
        return serverNames;
    }
}
