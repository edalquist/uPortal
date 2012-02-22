/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.snow;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Wraps an executed {@link HttpMethod}, calling {@link HttpMethod#getResponseBodyAsStream()} and
 * delegating to that. When {@link #close()} is called {@link HttpMethod#releaseConnection()} is
 * called.
 * 
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class HttpClientInputStream extends InputStream {
    private final HttpMethod httpMethod;
    private final InputStream inputStream;
    
    public HttpClientInputStream(HttpMethod httpMethod) throws IOException {
        this.httpMethod = httpMethod;
        this.inputStream = this.httpMethod.getResponseBodyAsStream();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        try {
            this.inputStream.close();
        }
        finally {
            this.httpMethod.releaseConnection();
        }
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public void mark(int readlimit) {
        this.inputStream.mark(readlimit);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    @Override
    public int read(byte[] b) throws IOException {
        return this.inputStream.read(b);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.inputStream.read(b, off, len);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    @Override
    public void reset() throws IOException {
        this.inputStream.reset();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long n) throws IOException {
        return this.inputStream.skip(n);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return this.inputStream.equals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.inputStream.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.inputStream.toString();
    }
}
