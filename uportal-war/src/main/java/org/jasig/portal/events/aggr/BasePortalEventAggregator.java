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

package org.jasig.portal.events.aggr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jasig.portal.events.PortalEvent;
import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;
import org.jasig.portal.events.aggr.session.EventSession;
import org.jasig.portal.jpa.BaseAggrEventsJpaDao.AggrEventsTransactional;
import org.jasig.portal.utils.cache.CacheKey;
import org.joda.time.DateMidnight;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Base {@link PortalEvent} aggregator, useful for aggregations that extend from {@link BaseAggregationImpl} 
 * 
 * @author Eric Dalquist
 * @param <E> The {@link PortalEvent} type handled by this aggregator
 * @param <T> The {@link BaseAggregationImpl} subclass operated on by this aggregator 
 * @param <K> The {@link BaseAggregationKey} type used by this aggregator
 */
public abstract class BasePortalEventAggregator<
            E extends PortalEvent, 
            T extends BaseAggregationImpl,
            K extends BaseAggregationKey> 
    implements IPortalEventAggregator<E> {
    
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String cacheKeySource = this.getClass().getName();
    
//    private AggregationIntervalHelper aggregationIntervalHelper;
//
//    @Autowired
//    public final void setAggregationIntervalHelper(AggregationIntervalHelper aggregationIntervalHelper) {
//        this.aggregationIntervalHelper = aggregationIntervalHelper;
//    }
    
    /**
     * @return The private aggregation DAO to use
     */
    protected abstract BaseAggregationPrivateDao<T, K> getAggregationDao();

    /**
     * Called for each {@link BaseAggregationImpl} that needs to be updated 
     * 
     * @param e The {@link PortalEvent} to get the data from
     * @param intervalInfo The info about the interval the aggregation is for
     * @param aggregation The aggregation to update
     */
    protected abstract void updateAggregation(E e, AggregationIntervalInfo intervalInfo, T aggregation);
    
    /**
     * Create a unique key that describes the aggregation.
     * 
     * @param intervalInfo The info about the interval the aggregation is for
     * @param aggregatedGroup The group the aggregation is for
     * @param event The event the aggregation is for
     */
    protected abstract K createAggregationKey(AggregationIntervalInfo intervalInfo, AggregatedGroupMapping aggregatedGroup, E event); 

    @AggrEventsTransactional
    @Override
    public final void aggregateEvent(E e, EventSession eventSession,
            EventAggregationContext eventAggregationContext,
            Map<AggregationInterval, AggregationIntervalInfo> currentIntervals) {
        
        final BaseAggregationPrivateDao<T, K> aggregationDao = this.getAggregationDao();
        
        for (Map.Entry<AggregationInterval, AggregationIntervalInfo> intervalInfoEntry : currentIntervals.entrySet()) {
            final AggregationIntervalInfo intervalInfo = intervalInfoEntry.getValue();
            
            final Collection<T> cachedAggregations = getOrLoadAggregations(eventAggregationContext, intervalInfo, e);
            
            //If the number of cached aggregations != number of group mappings we need to figure out which groups don't have an
            //aggregation yet. This is done by cloning the groupMappings map and removing groups we see from it as the cached
            //aggregations are parsed
            final Set<AggregatedGroupMapping> groupMappings;
            if (eventSession == null) {
                groupMappings = Collections.emptySet();
            }
            else {
                groupMappings = eventSession.getGroupMappings();
            }
            final Set<AggregatedGroupMapping> mutableGroupMappings = new LinkedHashSet<AggregatedGroupMapping>(groupMappings);
        
            //Update the aggregation for each cached aggr
            for (final T aggregation : cachedAggregations) {
                //Remove the aggregation from the group set to mark that it has been updated
                if (mutableGroupMappings != null) {
                    mutableGroupMappings.remove(aggregation.getAggregatedGroup());
                }
                
                updateAggregation(e, intervalInfo, aggregation);
            }
            
            //Create aggregations for any left over groups
            if (!mutableGroupMappings.isEmpty()) {
                for (final AggregatedGroupMapping aggregatedGroup : mutableGroupMappings) {
                    final K key = this.createAggregationKey(intervalInfo, aggregatedGroup, e);
                    final T aggregation = aggregationDao.createAggregation(key);
                    cachedAggregations.add(aggregation);
                    updateAggregation(e, intervalInfo, aggregation);
                }
            }
        }
    }

    @AggrEventsTransactional
    @Override
    public final void handleIntervalBoundary(AggregationInterval interval, EventAggregationContext eventAggregationContext,
            Map<AggregationInterval, AggregationIntervalInfo> intervals) {
        
        final AggregationIntervalInfo intervalInfo = intervals.get(interval);
        
        final BaseAggregationPrivateDao<T, K> aggregationDao = this.getAggregationDao();
        
        //Complete all of the aggregations that have been touched by this session, can be null if no events of
        //the handled type have been seen so far in this session
        final Iterable<T> aggregations = this.getCachedAggregations(eventAggregationContext, intervalInfo);
        for (final T aggregation : aggregations) {
            final int duration = intervalInfo.getTotalDuration();
            aggregation.intervalComplete(duration);
        }
        aggregationDao.updateAggregations(aggregations);
        
//        //Look for any uncomplete aggregations from the previous interval
//        final AggregationIntervalInfo prevIntervalInfo = this.aggregationIntervalHelper.getIntervalInfo(interval, intervalInfo.getStart().minusMinutes(1));
//        final Collection<T> unclosedAggregations = aggregationDao.getUnclosedAggregations(prevIntervalInfo.getStart(), prevIntervalInfo.getEnd(), interval);
//        for (final T aggregation : unclosedAggregations) {
//            final int duration = intervalInfo.getTotalDuration();
//            aggregation.intervalComplete(duration);
//            logger.debug("Marked complete, was previously unclosed: {}", aggregation);
//        }
//        aggregationDao.updateAggregations(unclosedAggregations);
    }

    /**
     * Get the set of existing aggregations looking first in the aggregation session and then in the db
     */
    private Collection<T> getOrLoadAggregations(EventAggregationContext eventAggregationContext, AggregationIntervalInfo intervalInfo, E event) {
        final K aggregationKey = this.createAggregationKey(intervalInfo, null, event);
        final CacheKey cachedAggregationsCacheKey = CacheKey.build(cacheKeySource, aggregationKey);
        
        Collection<T> cachedAggregations = eventAggregationContext.getAttribute(cachedAggregationsCacheKey);
        if (cachedAggregations == null) {
            //Nothing in the aggr session yet, cache the current set of aggregations from the DB in the aggr session
            cachedAggregations = this.getAggregationDao().getAggregationsForInterval(aggregationKey);
            eventAggregationContext.setAttribute(cachedAggregationsCacheKey, cachedAggregations);
            
            //Track all event scoped collections
            final CacheKey eventScopedKeysCacheKey = createAggregationSessionCacheKey(intervalInfo);
            Set<CacheKey> eventScopedKeys = eventAggregationContext.getAttribute(eventScopedKeysCacheKey);
            if (eventScopedKeys == null) {
                eventScopedKeys = new HashSet<CacheKey>();
                eventAggregationContext.setAttribute(eventScopedKeysCacheKey, eventScopedKeys);
            }
            eventScopedKeys.add(cachedAggregationsCacheKey);
        }
        
        return cachedAggregations;
    }

    /**
     * Get the set of existing aggregations from the aggregation session
     */
    private Iterable<T> getCachedAggregations(final EventAggregationContext eventAggregationContext, AggregationIntervalInfo intervalInfo) {
        final CacheKey eventScopedKeysCacheKey = createAggregationSessionCacheKey(intervalInfo);
        final Set<CacheKey> eventScopedKeys = eventAggregationContext.getAttribute(eventScopedKeysCacheKey);
        if (eventScopedKeys == null || eventScopedKeys.isEmpty()) {
            return Collections.emptySet();
        }
        
        //Use a little iterator of iterator pattern to avoid extra trips over the collection of cached aggregations
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new UnmodifiableIterator<T>() {
                    final Iterator<CacheKey> eventScopedKeysItr = eventScopedKeys.iterator();
                    Iterator<T> cachedAggregationsItr;
                    
                    @Override
                    public boolean hasNext() {
                        checkState();
                        
                        return cachedAggregationsItr != null && cachedAggregationsItr.hasNext();
                    }

                    @Override
                    public T next() {
                        checkState();
                        
                        return cachedAggregationsItr.next();
                    }

                    private void checkState() {
                        while ((cachedAggregationsItr == null || !cachedAggregationsItr.hasNext()) && eventScopedKeysItr.hasNext()) {
                            final CacheKey cachedAggregationsCacheKey = eventScopedKeysItr.next();
                            final Collection<T> cachedAggregations = eventAggregationContext.getAttribute(cachedAggregationsCacheKey);
                            cachedAggregationsItr = cachedAggregations.iterator();
                        }
                    }
                };
            }
        };
    }

    /**
     * Create the CacheKey used to store data in the aggregation session
     */
    private CacheKey createAggregationSessionCacheKey(AggregationIntervalInfo intervalInfo) {
        final DateMidnight date = intervalInfo.getDateDimension().getDate();
        final LocalTime time = intervalInfo.getTimeDimension().getTime();
        final AggregationInterval aggregationInterval = intervalInfo.getAggregationInterval();
        return CacheKey.build(cacheKeySource, date, time, aggregationInterval);
    }
}
