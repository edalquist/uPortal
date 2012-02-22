/*
 * Created on Oct 10, 2006
 *
 */
package edu.wisc.my.groups.pags.testers;

import org.apache.commons.lang.StringUtils;
import org.jasig.portal.groups.pags.testers.BaseAttributeTester;
import org.jasig.portal.security.IPerson;

/**
 * The tester returns true if the attribute is null or if there are
 * no values set for this attribute.
 * 
 * @author john.hare@doit.wisc.edu
 */
public class NoValuesTester extends BaseAttributeTester {

    public NoValuesTester(String attribute, String test) {
        super(attribute, test);
    }
    
    public boolean test(IPerson person) {
        //Get the list of values for the attribute
        Object[] vals = person.getAttributeValues(getAttributeName());
        
        return !containsNonEmptyString(vals);
    }
    
    public boolean containsNonEmptyString(Object[] vals) {
        if (vals == null) {
            return false;
        }
        
        for (final Object val : vals) {
            if (val != null && StringUtils.isNotBlank(String.valueOf(val))) {
                return true;
            }
        }
        
        return false;
    }
}
