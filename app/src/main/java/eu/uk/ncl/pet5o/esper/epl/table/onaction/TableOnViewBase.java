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
package eu.uk.ncl.pet5o.esper.epl.table.onaction;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public abstract class TableOnViewBase extends ViewSupport implements StopCallback, TableOnView {
    private static final Logger log = LoggerFactory.getLogger(TableOnViewBase.class);

    protected final SubordWMatchExprLookupStrategy lookupStrategy;
    protected final TableStateInstance tableStateInstance;
    protected final ExprEvaluatorContext exprEvaluatorContext;
    protected final TableMetadata metadata;
    protected final boolean acquireWriteLock;

    protected TableOnViewBase(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance tableStateInstance, ExprEvaluatorContext exprEvaluatorContext, TableMetadata metadata, boolean acquireWriteLock) {
        this.lookupStrategy = lookupStrategy;
        this.tableStateInstance = tableStateInstance;
        this.exprEvaluatorContext = exprEvaluatorContext;
        this.metadata = metadata;
        this.acquireWriteLock = acquireWriteLock;
    }

    public abstract void handleMatching(eu.uk.ncl.pet5o.esper.client.EventBean[] triggerEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] matchingEvents);

    public void stop() {
        log.debug(".stop");
    }

    public void update(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        if (newData == null) {
            return;
        }

        if (acquireWriteLock) {
            tableStateInstance.getTableLevelRWLock().writeLock().lock();
            try {
                eu.uk.ncl.pet5o.esper.client.EventBean[] eventsFound = lookupStrategy.lookup(newData, exprEvaluatorContext);
                handleMatching(newData, eventsFound);
            } finally {
                tableStateInstance.getTableLevelRWLock().writeLock().unlock();
            }
        } else {
            tableStateInstance.getTableLevelRWLock().readLock().lock();
            try {
                eu.uk.ncl.pet5o.esper.client.EventBean[] eventsFound = lookupStrategy.lookup(newData, exprEvaluatorContext);
                handleMatching(newData, eventsFound);
            } finally {
                tableStateInstance.getTableLevelRWLock().readLock().unlock();
            }
        }
    }

    /**
     * returns expr context.
     *
     * @return context
     */
    public ExprEvaluatorContext getExprEvaluatorContext() {
        return exprEvaluatorContext;
    }

    public Iterator<EventBean> iterator() {
        return CollectionUtil.NULL_EVENT_ITERATOR;
    }

    public EventType getEventType() {
        return metadata.getPublicEventType();
    }
}
