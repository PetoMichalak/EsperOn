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

import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.dispatch.DispatchService;
import com.espertech.esper.util.MutableBoolean;

/**
 * Convenience view for dispatching view updates received from a parent view to update listeners
 * via the dispatch service.
 */
public class UpdateDispatchViewNonBlocking extends UpdateDispatchViewBase {
    /**
     * Ctor.
     *
     * @param dispatchService            - for performing the dispatch
     * @param statementResultServiceImpl - handles result delivery
     */
    public UpdateDispatchViewNonBlocking(StatementResultService statementResultServiceImpl, DispatchService dispatchService) {
        super(statementResultServiceImpl, dispatchService);
    }

    public void update(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
        newResult(new UniformPair<>(newData, oldData));
    }

    public void newResult(UniformPair<com.espertech.esper.client.EventBean[]> results) {
        statementResultService.indicate(results);
        MutableBoolean waiting = isDispatchWaiting.get();
        if (!waiting.isValue()) {
            dispatchService.addExternal(this);
            waiting.setValue(true);
        }
    }
}
