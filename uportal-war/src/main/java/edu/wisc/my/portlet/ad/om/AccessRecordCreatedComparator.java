/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.om;

import java.util.Comparator;
import java.util.Date;

/**
 * Compares two AccessRecords by date
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class AccessRecordCreatedComparator implements Comparator<AccessRecord> {
    public static final AccessRecordCreatedComparator DESCENDING = new AccessRecordCreatedComparator(false);
    public static final AccessRecordCreatedComparator ASCENDING = new AccessRecordCreatedComparator(true);
    
    private final int toggle;
    
    private AccessRecordCreatedComparator(boolean ascending) {
        this.toggle = ascending ? 1 : -1;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(AccessRecord o1, AccessRecord o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return this.toggle * -1;
        }
        
        final Date c1 = o1.getCreated();
        final Date c2 = o2.getCreated();
        if (c1 == c2) {
            return 0;
        }
        if (c1 == null) {
            return this.toggle * -1;
        }
        
        return this.toggle * c1.compareTo(c2);
    }

}
