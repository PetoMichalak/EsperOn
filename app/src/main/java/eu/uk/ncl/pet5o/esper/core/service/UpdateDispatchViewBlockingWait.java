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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.dispatch.DispatchService;
import eu.uk.ncl.pet5o.esper.util.MutableBoolean;

/**
 * Convenience view for dispatching view updates received from a parent view to update listeners
 * via the dispatch service.
 */
public class UpdateDispatchViewBlockingWait extends UpdateDispatchViewBase {
    private UpdateDispatchFutureWait currentFutureWait;
    private long msecTimeout;

    /**
     * Ctor.
     *
     * @param dispatchService            - for performing the dispatch
     * @param msecTimeout                - timeout for preserving dispatch order through blocking
     * @param statementResultServiceImpl - handles result delivery
     */
    public UpdateDispatchViewBlockingWait(StatementResultService statementResultServiceImpl, DispatchService dispatchService, long msecTimeout) {
        super(statementResultServiceImpl, dispatchService);
        this.currentFutureWait = new UpdateDispatchFutureWait(); // use a completed future as a start
        this.msecTimeout = msecTimeout;
    }

    public void update(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        newResult(new UniformPair<>(newData, oldData));
    }

    public void newResult(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> results) {
        statementResultService.indicate(results);
        MutableBoolean waiting = isDispatchWaiting.get();
        if (!waiting.isValue()) {
            UpdateDispatchFutureWait nextFutureWait;
            synchronized (this) {
                nextFutureWait = new UpdateDispatchFutureWait(this, currentFutureWait, msecTimeout);
                currentFutureWait.setLater(nextFutureWait);
                currentFutureWait = nextFutureWait;
            }
            dispatchService.addExternal(nextFutureWait);
            waiting.setValue(true);
        }
    }
}
