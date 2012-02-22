/*
 * Created on Jul 21, 2004
 *
 */
package edu.wisc.my.groups.pags.testers;

import org.jasig.portal.groups.pags.testers.BaseAttributeTester;
import org.jasig.portal.security.IPerson;

/**
 * The tester returns true if the attribute is null or if none of the
 * values of the attribute equal the specified attribute.
 * 
 * @author edalquist@unicon.net
 */
public class NegatedStringTester extends BaseAttributeTester {

    public NegatedStringTester(String attribute, String test) {
        super(attribute, test);
    }
    
    public boolean test(IPerson person) {
        //Get the list of values for the attribute
        Object[] vals = person.getAttributeValues(getAttributeName());
        
        //No values, test passed
        if (vals == null) {
            return true;
        }
        else {
            //Loop through the values of the attribute, if one is equal
            //to the test case the test fails and returns false
            for (int i=0; i<vals.length; i++)
            { 
                String val = (String)vals[i];
                
                if (val.equalsIgnoreCase(testValue)) 
                    return false;
            }
            
            //None of the values equaled the test case, test passed
            return true;
        }
    }
}
