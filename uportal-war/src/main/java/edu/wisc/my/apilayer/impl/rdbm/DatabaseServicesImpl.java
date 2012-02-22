/**
 * Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
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
 */
package edu.wisc.my.apilayer.impl.rdbm;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.rdbm.IDatabaseMetadata;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import edu.wisc.my.apilayer.internal.IDatabaseServices;
import edu.wisc.my.apilayer.rdbm.IDatabaseServer;


/**
 * Provides wrapped access to portal configured datasources
 * 
 * TODO this may need to support global JNDI lookups instead
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision $
 * @since 1.0
 * @see edu.wisc.my.apilayer.internal.IDatabaseServices
 */
public final class DatabaseServicesImpl implements IDatabaseServices, ApplicationContextAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private ApplicationContext applicationContext;
    private DataSource defaultDataSource;
    private IDatabaseMetadata defaultDatabaseMetadata;
    private IDatabaseServer defaultDatabaseServer;
    
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /**
     * @return the defaultDataSource
     */
    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }
    /**
     * @param defaultDataSource the defaultDataSource to set
     */
    @Required
    public void setDefaultDataSource(DataSource defaultDataSource) {
        Validate.notNull(defaultDataSource);
        this.defaultDataSource = defaultDataSource;
        this.initDefaultServer();
    }

    /**
     * @return the defaultDatabaseMetadata
     */
    public IDatabaseMetadata getDefaultDatabaseMetadata() {
        return defaultDatabaseMetadata;
    }
    /**
     * @param defaultDatabaseMetadata the defaultDatabaseMetadata to set
     */
    @Required
    public void setDefaultDatabaseMetadata(IDatabaseMetadata defaultDatabaseMetadata) {
        Validate.notNull(defaultDatabaseMetadata);
        this.defaultDatabaseMetadata = defaultDatabaseMetadata;
        this.initDefaultServer();
    }
    
    
    /**
     * Called by the setters to initialize the default database server
     */
    private void initDefaultServer() {
        if (this.defaultDataSource != null && this.defaultDatabaseMetadata != null) {
            this.defaultDatabaseServer = new DatabaseServerImpl(this.defaultDataSource, this.defaultDatabaseMetadata);
        }
    }
    
    
    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IDatabaseServices#getDefaultServer()
     */
    public IDatabaseServer getDefaultServer() {
        return this.defaultDatabaseServer;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IDatabaseServices#getServer(java.lang.String)
     */
    public IDatabaseServer getServer(final String serverName) {
        final DataSource dataSource;
        try {
            dataSource = (DataSource)this.applicationContext.getBean(serverName, DataSource.class);
        }
        catch (NoSuchBeanDefinitionException nsbde) {
            this.logger.info(nsbde);
            return null;
        }
        
        return new DatabaseServerImpl(dataSource);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.apilayer.internal.IDatabaseServices#getServerNames()
     */
    public String[] getServerNames() {
        final String[] beanNames = this.applicationContext.getBeanNamesForType(DataSource.class);
        return beanNames;
    }
}
