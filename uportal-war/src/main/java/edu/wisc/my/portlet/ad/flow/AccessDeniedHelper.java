/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.flow;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.events.IPortalEventFactory;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.IPersonManager;
import org.jasig.portal.url.IPortalRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Controller that handles requests from users with no portal access rights 
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
@Service("accessDeniedHelper")
public class AccessDeniedHelper {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IPersonManager personManager;
    private IPortalEventFactory portalEventFactory;
    private IPortalRequestUtils portalRequestUtils;
    
    
    @Autowired
    public void setPersonManager(IPersonManager personManager) {
        this.personManager = personManager;
    }

    @Autowired
    public void setPortalEventFactory(IPortalEventFactory portalEventFactory) {
        this.portalEventFactory = portalEventFactory;
    }

    @Autowired
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        this.portalRequestUtils = portalRequestUtils;
    }


    public String getPortalEventSessionId(RenderRequest request) {
        final HttpServletRequest portletHttpRequest = this.portalRequestUtils.getPortletHttpRequest(request);
        final IPerson person = this.personManager.getPerson(portletHttpRequest);
        return this.portalEventFactory.getPortalEventSessionId(portletHttpRequest, person);
    }
}
