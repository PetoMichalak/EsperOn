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
package eu.uk.ncl.pet5o.esper.filter;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.filterspec.FilterValueSet;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class FilterServiceLockFine extends FilterServiceBase {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FilterServiceLockFine(boolean allowIsolation) {
        super(new FilterServiceGranularLockFactoryReentrant(), allowIsolation);
    }

    public void acquireWriteLock() {
        lock.writeLock().lock();
    }

    public void releaseWriteLock() {
        lock.writeLock().unlock();
    }

    public FilterSet take(Set<Integer> statementId) {
        lock.readLock().lock();
        try {
            return super.takeInternal(statementId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void apply(FilterSet filterSet) {
        lock.readLock().lock();
        try {
            super.applyInternal(filterSet);
        } finally {
            lock.readLock().unlock();
        }
    }

    public long evaluate(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandle> matches) {
        lock.readLock().lock();
        try {
            return super.evaluateInternal(theEvent, matches);
        } finally {
            lock.readLock().unlock();
        }
    }

    public long evaluate(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, Collection<FilterHandle> matches, int statementId) {
        lock.readLock().lock();
        try {
            return super.evaluateInternal(theEvent, matches, statementId);
        } finally {
            lock.readLock().unlock();
        }
    }

    public FilterServiceEntry add(FilterValueSet filterValueSet, FilterHandle callback) {
        return super.addInternal(filterValueSet, callback);
    }

    public void remove(FilterHandle callback, FilterServiceEntry filterServiceEntry) {
        super.removeInternal(callback, filterServiceEntry);
    }

    public void removeType(EventType type) {
        super.removeTypeInternal(type);
    }
}
