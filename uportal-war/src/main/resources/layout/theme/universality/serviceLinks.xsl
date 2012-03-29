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
        <a target="_new_wiscmail" href="{$WISCMAIL_URL}" title="WiscMail" role="menuitem">  <!-- Navigation item link. -->
          <span>WiscMail</span>
        </a>
        <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.users-calendar')">
      <span id="portalPageBar_wisccal">
        <a target="_new_wisccal" href="{$WISCCAL_URL}" title="WiscCal" role="menuitem">  <!-- Navigation item link. -->
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
        <a target="_new_mywebspace" href="{$MYWEBSPACE_URL}" title="My&#160;WebSpace" role="menuitem">  <!-- Navigation item link. -->
          <span>My&#160;WebSpace</span>
        </a>
          <xsl:call-template name="portal.pipe" />
      </span>
    </xsl:if>
    <xsl:if test="upGroup:isUserDeepMemberOf($USER_ID, 'pags.student') or upGroup:isUserDeepMemberOf($USER_ID, 'pags.facstaff')">
      <span id="portalPageBar_learnatuw">
        <a target="_new_learnatuw" href="{$LEARNAUW_URL}" title="Learn@UW" role="menuitem">  <!-- Navigation item link. -->
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

</xsl:stylesheet>