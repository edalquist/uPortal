<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:dlm="http://www.uportal.org/layout/dlm"
    xmlns:upAuth="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanAuthorizationHelper"
    xmlns:upGroup="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanGroupMembershipHelper"
    xmlns:upMsg="http://xml.apache.org/xalan/java/org.jasig.portal.security.xslt.XalanMessageHelper"
    xmlns:url="https://source.jasig.org/schemas/uportal/layout/portal-url"
    xsi:schemaLocation="
            https://source.jasig.org/schemas/uportal/layout/portal-url https://source.jasig.org/schemas/uportal/layout/portal-url-4.0.xsd"
    exclude-result-prefixes="url upAuth upGroup upMsg" 
    version="1.0">
  
  <!-- Links to major apps displayed in the page bar -->  
  <xsl:template name="quicklinks.static.page.bar.links">
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-email')">
      <span id="portalPageBar_wiscmail">
        <a target="_new_wiscmail" href="https://wisctest.wisc.edu/login" title="WiscMail" role="menuitem">  <!-- Navigation item link. -->
          <span>WiscMail</span>
        </a>
        <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-calendar')">
      <span id="portalPageBar_wisccal">
        <a target="_new_wisccal" href="https://oin.doit.wisc.edu/ocas-bin/ocas.fcgi?sub=web" title="WiscCal" role="menuitem">  <!-- Navigation item link. -->
          <span>WiscCal</span>
        </a>
        <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.applicant-undergraduate') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.applicant-graduate') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.student') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'AT Support') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'Administrators - ISIS')">
      <span id="portalPageBar_studentcenter">
        <xsl:variable name="studentCenterUrl">
          <xsl:call-template name="portalUrl">
              <xsl:with-param name="url">
                  <url:portal-url type="ACTION">
                      <url:fname>student-center</url:fname>
                      <url:portlet-url state="MAXIMIZED">
                        <url:param name="action" value="loginAction"/>
                      </url:portlet-url>
                  </url:portal-url>
              </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <a href="javascript: window.open('{$studentCenterUrl}','_new_studentcenter','toolbar=no,directories=no,menubar=yes,resizable=yes,dependent=no,width=800,height=800'); void('');" title="Student&#160;Center" role="menuitem">            <!-- Navigation item link. -->
          <span>Student&#160;Center</span>
        </a>
          <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-mywebspace')">
      <span id="portalPageBar_mywebspace">
        <a target="_new_mywebspace" href="https://mywebspacetest.wisc.edu/xythoswfs/webui" title="My&#160;WebSpace" role="menuitem">  <!-- Navigation item link. -->
          <span>My&#160;WebSpace</span>
        </a>
          <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.student') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.facstaff')">
      <span id="portalPageBar_learnatuw">
        <a target="_new_learnatuw" href="https://uwmad.courses.wisconsin.edu/Shibboleth.sso/Login?target=https://uwmad.courses.wisconsin.edu/d2l/shibbolethSSO/deepLinkLogin.d2l" title="Learn@UW" role="menuitem">  <!-- Navigation item link. -->
          <span>Learn@UW</span>
        </a>
          <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <span id="portalPageBar_mybookmarks">
        <xsl:variable name="bookmarksUrl">
          <xsl:call-template name="portalUrl">
              <xsl:with-param name="url">
                  <url:portal-url type="RENDER">
                      <url:fname>my-bookmarks</url:fname>
                      <url:portlet-url state="DETACHED" />
                  </url:portal-url>
              </xsl:with-param>
          </xsl:call-template>
        </xsl:variable>
        <a href="javascript: window.open('{$bookmarksUrl}','_new_mybookmarks','status=yes,scrollbars=yes,resizable=yes,width=400,height=500'); void('');" title="My&#160;Bookmarks" role="menuitem">  <!-- Navigation item link. -->
          <span>Bookmarks</span>
        </a>
        <xsl:call-template name="portal.pipe" />
    </span>
  </xsl:template>
    
  <!-- Keep these alphabetical until another order is defined -->  
  <xsl:template name="quicklinks.static.menu.links">
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-instructors') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'AT Support') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'Administrators - ISIS')">
      <li id="ql_facultycenter" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a href="javascript: window.open('render.userLayoutRootNode.uP?uP_fname=faculty-center&amp;pltc_type=ACTION&amp;pltp_action=loginAction','_new_facultycenter','toolbar=no,directories=no,menubar=yes,resizable=yes,dependent=no,width=800,height=800'); void('');" title="Faculty&#160;Center" role="menuitem">            <!-- Navigation item link. -->
          <span>Faculty&#160;Center</span>
        </a>
      </li>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.student') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.facstaff')">
      <li id="ql_learnatuw" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a target="_new_learnatuw" href="https://uwmad.courses.wisconsin.edu/Shibboleth.sso/Login?target=https://uwmad.courses.wisconsin.edu/d2l/shibbolethSSO/deepLinkLogin.d2l" title="Learn@UW" role="menuitem">  <!-- Navigation item link. -->
          <span>Learn@UW</span>
        </a>
      </li>
    </xsl:if>
    <li id="ql_mybookmarks" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
      <a href="javascript: window.open('render.userLayoutRootNode.uP?uP_fname=my-bookmarks&amp;pltc_type=ACTION&amp;pltc_state=detached','_new_mybookmarks','status=yes,scrollbars=yes,resizable=yes,width=400,height=500'); void('');" title="My&#160;Bookmarks" role="menuitem">  <!-- Navigation item link. -->
        <span>My&#160;Bookmarks</span>
      </a>
    </li>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-mywebspace')">
      <li id="ql_mywebspace" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a target="_new_mywebspace" href="https://mywebspacetest.wisc.edu/xythoswfs/webui" title="My&#160;WebSpace" role="menuitem">  <!-- Navigation item link. -->
          <span>My&#160;WebSpace</span>
        </a>
      </li>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.applicant-undergraduate') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.applicant-graduate') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.student') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'AT Support') or upGroup:isUserDeepMemberOfGroupName($USER_ID, 'Administrators - ISIS')">
      <li id="ql_studentcenter" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a href="javascript: window.open('render.userLayoutRootNode.uP?uP_fname=student-center&amp;pltc_type=ACTION&amp;pltp_action=loginAction','_new_studentcenter','toolbar=no,directories=no,menubar=yes,resizable=yes,dependent=no,width=800,height=800'); void('');" title="Student&#160;Center" role="menuitem">            <!-- Navigation item link. -->
          <span>Student&#160;Center</span>
        </a>
      </li>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-calendar')">
      <li id="ql_wisccal" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a target="_new_wisccal" href="https://oin.doit.wisc.edu/ocas-bin/ocas.fcgi?sub=web" title="WiscCal" role="menuitem">  <!-- Navigation item link. -->
          <span>WiscCal</span>
        </a>
      </li>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-email')">
      <li id="ql_wiscmail" role="presentation"> <!-- Each subnavigation menu item.  The unique ID can be used in the CSS to give each menu item a unique icon, color, or presentation. -->
        <a target="_new_wiscmail" href="https://wisctest.wisc.edu/login" title="WiscMail" role="menuitem">  <!-- Navigation item link. -->
          <span>WiscMail</span>
        </a>
      </li>
    </xsl:if>
  </xsl:template>

  <xsl:template name="myuw.login.link">
    <div id="portalWelcome">
        <div id="portalWelcomeInner">
          <p><span><a href="render.userLayoutRootNode.uP?uP_fname=portal_login_general" title="Sign in to My UW-Madison">Sign in »</a></span></p>
        </div>
      </div>
  </xsl:template>

</xsl:stylesheet>