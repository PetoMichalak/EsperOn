/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.epl.db;

import eu.uk.ncl.pet5o.esper.client.ConfigurationDataCache;
import eu.uk.ncl.pet5o.esper.client.ConfigurationExpiryTimeCache;
import eu.uk.ncl.pet5o.esper.client.ConfigurationLRUCache;
import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandle;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.schedule.ScheduleBucket;
import eu.uk.ncl.pet5o.esper.schedule.SchedulingService;

/**
 * Factory for data caches for use caching database query results and method invocation results.
 */
public class DataCacheFactory {
    /**
     * Creates a cache implementation for the strategy as defined by the cache descriptor.
     *
     * @param cacheDesc                      cache descriptor
     * @param epStatementAgentInstanceHandle statement handle for timer invocations
     * @param schedulingService              scheduling service for time-based caches
     * @param scheduleBucket                 for ordered timer invokation
     * @param statementContext               statement context
     * @param streamNum                      stream number
     * @return data cache implementation
     */
    public DataCache getDataCache(ConfigurationDataCache cacheDesc,
                                  StatementContext statementContext,
                                  EPStatementAgentInstanceHandle epStatementAgentInstanceHandle,
                                  SchedulingService schedulingService,
                                  ScheduleBucket scheduleBucket,
                                  int streamNum) {
        if (cacheDesc == null) {
            return new DataCacheNullImpl();
        }

        if (cacheDesc instanceof ConfigurationLRUCache) {
            ConfigurationLRUCache lruCache = (ConfigurationLRUCache) cacheDesc;
            return new DataCacheLRUImpl(lruCache.getSize());
        }

        if (cacheDesc instanceof ConfigurationExpiryTimeCache) {
            ConfigurationExpiryTimeCache expCache = (ConfigurationExpiryTimeCache) cacheDesc;
            return makeTimeCache(expCache, statementContext, epStatementAgentInstanceHandle, schedulingService, scheduleBucket, streamNum);
        }

        throw new IllegalStateException("Cache implementation class not configured");
    }

    protected DataCache makeTimeCache(ConfigurationExpiryTimeCache expCache, StatementContext statementContext, EPStatementAgentInstanceHandle epStatementAgentInstanceHandle, SchedulingService schedulingService, ScheduleBucket scheduleBucket, int streamNum) {
        return new DataCacheExpiringImpl(expCache.getMaxAgeSeconds(), expCache.getPurgeIntervalSeconds(), expCache.getCacheReferenceType(),
                schedulingService, scheduleBucket.allocateSlot(), epStatementAgentInstanceHandle, statementContext.getTimeAbacus());
    }
}
