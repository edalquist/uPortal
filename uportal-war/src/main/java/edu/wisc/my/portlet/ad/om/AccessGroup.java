/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.om;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * group memebership information
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
@Entity
@Table(name = "AD_GROUP")
@GenericGenerator(
        name = "AD_GROUP_ID_GEN",
        strategy =  "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
                @Parameter(name="sequence_name", value="ad_group_seq"),
                @Parameter(name="increment_size", value="10"),
                @Parameter(name="optimizer", value="pooled")
                     }
    )
public class AccessGroup {
    @Id
    @GeneratedValue(generator = "AD_GROUP_ID_GEN")
    @Column(name = "GROUP_ID")
    private final long id;
    
    @Column(name="KEY", nullable = false, length = 500)
    private String key;
    
    @Column(name="NAME", nullable = false, length = 4000)
    private String name;
    
    public AccessGroup() {
        this.id = 0;
    }
    
    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccessGroup other = (AccessGroup) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        }
        else if (!key.equals(other.key))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AccessGroup [id=" + id + ", key=" + key + ", name=" + name + "]";
    }
}
