package org.jasig.portal.portlets.skin;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.IUserPreferencesManager;
import org.jasig.portal.IUserProfile;
import org.jasig.portal.layout.dao.IStylesheetDescriptorDao;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.url.IPortalRequestUtils;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller
@RequestMapping("VIEW")
public class SkinPickerController {
    private IPortalRequestUtils portalRequestUtils;
    private IUserInstanceManager userInstanceManager;
    private IStylesheetDescriptorDao stylesheetDescriptorDao;
    
    @Autowired
    public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
        this.portalRequestUtils = portalRequestUtils;
    }

    @Autowired
    public void setUserInstanceManager(IUserInstanceManager userInstanceManager) {
        this.userInstanceManager = userInstanceManager;
    }

    @Autowired
    public void setStylesheetDescriptorDao(IStylesheetDescriptorDao stylesheetDescriptorDao) {
        this.stylesheetDescriptorDao = stylesheetDescriptorDao;
    }
    
    @ModelAttribute("mediaPath")
    public String getMediaPath(PortletRequest portletRequest) {
        final HttpServletRequest portletHttpRequest = this.portalRequestUtils.getPortletHttpRequest(portletRequest);
        final IUserInstance userInstance = this.userInstanceManager.getUserInstance(portletHttpRequest);
        
        final IUserPreferencesManager preferencesManager = userInstance.getPreferencesManager();
        final IUserProfile userProfile = preferencesManager.getUserProfile();
        final int themeStylesheetId = userProfile.getThemeStylesheetId();
        
        final IStylesheetDescriptor stylesheetDescriptor = stylesheetDescriptorDao.getStylesheetDescriptor(themeStylesheetId);
        if ("UniversalityMobile".equals(stylesheetDescriptor.getName())) {
            return "muniversality";
        }
        return "universality";
         
        // https://up41-nightly.jasig.org/media/skins/universality/skinList.xml
    }
    
    @RenderMapping
    public String showSkinList() {
        return "jsp/SkinPicker/skinList";
    }
}
