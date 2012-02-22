/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.portlet.ad.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.wisc.my.portlet.ad.om.AccessRecord;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
@Repository
public class JpaAccessRecordDao implements AccessRecordDao {
    protected final Log logger = LogFactory.getLog(getClass());
    private final ReferenceKeyGenerator keyGenerator = new ReferenceKeyGenerator();
    
    private int referenceKeyLength = 8;
    private int maxKeyGenerationAttempts = 500;
    private EntityManager entityManager;
    
    /**
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Length of reference key to generate, defaults to 8
     */
    public void setReferenceKeyLength(int referenceKeyLength) {
        this.referenceKeyLength = referenceKeyLength;
    }

    /**
     * Maximum number of attempts at unique random key generation, defaults to 500
     */
    public void setMaxKeyGenerationAttempts(int maxKeyGenerationAttempts) {
        this.maxKeyGenerationAttempts = maxKeyGenerationAttempts;
    }



    /* (non-Javadoc)
     * @see edu.wisc.my.portlet.ad.dao.AccessRecordDao#createAccessRecord(java.lang.String)
     */
    @Override
    @Transactional
    public AccessRecord createAccessRecord(String username) {
        final String referenceKey = generateReferenceKey();
        
        final AccessRecord accessRecord = new AccessRecord(referenceKey, username);
        
        this.entityManager.persist(accessRecord);
        
        return accessRecord;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.portlet.ad.dao.AccessRecordDao#storeAccessRecord(edu.wisc.my.portlet.ad.om.AccessRecord)
     */
    @Override
    @Transactional
    public void storeAccessRecord(AccessRecord record) {
        Validate.notNull(record, "AccessRecord can not be null");
        
        this.entityManager.persist(record);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.portlet.ad.dao.AccessRecordDao#getAccessRecordByKey(java.lang.String)
     */
    @Override
    public AccessRecord getAccessRecordByKey(String referenceKey) {
        final Query query = this.entityManager.createNamedQuery("AccessRecord.getByReferenceKey");
        query.setParameter("referenceKey", referenceKey);
        final List<?> results = query.getResultList();
        return (AccessRecord)DataAccessUtils.singleResult(results);
    }
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlet.ad.dao.AccessRecordDao#getAccessRecordsForUser(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<AccessRecord> getAccessRecordsForUser(String username) {
        final Query query = this.entityManager.createNamedQuery("AccessRecord.getByUsername");
        query.setParameter("username", username);
        return query.getResultList();
    }
    
    
    /* (non-Javadoc)
     * @see edu.wisc.my.portlet.ad.dao.AccessRecordDao#getMostRecentAccessRecords(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AccessRecord> getAccessRecordsRange(int startPosition, int maxResults) {
        final Query query = this.entityManager.createNamedQuery("AccessRecord.getMostRecent");
        query.setFirstResult(startPosition);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    /**
     * Generate a unique, random reference key
     */
    protected String generateReferenceKey() {
        for (int keyTry = 0; keyTry < maxKeyGenerationAttempts; keyTry++) {
            final String referenceKey = keyGenerator.generateKey(referenceKeyLength);
            final AccessRecord accessRecord = this.getAccessRecordByKey(referenceKey);
            if (accessRecord == null) {
                logger.info("Generated reference key " + referenceKey);
                return referenceKey;
            }
            
            logger.debug("Reference key " + referenceKey + " already exists in the database, trying again.");
        }
        
        throw new IllegalStateException("Could not generate unique refernce key after " + maxKeyGenerationAttempts + " attempts");
    }
}
