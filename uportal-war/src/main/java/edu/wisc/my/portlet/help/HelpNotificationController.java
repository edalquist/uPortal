/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.wisc.my.portlet.help;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
@Controller
@RequestMapping("VIEW")
public class HelpNotificationController {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    @RequestMapping
    public String renderHelp(RenderRequest request, ModelMap model) {
        final PortletPreferences preferences = request.getPreferences();
        
        final String[] videoGroups = preferences.getValues("videoGroups", new String[0]);
        final String[] videoLinks = preferences.getValues("videoLinks", new String[0]);
        final String[] videoDialogs = preferences.getValues("videoDialogs", new String[0]);
        
        if (videoGroups.length != videoLinks.length || videoGroups.length != videoDialogs.length) {
            logger.warn("Invalid HelpNotification config: videoGroups=" + videoGroups.length + " videoLinks=" + videoLinks.length + " videoDialogs=" + videoDialogs.length);
        }
        
        final StringBuilder videoLinksContent = new StringBuilder();
        final StringBuilder videoDialogsContent = new StringBuilder();
        
        int videoCount = 0;
        for (int i = 0; i < videoGroups.length; i++) {
            final String videoGroup = videoGroups[i];
            
            final String[] groups = videoGroup.split(",");
            boolean isUserInRole = false;
            for (final String group : groups) {
                if (request.isUserInRole(group)) {
                    isUserInRole = true;
                    break;
                }
            }
            
            if (!isUserInRole) {
                //User is not in a valid role, skip this video
                continue;
            }
            
            videoLinksContent.append(videoLinks[i]);
            videoDialogsContent.append(videoDialogs[i]);
            videoCount++;
        }
        
        
        String helpMessage = preferences.getValue("helpMessage", "");
        helpMessage = helpMessage.replace("##VIDEO_LINKS##", videoLinksContent);
        helpMessage = helpMessage.replace("##VIDEO_DIALOGS##", videoDialogsContent);
        helpMessage = helpMessage.replace("##VIDEO_COUNT##", Integer.toString(videoCount));
        
        model.addAttribute("helpMessage", helpMessage);
        
        return "/jsp/HelpNotification/help";
    }
}
