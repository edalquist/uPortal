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
package edu.wisc.my.apilayer.impl.groups;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier;


/**
 * Wraps a uPortal {@link org.jasig.portal.groups.CompositeEntityIdentifier} so API layer
 * client can use it.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.1 $
 * @see org.jasig.portal.groups.CompositeEntityIdentifier
 * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier
 */
public class CompositeEntityIdentifierWrapper extends EntityIdentifierWrapper implements ICompositeEntityIdentifier, Wrapper {
    /** Reference to the wrapped composite entity identifier */
    private final org.jasig.portal.groups.CompositeEntityIdentifier wrappedCompositeEntityIdentifier;
    
    /**
     * Create new wrapper.
     * 
     * @param eg The composite entity identifier to wrap.
     */
    public CompositeEntityIdentifierWrapper(final org.jasig.portal.groups.CompositeEntityIdentifier ceid) {
        super(ceid);

        if (ceid == null)
            throw new IllegalArgumentException("CompositeEntityIdentifier cannot be null");
        
        this.wrappedCompositeEntityIdentifier = ceid;
    }
    
    /**
     * Provides access to the wrapped uPortal class for other classes in this
     * package.
     * 
     * @return The wrapped uPortal class.
     */
    protected org.jasig.portal.groups.CompositeEntityIdentifier getWrappedCompositeEntityIdentifier() {
        return this.wrappedCompositeEntityIdentifier;
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.impl.groups.EntityIdentifierWrapper#getWrappedObject()
     */
    @Override
    public Object getWrappedObject() {
        return this.wrappedCompositeEntityIdentifier;
    }
    
    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#getLocalKey()
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#getLocalKey()
     */
    public String getLocalKey() {
        return this.wrappedCompositeEntityIdentifier.getLocalKey();
    }

    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#getServiceName()
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#getServiceName()
     */
    public Name getServiceName() {
        return this.wrappedCompositeEntityIdentifier.getServiceName();
    }

    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#popNode()
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#popNode()
     */
    public String popNode() throws InvalidNameException {
        return this.wrappedCompositeEntityIdentifier.popNode();
    }

    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#pushNode(String)
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#pushNode(java.lang.String)
     */
    public Name pushNode(final String newNode) throws InvalidNameException {
        return this.wrappedCompositeEntityIdentifier.pushNode(newNode);
    }

    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#setCompositeKey(Name)
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#setCompositeKey(javax.naming.Name)
     */
    public void setCompositeKey(final Name newCompositeKey) {
        this.wrappedCompositeEntityIdentifier.setCompositeKey(newCompositeKey);
    }

    /**
     * @see org.jasig.portal.groups.CompositeEntityIdentifier#setServiceName(Name)
     * @see edu.wisc.my.apilayer.groups.ICompositeEntityIdentifier#setServiceName(javax.naming.Name)
     */
    public void setServiceName(final Name newServiceName) throws InvalidNameException {
        this.wrappedCompositeEntityIdentifier.setServiceName(newServiceName);
    }

}