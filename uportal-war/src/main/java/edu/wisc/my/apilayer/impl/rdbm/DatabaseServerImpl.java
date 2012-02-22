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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jasig.portal.rdbm.IDatabaseMetadata;

import edu.wisc.my.apilayer.rdbm.IDatabaseServer;


/**
 * Wraps a {@link javax.sql.DataSource} and delegates all
 * method calls to it, wrapping the uPortal specific classes with API layer
 * interface implementations before returning them.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision $
 * @since 1.0
 * @see edu.wisc.my.apilayer.rdbm.IDatabaseServer
 */
public class DatabaseServerImpl implements IDatabaseServer {
    private final DataSource ds;
    private final boolean supportsOuterJoins;
    private final boolean supportsTransactions;
    
    /**
     * @see #DatabaseServerImpl(DataSource, IDatabaseMetadata)
     */
    public DatabaseServerImpl(final DataSource source) {
        this(source, null);
    }
    
    /**
     * Constructs a new {@link DatabaseServerImpl} that wraps a
     * {@link DataSource} and {@link IDatabaseMetadata}.
     * 
     * @param source The {@link DataSource} to wrap. Cannot be null.
     * @param metaData The {@link IDatabaseMetadata} to wrap.
     * @throws IllegalArgumentException if the specified {@link DataSource} is null.
     */
    public DatabaseServerImpl(final DataSource source, final IDatabaseMetadata data) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }

        this.ds = source;
        
        if (data != null) {
            this.supportsOuterJoins = data.supportsOuterJoins();
            this.supportsTransactions = data.supportsTransactions();
        }
        else {
            boolean transactions = false;
            boolean joins = false;

            Connection conn = null;
            try {
                conn = this.ds.getConnection();
                final DatabaseMetaData md = conn.getMetaData();
                transactions = md.supportsTransactions();
                joins = md.supportsOuterJoins();
            }
            catch (SQLException sqle) {
                //Ignore exception while determining meta data
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    }
                    catch (SQLException sqle) {
                        //Ignore exception on close
                    }
                }
            }
            
            this.supportsOuterJoins = joins;
            this.supportsTransactions = transactions;
        }
    }

    /**
     * @see edu.wisc.my.apilayer.rdbm.IDatabaseServer#getDataSource()
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * @see edu.wisc.my.apilayer.rdbm.IDatabaseServer#supportsOuterJoins()
     */
    public boolean supportsOuterJoins() {
        return this.supportsOuterJoins;
    }

    /**
     * @see edu.wisc.my.apilayer.rdbm.IDatabaseServer#supportsTransactions()
     */
    public boolean supportsTransactions() {
        return this.supportsTransactions;
    }
}
