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
package org.jasig.portal.portlets.search;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jasig.portal.search.SearchResult;
import org.jasig.portal.utils.Tuple;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Used to collate search results for the SearchPortletController
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class PortalSearchResults implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final List<String> DEFAULT_TYPES = Arrays.asList("Default");
    
    //Map of <result type, <result, url>>
    private final LoadingCache<String, List<Tuple<SearchResult, String>>> results;
    
    public PortalSearchResults() {
        this.results = CacheBuilder.newBuilder().<String, List<Tuple<SearchResult, String>>>build(new CacheLoader<String, List<Tuple<SearchResult, String>>>() {
            @Override
            public List<Tuple<SearchResult, String>> load(String key) throws Exception {
                return Collections.synchronizedList(new LinkedList<Tuple<SearchResult,String>>());
            }
        });
    }
    
    public ConcurrentMap<String, List<Tuple<SearchResult, String>>> getResults() {
        return this.results.asMap();
    }
    
    public void addPortletSearchResults(String url, SearchResult result) {
        final List<String> types = this.getTypes(result);
        for (final String type : types) {
            final List<Tuple<SearchResult, String>> typeResults = this.results.getUnchecked(type);
            typeResults.add(new Tuple<SearchResult, String>(result, url));
        }
    }
    
    protected List<String> getTypes(SearchResult result) {
        final List<String> type = result.getType();
        if (type != null && !type.isEmpty()) {
            return type;
        }
        
        return DEFAULT_TYPES;
    }
}
