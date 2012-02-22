/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.snow;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;

import edu.wisc.my.snow.om.DeclaredSnowEmergency;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public class SnowEmergencyDaoImplTest extends TestCase {
    private SnowEmergencyDaoImpl dao;

    @Override
    protected void setUp() throws Exception {
        this.dao = new SnowEmergencyDaoImpl();
    }

    @Override
    protected void tearDown() throws Exception {
        this.dao = null;
    }
    
    public void testParseNoEmergency() throws Exception {
        this.dao.setSnowEmergencyFeed(new ClassPathResource("/edu/wisc/my/snow/noSnowEmerg.xml"));
        
        final DeclaredSnowEmergency snowEmergencyStatus = (DeclaredSnowEmergency)this.dao.createEntry(null);
        
        assertNotNull(snowEmergencyStatus);
        assertFalse(snowEmergencyStatus.isStatus());
        assertNull(snowEmergencyStatus.getEndDateTime());
        assertNull(snowEmergencyStatus.getMessage());
        assertEquals("http://www.cityofmadison.com/winter/", snowEmergencyStatus.getLink());
    }
    
    public void testParseEmergency() throws Exception {
        this.dao.setSnowEmergencyFeed(new ClassPathResource("/edu/wisc/my/snow/snowEmerg.xml"));
        
        final DeclaredSnowEmergency snowEmergencyStatus = (DeclaredSnowEmergency)this.dao.createEntry(null);
        
        assertNotNull(snowEmergencyStatus);
        assertTrue(snowEmergencyStatus.isStatus());
        assertEquals("02/23/2009 7:00 AM", snowEmergencyStatus.getEndDateTime());
        assertEquals("02/22/09 9:09 AM: City of Madison Declared snow emergency\n" + 
        		"        continues thru 7AM 03/23/09. Park on odd side tonight. 261-9111 or\n" + 
        		"        www.cityofmadison.com/winter for info.", snowEmergencyStatus.getMessage());
        assertEquals("http://www.cityofmadison.com/winter/", snowEmergencyStatus.getLink());
    }
}
