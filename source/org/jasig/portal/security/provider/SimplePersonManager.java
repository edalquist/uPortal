/**
 * Copyright � 2001 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  org.jasig.portal.security.provider;

import  java.util.HashMap;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpSessionBindingListener;
import  javax.servlet.http.HttpSessionBindingEvent;
import  org.jasig.portal.services.LogService;
import  org.jasig.portal.security.IPersonManager;
import  org.jasig.portal.security.IPerson;
import  org.jasig.portal.security.PortalSecurityException;
import  org.jasig.portal.security.InitialSecurityContext;


/**
 * @author Bernie Durfee (bdurfee@interactivebusiness.com)
 */
public class SimplePersonManager
    implements IPersonManager {

  /**
   * Retrieve an IPerson object for the incoming request
   * @param request
   * @return 
   */
  public IPerson getPerson (HttpServletRequest request) throws PortalSecurityException {
    // Return the person object if it exists in the user's session
    IPerson person = (IPerson)request.getSession(false).getAttribute("org.jasig.portal.IPerson");
    if (person != null) {
      return  (person);
    }
    // Create a new instance of a person
    person = new PersonImpl();
    try {
      // Add the initial security context to the person
      person.setSecurityContext(new InitialSecurityContext("root"));
    } catch (Exception e) {
      // Log the exception
      LogService.log(LogService.ERROR, e);
    }
    // By default new user's have the UID of 1
    person.setID(1);
    // Add this person object to the user's session
    request.getSession(false).setAttribute("org.jasig.portal.IPerson", person);
    // Return the new person object
    return  (person);
  }
}



