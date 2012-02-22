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

import org.jasig.portal.EntityIdentifier;

import edu.wisc.my.apilayer.groups.IBasicEntity;
import edu.wisc.my.apilayer.groups.IEntityIdentifier;


/**
 * Wraps a uPortal {@link org.jasig.portal.EntityIdentifier} so API layer
 * client can use it.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.1 $
 * @see org.jasig.portal.EntityIdentifier
 * @see edu.wisc.my.apilayer.groups.IEntityIdentifier
 */
public class EntityIdentifierWrapper implements IEntityIdentifier, Wrapper {
    /** Reference to the wrapped entity identifier */
    private final EntityIdentifier wrappedEntityIdentifier;
    
    /**
     * Create new wrapper.
     * 
     * @param ei The entity identifier to wrap.
     */
    public EntityIdentifierWrapper(final EntityIdentifier ei) {
        if (ei == null)
            throw new IllegalArgumentException("IEntityIdentifier cannot be null");

        this.wrappedEntityIdentifier = ei;
    }
    
    /**
     * Provides access to the wrapped uPortal class for other classes in this
     * package.
     * 
     * @return The wrapped uPortal class.
     */
    protected EntityIdentifier getWrappedEntityIdentifier() {
        return this.wrappedEntityIdentifier;
    }
    
    /**
     * @see edu.wisc.my.apilayer.impl.groups.Wrapper#getWrappedObject()
     */
    public Object getWrappedObject() {
        return this.wrappedEntityIdentifier;
    }
    
    /**
     * @see edu.wisc.my.apilayer.groups.IEntityIdentifier#getKey()
     */
    public String getKey() {
        return this.wrappedEntityIdentifier.getKey();
    }

    /**
     * @see edu.wisc.my.apilayer.groups.IEntityIdentifier#getType()
     */
    @SuppressWarnings("unchecked")
    public Class<? extends IBasicEntity> getType() {
        return (Class<? extends IBasicEntity>)GroupServiceProxyUtils.wrap(this.wrappedEntityIdentifier.getType());
    }

    /**
     * @see EntityIdentifier#equals(Object)
     */
    @Override
    public boolean equals(final Object o) {
        final Object realObj = GroupServiceProxyUtils.unwrap(o);
        return this.wrappedEntityIdentifier.equals(realObj);
    }
    
    /**
     * @see EntityIdentifier#hashCode()
     */
    @Override
    public int hashCode() {
        return getType().hashCode() + getKey().hashCode();
    }

    /**
     * @see EntityIdentifier#toString()
     */
    @Override
    public String toString() {
        return "EntityIdentifier (" + getType() + "(" + getKey() + "))";
    }
}
