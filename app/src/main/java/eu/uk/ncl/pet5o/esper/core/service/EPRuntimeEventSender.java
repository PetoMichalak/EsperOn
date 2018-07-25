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

/**
 * For use by {@link eu.uk.ncl.pet5o.esper.client.EventSender} for direct feed of wrapped events for processing.
 */
public interface EPRuntimeEventSender {
    /**
     * Equivalent to the sendEvent method of EPRuntime, for use to process an known event.
     *
     * @param eventBean is the event object wrapped by an event bean providing the event metadata
     */
    public void processWrappedEvent(eu.uk.ncl.pet5o.esper.client.EventBean eventBean);

    /**
     * For processing a routed event.
     *
     * @param theEvent routed event
     */
    public void routeEventBean(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);
}
