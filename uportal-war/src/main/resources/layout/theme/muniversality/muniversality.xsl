<?xml version="1.0" encoding="utf-8"?>
<!--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

-->

<!-- ========================================================================= -->
<!-- ========== README ======================================================= -->
<!-- ========================================================================= -->
<!-- 
 | The theme is written in XSL. For more information on XSL, refer to [http://www.w3.org/Style/XSL/].
 | Baseline XSL skill is strongly recommended before modifying this file.
 |
 | This file has two purposes:
 | 1. To instruct the portal how to compile and configure the theme for use in mobile devices.
 | 2. To provide theme configuration and customization.
 |
 | As such, this file has a mixture of code that should not be modified, and code that exists explicitly to be modified.
 | To help make clear what is what, a RED-YELLOW-GREEN legend has been added to all of the sections of the file.
 |
 | RED: Stop! Do not modify.
 | YELLOW: Warning, proceed with caution.  Modifications can be made, but should not generally be necessary and may require more advanced skill.
 | GREEN: Go! Modify as desired.
-->

<!-- ========================================================================= -->
<!-- ========== DOCUMENT DESCRIPTION ========================================= -->
<!-- ========================================================================= -->
<!-- 
 | Date: 08/14/2008
 | Author: Matt Polizzotti
 | Company: Unicon,Inc.
 | uPortal Version: uP3.0.0 and uP3.0.1
 | 
 | General Description: This file, muniversality.xsl, was developed with mcolumn.xsl in order 
 | to enable uPortal 3.0.1 to be viewable by mobile devices. Supported mobile devices 
 | consist of Internet-enabled mobile devices running Windows Mobile 5+ and the BlackBerry Browser 4+. 
 | 
 | This file transforms the xml content generated by the mcolumn.xsl file into html, which 
 | is then rendered in a mobile device. This file formats the html markup to render uPortal's tabs 
 | and their associated channels and portlets for mobile display.
-->


<!-- ========================================================================= -->
<!-- ========== STYLESHEET DELCARATION ======================================= -->
<!-- ========================================================================= -->
<!-- 
 | RED
 | This statement defines this document as XSL.
-->
<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:dlm="http://www.uportal.org/layout/dlm"
    xmlns:upAuth="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanAuthorizationHelper"
    xmlns:upGroup="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanGroupMembershipHelper"
    xmlns:upMsg="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanMessageHelper"
    xmlns:url="https://source.jasig.org/schemas/uportal/layout/portal-url"
    xsi:schemaLocation="
            https://source.jasig.org/schemas/uportal/layout/portal-url ../../../xsd/layout/portal-url-4.0.xsd"
    exclude-result-prefixes="url upAuth upGroup upMsg" 
    version="1.0">

<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== IMPORTS ====================================================== -->
<!-- ========================================================================= -->
<!-- 
| RED
| Imports are the XSL files that build the theme.
| Import statments and the XSL files they refer to should not be modified.
-->
<xsl:import href="../resourcesTemplates.xsl" />  <!-- Templates for Skin Resource generation -->
<xsl:import href="../urlTemplates.xsl" />        <!-- Templates for URL generation -->
<xsl:import href="components.xsl" />
<!-- ========================================================================= -->


<!-- ========================================= -->
<!-- ========== OUTPUT DELCARATION =========== -->
<!-- ========================================= -->
<!-- 
    | RED
    | This statement instructs the XSL how to output.
-->
<xsl:output method="xml" indent="yes" media-type="text/html" doctype-system="EMPTY"/>
<!-- ========================================= -->

<!-- ============================================== -->
<!-- ========== VARIABLES and PARAMETERS ========== -->
<!-- ============================================== -->
<!-- 
| YELLOW - GREEN
| These variables and parameters provide flexibility and customization of the user interface.
| Changing the values of the variables and parameters signals the theme to reconfigure use 
| and location of user interface components. Most text used within the theme is localized.  
-->
  
  
<!-- ****** XSL UTILITY PARAMETERS ****** -->
<!-- 
| RED
| Parameters used by XSL->Java Callbacks
-->
<xsl:param name="CURRENT_REQUEST" />
<xsl:param name="RESOURCES_ELEMENTS_HELPER" />
<xsl:param name="XSLT_PORTAL_URL_PROVIDER" />


<!-- ****** SKIN SETTINGS ****** -->
<!-- 
| YELLOW
| Skin Settings can be used to change the location of skin files.
--> 
<xsl:param name="skin">iphone</xsl:param>
<xsl:param name="CONTEXT_PATH">/NOT_SET</xsl:param>
<xsl:param name="view">grid</xsl:param>
<xsl:variable name="SKIN" select="$skin"/>
<xsl:variable name="VIEW" select="$view"/>
<xsl:variable name="MEDIA_PATH">media/skins/muniversality</xsl:variable>
<xsl:variable name="ABSOLUTE_MEDIA_PATH" select="concat($CONTEXT_PATH,'/',$MEDIA_PATH)"/>
<xsl:variable name="SKIN_RESOURCES_PATH" select="concat('/',$MEDIA_PATH,'/',$SKIN,'/skin.xml')"/>
<xsl:variable name="SKIN_PATH" select="concat($ABSOLUTE_MEDIA_PATH,'/',$SKIN)"/>
<xsl:variable name="PORTAL_SHORTCUT_ICON" select="concat($CONTEXT_PATH,'/favicon.ico')" />
<xsl:variable name="FLUID_THEME">
    <xsl:call-template name="skinParameter">
        <xsl:with-param name="path" select="$SKIN_RESOURCES_PATH" />
        <xsl:with-param name="name">fss-theme</xsl:with-param>
    </xsl:call-template>
  </xsl:variable>
<xsl:variable name="FLUID_THEME_CLASS">
    <xsl:choose>
        <xsl:when test="$FLUID_THEME"><xsl:value-of select="$FLUID_THEME"/></xsl:when>
        <xsl:otherwise>fl-theme-uportal</xsl:otherwise>
    </xsl:choose>
</xsl:variable>
<xsl:variable name="FOCUSED_CLASS">
    <xsl:choose>
        <xsl:when test="//content/focused">focused <xsl:value-of select="//content/focused/channel/@fname"/></xsl:when>
        <xsl:otherwise>dashboard</xsl:otherwise>
    </xsl:choose>
</xsl:variable>
  
<!-- 
 | The unofficial "theme-switcher".
 | The INSTITUTION variable can be used to make logical tests and configure the theme on a per skin basis.
 | Allows the the theme to configure differently for a skin or group of skins, yet not break for other skins that might require a different configuration.
 | The implementation is hard-coded, but it works.
 | May require the addition of an xsl:choose statement around parameters, vairables, and template calls.
-->
<xsl:variable name="INSTITUTION">
  <xsl:choose>
    <xsl:when test="$SKIN='coal'">coal</xsl:when>
    <xsl:when test="$SKIN='ivy'">ivy</xsl:when>
    <xsl:when test="$SKIN='hc'">hc</xsl:when>
    <xsl:when test="$SKIN='wisc.edu' or $SKIN='wisc.edu-sys'">madison</xsl:when> <!-- Set all institution skins to a specific theme configuration  -->
    <xsl:otherwise>system</xsl:otherwise>
  </xsl:choose>
</xsl:variable>
<!-- ======================================== -->


<!-- ****** LOCALIZATION SETTINGS ****** -->
<!-- 
| GREEN
| Locatlization Settings can be used to change the localization of the theme.
-->
<xsl:param name="MESSAGE_DOC_URL">messages.xml</xsl:param>
<xsl:param name="USER_LANG">en</xsl:param>
<!-- ======================================== -->


<!-- ****** PORTAL SETTINGS ****** -->
<!-- 
| YELLOW
| Portal Settings should generally not be (and not need to be) modified.
-->
<xsl:param name="AUTHENTICATED" select="'false'"/>
<xsl:param name="USER_ID">guest</xsl:param>
<xsl:param name="userName">Guest User</xsl:param>
<xsl:param name="USER_NAME"><xsl:value-of select="$userName"/></xsl:param>
<xsl:param name="USE_SELECT_DROP_DOWN">true</xsl:param>
<xsl:param name="uP_productAndVersion">uPortal</xsl:param>
<xsl:param name="UP_VERSION"><xsl:value-of select="$uP_productAndVersion"/></xsl:param>
<xsl:param name="baseActionURL">render.uP</xsl:param>
<xsl:variable name="BASE_ACTION_URL"><xsl:value-of select="$baseActionURL"/></xsl:variable>
<!--  
<xsl:param name="HOME_ACTION_URL"><xsl:value-of select="$BASE_ACTION_URL"/>?uP_root=root&amp;uP_reload_layout=true&amp;uP_sparam=targetRestriction&amp;targetRestriction=no targetRestriction parameter&amp;uP_sparam=targetAction&amp;targetAction=no targetAction parameter&amp;uP_sparam=selectedID&amp;selectedID=&amp;uP_cancel_targets=true&amp;uP_sparam=mode&amp;mode=view</xsl:param> 
-->

<xsl:param name="EXTERNAL_LOGIN_URL"></xsl:param>
    

<!-- ========================================================================= -->
<!-- ========== TEMPLATE: PAGE JAVASCRIPT ==================================== -->
<!-- ========================================================================= -->
<!-- 
| GREEN
| This template renders the Javascript links in the page <head>.
| Javascript files are located in the uPortal skins directory:
| /media/skins/muniversality/common/[theme_name]/javascript
| Support across mobile browsers for Javascript is limited and
| should be used with caution when developing solutions.
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template name="page.js">
    <script type="text/javascript">
        var up = up || {};
        up.jQuery = jQuery.noConflict(true);
        up.fluid = fluid;
        fluid = null;
        fluid_1_4 = null;
        
        <xsl:if test="$VIEW != 'grid'">
            up.jQuery(document).ready(function() {
                up.jQuery('ul[data-role=listview].up-portal-nav').jqmAccordion();
            });
        </xsl:if>
    </script>    
</xsl:template>
<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== TEMPLATE: PAGE TITLE ========================================= -->
<!-- ========================================================================= -->
<!-- 
| GREEN
| This template renders the page title in the <head>.
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template name="page.title">
   <title><xsl:value-of select="upMsg:getMessage(concat($INSTITUTION, '_portal.page.title'), $USER_LANG)" /></title>
</xsl:template>
<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== TEMPLATE: PAGE META ========================================== -->
<!-- ========================================================================= -->
<!-- 
| GREEN
| This template renders keywords and descriptions in the <head>.
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template name="page.meta">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="black" />
    <meta name="description" content="{upMsg:getMessage(concat($INSTITUTION, '_portal.page.meta.description'), $USER_LANG)}" />
    <meta name="keywords" content="{upMsg:getMessage('portal.page.meta.keywords', $USER_LANG)}" />
</xsl:template>
<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== TEMPLATE: FOOTER ============================================= -->
<!-- ========================================================================= -->
<!-- 
| GREEN
| The footer template currently contains the portal's copyright information. This area can be 
| customized to contain any number of links or institution identifiers. This template renders 
| in all areas of the portal (unauthenticated, focused and non-focused). 
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template name="footer">
    <!--<p>
    	<a href="http://www.jasig.org/uportal/about/license">uPortal is licensed under the Apache License, Version 2.0</a>
    </p>-->
</xsl:template>
<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== TEMPLATE: LOGO =============================================== -->
<!-- ========================================================================= -->
<!-- 
| GREEN
| A place to put a logo on the dashboard view.
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template name="logo">
    <!--div class="logo">
    	<img src="$CONTEXT_PATH/media/skins/muniversality/common/images/umobile_logo_flat.png" alt="uMobile" />
    </div-->
</xsl:template>
<!-- ========================================================================= -->


<!-- ========================================================================= -->
<!-- ========== TEMPLATE: ROOT =============================================== -->
<!-- ========================================================================= -->
<!-- 
| RED
| This is the root xsl template and it defines the overall structure of the html markup. 
| Focused and Non-focused content is controlled through an xsl:choose statement.
| Template contents can be any valid XSL or XHTML.
-->
<xsl:template match="/">
    <html>
        <head>
            <xsl:call-template name="page.title" />
            <xsl:call-template name="page.meta" />
            <xsl:call-template name="skinResources">
              <xsl:with-param name="path" select="$SKIN_RESOURCES_PATH" />
            </xsl:call-template>

            <xsl:call-template name="page.js" />
        </head>
        <body class="up {$FLUID_THEME_CLASS} dashboard-{$VIEW}">
            <div class="portal {$FOCUSED_CLASS}" data-role="page" id="page">
                <xsl:choose>
                    <xsl:when test="//focused">
                        <xsl:call-template name="mobile.header.focused" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="mobile.header" />
                    </xsl:otherwise>
                </xsl:choose>
                
                <xsl:choose>
                    <xsl:when test="//focused">
                        <xsl:call-template name="mobile.channel.content.focused" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$VIEW = 'grid'">
                                <xsl:call-template name="mobile.navigation.grid" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="mobile.navigation.list" />
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:call-template name="logo" />
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:call-template name="footer" />
            </div>
        </body>
    </html>
</xsl:template>
<!-- ========================================================================= -->


</xsl:stylesheet>