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
<portlet:resourceURL var="resourceUrl" />
<c:set var="ns"><portlet:namespace /></c:set>

<div class="fl-widget portlet" id="${ns}container">
  <ul id="${ns}_skinList">
  </ul>
  
  <script type="text/javascript"><rs:compressJs>
  up.jQuery(function($) {
     $.ajax("${renderRequest.contextPath}/media/skins/${mediaPath}/skinList.xml", {
        dataType: 'xml',
        success: function(xml) {
           var skinList = $("#${ns}_skinList");
           var root = $(xml);
           var skinNodes = root.find("skin");
           
           // Parse skinList.xml & construct ui.
           $.each(skinNodes, function (idx, obj) {
               var skin = $(obj);
               var key = skin.children("skin-key").text();
               var name = skin.children("skin-name").text();
               var description = skin.children("skin-description").text();
               //thumbnailPath = (that.options.mediaPath + "/" + key + "/" + "thumb.gif");
               
               skinList.append('<li><a href="' + key + '" title="' + description + '">' + name + '</a></li>');
           });
           
           $("#${ns}_skinList a").click(function() {
              var skinKey = $(this).attr("href");
              $.post('${renderRequest.contextPath}/api/layout', {
                 action: 'chooseSkin',
                 skinName: skinKey
              },
              function() {
                 location.reload();
              });
              
              return false;
           });
           
           //action=chooseSkin
        }
     });
  });
  </rs:compressJs></script>
</div>
