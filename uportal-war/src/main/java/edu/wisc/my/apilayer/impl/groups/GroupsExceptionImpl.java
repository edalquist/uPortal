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

import edu.wisc.my.apilayer.groups.GroupsException;


/**
 * Wraps a {@link org.jasig.portal.groups.GroupsException} so API layer
 * clients can interpret it.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.1 $
 */
public class GroupsExceptionImpl extends GroupsException {
    private static final long serialVersionUID = 1L;

    private final org.jasig.portal.groups.GroupsException wrappedException;
    
    /**
     * Create a new groups exception without overridding the original message.
     * 
     * @param ge The {@link org.jasig.portal.groups.GroupsException} to wrap.
     */
    public GroupsExceptionImpl(final org.jasig.portal.groups.GroupsException ge) {
        this(ge.getMessage(), ge);
    }
    
    /**
     * Create a new groups exception overridding the original message.
     * 
     * @param msg The message to override the GroupsException method with.
     * @param ge The {@link org.jasig.portal.groups.GroupsException} to wrap.
     */
    public GroupsExceptionImpl(final String msg, final org.jasig.portal.groups.GroupsException ge) {
        super(msg, ge);
        
        if (this.wrappedException == null)
            throw new IllegalArgumentException("Causing GroupsException cannot be null");
        
        this.wrappedException = ge;
    }
    
    /**
     * @see edu.wisc.my.apilayer.groups.GroupsException#getExceptionCode()
     */
    @Override
    public int getExceptionCode() {
        return 0;
    }
}
