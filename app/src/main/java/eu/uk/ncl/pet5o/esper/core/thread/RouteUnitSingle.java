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
package eu.uk.ncl.pet5o.esper.core.thread;

import eu.uk.ncl.pet5o.esper.core.service.EPRuntimeImpl;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementHandleCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Route unit for single match.
 */
public class RouteUnitSingle implements RouteUnitRunnable {
    private static final Logger log = LoggerFactory.getLogger(RouteUnitSingle.class);

    private final EPRuntimeImpl epRuntime;
    private EPStatementHandleCallback handleCallback;
    private final eu.uk.ncl.pet5o.esper.client.EventBean theEvent;
    private final long filterVersion;

    /**
     * Ctor.
     *
     * @param epRuntime      runtime to process
     * @param handleCallback callback
     * @param theEvent       event
     * @param filterVersion  version of filter
     */
    public RouteUnitSingle(EPRuntimeImpl epRuntime, EPStatementHandleCallback handleCallback, eu.uk.ncl.pet5o.esper.client.EventBean theEvent, long filterVersion) {
        this.epRuntime = epRuntime;
        this.theEvent = theEvent;
        this.handleCallback = handleCallback;
        this.filterVersion = filterVersion;
    }

    public void run() {
        try {
            epRuntime.processStatementFilterSingle(handleCallback.getAgentInstanceHandle(), handleCallback, theEvent, filterVersion, 0);

            epRuntime.dispatch();

            epRuntime.processThreadWorkQueue();
        } catch (RuntimeException e) {
            log.error("Unexpected error processing route execution: " + e.getMessage(), e);
        }
    }

}
