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

package edu.wisc.my.portlets.search.people;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.codehaus.jackson.JsonNode;
import org.jasig.portal.portlets.search.IPortalSearchService;
import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResult;
import org.jasig.portal.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import com.google.common.collect.ImmutableMap;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
@Controller
public class PeopleSearchService implements IPortalSearchService {

    private String queryParam = "name";
    private String baseSearchUrl = "http://www.wisc.edu/directories/json/?" + queryParam + "={" + queryParam + "}";
    private String detailsParam = "name";
    private String personDetailsUrl = "http://www.wisc.edu/directories/person.php?" + detailsParam + "={" + detailsParam + "}";
    
    private RestOperations restOperations;
    private String resultType = "uwPerson";
    private String errorResultType = "uwPersonError";

    @Autowired
    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    @Override
    public SearchResults getSearchResults(PortletRequest request, SearchRequest query) {
        final Map<String, Object> parameters = new LinkedHashMap<String, Object>();
        
        parameters.put(queryParam, query.getSearchTerms());

        final JsonNode personResponse = this.restOperations.getForObject(this.baseSearchUrl, JsonNode.class, parameters);
        
        final SearchResults searchResults = new SearchResults();
        searchResults.setQueryId(query.getQueryId());
        final List<SearchResult> searchResultList = searchResults.getSearchResult();
        
        for (final Iterator<JsonNode> errorsItr = personResponse.get("errors").getElements(); errorsItr.hasNext();) {
            final JsonNode error = errorsItr.next();
            
            final JsonNode msg = error.get("error_msg");
            if (msg != null) {
                final SearchResult searchResult = new SearchResult();
                searchResult.setTitle(msg.asText());
                searchResult.getType().add(errorResultType);
                
                searchResultList.add(searchResult);
                
                break;
            }
        }
        
        //Must have been errors, short-circuit
        if (!searchResultList.isEmpty()) {
            return searchResults;
        }
        
        final JsonNode results = personResponse.get("records");
        for (final Iterator<JsonNode> resultItr = results.getElements(); resultItr.hasNext();) {
            final JsonNode personResult = resultItr.next();
            
            final SearchResult searchResult = new SearchResult();
            searchResult.setTitle(personResult.get("fullName").asText());
            
            final StringBuilder summary = new StringBuilder();
            final Iterator<JsonNode> emailsItr = personResult.get("emails").getElements();
            if (emailsItr.hasNext()) {
                summary.append(emailsItr.next().asText());
            }
            summary.append('|');
            final Iterator<JsonNode> phonesItr = personResult.get("phones").getElements();
            if (phonesItr.hasNext()) {
                summary.append(phonesItr.next().asText());
            }
            
            searchResult.setSummary(summary.toString());
            final String externalUrl = this.buildExternalUrl(searchResult);
            searchResult.setExternalUrl(externalUrl);
            
            searchResult.getType().add(resultType);
            
            searchResultList.add(searchResult);
        }
        
        return searchResults;
    }

    private String buildExternalUrl(final SearchResult searchResult) {
        final UriTemplate uriTemplate = new UriTemplate(personDetailsUrl);
        uriTemplate.expand(searchResult.getTitle());
        final URI externalUri = uriTemplate.expand(ImmutableMap.of(detailsParam, searchResult.getTitle()));
        return externalUri.toString();
    }
}
