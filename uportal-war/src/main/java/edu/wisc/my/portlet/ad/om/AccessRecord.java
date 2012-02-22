/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.om;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name = "AD_RECORD")
@GenericGenerator(
        name = "AD_RECORD_ID_GEN",
        strategy =  "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                @Parameter(name="sequence_name", value="ad_record_seq"),
                @Parameter(name="increment_size", value="10"),
                @Parameter(name="optimizer", value="pooled")
                     }
    )
@NamedQueries ({
    @NamedQuery(
            name="AccessRecord.getByReferenceKey",
            hints= {
                    @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                    @QueryHint(name = "org.hibernate.cacheRegion", value = "edu.wisc.my.portlet.ad.om.AccessRecord.getByReferenceKey")
            },
            query=  "SELECT ar " +
                    "FROM AccessRecord ar " +
                    "WHERE ar.referenceKey = :referenceKey"),
    @NamedQuery(
            name="AccessRecord.getByUsername",
            hints= {
                    @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                    @QueryHint(name = "org.hibernate.cacheRegion", value = "edu.wisc.my.portlet.ad.om.AccessRecord.username")
            },
            query=  "SELECT ar " +
                    "FROM AccessRecord ar " +
                    "WHERE ar.username = :username"),
    @NamedQuery(
            name="AccessRecord.getMostRecent",
            hints= {
                    @QueryHint(name = "org.hibernate.cacheable", value = "true"),
                    @QueryHint(name = "org.hibernate.cacheRegion", value = "edu.wisc.my.portlet.ad.om.AccessRecord.mostRecent")
            },
            query=  "SELECT ar " +
                    "FROM AccessRecord ar " +
                    "ORDER BY ar.created desc")
})
public class AccessRecord {
    @Id
    @GeneratedValue(generator = "AD_RECORD_ID_GEN")
    @Column(name = "RECORD_ID")
    private final long id;
    
    @Index(name="IDX_ACCESS_RECORD_REFKEY")
    @Column(name = "REFERENCE_KEY", nullable=false, unique=true)
    private final String referenceKey;
    
    @Index(name="IDX_ACCESS_RECORD_USERNAME")
    @Column(name = "USERNAME", nullable=false)
    private final String username;
    
    @Index(name="IDX_ACCESS_RECORD_CREATED")
    @Column(name = "CREATED", nullable=false)
    private final Date created;
    
    @OneToMany(cascade = CascadeType.ALL, targetEntity = AccessGroup.class, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "RECORD_ID")
    @Fetch(FetchMode.JOIN)
    private Collection<AccessGroup> groups = new LinkedList<AccessGroup>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "AD_ATTRIBUTE", joinColumns = @JoinColumn(name = "RECORD_ID"))
    @MapKey(name = "NAME")
    @Column(name = "VALUE")
    @Fetch(FetchMode.JOIN)
    private Map<String, String> attributes = new LinkedHashMap<String, String>();
    

    /**
     * Constructor for Hiberbate 
     */
    AccessRecord() {
        this.id = 0;
        this.referenceKey = null;
        this.username = null;
        this.created = null;
    }

    public AccessRecord(String referenceKey, String username) {
        this.id = 0;
        this.referenceKey = referenceKey;
        this.username = username;
        this.created = new Date();
    }
    
    public String getReferenceKey() {
        return referenceKey;
    }
    public String getUsername() {
        return username;
    }
    public Date getCreated() {
        return created;
    }
    public Collection<AccessGroup> getGroups() {
        return groups;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceKey == null) ? 0 : referenceKey.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccessRecord other = (AccessRecord) obj;
        if (referenceKey == null) {
            if (other.referenceKey != null)
                return false;
        }
        else if (!referenceKey.equals(other.referenceKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccessRecord [id=" + id + ", referenceKey=" + referenceKey + ", username=" + username + ", created="
                + created + ", groups=" + groups + ", attributes=" + attributes + "]";
    }
}
