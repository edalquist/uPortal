/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.snow;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import edu.wisc.my.snow.om.DeclaredSnowEmergency;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.4 $
 */
public class SnowEmergencyDaoImpl implements SnowEmergencyDao, CacheEntryFactory {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private Resource snowEmergencyFeed;
    private Ehcache feedDataCache;
    private HttpClient httpClient;
    
    public Resource getSnowEmergencyFeed() {
        return this.snowEmergencyFeed;
    }

    public void setSnowEmergencyFeed(Resource snowEmergencyFeedUrl) {
        Validate.notNull(snowEmergencyFeedUrl);
        this.snowEmergencyFeed = snowEmergencyFeedUrl;
    }
    
    public void setFeedDataCache(Ehcache feedDataCache) {
        this.feedDataCache = feedDataCache;
    }

    @Autowired
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Object createEntry(Object key) throws Exception {
        InputStream xmlInputStream = null;
        try {
            xmlInputStream = this.getXmlInputStream();
            final JAXBContext jaxbContext = this.getJAXBContext();
            final Unmarshaller unmarshaller = this.getUnmarshaller(jaxbContext);
            final DeclaredSnowEmergency snowEmergency = this.unmarshal(xmlInputStream, unmarshaller);
            
            this.logger.info("Loaded: " + snowEmergency);
            
            return snowEmergency;
        }
        catch (Exception e) {
            final DeclaredSnowEmergency snowEmergency = new DeclaredSnowEmergency();
            snowEmergency.setStatus(false);
            snowEmergency.setLink("http://www.cityofmadison.com/winter/");
            snowEmergency.setMessage("Failed to load actual snow emergency information due to feed error: " + e.getMessage());
            
            this.logger.warn("Error while loading DeclaredSnowEmergency data, returning mock DeclaredSnowEmergency data instead: " + snowEmergency, e);
            
            return snowEmergency;
        }
        finally {
            IOUtils.closeQuietly(xmlInputStream);
        }
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.snow.om.SnowEmergencyDao#getSnowEmergencyStatus()
     */
    public DeclaredSnowEmergency getSnowEmergencyStatus() {
        final Element element = this.feedDataCache.get("");
        return (DeclaredSnowEmergency)element.getObjectValue();
    }
    
    /**
     * @return an InputStream for the XML data
     */
    protected InputStream getXmlInputStream() throws IOException {
        final URI uri = this.snowEmergencyFeed.getURI();
        final String scheme = uri.getScheme();
        
        if (scheme.startsWith("http")) {
            final GetMethod getMethod = new GetMethod(uri.toString());
            try {
                final int result = httpClient.executeMethod(getMethod);
                if (result != 200) {
                    throw new RuntimeException("Failed to retrieve snow emergency data from '" + this.snowEmergencyFeed + "' due to return code of: " + result);
                }
                
                return new HttpClientInputStream(getMethod);
            }
            catch (HttpException e) {
                throw new RuntimeException("Failed to retrieve snow emergency data from '" + this.snowEmergencyFeed + "'", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to retrieve snow emergency data from '" + this.snowEmergencyFeed + "'", e);
            }
        }
        
        return this.snowEmergencyFeed.getInputStream();
    }

    /**
     * @return The JAXB context to parse the XML resource with
     */
    protected JAXBContext getJAXBContext() {
        final Package loadedPackage = DeclaredSnowEmergency.class.getPackage();
        final String filterDisplayPackage = loadedPackage.getName();
        try {
            return JAXBContext.newInstance(filterDisplayPackage);
        }
        catch (JAXBException e) {
            throw new RuntimeException("Failed to create " + JAXBContext.class + " to unmarshal " + DeclaredSnowEmergency.class, e);
        }
    }

    /**
     * @param jaxbContext The context to get an unmarshaller for
     * @return An unmarshaller to use to generate object from the XML
     */
    protected Unmarshaller getUnmarshaller(final JAXBContext jaxbContext) {
        try {
            return jaxbContext.createUnmarshaller();
        }
        catch (JAXBException e) {
            throw new RuntimeException("Failed to create " + Unmarshaller.class + " to unmarshal " + DeclaredSnowEmergency.class, e);
        }
    }

    /**
     * @param xmlInputStream InputStream to read the XML from
     * @param unmarshaller Unmarshaller to generate the object from the XML with
     * @return An unmarshalled object model of the XML
     */
    protected DeclaredSnowEmergency unmarshal(final InputStream xmlInputStream, final Unmarshaller unmarshaller) {
        try {
            return (DeclaredSnowEmergency)unmarshaller.unmarshal(xmlInputStream);
        }
        catch (JAXBException e) {
            throw new RuntimeException("Unexpected JAXB error while unmarshalling DeclaredSnowEmergency", e);
        }
    }
}
