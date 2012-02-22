/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.apilayer.impl.portlet;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import edu.wisc.my.apilayer.portlet.IScopeIdentifier;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public final class PortletScopeIdentifier implements IScopeIdentifier, Serializable {
    private static final long serialVersionUID = 1L;

    private final String systemIdentifier;
    private final String applicationIdentifier;
    private final String publishIdentifier;
    private final String instanceIdentifier;
    private final String userIdentifier;
    
    private final String toString;
    private final int hashCode;
    
    public PortletScopeIdentifier(final String appId, final int pubId, final String instId, final String userId) {
        this.systemIdentifier = "uPortal";
        this.applicationIdentifier = appId;
        this.publishIdentifier = Integer.toString(pubId);
        this.instanceIdentifier = instId;
        this.userIdentifier = userId;

        this.toString = this.systemIdentifier + "." +
            this.applicationIdentifier + "." +
            this.publishIdentifier + "." +
            this.instanceIdentifier + "." +
            this.userIdentifier;
        
        this.hashCode = new HashCodeBuilder(-1725993431, -1196699485)
            .append(this.publishIdentifier)
            .append(this.applicationIdentifier)
            .append(this.systemIdentifier)
            .append(this.instanceIdentifier)
            .append(this.userIdentifier)
            .toHashCode();
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getApplicationIdentifier()
     */
    public String getApplicationIdentifier() {
        return this.applicationIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getInstanceIdentifier()
     */
    public String getInstanceIdentifier() {
        return this.instanceIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getPublishedIdentifier()
     */
    public String getPublishedIdentifier() {
        return this.publishIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getSystemIdentifier()
     */
    public String getSystemIdentifier() {
        return this.systemIdentifier;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getUserIdentifier()
     */
    public String getUserIdentifier() {
        return this.userIdentifier;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof PortletScopeIdentifier)) {
            return false;
        }
        final PortletScopeIdentifier rhs = (PortletScopeIdentifier) object;
        return new EqualsBuilder()
            .append(this.publishIdentifier, rhs.publishIdentifier)
            .append(this.applicationIdentifier, rhs.applicationIdentifier)
            .append(this.systemIdentifier, rhs.systemIdentifier)
            .append(this.instanceIdentifier, rhs.instanceIdentifier)
            .append(this.userIdentifier, rhs.userIdentifier)
            .isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.toString;
    }
}
