package edu.wisc.my.portlets.search.people;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Tests a GoogleAjaxSearchService
 * 
 * @author Eric Dalquist
 */
@RunWith(MockitoJUnitRunner.class)
public class PeopleSearchServiceTest {
    @InjectMocks private PeopleSearchService peopleSearchService = new PeopleSearchService();
    @InjectMocks private RestTemplate restTemplate = new RestTemplate();
    @Mock private PortletRequest portletRequest;
    @Mock private ClientHttpRequestFactory clientHttpRequestFactory;
    @Mock private ClientHttpRequest clientHttpRequest;
    @Mock private ClientHttpResponse clientHttpResponse;
    @Mock private HttpHeaders requestHttpHeaders;
    @Mock private HttpHeaders responseHttpHeaders;
    
    @Before
    public void setup() throws Exception {
        //Add handling of text/javascript content type
        final MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        final List<MediaType> supportedMediaTypes = new LinkedList<MediaType>(converter.getSupportedMediaTypes());
        final MediaType textJavascriptMediaType = new MediaType("text", "javascript", MappingJacksonHttpMessageConverter.DEFAULT_CHARSET);
        supportedMediaTypes.add(textJavascriptMediaType);
        converter.setSupportedMediaTypes(supportedMediaTypes);
        restTemplate.getMessageConverters().add(converter);
        
        this.peopleSearchService.setRestOperations(restTemplate);

        //Uncomment to make real requests 
        //restTemplate.setRequestFactory(new CommonsClientHttpRequestFactory());
        
        when(clientHttpRequest.getHeaders()).thenReturn(requestHttpHeaders);
        when(clientHttpRequest.execute()).thenReturn(clientHttpResponse);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(clientHttpResponse.getHeaders()).thenReturn(responseHttpHeaders);
        when(responseHttpHeaders.getContentType()).thenReturn(textJavascriptMediaType);
    }
    
    @Test
    public void testPersonSearchController() throws Exception {
        final String json = "{\"count\":5,\"errors\":[],\"records\":[{\"fullName\":\"Ann M Helwig\",\"wisceduephemeralid\":\"aa784454857943ca3de9de9c0fcbcc0a\",\"phones\":[\"890-5682\"],\"emails\":[\"ahelwig@uwhealth.org\"],\"titles\":[{\"title\":\"Dir Health System Affiliations\",\"division\":\"UW Hospital and Clinics\",\"department\":\"UHC-Regional Hospital Programs\"}],\"address\":{\"streetAddress\":\"301 S. Westfield Rd \",\"cityStateZip\":\"Madison, WI 53717\"}},{\"fullName\":\"JANET K HELWIG\",\"wisceduephemeralid\":\"8968c628d535fc3358fc8588f2c5d662\",\"phones\":[\"(608) 262-9851\"],\"emails\":[\"jkh@athletics.wisc.edu\"],\"titles\":[{\"title\":\"ATHLETIC TRAINER III\",\"division\":\"INTERCOLLEGIATE ATHLETICS\",\"department\":\"GENERAL OPERATIONS\",\"subdepartment\":\"SPORTS MEDICINE\"},{\"title\":\"ASSOC LECTURER\",\"division\":\"SCHOOL OF EDUCATION\",\"department\":\"KINESIOLOGY\",\"subdepartment\":\"KINESIOLOGY-GEN\"}],\"address\":{\"room\":\"1035 Gymnasium-Natatorium\",\"streetAddress\":\"2000 Observatory Dr\",\"cityStateZip\":\"Madison, WI 53706\"}},{\"fullName\":\"ANNA M HELWIG\",\"wisceduephemeralid\":\"c3249d513097a98c25f9276b0d14291e\",\"phones\":[\"\"],\"emails\":[\"ahelwig@wisc.edu\"],\"titles\":[{\"title\":\"NURSE CLINICN 2\",\"division\":\"SCHOOL OF MEDICINE AND PUBLIC HEALTH\",\"department\":\"FAMILY MEDICINE\",\"subdepartment\":\"CLINIC - BELLEVILLE\"}],\"address\":{\"room\":\"Family Medicine Clinic-Blvl\",\"streetAddress\":\"21 S Vine St\",\"cityStateZip\":\"Belleville, WI 53508\"}},{\"fullName\":\"DENNIS C HELWIG\",\"wisceduephemeralid\":\"228d42aef76b0f137cabe81100b4c856\",\"phones\":[\"(608) 262-3630\",\"(608) 262-3798\"],\"emails\":[\"dch@athletics.wisc.edu\"],\"titles\":[{\"title\":\"ASST DIR, ATHL\\/L\",\"division\":\"INTERCOLLEGIATE ATHLETICS\",\"department\":\"GENERAL OPERATIONS\",\"subdepartment\":\"SPORTS MEDICINE\"}],\"address\":{\"room\":\"The Kohl Center\",\"streetAddress\":\"601 W Dayton St\",\"cityStateZip\":\"Madison, WI 53715\"}},{\"fullName\":\"JAMES W HELWIG\",\"wisceduephemeralid\":\"eab4fa330ac0a6105c6f6bd3870dd073\",\"phones\":[\"(608) 262-8440\"],\"emails\":[\"jim.helwig@doit.wisc.edu\"],\"titles\":[{\"title\":\"IS TECH SRV CONS\\/ADM\",\"division\":\"INFORMATION TECHNOLOGY\",\"department\":\"ENTERPRISE INTERNET SERVICES\",\"subdepartment\":\"INTERNET INFRASTRUCTURE APPLICATIONS\"}],\"address\":{\"room\":\"3245 Computer Sciences & Statistics\",\"streetAddress\":\"1210 W Dayton St\",\"cityStateZip\":\"Madison, WI 53706\"}}]}";
        
        when(clientHttpRequestFactory.createRequest(new URI("http://www.wisc.edu/directories/json/?name=helwig"), HttpMethod.GET)).thenReturn(clientHttpRequest);
        when(clientHttpResponse.getBody()).thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(responseHttpHeaders.getContentLength()).thenReturn((long)json.length());
        
        final SearchRequest query = new SearchRequest();
        query.setSearchTerms("helwig");
        
        final SearchResults results = peopleSearchService.getSearchResults(portletRequest, query);

        assertNotNull(results);
        assertEquals(5, results.getSearchResult().size());
    }
    
    @Test
    public void testPersonSearchControllerError() throws Exception {
        final String json = "{\"count\":0,\"errors\":[{\"code\":4},{\"error_msg\":\"Your search returned more than the maximum number of allowable matches (25).\"}],\"records\":[]}";
        
        when(clientHttpRequestFactory.createRequest(new URI("http://www.wisc.edu/directories/json/?name=dal"), HttpMethod.GET)).thenReturn(clientHttpRequest);
        when(clientHttpResponse.getBody()).thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(responseHttpHeaders.getContentLength()).thenReturn((long)json.length());
        
        final SearchRequest query = new SearchRequest();
        query.setSearchTerms("dal");
        
        final SearchResults results = peopleSearchService.getSearchResults(portletRequest, query);

        assertNotNull(results);
        assertEquals(1, results.getSearchResult().size());
    }
    
    
}
