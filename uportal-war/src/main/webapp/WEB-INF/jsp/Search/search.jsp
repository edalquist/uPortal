<%--

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

--%>

<%@ include file="/WEB-INF/jsp/include.jsp"%>

<portlet:actionURL var="formUrl"/>
<c:set var="n"><portlet:namespace/></c:set>

<!-- Portlet -->
<div class="fl-widget portlet search-portlet" role="section">

  <!-- Portlet Titlebar 
  <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
    <h2 class="title" role="heading"><spring:message code="search"/></h2>
  </div>
  -->
  
  <!-- Portlet Body -->
  <div class="fl-widget-content portlet-body" role="main">
  
    <!-- Portlet Section -->
    <div id="${n}search" class="portlet-section" role="region">

      <div class="portlet-section-body">

        <form action="${ formUrl }" method="POST">
            <input name="query" value="${ fn:escapeXml(query )}"/> <input type="submit" value="Search"/>
        </form>
        
        <c:if test="${hitMaxQueries}">
            <div>
                <spring:message code="search.rate.limit.reached"/>
            </div>
        </c:if>

        <c:if test="${not empty results}">

            <div class="portlet-section" role="region">
          
                <div class="content">
                    <div id="${n}searchResults">
                        <ul>
                            <li><a href="#${n}_DEFAULT_TAB"><span><spring:message code="${defaultTabKey}"/></span></a></li>
                            <c:forEach var="tabKey" items="${tabKeys}" varStatus="loopStatus">
                              <c:if test="${fn:length(results[tabKey]) > 0}">
                                <li><a href="#${n}_${loopStatus.index}"><span><spring:message code="${tabKey}"/></span></a></li>
                              </c:if>
                            </c:forEach>
                        </ul>
                        
                        <%--
                         | result.first is the SearchResult object
                         | result.second is the calculated URL
                         +--%>
                        
                        <!-- Write out the default results tab -->
                        <div id="${n}_DEFAULT_TAB">
                          <div class="search-results">
                            <c:forEach items="${ results[defaultTabKey] }" var="result">
                              <div class="search-result">
                                <div>
                                  <a class="result_link" href="${result.second}"><span class="result_title">${ result.first.title }</span></a>
                                </div>
                                <div class="result_excerpt">${ result.first.summary }</div>
                              </div>
                            </c:forEach>
                          </div>
                        </div>
                        
                        <!-- write out each additional results tab -->
                        <c:forEach var="tabKey" items="${tabKeys}" varStatus="loopStatus">
                          <c:if test="${fn:length(results[tabKey]) > 0}">
                            <div id="${n}_${loopStatus.index}" class="${tabKey}">
                              <div class="search-results">
                                <c:forEach items="${ results[tabKey] }" var="result">
                                  <c:choose>
                                    <%-- Special Handling for UW Person Search formatting --%>
                                    <c:when test="${tabKey == 'uwPersonError.results'}">
                                      <div class="error-match">
                                        <div class="error-msg">${ result.first.title }</div>
                                      </div>
                                    </c:when>
                                    <c:when test="${tabKey == 'uwPerson.results'}">
                                      <c:set var="summaryParts" value="${fn:split(result.first.summary, '|')}"/>
                                      
                                      <div class="person-match search-result">
                                        <div class="person_name result_title">${ result.first.title }</div>
                                        <c:if test="${not empty summaryParts[0]}">
                                          <div class="person_email">
                                            <a class="person_email-link" href="mailto:${summaryParts[0]}">${summaryParts[0]}</a>
                                          </div>
                                        </c:if>
                                        <c:if test="${fn:length(summaryParts) gt 1 and not empty summaryParts[1]}">
                                          <div class="person_phone">${summaryParts[1]}</div>
                                          <div class="person_more">
                                            <a class="person_more-link" href="${result.second}">More &raquo;</a>
                                          </div>
                                        </c:if>
                                      </div>
                                    </c:when>
                                    
                                    <%-- Default result formatting --%>
                                    <c:otherwise>
                                      <div class="search-result">
                                        <div>
                                          <a class="result_link" href="${result.second}"><span class="result_title">${ result.first.title }</span></a>
                                        </div>
                                        <div class="result_excerpt">${ result.first.summary }</div>
                                      </div>
                                    </c:otherwise>
                                  </c:choose>
                                </c:forEach>
                              </div>
                            </div>
                          </c:if>
                        </c:forEach>

                    </div>
                </div>
            </div>

<script type="text/javascript">
up.jQuery(function () {
  var $ = up.jQuery;
  var fluid = up.fluid;
  
  up.jQuery(document).ready(function () {
    up.jQuery("#${n}searchResults").tabs();
  });
});
</script>
        </c:if>

      </div>  

    </div>
    
  </div>

</div>
