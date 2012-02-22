/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.dao;

import java.util.Collection;
import java.util.List;

import edu.wisc.my.portlet.ad.om.AccessRecord;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public interface AccessRecordDao {
    public AccessRecord createAccessRecord(String username);
    
    public void storeAccessRecord(AccessRecord record);
    
    public AccessRecord getAccessRecordByKey(String referenceKey);
    
    public Collection<AccessRecord> getAccessRecordsForUser(String username);
    
    public List<AccessRecord> getAccessRecordsRange(int startPosition, int maxResults);
}
