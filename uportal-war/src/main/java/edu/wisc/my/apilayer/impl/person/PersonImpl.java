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
import java.util.Enumeration;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.apilayer.groups.IEntityIdentifier;
import edu.wisc.my.apilayer.impl.groups.GroupServiceProxyUtils;
import edu.wisc.my.apilayer.person.IPerson;


/**
 * An implementation of the API layer's {@link edu.wisc.my.apilayer.person.IPerson}
 * which wraps a uPortal {@link org.jasig.portal.security.IPerson}.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.1 $
 */
public class PersonImpl implements IPerson {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    final org.jasig.portal.security.IPerson wrappedPerson;
    
    /**
     * Creates a new wrapper around the specified {@link org.jasig.portal.security.IPerson}.
     * 
     * @param person The {@link org.jasig.portal.security.IPerson} to wrap.
     */
    public PersonImpl(final org.jasig.portal.security.IPerson person) {
        this.wrappedPerson = person;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getKey()
     */
    public String getKey() {
        final int pId = this.wrappedPerson.getID();

        if (pId == -1) {
            return null;
        }

        return Integer.toString(pId);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getUserName()
     */
    public String getUserName() {
        return (String)this.wrappedPerson.getAttribute(org.jasig.portal.security.IPerson.USERNAME);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getAttributeValues(java.lang.String)
     */
    public Object[] getAttributeValues(String attributeName) {
        return this.wrappedPerson.getAttributeValues(attributeName);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getAttributeValue(java.lang.String)
     */
    public Object getAttributeValue(String attributeName) {
        return this.wrappedPerson.getAttribute(attributeName);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getAttributeNames()
     */
    public Enumeration<String> getAttributeNames() {
        final Enumeration<String> attrEnum = this.wrappedPerson.getAttributeNames();
        
        if (attrEnum == null) {
            final Set<String> emptySet = Collections.emptySet();
            return Collections.enumeration(emptySet);
        }

        return attrEnum;
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.person.IPerson#getIdentifier()
     */
    public IEntityIdentifier getIdentifier() {
        return (IEntityIdentifier)GroupServiceProxyUtils.wrap(this.wrappedPerson.getEntityIdentifier());
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.groups.IBasicEntity#getEntityIdentifier()
     */
    public IEntityIdentifier getEntityIdentifier() {
        return this.getIdentifier();
    }
}
