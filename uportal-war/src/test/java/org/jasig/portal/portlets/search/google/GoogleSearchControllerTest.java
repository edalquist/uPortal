package org.jasig.portal.portlets.search.google;

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
 * Tests a GoogleSearchController
 * 
 * @author Eric Dalquist
 */
@RunWith(MockitoJUnitRunner.class)
public class GoogleSearchControllerTest {
    @InjectMocks private GoogleSearchController googleSearchController = new GoogleSearchController();
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
        
        this.googleSearchController.setRestOperations(restTemplate);

        //Uncomment to make real requests 
        //restTemplate.setRequestFactory(new CommonsClientHttpRequestFactory());
        
        when(clientHttpRequest.getHeaders()).thenReturn(requestHttpHeaders);
        when(clientHttpRequest.execute()).thenReturn(clientHttpResponse);
        when(clientHttpResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(clientHttpResponse.getHeaders()).thenReturn(responseHttpHeaders);
        when(responseHttpHeaders.getContentType()).thenReturn(textJavascriptMediaType);
    }
    
    @Test
    public void testGoogleSearchController() throws Exception {
        final String json = "{\"responseData\": {\"results\":[{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.news.wisc.edu/\",\"url\":\"http://www.news.wisc.edu/\",\"visibleUrl\":\"www.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:Y4-El_XBFRwJ:www.news.wisc.edu\",\"title\":\"\u003cb\u003eNews\u003c/b\u003e from UW-Madison\",\"titleNoFormatting\":\"News from UW-Madison\",\"content\":\"\u003cb\u003eNews\u003c/b\u003e letter maintained by the University of Wisconsin, Madison.\",\"formattedUrl\":\"www.\u003cb\u003enews\u003c/b\u003e.wisc.edu/\",\"richSnippet\":{\"cseImage\":{\"src\":\"http://www.news.wisc.edu/thumbs/0000/1298/DARE_page_detail12_5340-t.jpg\"}},\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://www.news.wisc.edu/\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CAQQFjAA\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNGIuWHH68acjH0O_xDAUAHuWrEcnw\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.news.wisc.edu/releases/\",\"url\":\"http://www.news.wisc.edu/releases/\",\"visibleUrl\":\"www.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:N_P7tAZgbkUJ:www.news.wisc.edu\",\"title\":\"UW-Madison \u003cb\u003eNews\u003c/b\u003e Releases: March 2012\",\"titleNoFormatting\":\"UW-Madison News Releases: March 2012\",\"content\":\"\u003cb\u003eNews\u003c/b\u003e from UW-Madison. Search term: Submit search: \u003cb\u003e...\u003c/b\u003e\",\"formattedUrl\":\"www.\u003cb\u003enews\u003c/b\u003e.wisc.edu/releases/\",\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://www.news.wisc.edu/releases/\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CAYQFjAB\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNH7eNY7irPJ71uFIBzO8AX2anBfVQ\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://stemcells.wisc.edu/\",\"url\":\"http://stemcells.wisc.edu/\",\"visibleUrl\":\"stemcells.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:1ZzUqIHY38YJ:stemcells.wisc.edu\",\"title\":\"University of Wisconsin Stem Cell and Regenerative Medicine Center\",\"titleNoFormatting\":\"University of Wisconsin Stem Cell and Regenerative Medicine Center\",\"content\":\"Recently Released (UW-Madison \u003cb\u003eNews\u003c/b\u003e) \u003cb\u003e...\u003c/b\u003e Stem Cells: New Hope for Heart   Failure Patients ABC \u003cb\u003eNews\u003c/b\u003e. July 27, 2011. Judge Rules in Favor of Obama \u003cb\u003e...\u003c/b\u003e\",\"formattedUrl\":\"stemcells.wisc.edu/\",\"richSnippet\":{\"cseImage\":{\"src\":\"http://stemcells.wisc.edu/images/SCRMC_logo.gif\"}},\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://stemcells.wisc.edu/\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CAgQFjAC\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNHBbN_LUt1S2S8BBy07-80hzsPM7w\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://experts.news.wisc.edu/\",\"url\":\"http://experts.news.wisc.edu/\",\"visibleUrl\":\"experts.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:tWPj4qWoYNYJ:experts.news.wisc.edu\",\"title\":\"UW-Madison Experts Guide\",\"titleNoFormatting\":\"UW-Madison Experts Guide\",\"content\":\"These experts from the UW-Madison faculty and staff have agreed to comment on   breaking \u003cb\u003enews\u003c/b\u003e, ongoing developments and trends in their areas of expertise.\",\"formattedUrl\":\"experts.\u003cb\u003enews\u003c/b\u003e.wisc.edu/\",\"richSnippet\":{\"cseImage\":{\"src\":\"http://experts.news.wisc.edu/headshots/40/thumb/Burden_barry_hs07_4745.jpg?1236191752\"},\"cseThumbnail\":{\"width\":\"80\",\"height\":\"119\",\"src\":\"http://t0.gstatic.com/images?q\u003dtbn:ANd9GcQJlW53uwqRTBR_uYinEzM9-PKlxzVbI_S49SqpaWZ4AqHXVALvQaQnNg\"}},\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://experts.news.wisc.edu/\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CAoQFjAD\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNF36z4n2xhedXbUxfGrX27vLW6_pg\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.news.wisc.edu/arts\",\"url\":\"http://www.news.wisc.edu/arts\",\"visibleUrl\":\"www.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:29QAQkuSO0MJ:www.news.wisc.edu\",\"title\":\"UW-Madison \u003cb\u003eNews\u003c/b\u003e: Arts\",\"titleNoFormatting\":\"UW-Madison News: Arts\",\"content\":\"Stories indexed under: Arts. Total: 788 RSS feed.\",\"formattedUrl\":\"www.\u003cb\u003enews\u003c/b\u003e.wisc.edu/arts\",\"richSnippet\":{\"metatags\":{\"viewport\":\"width \u003d device-width\"},\"cseImage\":{\"src\":\"http://www.news.wisc.edu/thumbs/0000/1305/LTER_at_NSF.jpg\"}},\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://www.news.wisc.edu/arts\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CAwQFjAE\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNGKKdAGXtl7rJFhoiJBQHPqp_tF_Q\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.news.wisc.edu/humanities\",\"url\":\"http://www.news.wisc.edu/humanities\",\"visibleUrl\":\"www.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:2T-k3N3XzYkJ:www.news.wisc.edu\",\"title\":\"UW-Madison \u003cb\u003eNews\u003c/b\u003e: Humanities\",\"titleNoFormatting\":\"UW-Madison News: Humanities\",\"content\":\"Stories indexed under: Humanities. Total: 54 RSS feed.\",\"formattedUrl\":\"www.\u003cb\u003enews\u003c/b\u003e.wisc.edu/humanities\",\"richSnippet\":{\"metatags\":{\"viewport\":\"width \u003d device-width\"},\"cseImage\":{\"src\":\"http://www.news.wisc.edu/thumbs/0000/1298/DARE_page_detail12_5340-t.jpg\"}},\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://www.news.wisc.edu/humanities\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CA4QFjAF\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNGHg0fpsL4Iz1Jmi876iVsp2yjuYw\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://www.news.wisc.edu/inthenews\",\"url\":\"http://www.news.wisc.edu/inthenews\",\"visibleUrl\":\"www.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:nuUywZ0v3boJ:www.news.wisc.edu\",\"title\":\"UW-Madison \u003cb\u003eNews\u003c/b\u003e:\",\"titleNoFormatting\":\"UW-Madison News:\",\"content\":\"UW-Madison in the Media. A selection of media coverage about the university   and its people. Bird Flu Studies Getting Another Round Of Scrutiny By Panel NPR \u003cb\u003e...\u003c/b\u003e\",\"formattedUrl\":\"www.\u003cb\u003enews\u003c/b\u003e.wisc.edu/inthe\u003cb\u003enews\u003c/b\u003e\",\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://www.news.wisc.edu/inthenews\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CBAQFjAG\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNFrQEGmM_jvrTKy-VVVUUouc99Q5Q\"},{\"GsearchResultClass\":\"GwebSearch\",\"unescapedUrl\":\"http://photos.news.wisc.edu/\",\"url\":\"http://photos.news.wisc.edu/\",\"visibleUrl\":\"photos.news.wisc.edu\",\"cacheUrl\":\"http://www.google.com/search?q\u003dcache:nU4brX9SAykJ:photos.news.wisc.edu\",\"title\":\"Photo Library « University Communications « UW-Madison\",\"titleNoFormatting\":\"Photo Library « University Communications « UW-Madison\",\"content\":\"UW-Madison faculty, staff, students and alumni and the \u003cb\u003enews\u003c/b\u003e media may use any   of the images on this site for non-commercial, publicity communication pieces \u003cb\u003e...\u003c/b\u003e\",\"formattedUrl\":\"photos.\u003cb\u003enews\u003c/b\u003e.wisc.edu/\",\"clicktrackUrl\":\"http://www.google.com/url?q\u003dhttp://photos.news.wisc.edu/\u0026sa\u003dU\u0026ei\u003dS8l0T5OlO4P1ggeYxNkt\u0026ved\u003d0CBIQFjAH\u0026client\u003dinternal-uds-cse\u0026usg\u003dAFQjCNHR95NtS7cdzSyI3Yn2m6vF4lo7uA\"}],\"cursor\":{\"resultCount\":\"127,000\",\"pages\":[{\"start\":\"0\",\"label\":1},{\"start\":\"8\",\"label\":2},{\"start\":\"16\",\"label\":3},{\"start\":\"24\",\"label\":4},{\"start\":\"32\",\"label\":5},{\"start\":\"40\",\"label\":6},{\"start\":\"48\",\"label\":7},{\"start\":\"56\",\"label\":8},{\"start\":\"64\",\"label\":9},{\"start\":\"72\",\"label\":10}],\"estimatedResultCount\":\"127000\",\"currentPageIndex\":0,\"moreResultsUrl\":\"http://www.google.com/cse?oe\u003dutf8\u0026ie\u003dutf8\u0026source\u003duds\u0026cx\u003d001601028090761970182:2g0iwqsnk2m\u0026start\u003d0\u0026hl\u003den\u0026q\u003dnews\",\"searchResultTime\":\"0.30\"},\"context\":{\"title\":\"UW-Madison Search\",\"facets\":[]}}, \"responseDetails\": null, \"responseStatus\": 200}";
        
        when(clientHttpRequestFactory.createRequest(new URI("http://ajax.googleapis.com/ajax/services/search/web?q=news&v=1.0&userip=128.104.17.46&rsz=large&cx=001601028090761970182:2g0iwqsnk2m"), HttpMethod.GET)).thenReturn(clientHttpRequest);
        when(clientHttpResponse.getBody()).thenReturn(new ByteArrayInputStream(json.getBytes()));
        when(responseHttpHeaders.getContentLength()).thenReturn((long)json.length());
        when(portletRequest.getProperty("REMOTE_ADDR")).thenReturn("128.104.17.46");
        
        final SearchRequest query = new SearchRequest();
        query.setSearchTerms("news");
        
        final SearchResults results = googleSearchController.getSearchResults(portletRequest, query);

        assertNotNull(results);
        assertEquals(8, results.getSearchResult().size());
    }
}
