/*
 * Created on Aug 25, 2004
 *
 */
package edu.wisc.my.groups.pags.testers;

import org.jasig.portal.groups.pags.testers.StringTester;

/**
 * @author nblair
 * Some of our groups are determined simply if the user has "something"
 * set for a particular attribute
 * this tester ignores the test-value field
 * if the attribute has some value, then it returns true
 */
public class AttributeExistsTester extends StringTester {

	public AttributeExistsTester(String attribute, String test) {
        super(attribute, test);
    }
    
    public boolean test(String att) {
        boolean result = false;
        if(att != null && !att.equals("")) {
        	result = true;
        }
        return result;
    }
}
