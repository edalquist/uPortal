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

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.jasig.portal.ldap.ILdapServer;


/**
 * Wraps a {@link org.jasig.portal.ldap.ILdapServer} and delegates all
 * method calls to it, wrapping the uPortal specific classes with API layer
 * interface implementations before returning them.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision $
 * @since 1.0
 * @see edu.wisc.my.apilayer.ldap.ILdapServer
 */
@SuppressWarnings("deprecation")
public class LdapServerImpl implements edu.wisc.my.apilayer.ldap.ILdapServer {
    private final ILdapServer wrappedServer;
    
    /**
     * Creates a {@link LdapServerImpl} that wraps a uPortal
     * {@link org.jasig.portal.ldap.ILdapServer} instance and just delegates
     * the calls to the uPortal class.
     * 
     * @param server The {@link org.jasig.portal.ldap.ILdapServer} instance to wrap.
     * @since 1.0
     * @throws IllegalArgumentException if the specified {@link org.jasig.portal.ldap.ILdapServer} is null.
     */
    public LdapServerImpl(final ILdapServer server) {
        if (server == null) {
            throw new IllegalArgumentException("server cannot be null");
        }

        this.wrappedServer = server;
    }

    /**
     * @see edu.wisc.my.apilayer.ldap.ILdapServer#getConnection()
     */
    public DirContext getConnection() throws NamingException {
        return this.wrappedServer.getConnection();
    }

    /**
     * @see edu.wisc.my.apilayer.ldap.ILdapServer#releaseConnection(javax.naming.directory.DirContext)
     */
    public void releaseConnection(final DirContext ctx) {
        this.wrappedServer.releaseConnection(ctx);
    }

    /**
     * @see edu.wisc.my.apilayer.ldap.ILdapServer#getBaseDN()
     */
    public String getBaseDN() {
        return this.wrappedServer.getBaseDN();
    }

    /**
     * @see edu.wisc.my.apilayer.ldap.ILdapServer#getUidAttribute()
     */
    public String getUidAttribute() {
        return this.wrappedServer.getUidAttribute();
    }
}
